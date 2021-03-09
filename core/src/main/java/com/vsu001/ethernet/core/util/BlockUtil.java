package com.vsu001.ethernet.core.util;

import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.service.UpdateRequest;
import com.vsu001.ethernet.core.util.interval.Interval;
import com.vsu001.ethernet.core.util.interval.Interval.Bounded;
import com.vsu001.ethernet.core.util.interval.IntervalTree;
import com.vsu001.ethernet.core.util.interval.LongInterval;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class BlockUtil {

  /**
   * Ensure that the UpdateRequest object is a valid request.
   * <p>
   * Validation is performed to ensure that the provided start index is smaller than the end index.
   *
   * @param updateRequest The block-number range to query for is defined and wrapped in the
   *                      UpdateRequest object. The user defined `start` and `end` object are
   *                      inclusive when translated to the BigQuery legacy SQL query constraints
   *                      equivalent.
   * @return Boolean flag to check if the start index is smaller than the end index.
   */
  public static boolean validateRequest(UpdateRequest updateRequest) {
    return updateRequest.getStartBlockNumber() < updateRequest.getEndBlockNumber();
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
    List<Long> cloneList = new ArrayList<>(longList);

    // Add start and end sentinel values
    cloneList.add(0, start - 1);
    cloneList.add(end + 1);

    return findMissingContRange(cloneList);
  }

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
      if (sortedList.get(i - 1) + 1 != sortedList.get(i)) {
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
   * elements as recorded by in a specified cache file.
   * <p>
   * The cache file will contain rows of intervals that of block number rangers that have already
   * been fetched from BigQuery for a specific proto specification.
   *
   * @param filePath Path to the cache file containing the intervals.
   * @param start    Starting sentinel value or range-start-number.
   * @param end      Ending sentinel value that or inclusive range-end-number.
   * @return List of contiguous sequence(s) of Long-type elements that are missing from the range
   * between the sorted `longList`'s first and last element.
   * @throws FileNotFoundException If the file to be read is not found
   */
  public static List<List<Long>> readFromCache(String filePath, Long start, Long end)
      throws FileNotFoundException {
    // Instantiate a new tree
    IntervalTree<Long> tree = new IntervalTree<>();

    FileInputStream fis = new FileInputStream(filePath);
    Scanner sc = new Scanner(fis);

    // Returns true if there is another line to read
    while (sc.hasNextLine()) {
      String lineData = sc.nextLine();
      System.out.println(lineData);
      String[] row = lineData.split(",");
      tree.add(new LongInterval(Long.parseLong(row[0]), Long.parseLong(row[1]), Bounded.CLOSED));
    }
    sc.close();

    Set<Interval<Long>> result = tree.query(new LongInterval(start, end, Bounded.CLOSED));

    List<Long> longList = new ArrayList<>();
    for (Interval<Long> interval : result) {
      List<Long> range = LongStream.rangeClosed(interval.getStart(), interval.getEnd())
          .boxed().collect(Collectors.toList());
      longList.addAll(range);
    }

    return findMissingContRange(longList, start, end);
  }

  /**
   * Update the cache file with the newly fetched intervals from BigQuery so that they are flagged.
   * <p>
   * By doing so, the intervals will not be fetched from BigQuery again.
   *
   * @param filePath       Path to the cache file containing the intervals to be updated.
   * @param intervalsToAdd List of Long type intervals to be added to the cache file during the
   *                       update.
   * @throws IOException If the named file exists but is a directory rather than a regular file,
   *                     does not exist but cannot be created, or cannot be opened for any other
   *                     reason
   */
  public static void updateCache(String filePath, List<List<Long>> intervalsToAdd)
      throws IOException {
    FileWriter fileWriter = new FileWriter(filePath, true);
    for (List<Long> longList : intervalsToAdd) {
      fileWriter.write(String.format("%s,%s\n", longList.get(0), longList.get(1)));
    }
    fileWriter.close();
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
