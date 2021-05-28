package com.vsu001.ethernet.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessUtil {

  /**
   * Create a process by running a shell command.
   * <p>
   * The process to be executed cannot be an interactive program.
   *
   * @param command Shell command to be executed.
   * @return BufferedReader object containing any output of the shell command that may be printed to
   * the console.
   * @throws IOException If an I/O error occurs
   */
  public static BufferedReader createProcess(String command) throws IOException {
    ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
    builder.redirectErrorStream(true);

    Process process = builder.start();
    InputStream is = process.getInputStream();

    return new BufferedReader(new InputStreamReader(is));
  }

  /**
   * This function is used for debugging purposes.
   * <p>
   * The function takes a BufferedReader object, reads it line by line and print the contents to the
   * console.
   *
   * @param bufferedReader BufferedReader containing the lines to be processed.
   * @throws IOException If an I/O error occurs
   */
  public static void readBufferedReader(BufferedReader bufferedReader) throws IOException {
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      log.debug(line);
    }
  }

}
