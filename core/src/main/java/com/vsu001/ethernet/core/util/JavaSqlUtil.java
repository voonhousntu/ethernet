package com.vsu001.ethernet.core.util;

import com.google.protobuf.Descriptors.FieldDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JavaSqlUtil {

  /**
   * Convert native Java SQL types to protobuf types.
   *
   * @param fieldDescriptor <code>FieldDescriptor</code> of field to convert in proto object.
   * @param resultSet       <code>ResultSet</code> of interest to convert.
   * @return Protobuf type of the field of interest.
   * @throws SQLException
   */
  public static Object jSqlToProtoType(FieldDescriptor fieldDescriptor, ResultSet resultSet)
      throws SQLException {
    String javaTypeName = fieldDescriptor.getJavaType().name();
    String fieldName = fieldDescriptor.getName();
    switch (javaTypeName) {
      case "STRING":
        return resultSet.getString(fieldName);
      case "LONG":
        return resultSet.getLong(fieldName);
      case "DOUBLE":
        return resultSet.getDouble(fieldName);
      case "BOOLEAN":
        return resultSet.getBoolean(fieldName);
      case "MESSAGE":
        if (fieldDescriptor.getName().contains("timestamp")) {
          java.sql.Timestamp ts = resultSet.getTimestamp(fieldName);
          return com.google.protobuf.Timestamp.newBuilder()
              .setSeconds(ts.getTime())
              .setNanos(ts.getNanos())
              .build();
        }
        throw new RuntimeException(javaTypeName + "type not supported");
      default:
        throw new RuntimeException(javaTypeName + "type not supported");
    }
  }

}
