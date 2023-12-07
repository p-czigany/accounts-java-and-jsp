package com.xcite.javatest.action.standalone;

public class UserStatFromJson extends UserStat {

  private final FileReader fileReader = new JsonFileReader();

  public static void main(String[] args) {
    UserStatFromJson userStatFromJson = new UserStatFromJson();
//    userStatFromJson.stat1();
//    System.out.println("\t-\t-\t-");
//    userStatFromJson.stat2();
    System.out.println("\t-\t-\t-");
    userStatFromJson.stat3();
    System.out.println("\t-\t-\t-");
//    userStatFromJson.stat4();
  }

  @Override
  public FileReader getFileReader() {
    return fileReader;
  }
}
