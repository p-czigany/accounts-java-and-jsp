package com.xcite.javatest.action.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class UserStat {

	private static final boolean isTest = false;
	private static final int NUMBER_OF_USERS_IN_TEST = 10;
	private static final int HEADER_ROWS = 1;

	public static void main(String[] args) {
		stat1();
	    System.out.println("\t-\t-\t-");
		stat2();
		System.out.println("\t-\t-\t-");
		stat3();
	}

	private static void stat1() {
		printStatEntries(usersByEmailDomain());
	}

	private static void stat2() {
		printStatEntries(registrationsByMonth());
	}

	private static void stat3() {
		printStatEntries(subscriptionsByMonthByNewsletter());
	}

	private static Map<String, Integer> usersByEmailDomain() {
		List<User> users = getUsersFromFile();

		return users.stream()
				.collect(Collectors.toMap(User::getEmailDomain, user -> 1, Integer::sum));
	}

	private static Map<YearMonth, Integer> registrationsByMonth() {
		List<User> users = getUsersFromFile();

		return users.stream()
				.collect(Collectors.toMap(User::getRegMonth, user -> 1, Integer::sum));
	}

	private static Map<YearMonth, Integer> subscriptionsByMonthByNewsletter() {
		List<Subscription> subscriptions = getSubscriptionsFromFile();

		return subscriptions.stream()
				.collect(Collectors.toMap(Subscription::getCreateMonth, subscription -> 1, Integer::sum));
	}

	private static <K> void printStatEntries(Map<K, Integer> statEntries) {
		Map<K, Integer> sortedMap = new TreeMap<>(statEntries);
		sortedMap.forEach((key, value) -> System.out.println(key + "\t" + value));
    	System.out.println("TOTAL: " + statEntries.values().stream().mapToInt(Integer::intValue).sum());
	}

	private static List<User> getUsersFromFile() {
		String[] userData = getFile("WebContent/data/users.txt").split("\n");
		return Arrays.stream(userData)
				.skip(HEADER_ROWS)
				.limit(isTest ? NUMBER_OF_USERS_IN_TEST : userData.length - 1)
				.map(userFields -> new User(userFields.split(",")))
				.collect(Collectors.toList());
	}

	private static List<Subscription> getSubscriptionsFromFile() {
		String[] subscriptionData = getFile("WebContent/data/newslettersubs.txt").split("\n");
		return Arrays.stream(subscriptionData)
				.skip(HEADER_ROWS)
				.map(subscriptionRow -> new Subscription(subscriptionRow.split(",")))
				.collect(Collectors.toList());
	}

	private static String getFile(String path) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(new File(path)));
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
