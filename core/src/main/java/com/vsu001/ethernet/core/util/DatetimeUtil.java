package com.vsu001.ethernet.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatetimeUtil {

  /**
   * Get the current datetime and create a ISO string as specified in the provided pattern with a
   * UTC timezone.
   *
   * @param pattern The pattern describing the date and time format
   * @return ISO string of the pattern provided with UTC timezone
   */
  public static String getCurrentISOStr(String pattern) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat(pattern);
    df.setTimeZone(tz);
    return df.format(new Date());
  }

}
