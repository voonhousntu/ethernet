package com.vsu001.ethernet.core.util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.mock.MockResultSet;
import com.vsu001.ethernet.core.model.Contract;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class JavaSqlUtilTest {

  @Test
  public void testJSqlToProtoType() throws SQLException {
    // Initialise ResultSet
    ResultSet resultSet = new MockResultSet(
        "address,block_number,block_timestamp,is_erc20",
        "address_str,99,2018-12-09 02:24:27,true"
    );
    // Ensure that cursor is pointing to the correct row
    resultSet.next();

    // Test STRING FieldDescriptor
    assertEquals(
        String.class,
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("address"),
            resultSet
        ).getClass()
    );

    // Test LONG FieldDescriptor
    assertEquals(
        Long.class,
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("block_number"),
            resultSet
        ).getClass()
    );

    // Test MESSAGE FieldDescriptor
    assertEquals(
        Timestamp.class,
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("block_timestamp"),
            resultSet
        ).getClass()
    );

    // Test BOOLEAN FieldDescriptor
    assertEquals(
        Boolean.class,
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("is_erc20"),
            resultSet
        ).getClass()
    );

    // Defined protobufs do not support other types, no further tests on other types
  }

}
