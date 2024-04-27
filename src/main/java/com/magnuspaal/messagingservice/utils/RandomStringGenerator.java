package com.magnuspaal.messagingservice.utils;

import java.util.Random;

public class RandomStringGenerator {
  static String alphabet = "abcdefghijklmnopqrstubwxyz";
  static StringBuilder sb = new StringBuilder();
  static Random random = new Random();
  static int length = 16;

  public static String getRandomString() {
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(alphabet.length());
      char randomChar = alphabet.charAt(index);
      sb.append(randomChar);
    }
    String string = sb.toString();
    sb.setLength(0);
    return string.replace("\0", "");
  }
}