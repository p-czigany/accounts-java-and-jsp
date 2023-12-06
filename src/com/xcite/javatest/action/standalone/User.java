package com.xcite.javatest.action.standalone;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
  private final Integer id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final LocalDateTime regDate;

  public User(
      String idString, String firstName, String lastName, String email, String regDateString) {
    this(
        Integer.valueOf(idString),
        firstName,
        lastName,
        email,
        LocalDateTime.parse(regDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }

  public User(Integer id, String firstName, String lastName, String email, LocalDateTime regDate) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.regDate = regDate;
  }

  public Integer getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getEmailDomain() {
    return email.split("@")[1];
  }

  public LocalDateTime getRegDate() {
    return regDate;
  }
}
