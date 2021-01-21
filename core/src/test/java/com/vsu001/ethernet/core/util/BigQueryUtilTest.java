package com.vsu001.ethernet.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValue.Attribute;
import com.google.cloud.bigquery.LegacySQLTypeName;
import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BigQueryUtilTest {

  private static Field strField;
  private static Field bytesField;
  private static Field intField;
  private static Field floatField;
  private static Field boolField;
  private static Field tsField;
  private static Field defaultField;
  private static FieldValue strFieldValue;
  private static FieldValue bytesFieldValue;
  private static FieldValue intFieldValue;
  private static FieldValue floatFieldValue;
  private static FieldValue boolFieldValue;
  private static FieldValue tsFieldValue;
  private static FieldValue defaultFieldValue;

  @BeforeAll
  public static void init() {
    strField = Field.newBuilder("string_field", LegacySQLTypeName.STRING).build();
    bytesField = Field.newBuilder("bytes_field", LegacySQLTypeName.BYTES).build();
    intField = Field.newBuilder("integer_field", LegacySQLTypeName.INTEGER).build();
    floatField = Field.newBuilder("float_field", LegacySQLTypeName.FLOAT).build();
    boolField = Field.newBuilder("boolean_field", LegacySQLTypeName.BOOLEAN).build();
    tsField = Field.newBuilder("timestamp_field", LegacySQLTypeName.TIMESTAMP).build();
    defaultField = Field.newBuilder("default_field", LegacySQLTypeName.NUMERIC).build();

    strFieldValue = FieldValue.of(Attribute.PRIMITIVE, "string");
    bytesFieldValue = FieldValue.of(Attribute.REPEATED, "0x746573745F737472696E67");
    intFieldValue = FieldValue.of(Attribute.PRIMITIVE, "1000");
    floatFieldValue = FieldValue.of(Attribute.PRIMITIVE, "1.22");
    boolFieldValue = FieldValue.of(Attribute.PRIMITIVE, "true");
    tsFieldValue = FieldValue.of(Attribute.PRIMITIVE, "1611198558000");
    defaultFieldValue = FieldValue.of(Attribute.PRIMITIVE, "default_field_value");
  }

  @Test
  public void testGetJavaValue() {
    // Test STRING Field type
    assertEquals(BigQueryUtil.getJavaValue(strField, strFieldValue).getClass(), String.class);

    // Test BYTES Field type
    assertEquals(BigQueryUtil.getJavaValue(bytesField, bytesFieldValue).getClass(), byte[].class);

    // Test INTEGER Field type
    assertEquals(BigQueryUtil.getJavaValue(intField, intFieldValue).getClass(), Long.class);

    // Test FLOAT Field type
    assertEquals(BigQueryUtil.getJavaValue(floatField, floatFieldValue).getClass(), Double.class);

    // Test BOOLEAN Field type
    assertEquals(BigQueryUtil.getJavaValue(boolField, boolFieldValue).getClass(), Boolean.class);

    // Test TIMESTAMP Field type
    assertEquals(BigQueryUtil.getJavaValue(tsField, tsFieldValue).getClass(), Timestamp.class);

    // Test `default` switch case
    assertEquals(
        BigQueryUtil.getJavaValue(defaultField, defaultFieldValue).getClass(),
        String.class
    );
  }

  @Test
  public void testGetOrcType() {
    // Test STRING Field type
    assertEquals("string", BigQueryUtil.getOrcType(strField));

    // Test INTEGER Field type
    assertEquals("bigint", BigQueryUtil.getOrcType(intField));

    // Test FLOAT Field type
    assertEquals("double", BigQueryUtil.getOrcType(floatField));

    // Test BOOLEAN Field type
    assertEquals("boolean", BigQueryUtil.getOrcType(boolField));

    // Test TIMESTAMP Field type
    assertEquals("timestamp", BigQueryUtil.getOrcType(tsField));

    // Test BYTES Field type
    assertThrows(
        RuntimeException.class,
        () -> BigQueryUtil.getOrcType(bytesField),
        "BYTES type not supported"
    );

    // Test `default` switch case
    assertThrows(
        RuntimeException.class,
        () -> BigQueryUtil.getOrcType(defaultField),
        "NUMERIC type not supported"
    );
  }

}
