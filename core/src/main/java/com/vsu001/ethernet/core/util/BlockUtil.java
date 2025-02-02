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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
   * Intervals.
   *
   * @param intervals List of intervals.
   * @param start    Starting sentinel value or range-start-number.
   * @param end      Ending sentinel value that or inclusive range-end-number.
   * @return List of contiguous sequence(s) of Long-type elements that are missing from the range
   * between `start` and `end` (inclusive).
   */
  public static List<List<Long>> findMissingContRange(
      Set<Interval<Long>> intervals,
      Long start,
      Long end
  ) {
    // List to store the result
    List<List<Long>> result = new ArrayList<>();

    // If intervals set is empty, return start and end
    if (intervals.size() == 0) {
      result.add(
          new ArrayList<>(Arrays.asList(start, end))
      );
      return result;
    }

    // ArrayList to store markers
    ArrayList<Marker> markers = new ArrayList<>();

    // Create the markers
    for (Interval<Long> i : intervals) {
      markers.add(new Marker(i.getStart(), true));
      markers.add(new Marker(i.getEnd(), false));
    }

    // Sort the markers in ascending order
    markers.sort(Comparator.comparing(a -> a.val));

    int overlap = 0;
    boolean endReached = false;

    // If the first
    if (markers.get(0).val > start) {
      ArrayList<Long> missingInterval = new ArrayList<>(
          Arrays.asList(start, markers.get(0).val - 1)
      );
      result.add(missingInterval);
    }

    // Iterate through markers
    for (int i = 0; i < markers.size() - 1; i++) {
      Marker m = markers.get(i);

      // Integer base stack implementation
      overlap += m.start ? 1 : -1;
      Marker next = markers.get(i + 1);

      // Only handle when all "parenthesis/markers" are closed
      if (!m.val.equals(next.val) && overlap == 0 && next.val > start) {
        Long intervalStart = m.val > start ? m.val : start;
        Long intervalEnd = next.val;
        if (next.val > end) {
          intervalEnd = end;
          endReached = true;
        }

        ArrayList<Long> missingInterval = new ArrayList<>(
            Arrays.asList(intervalStart + 1, intervalEnd - 1)
        );
        result.add(missingInterval);
        if (endReached) {
          break;
        }
      }
    }

    // Process any dangling overlaps
    Marker m = markers.get(markers.size() - 1);
    if (!endReached && m.val < end) {
      ArrayList<Long> missingInterval = new ArrayList<>(Arrays.asList(m.val + 1, end));
      result.add(missingInterval);
    }

    return result;
  }

  /**
   * Find all intervals that have already been fetched from BigQuery.
   * <p>
   * The cache file will contain rows of intervals that of block number rangers that have already
   * been fetched from BigQuery for a specific proto specification.
   *
   * @param filePath Path to the cache file containing the intervals.
   * @param start    Starting sentinel value or range-start-number.
   * @param end      Ending sentinel value that or inclusive range-end-number.
   * @return Set of Long-type intervals that have already been fetched from BigQuery.
   * @throws FileNotFoundException If the file to be read is not found
   */
  public static Set<Interval<Long>> readFromCache(String filePath, Long start, Long end)
      throws FileNotFoundException {
    // Instantiate a tree
    IntervalTree<Long> tree = new IntervalTree<>();

    FileInputStream fis = new FileInputStream(filePath);
    Scanner sc = new Scanner(fis);

    // Read contents of the file (line-by-line)
    while (sc.hasNextLine()) {
      String lineData = sc.nextLine();
      // Each row is in the {bounded_start_range,bounded_end_range} format
      String[] row = lineData.split(",");
      tree.add(new LongInterval(Long.parseLong(row[0]), Long.parseLong(row[1]), Bounded.CLOSED));
    }
    // Close the file reader
    sc.close();

    // Query for intervals intersecting [start, end]
    return tree.query(new LongInterval(start, end, Bounded.CLOSED));
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

/**
 * Class to act as a parenthesis marker.
 *
 * If `start` is true, it represents an `opening` parenthesis.
 * if `start` is false, it represents a `closing` parenthesis.
 */
class Marker {

  public Long val;
  public boolean start;

  public Marker(Long val, boolean start) {
    this.val = val;
    this.start = start;
  }

}

