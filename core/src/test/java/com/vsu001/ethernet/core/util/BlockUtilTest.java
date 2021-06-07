package com.vsu001.ethernet.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vsu001.ethernet.core.service.UpdateRequest;
import com.vsu001.ethernet.core.util.interval.Interval;
import com.vsu001.ethernet.core.util.interval.Interval.Bounded;
import com.vsu001.ethernet.core.util.interval.LongInterval;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    // Test empty list
    Set<Interval<Long>> longList = new HashSet<>(new ArrayList<>());
    Long start = 0L;
    Long end = 99L;

    List<List<Long>> expected = new ArrayList<>();
    expected.add(new ArrayList<>(Arrays.asList(0L, 99L)));
    assertEquals(expected, BlockUtil.findMissingContRange(longList, start, end));

    longList = new HashSet<>();
    longList.add(new LongInterval(4L, 5L, Bounded.CLOSED));
    longList.add(new LongInterval(8L, 8L, Bounded.CLOSED));
    longList.add(new LongInterval(10L, 12L, Bounded.CLOSED));
    longList.add(new LongInterval(15L, 17L, Bounded.CLOSED));
    longList.add(new LongInterval(19L, 19L, Bounded.CLOSED));
    longList.add(new LongInterval(25L, 25L, Bounded.CLOSED));

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
    assertEquals(expected, BlockUtil.findMissingContRange(longList, start, end));
  }

}
