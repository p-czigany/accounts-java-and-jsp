package com.xcite.javatest.action.standalone;

import org.junit.jupiter.api.Test;

import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionPojoTest {

  @Test
  void testCreationOfNewUserObject() {
    String[] subscriptionFields = {"86396", "true", "8", "2018-05-04 18:07:20"};

    Subscription mySubscription = new Subscription(subscriptionFields);

    assertThat(mySubscription.getUserId()).isEqualTo(86396);
    assertThat(mySubscription.getSubscribed()).isEqualTo(true);
    assertThat(mySubscription.getCreateDate().getYear()).isEqualTo(2018);
    assertThat(mySubscription.getCreateDate().getMonth()).isEqualTo(Month.MAY);
    assertThat(mySubscription.getCreateDate().getDayOfMonth()).isEqualTo(4);
    assertThat(mySubscription.getCreateDate().getHour()).isEqualTo(18);
    assertThat(mySubscription.getCreateDate().getMinute()).isEqualTo(7);
    assertThat(mySubscription.getCreateDate().getSecond()).isEqualTo(20);
  }
}
