package com.xcite.javatest.action.standalone;

import java.util.stream.Stream;

public interface FileReader {
  Stream<Subscription> getSubscriptionsFromFile();

  Stream<User> getUsersFromFile();
}
