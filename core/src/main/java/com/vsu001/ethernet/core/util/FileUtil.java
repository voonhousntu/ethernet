package com.vsu001.ethernet.core.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

  /**
   * Create a directory if it does not exist.
   *
   * @param dir Directory to create.
   * @throws IOException If an IOException is thrown
   */
  public static void createDir(String dir) throws IOException {
    Files.createDirectories(Paths.get(dir));
  }

}
