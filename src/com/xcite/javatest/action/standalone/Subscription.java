package com.xcite.javatest.action.standalone;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Subscription {
  private Integer userId;
  private Boolean subscribed;
  private Integer listId;
  private LocalDateTime createDate;

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public Subscription(String[] subscriptionFields) {
    this(
        Integer.valueOf(subscriptionFields[0]),
        Boolean.valueOf(subscriptionFields[1]),
        Integer.valueOf(subscriptionFields[2]),
        LocalDateTime.parse(subscriptionFields[3], DATE_TIME_FORMATTER));
  }

  public Subscription(
      Integer userId, Boolean subscribed, Integer listId, LocalDateTime createDate) {
    this.userId = userId;
    this.subscribed = subscribed;
    this.listId = listId;
    this.createDate = createDate;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Boolean getSubscribed() {
    return subscribed;
  }

  public void setSubscribed(Boolean subscribed) {
    this.subscribed = subscribed;
  }

  public Integer getListId() {
    return listId;
  }

  public void setListId(Integer listId) {
    this.listId = listId;
  }

  public LocalDateTime getCreateDate() {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
  }

  public void setCreateDate(String createDateString) {
    this.createDate = LocalDateTime.parse(createDateString, DATE_TIME_FORMATTER);
  }

  public YearMonth getCreateMonth() {
    return YearMonth.of(createDate.getYear(), createDate.getMonth());
  }
}
