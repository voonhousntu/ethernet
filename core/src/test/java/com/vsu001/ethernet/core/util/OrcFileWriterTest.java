package com.vsu001.ethernet.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vsu001.ethernet.core.model.Contract;
import org.junit.jupiter.api.Test;

public class OrcFileWriterTest {

  @Test
  public void testProtoToOrcType() {
    // Test STRING Field type
    assertEquals(
        "string",
        OrcFileWriter.protoToOrcType(Contract.getDescriptor().findFieldByName("address"))
    );

    // Test LONG Field type
    assertEquals(
        "bigint",
        OrcFileWriter.protoToOrcType(Contract.getDescriptor().findFieldByName("block_number"))
    );

    // Test DOUBLE Field type
    // No double type implemented, not tested

    // Test BOOLEAN Field type
    assertEquals(
        "boolean",
        OrcFileWriter.protoToOrcType(Contract.getDescriptor().findFieldByName("is_erc20"))
    );

    // Test TIMESTAMP Field type
    assertEquals(
        "timestamp",
        OrcFileWriter.protoToOrcType(Contract.getDescriptor().findFieldByName("block_timestamp"))
    );

    // Test `default` switch case
    // No other types implemented other than the four tested cases above
  }

  @Test
  public void testProtoToOrcStructStr() {
    String expectedStructStr =
        "struct<"
            + "address:string,"
            + "bytecode:string,"
            + "function_sighashes:string,"
            + "is_erc20:boolean,"
            + "is_erc721:boolean,"
            + "block_timestamp:timestamp,"
            + "block_number:bigint,"
            + "block_hash:string"
            + ">";
    assertEquals(
        expectedStructStr,
        OrcFileWriter.protoToOrcStructStr(Contract.getDescriptor().getFields())
    );
  }

}
