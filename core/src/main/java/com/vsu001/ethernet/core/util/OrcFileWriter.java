package com.vsu001.ethernet.core.util;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValue;
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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class OrcFileWriter {

  /**
   * Write rows defined in a <code>List</code> of <code>Map</code> with column name as the key with
   * its respective object value into an ORC file.
   *
   * @param path                The HDFS directory to write the file to.
   * @param struct              The struct string defining the "schema" of an ORC file.
   * @param data                The rows to be written into the ORC file defined in a
   *                            <code>List</code> of
   *                            <code>Map</code> with column name as the key with * its respective
   *                            object value.
   * @param defaultFs           The name of the default file system. A URI whose scheme and
   *                            authority determine the FileSystem implementation. The uri's scheme
   *                            determines the config property (fs.SCHEME.impl) naming the
   *                            FileSystem implementation class. The uri's authority is used to
   *                            determine the host, port, etc. for a filesystem.
   * @param datanodeUseHostname Whether datanodes should use datanode hostnames when connecting to
   *                            other datanodes for data transfer.
   * @param clientUseHostname   Whether clients should use datanode hostnames when connecting to
   *                            datanodes.
   * @throws IOException
   */
  public static void write(
      String path,
      String struct,
      List<Map<String, Object>> data,
      String defaultFs,
      boolean datanodeUseHostname,
      boolean clientUseHostname
  ) throws IOException {
    // Use default configurations
    Configuration config = new Configuration();
    config.set("fs.defaultFS", defaultFs);
    config.set("dfs.datanode.use.datanode.hostname", Boolean.toString(datanodeUseHostname));
    config.set("dfs.client.use.datanode.hostname", Boolean.toString(clientUseHostname));
    write(config, path, struct, data);
  }

  /**
   * Write rows defined in a <code>List</code> of <code>Map</code> with column name as the key with
   * its respective object value into an ORC file.
   *
   * @param configuration Hadoop client configuration to use when executing Hadoop client related
   *                      routines.
   * @param path          The HDFS directory to write the file to.
   * @param struct        The struct string defining the "schema" of an ORC file.
   * @param data          The rows to be written into the ORC file defined in a <code>List</code>
   *                      of
   *                      <code>Map</code> with column name as the key with * its respective object
   *                      value.
   * @throws IOException
   */
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

  /**
   * Write the <code>TableResult</code> obtained from a BigQuery job into HDFS directory as an ORC
   * file.
   *
   * @param path                The HDFS directory to write the file to.
   * @param struct              The struct string defining the "schema" of an ORC file.
   * @param tableResult         The <code>TableResult</code> of a BigQuery job to be written into a
   *                            HDFS directory as an ORC file.
   * @param defaultFs           The name of the default file system. A URI whose scheme and
   *                            authority determine the FileSystem implementation. The uri's scheme
   *                            determines the config property (fs.SCHEME.impl) naming the
   *                            FileSystem implementation class. The uri's authority is used to
   *                            determine the host, port, etc. for a filesystem.
   * @param datanodeUseHostname Whether datanodes should use datanode hostnames when connecting to
   *                            other datanodes for data transfer.
   * @param clientUseHostname   Whether clients should use datanode hostnames when connecting to
   *                            datanodes.
   * @throws IOException
   */
  public static void writeTableResults(
      String path,
      String struct,
      TableResult tableResult,
      String defaultFs,
      boolean datanodeUseHostname,
      boolean clientUseHostname
  ) throws IOException {
    // Use default configurations
    Configuration config = new Configuration();
    config.set("fs.defaultFS", defaultFs);
    config.set("dfs.datanode.use.datanode.hostname", Boolean.toString(datanodeUseHostname));
    config.set("dfs.client.use.datanode.hostname", Boolean.toString(clientUseHostname));
    writeTableResults(config, path, struct, tableResult);
  }

  /**
   * Write the <code>TableResult</code> obtained from a BigQuery job into HDFS directory as an ORC
   * file.
   *
   * @param configuration Hadoop client configuration to use when executing Hadoop client related
   *                      routines.
   * @param path          The HDFS directory to write the file to.
   * @param struct        The struct string defining the "schema" of an ORC file.
   * @param tableResult   The <code>TableResult</code> of a BigQuery job to be written into a HDFS
   *                      directory as an ORC file.
   * @throws IOException
   */
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
    FieldList fieldList = tableResult.getSchema().getFields();

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

//        log.info("rowNum: {}", rowNum);

        // Write each column to the associated column vector
        for (int i = 0; i < fieldNames.size(); i++) {
          String fieldName = fieldNames.get(i);
          Field field = fieldList.get(fieldName);
          FieldValue fieldValue = fieldValueList.get(fieldName);

//          log.debug("{}: {}", fieldName, field.getType().name());
//          log.debug("{}: {}", fieldName, fieldValue);

          Object javaVal = BigQueryUtil.getJavaValue(field, fieldValue);

//          log.debug("{}: {}", fieldName, javaVal);

          consumers.get(i).accept(rowNum, javaVal);
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

  /**
   * Helper function to create a column writer.
   * <p>
   * The resulting <code>BiConsumer</code> represents a function which takes in two arguments and
   * produces a result. However it does not return any value.
   *
   * @param description  The <code>TypeDescription</code> object containing the type specification
   *                     of the column.
   * @param columnVector The <code>ColumnVector</code> object containing a batch of values of a
   *                     specific column.
   * @return BiConsumer that accepts a row number and ORC values.
   */
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
        if (val != null) {
          byte[] buffer = val.toString().getBytes(StandardCharsets.UTF_8);
          ((BytesColumnVector) columnVector).setRef(row, buffer, 0, buffer.length);
        } else {
          byte[] EMPTY_BYTES = "".getBytes(StandardCharsets.UTF_8);
          ((BytesColumnVector) columnVector).setRef(row, EMPTY_BYTES, 0, 0);
        }
      };
    } else if ("timestamp".equals(type)) {
      consumer = (row, val) -> ((TimestampColumnVector) columnVector).set(row, (Timestamp) val);
    } else {
      throw new RuntimeException("Unsupported type " + type);
    }
    return consumer;
  }

  /**
   * Obtain the ORC type string of a <code>FieldDescriptor</code> object specification of a protobuf
   * field.
   * <p>
   * This function is a degenerate <code>HashMap</code> containing the mappings between the protobuf
   * field's <code>FieldDescriptor</code> and the ORC types.
   * <p>
   * Only types that are required for the Ethernet project is implemented in this method.
   *
   * @param fieldDescriptor <code>FieldDescriptor</code> object definition containing the
   *                        protobuf's field specifications.
   * @return The equivalent ORC type string that is of the equivalent type as the
   * <code>FieldDescriptor</code> specification.
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
   * Generate the struct string required to define the "schema" of an ORC file from a protobuf's
   * <code>FieldDescriptor</code>s.
   *
   * @param fieldDescriptorList <code>FieldDescriptor</code>s describing the fields of a protobuf.
   * @return ORC struct string describing the protobuf fields.
   */
  public static String protoToOrcStructStr(List<FieldDescriptor> fieldDescriptorList) {
    String structStr = fieldDescriptorList.stream()
        .map(s -> String.format("%s:%s", s.getName(), protoToOrcType(s)))
        .collect(Collectors.joining(","));
    return "struct<" + structStr + ">";
  }

}

