package com.xcite.javatest.action.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.servlet.ProcessResult;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.SqlQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.servlet.http.*;
import java.util.*;

public class UserListTest {

  private UserList userList;
  private ParameterMap mockParameterMap;

  @BeforeEach
  void init() {
    userList = new UserList();
    mockParameterMap = Mockito.mock(ParameterMap.class);
  }

  @Test
  void givenStaticMethodWithArgs_whenMocked_thenReturnsMockSuccessfully() throws Throwable {
    // Arrange
//    List<Map<String, Object>> userData = new ArrayList<>();
    Map<String, Object> user1 = new HashMap<>();
    user1.put("id", 1);
    user1.put("email", "user1@xcite.hu");
//    userData.add(user1);
    Map<String, Object> user2 = new HashMap<>();
    user2.put("id", 2);
    user2.put("email", "user2@xcite.hu");
//    userData.add(user2);

//    SqlQuery query = new SqlQuery("dummy_table");

//    try (MockedStatic<DataBase> utilities = Mockito.mockStatic(DataBase.class)) {
//      utilities.when(() -> DataBase.select(query)).thenReturn(userData);

      // Act
      List<Map<String, Object>> result = (List<Map<String, Object>>) userList.processRequest(mockParameterMap).getObject("userList");

      // Assert
      assertThat(result).contains(user2, user1);
//    }
  }

  @Test
  public void testProcessRequestWhenValidRequestThenSuccess() throws Throwable {
    // Arrange
    List<Map<String, Object>> mockUserList = new ArrayList<>();
    Map<String, Object> user = new HashMap<>();
    user.put("id", 1);
    user.put("username", "testuser");
    mockUserList.add(user);

    SqlQuery mockQuery = new SqlQuery("xjfw.account");
    mockQuery.order.add("createDate");

    Mockito.when(DataBase.select(mockQuery)).thenReturn(mockUserList);

    // Act
    ProcessResult result = userList.processRequest(mockParameterMap);

    // Assert
    Assertions.assertNotNull(result, "The result should not be null");
    Assertions.assertEquals("userList", result.data, "The dispatch URL should be 'userList'");
    Assertions.assertTrue(
        result.hasStringParameter("userList"), "The result should contain 'userList' parameter");
    Assertions.assertEquals(
        mockUserList,
        result.getObject("userList"),
        "The 'userList' parameter should match the mock user list");
  }

  @Test
  public void testProcessRequestWhenNullRequestThenNullPointerException() {
    // Arrange & Act
    Exception exception =
        Assertions.assertThrows(
            NullPointerException.class,
            () -> {
              userList.processRequest(null);
            });

    // Assert
    String expectedMessage = "ParameterMap cannot be null";
    String actualMessage = exception.getMessage();
    Assertions.assertTrue(
        actualMessage.contains(expectedMessage),
        "Expected NullPointerException to be thrown with message containing '"
            + expectedMessage
            + "'");
  }
}
