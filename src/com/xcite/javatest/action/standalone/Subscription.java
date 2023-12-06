package com.xcite.javatest.action.standalone;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subscription {
  private Integer userId;
  private Boolean subscribed;
  private Integer listId;
  private LocalDateTime createDate;

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public Subscription(
      String userIdString, String subscribedString, String listIdString, String createDateString) {
    this(
        Integer.valueOf(userIdString),
        Boolean.valueOf(subscribedString),
        Integer.valueOf(listIdString),
        LocalDateTime.parse(createDateString, DATE_TIME_FORMATTER));
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
}
