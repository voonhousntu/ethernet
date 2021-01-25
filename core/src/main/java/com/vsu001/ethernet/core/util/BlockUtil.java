package com.vsu001.ethernet.core.util;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockUtil {

  /**
   * Find missing contiguous sequence(s) of Long-type elements that are not in the list of Long-type
   * elements.
   * <p>
   * This implementation is O(n).
   *
   * @param longList List of Long-type elements to check.
   * @return List of contiguous sequence(s) of Long-type elements that are missing from the range
   * between the sorted `longList`'s first and last element.
   */
  public static List<List<Long>> findMissingContRange(List<Long> longList) {
    // Sort the longList and collect the results into a new variable
    List<Long> sortedList = longList.stream().sorted().collect(Collectors.toList());

    List<List<Long>> lList = new ArrayList<>();
    List<Long> sList = new ArrayList<>(2);

    for (int i = 1; i < sortedList.size(); i++) {
      if (sortedList.get(i - 1) + 1 != longList.get(i)) {
        sList.add(sortedList.get(i - 1) + 1);
        sList.add(sortedList.get(i) - 1);

        lList.add(sList);

        sList = new ArrayList<>(2);
      }
    }
    return lList;
  }

  /**
   * Find missing contiguous sequence(s) of Long-type elements that are not in the list of Long-type
   * elements.
   * <p>
   * This implementation is O(n).
   *
   * @param longList List of Long-type elements to check.
   * @param start    Starting sentinel value or range-start-number.
   * @param end      Ending sentinel value that or inclusive range-end-number.
   * @return List of contiguous sequence(s) of Long-type elements that are missing from the range
   * between `start` and `end` (inclusive).
   */
  public static List<List<Long>> findMissingContRange(
      List<Long> longList,
      Long start,
      Long end
  ) {
    // Create a copy of longList
    List<Long> cloneList = new ArrayList<>(List.copyOf(longList));

    // Add start and end sentinel values
    cloneList.add(0, start - 1);
    cloneList.add(end + 1);

    return findMissingContRange(cloneList);
  }

  /**
   * Convert a Google protobuf timestamp object to a ISO8601 timestamp string.
   *
   * @param timestamp Google protobuf timestamp.
   * @return The ISO8601 string format of the Google protobuf timestamp.
   */
  public static String protoTsToISO(Timestamp timestamp) {
    // Convert to instant so we can get the ISO8601 timestamp format
    Instant instantTs = Instant.ofEpochSecond(timestamp.getSeconds() / 1000, timestamp.getNanos());
    return instantTs.toString();
  }

}
