package com.vsu001.ethernet.core.util;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Timestamp;
import com.opencsv.CSVWriter;
import com.vsu001.ethernet.core.model.Block;
import com.vsu001.ethernet.core.model.Contract;
import com.vsu001.ethernet.core.model.TokenTransfer;
import com.vsu001.ethernet.core.model.Trace;
import com.vsu001.ethernet.core.model.Transaction;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvUtil {

  /**
   * Helper function to create a CSV file of the <code>List</code> of objects.
   * <p>
   * This function will help convert a <code>List</code> of objects into a <code>List</code> of
   * <code>List</code> of string.
   *
   * @param objects <code>List</code> of objects to be written to a CSV file.
   * @param path    The path where the CSV file will be outputted to.
   * @param nonce   A random generated string that will only be used once to identify all assets
   *                associated with the an ETl.
   * @throws Exception If an Exception is thrown.
   */
  public static void toCsv(List<?> objects, String path, String nonce)
      throws IOException {
    String filename = generateFilename(objects.get(0), nonce);

    String filePath = String.format("%s/%s", path, filename);
    List<String[]> lList = new ArrayList<>();
    for (Object obj : objects) {
      lList.add(objToStrList(obj).toArray(new String[0]));
    }

    writeToCsv(lList, filePath);
  }

  /**
   * Helper function to create a CSV file of the <code>List</code> of String.
   *
   * @param lList    <code>List</code> of String to be written to a CSV file.
   * @param filePath The path where the CSV file will be outputted to.
   * @throws Exception If an Exception is thrown.
   */
  private static void writeToCsv(List<String[]> lList, String filePath)
      throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(filePath));
    writer.writeAll(lList);
    writer.close();
  }

  /**
   * Helper function to convert an object into a <code>List</code> of String that represents a CSV
   * row.
   *
   * @param object The object to be converted into a list of Strings.
   * @return A <code>List</code> of String representing a CSV row creatd from an object.
   */
  private static List<String> objToStrList(Object object) {
    List<String> strList = new ArrayList<>();
    switch (object.getClass().getSimpleName()) {
      case "Block":
        Block block = (Block) object;
        for (FieldDescriptor fieldDescriptor : Block.getDescriptor().getFields()) {
          String field = fieldToStr(fieldDescriptor, block.getField(fieldDescriptor));
          strList.add(field);
        }
        break;
      case "String":
        String str = (String) object;
        strList.add(str);
        break;
      case "Contract":
        Contract contract = (Contract) object;
        for (FieldDescriptor fieldDescriptor : Contract.getDescriptor().getFields()) {
          String field = fieldToStr(fieldDescriptor, contract.getField(fieldDescriptor));
          strList.add(field);
        }
        break;
      case "TokenTransfer":
        TokenTransfer tokenTransfer = (TokenTransfer) object;
        for (FieldDescriptor fieldDescriptor : TokenTransfer.getDescriptor().getFields()) {
          String field = fieldToStr(fieldDescriptor, tokenTransfer.getField(fieldDescriptor));
          strList.add(field);
        }
        break;
      case "Trace":
        Trace trace = (Trace) object;
        for (FieldDescriptor fieldDescriptor : Trace.getDescriptor().getFields()) {
          String field = fieldToStr(fieldDescriptor, trace.getField(fieldDescriptor));
          strList.add(field);
        }
        break;
      case "Transaction":
        Transaction transaction = (Transaction) object;
        for (FieldDescriptor fieldDescriptor : Transaction.getDescriptor().getFields()) {
          String field = fieldToStr(fieldDescriptor, transaction.getField(fieldDescriptor));
          strList.add(field);
        }
        break;
      default:
        throw new RuntimeException(
            String.format("Object type of %s is not implemented", object.getClass().getName()));
    }
    return strList;
  }

  /**
   * Helper function to create the CSV filename of according to the object type.
   *
   * @param object Object to create the CSV filename for.
   * @param nonce  A random generated string that will only be used once to identify all assets
   *               associated with the an ETl.
   * @return The CSV filename.
   */
  private static String generateFilename(Object object, String nonce) {
    String filename = null;
    switch (object.getClass().getSimpleName()) {
      case "Block":
        filename = "blocks_" + nonce + ".csv";
        break;
      case "String":
        filename = "addresses_" + nonce + ".csv";
        break;
      case "Contract":
        filename = "contracts_" + nonce + ".csv";
        break;
      case "TokenTransfer":
        filename = "token_transfers_" + nonce + ".csv";
        break;
      case "Trace":
        Trace trace = (Trace) object;
        break;
      case "Transaction":
        filename = "transactions_" + nonce + ".csv";
        break;
      default:
        throw new RuntimeException(
            String.format("Object type of %s is not implemented", object.getClass().getName()));
    }
    return filename;
  }

  private static String fieldToStr(FieldDescriptor fieldDescriptor, Object object) {
    if (fieldDescriptor.getName().contains("timestamp")) {
      return BlockUtil.protoTsToISO((Timestamp) object);
    } else {
      return object.toString();
    }
  }
}
