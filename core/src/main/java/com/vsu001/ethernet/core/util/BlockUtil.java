package com.vsu001.ethernet.core.util;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BlockUtil {

  /**
   * Find missing contiguous sequence(s) of Long-type elements that are not in the list of Long-type
   * elements.
   * <p>
   * This implementation is O(n).
   *
   * @param longList List of Long-type elements to check.
   * @param start    Starting sentinel value that is 1 less than the inclusive range-start-number.
   * @param end      Ending sentinel value that is 1 more than the inclusive range-end-number.
   * @return List of contiguous sequence(s) of Long-type elements that are missing from the range
   * between `start` and `end` (non-inclusive).
   */
  public static List<List<Long>> findMissingContRange(
      List<Long> longList,
      Long start,
      Long end
  ) {
    // Add start and end sentinel values
    longList.add(0, start);
    longList.add(end);

    List<List<Long>> lList = new ArrayList<>();
    List<Long> sList = new ArrayList<>(2);

    for (int i = 1; i < longList.size(); i++) {
      if (longList.get(i - 1) + 1 != longList.get(i)) {
        sList.add(longList.get(i - 1) + 1);
        sList.add(longList.get(i) - 1);

        lList.add(sList);

        sList = new ArrayList<>(2);
      }
    }
    return lList;
  }

  public static String protoTsToISO(Timestamp timestamp) {
    // Convert to instant so we can get the ISO8601 timestamp format
    Instant instantTs = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    return instantTs.toString();
  }

}
