package com.vsu001.ethernet.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vsu001.ethernet.core.service.UpdateRequest;
import com.vsu001.ethernet.core.util.interval.Interval;
import com.vsu001.ethernet.core.util.interval.Interval.Bounded;
import com.vsu001.ethernet.core.util.interval.IntervalTree;
import com.vsu001.ethernet.core.util.interval.LongInterval;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class BlockUtilTest {

  @Test
  public void testValidateRequest() {
    UpdateRequest request = UpdateRequest.newBuilder()
        .setStartBlockNumber(1)
        .setEndBlockNumber(1)
        .build();
    assertFalse(BlockUtil.validateRequest(request));

    request = UpdateRequest.newBuilder()
        .setStartBlockNumber(2)
        .setEndBlockNumber(1)
        .build();
    assertFalse(BlockUtil.validateRequest(request));

    request = UpdateRequest.newBuilder()
        .setStartBlockNumber(1)
        .setEndBlockNumber(2)
        .build();
    assertTrue(BlockUtil.validateRequest(request));
  }

  @Test
  public void testFindMissingContRange() {
    // Constant start and end
    Long start = 0L;
    Long end = 99L;

    // --- FIRST test (cache only has 1 block) ---

    // Instantiate a new tree
    IntervalTree<Long> tree = new IntervalTree<>();

    // Add some intervals
    tree.add(new LongInterval(0L, 0L, Bounded.CLOSED));

    // Query the tree
    Set<Interval<Long>> treeResult = tree.query(new LongInterval(start, end, Bounded.CLOSED));

    // Declare expected results
    List<List<Long>> expected = new ArrayList<>();
    expected.add(new ArrayList<>(Arrays.asList(1L, 99L)));

    // Test the function
    assertEquals(expected, BlockUtil.findMissingContRange(treeResult, start, end));

    // --- SECOND test (All blocks present) ---

    // Instantiate a new tree
    tree = new IntervalTree<>();

    // Add some intervals
    tree.add(new LongInterval(0L, 100L, Bounded.CLOSED));

    // Query the tree
    treeResult = tree.query(new LongInterval(start, end, Bounded.CLOSED));

    // Declare expected results
    expected = new ArrayList<>(Collections.emptyList());

    // Test the function
    assertEquals(expected, BlockUtil.findMissingContRange(treeResult, start, end));

    // --- Third test (Complex example) ---

    // Instantiate a new tree
    tree = new IntervalTree<>();

    // Add some intervals
    tree.add(new LongInterval(4L, 5L, Bounded.CLOSED));
    tree.add(new LongInterval(8L, 8L, Bounded.CLOSED));
    tree.add(new LongInterval(10L, 12L, Bounded.CLOSED));
    tree.add(new LongInterval(15L, 17L, Bounded.CLOSED));
    tree.add(new LongInterval(19L, 19L, Bounded.CLOSED));
    tree.add(new LongInterval(25L, 25L, Bounded.CLOSED));

    // Query the tree
    treeResult = tree.query(new LongInterval(start, end, Bounded.CLOSED));

    // Declare expected results
    expected = new ArrayList<>(
        Arrays.asList(
            new ArrayList<>(Arrays.asList(0L, 3L)),
            new ArrayList<>(Arrays.asList(6L, 7L)),
            new ArrayList<>(Arrays.asList(9L, 9L)),
            new ArrayList<>(Arrays.asList(13L, 14L)),
            new ArrayList<>(Arrays.asList(18L, 18L)),
            new ArrayList<>(Arrays.asList(20L, 24L)),
            new ArrayList<>(Arrays.asList(26L, 99L))
        )
    );

    assertEquals(expected, BlockUtil.findMissingContRange(treeResult, start, end));
  }

}
