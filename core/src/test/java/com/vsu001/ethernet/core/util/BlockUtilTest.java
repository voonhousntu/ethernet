package com.vsu001.ethernet.core.util;

import static com.vsu001.ethernet.core.util.BlockUtil.findMissingContRange;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BlockUtilTest {

  @Test
  public void testFindMissingContRange() {
    // Two overloaded functions to test
    List<Long> longList = new ArrayList<>();
    Long start = 0L;
    Long end = 99L;

    List<List<Long>> expected = new ArrayList<>();
    expected.add(new ArrayList<>(Arrays.asList(0L, 99L)));
    assertEquals(expected, findMissingContRange(longList, start, end));

    longList = new ArrayList<>(Arrays.asList(4L, 5L, 8L, 10L, 11L, 12L, 15L, 16L, 17L, 19L, 25L));
    expected = new ArrayList<>(
        Arrays.asList(
            new ArrayList<>(Arrays.asList(6L, 7L)),
            new ArrayList<>(Arrays.asList(9L, 9L)),
            new ArrayList<>(Arrays.asList(13L, 14L)),
            new ArrayList<>(Arrays.asList(18L, 18L)),
            new ArrayList<>(Arrays.asList(20L, 24L))
        )
    );
    assertEquals(expected, findMissingContRange(longList));
  }

}
