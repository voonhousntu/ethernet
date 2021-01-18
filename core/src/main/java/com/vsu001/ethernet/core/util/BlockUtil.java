package com.vsu001.ethernet.core.util;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BlockUtil {

  public static List<List<Long>> findMissingContRange(List<Long> longList) {
    // Find missing contiguous sequences of elements that are not in longList
    // This implementation is O(n)
    List<List<Long>> lList = new ArrayList<>();
    List<Long> sList = new ArrayList<>(2);

    for (int i = 1; i < longList.size(); i++) {
      if (longList.get(i-1) + 1 != longList.get(i)) {
        sList.add(longList.get(i -1) + 1);
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
