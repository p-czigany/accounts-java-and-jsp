package com.xcite.javatest.action.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class CsvFileReader implements FileReader {
  private static final boolean isTest = true;
  private static final int NUMBER_OF_USERS_IN_TEST = 10;
  private static final int HEADER_ROWS = 1;

  @Override
  public Stream<User> getUsersFromFile() {
    String[] userData = getFile("WebContent/data/users.txt").split("\n");
    return Arrays.stream(userData)
        .skip(HEADER_ROWS)
        .limit(isTest ? NUMBER_OF_USERS_IN_TEST : userData.length - 1)
        .map(userFields -> new User(userFields.split(",")));
  }

  @Override
  public Stream<Subscription> getSubscriptionsFromFile() {
    String[] subscriptionData = getFile("WebContent/data/newslettersubs.txt").split("\n");
    return Arrays.stream(subscriptionData)
        .skip(HEADER_ROWS)
        .map(subscriptionRow -> new Subscription(subscriptionRow.split(",")));
  }

  private static String getFile(String path) {
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new java.io.FileReader(path));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      return sb.toString();
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          System.out.println(e);
        }
      }
    }
    return "";
  }
}
