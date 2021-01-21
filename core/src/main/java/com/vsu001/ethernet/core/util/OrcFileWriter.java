package com.vsu001.ethernet.core.util;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.ColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.DecimalColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.DoubleColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.TimestampColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.orc.OrcFile;
import org.apache.orc.TypeDescription;
import org.apache.orc.Writer;

public class OrcFileWriter {

  public static void write(
      String path,
      String struct,
      List<Map<String, Object>> data
  ) throws IOException {
    // Use default configurations
    Configuration config = new Configuration();
    config.set("fs.defaultFS", "hdfs://namenode:8020");
    config.set("dfs.datanode.use.datanode.hostname", "true");
    config.set("dfs.client.use.datanode.hostname", "true");
    write(config, path, struct, data);
  }

  public static void write(
      Configuration configuration,
      String path,
      String struct,
      List<Map<String, Object>> data
  ) throws IOException {
    // Create the schemas and extract metadata from the schema
    TypeDescription schema = TypeDescription.fromString(struct);
    List<String> fieldNames = schema.getFieldNames();
    List<TypeDescription> columnTypes = schema.getChildren();

    // Create a row batch
    VectorizedRowBatch batch = schema.createRowBatch();

    // Get the column vector references
    List<BiConsumer<Integer, Object>> consumers = new ArrayList<>(columnTypes.size());
    for (int i = 0; i < columnTypes.size(); i++) {
      TypeDescription type = columnTypes.get(i);
      ColumnVector vector = batch.cols[i];
      consumers.add(createColumnWriter(type, vector));
    }

    // Open a writer to write the data to an ORC fle
    try (Writer writer = OrcFile
        .createWriter(new Path(path), OrcFile.writerOptions(configuration).setSchema(schema))) {
      for (Map<String, Object> row : data) {
        // batch.size should be increased externally
        int rowNum = batch.size++;

        // Write each column to the associated column vector
        for (int i = 0; i < fieldNames.size(); i++) {
          consumers.get(i).accept(rowNum, row.get(fieldNames.get(i)));
        }

        // If the buffer is full, write it to disk
        if (batch.size == batch.getMaxSize()) {
          writer.addRowBatch(batch);
          batch.reset();
        }
      }

      // Check unwritten rows before closing
      if (batch.size != 0) {
        writer.addRowBatch(batch);
      }
    }
  }

  public static void writeTableResults(
      String path,
      String struct,
      TableResult tableResult
  ) throws IOException {
    // Use default configurations
    Configuration config = new Configuration();
    config.set("fs.defaultFS", "hdfs://namenode:8020");
    config.set("dfs.datanode.use.datanode.hostname", "true");
    config.set("dfs.client.use.datanode.hostname", "true");
    writeTableResults(config, path, struct, tableResult);
  }

  public static void writeTableResults(
      Configuration configuration,
      String path,
      String struct,
      TableResult tableResult
  ) throws IOException {
    // Create the schemas and extract metadata from the schema
    TypeDescription schema = TypeDescription.fromString(struct);
    List<String> fieldNames = schema.getFieldNames();
    List<TypeDescription> columnTypes = schema.getChildren();

    // Create a row batch
    VectorizedRowBatch batch = schema.createRowBatch();

    // Get the column vector references
    List<BiConsumer<Integer, Object>> consumers = new ArrayList<>(columnTypes.size());
    for (int i = 0; i < columnTypes.size(); i++) {
      TypeDescription type = columnTypes.get(i);
      ColumnVector vector = batch.cols[i];
      consumers.add(createColumnWriter(type, vector));
    }

    // Open a writer to write the data to an ORC fle
    try (Writer writer = OrcFile
        .createWriter(new Path(path), OrcFile.writerOptions(configuration).setSchema(schema))) {
      for (FieldValueList fieldValueList : tableResult.iterateAll()) {
        // batch.size should be increased externally
        int rowNum = batch.size++;

        // Write each column to the associated column vector
        for (int i = 0; i < fieldNames.size(); i++) {
          consumers.get(i).accept(rowNum, fieldValueList.get(fieldNames.get(i)));
        }

        // If the buffer is full, write it to disk
        if (batch.size == batch.getMaxSize()) {
          writer.addRowBatch(batch);
          batch.reset();
        }
      }

      // Check unwritten rows before closing
      if (batch.size != 0) {
        writer.addRowBatch(batch);
      }
    }
  }

  private static BiConsumer<Integer, Object> createColumnWriter(
      TypeDescription description,
      ColumnVector columnVector
  ) {
    // Reference: https://orc.apache.org/docs/core-java.html
    String type = description.getCategory().getName();
    BiConsumer<Integer, Object> consumer;
    if ("tinyint".equals(type)) {
      consumer = (row, val) -> ((LongColumnVector) columnVector).vector[row] = ((Number) val)
          .longValue();
    } else if ("smallint".equals(type)) {
      consumer = (row, val) -> ((LongColumnVector) columnVector).vector[row] = ((Number) val)
          .longValue();
    } else if ("int".equals(type) || "date".equals(type)) {
      // Date is represented as int epoch days
      consumer = (row, val) -> ((LongColumnVector) columnVector).vector[row] = ((Number) val)
          .longValue();
    } else if ("bigint".equals(type)) {
      consumer = (row, val) -> ((LongColumnVector) columnVector).vector[row] = ((Number) val)
          .longValue();
    } else if ("boolean".equals(type)) {
      consumer = (row, val) -> ((LongColumnVector) columnVector).vector[row] =
          (Boolean) val ? 1 : 0;
    } else if ("float".equals(type)) {
      consumer = (row, val) -> ((DoubleColumnVector) columnVector).vector[row] = ((Number) val)
          .floatValue();
    } else if ("double".equals(type)) {
      consumer = (row, val) -> ((DoubleColumnVector) columnVector).vector[row] = ((Number) val)
          .doubleValue();
    } else if ("decimal".equals(type)) {
      consumer = (row, val) -> ((DecimalColumnVector) columnVector).vector[row]
          .set(HiveDecimal.create((BigDecimal) val));
    } else if ("string".equals(type) || type.startsWith("varchar") || "char".equals(type)) {
      consumer = (row, val) -> {
        byte[] buffer = val.toString().getBytes(StandardCharsets.UTF_8);
        ((BytesColumnVector) columnVector).setRef(row, buffer, 0, buffer.length);
      };
    } else if ("timestamp".equals(type)) {
      consumer = (row, val) -> ((TimestampColumnVector) columnVector).set(row, (Timestamp) val);
    } else {
      throw new RuntimeException("Unsupported type " + type);
    }
    return consumer;
  }

  /**
   * Obtain the ORC type string of a `FieldDescriptor` object specification of a protobuf field.
   * <p>
   * This function is a degenerate HashMap containing the mappings between the protobuf field's
   * FieldDescriptor and the ORC types.
   * <p>
   * Only types that are required for the Ethernet project is implemented in this method.
   *
   * @param fieldDescriptor FieldDescriptor object definition containing the protobuf's field
   *                        specifications.
   * @return The equivalent ORC type string that is of the equivalent type as the `FieldDescriptor`
   * specification.
   */
  public static String protoToOrcType(FieldDescriptor fieldDescriptor) {
    String javaTypeName = fieldDescriptor.getJavaType().name();
    switch (javaTypeName) {
      case "STRING":
        return "string";
      case "LONG":
        return "bigint";
      case "DOUBLE":
        return "double";
      case "BOOLEAN":
        return "boolean";
      case "MESSAGE":
        if (fieldDescriptor.getName().contains("timestamp")) {
          return "timestamp";
        }
        throw new RuntimeException(javaTypeName + " type not supported");
      default:
        throw new RuntimeException(javaTypeName + " type not supported");
    }
  }

  /**
   * Generate the struct string required to determine the "schema" of an ORC file from a a
   * protobuf's FieldDescriptors.
   *
   * @param fieldDescriptorList FieldDescriptors describing the fields of a protobuf.
   * @return The ORC struct string describing the protobuf fields.
   */
  public static String protoToOrcStructStr(List<FieldDescriptor> fieldDescriptorList) {
    String structStr = fieldDescriptorList.stream()
        .map(s -> String.format("%s:%s", s.getName(), protoToOrcType(s)))
        .collect(Collectors.joining(","));
    return "struct<" + structStr + ">";
  }

}

