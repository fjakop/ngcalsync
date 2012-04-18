/*
 */
package com.googlecode.syncnotes2google.util;

import java.io.File;

public class FileHelper {
  private static File sn2gHome;
  static {
    sn2gHome = new File(System.getProperty("user.home"), ".syncnotes2google");
    if (!sn2gHome.isDirectory()) {
      sn2gHome.mkdirs();
    }
  }

  public static File getFile(String name) {
    return new File(sn2gHome, name);
  }

}
