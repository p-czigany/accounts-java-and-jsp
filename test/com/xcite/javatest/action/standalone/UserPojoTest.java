package com.xcite.javatest.action.standalone;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;
import org.junit.jupiter.api.Test;

public class UserPojoTest {

  @Test
  void testCreationOfNewUserObject() {
    String[] userFields = {
      "86396", "Reyansh", "Carter", "reyansh.carter@yahoo.com", "2013-07-13 03:26:43"
    };

    User myUser = new User(userFields);

    assertThat(myUser.getId()).isEqualTo(86396);
    assertThat(myUser.getFirstName()).isEqualTo("Reyansh");
    assertThat(myUser.getLastName()).isEqualTo("Carter");
    assertThat(myUser.getEmail()).isEqualTo("reyansh.carter@yahoo.com");
    assertThat(myUser.getEmailDomain()).isEqualTo("yahoo.com");
    assertThat(myUser.getRegDate().getYear()).isEqualTo(2013);
    assertThat(myUser.getRegDate().getMonth()).isEqualTo(Month.JULY);
    assertThat(myUser.getRegDate().getDayOfMonth()).isEqualTo(13);
    assertThat(myUser.getRegDate().getHour()).isEqualTo(3);
    assertThat(myUser.getRegDate().getMinute()).isEqualTo(26);
    assertThat(myUser.getRegDate().getSecond()).isEqualTo(43);
  }
}
