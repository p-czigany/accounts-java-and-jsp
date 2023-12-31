package com.xcite.javatest.action.standalone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class User {
  private final Integer id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final LocalDateTime regDate;
//  private Boolean subbed;
//  private Integer listId;
//  private LocalDateTime subDate;
  private List<Subscription> subscriptions;

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public User(String[] userFields) {
    this(
        Integer.valueOf(userFields[0]),
        userFields[1],
        userFields[2],
        userFields[3],
        LocalDateTime.parse(userFields[4], DATE_TIME_FORMATTER),
        null);
  }

  public User(
      Integer id,
      String firstName,
      String lastName,
      String email,
      LocalDateTime regDate,
      List<Subscription> subscriptions) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.regDate = regDate;
    this.subscriptions = subscriptions;
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

//  public boolean isSubbed() {
//    return subbed;
//  }

//  public void setSubbed(boolean subbed) {
//    this.subbed = subbed;
//  }

//  public int getListId() {
//    return listId;
//  }

//  public void setListId(int listId) {
//    this.listId = listId;
//  }

//  public LocalDateTime getSubDate() {
//    return subDate;
//  }

//  public void setSubDate(LocalDateTime subDate) {
//    this.subDate = subDate;
//  }

//  public void setSubDate(String subDateString) {
//    this.subDate = LocalDateTime.parse(subDateString, DATE_TIME_FORMATTER);
//  }

  public List<Subscription> getSubscriptions() {
    return subscriptions;
  }

  public void setSubscriptions(List<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  public YearMonth getRegMonth() {
    return YearMonth.of(regDate.getYear(), regDate.getMonth());
  }
}
