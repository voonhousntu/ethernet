package com.vsu001.ethernet.core.util;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.mock.MockResultSet;
import com.vsu001.ethernet.core.model.Contract;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class JavaSqlUtilTest {

  @Test
  public void jSqlToProtoType() throws SQLException {
    // Initialise ResultSet
    String dataString =
        "address,block_number,block_timestamp,is_erc20\n"
            + "address_str,99,2018-12-09 02:24:27,true";
    ResultSet resultSet = new MockResultSet(dataString);
    resultSet.next();

    // Test STRING FieldDescriptor
    assertThat(
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("address"),
            resultSet
        ),
        instanceOf(String.class)
    );

    // Test LONG FieldDescriptor
    assertThat(
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("block_number"),
            resultSet
        ),
        instanceOf(long.class)
    );

    // Test MESSAGE FieldDescriptor
    assertThat(
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("block_timestamp"),
            resultSet
        ),
        instanceOf(Timestamp.class)
    );

    // Test BOOLEAN FieldDescriptor
    assertThat(
        JavaSqlUtil.jSqlToProtoType(
            Contract.getDescriptor().findFieldByName("is_erc20"),
            resultSet
        ),
        instanceOf(boolean.class)
    );

    // Defined protobufs do not support other types, no further tests on other types
  }

}
