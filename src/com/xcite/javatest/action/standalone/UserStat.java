package com.xcite.javatest.action.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class UserStat {

	private static final boolean isTest = false;
	private static final int NUMBER_OF_USERS_IN_TEST = 10;
	private static final int HEADER_ROW_INDEX = 0;
	private static final int HEADER_ROWS = 1;

	public static void main(String[] args) {
		stat1();
		stat2();
	}

	private static void stat1() {
		printEntries(usersByEmailDomain());
	}

	private static void stat2() {
		printEntries(registrationsByMonth());
	}

	private static Map<String, Integer> usersByEmailDomain() {
		return getUsersFromFile().stream()
				.collect(Collectors.toMap(User::getEmailDomain, u -> 1, Integer::sum));
	}

	private static Map<Month, Integer> registrationsByMonth() {
		Map<Month, Integer> baseStat = new HashMap<>();
		List<User> users = getUsersFromFile();

		for (User user : users) {
			Month key = user.getRegMonth();
			baseStat.put(key, baseStat.getOrDefault(key, 0) + 1);
		}

		return baseStat;
	}

	private static <K> void printEntries(Map<K, Integer> subscriptionAmounts) {
		for (Entry<K, Integer> entry : subscriptionAmounts.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	private static List<User> getUsersFromFile() {
		String[] userData = getFile("WebContent/data/users.txt").split("\n");
		return Arrays.stream(userData)
				.skip(HEADER_ROWS)
				.limit(isTest ? NUMBER_OF_USERS_IN_TEST : userData.length - 1)
				.map(userFields -> new User(userFields.split(",")))
				.collect(Collectors.toList());
	}

	private static Map<Integer, List<Subscription>> getUserSubscriptionsFromFile() {
		String[] subscriptionData = getFile("WebContent/data/newslettersubs.txt").split("\n");
		List<String> subscriptionRows = new ArrayList<>(Arrays.asList(subscriptionData));
		subscriptionRows.remove(HEADER_ROW_INDEX);
		return subscriptionRows.stream()
				.map(subscriptionRow -> new Subscription(subscriptionRow.split(",")))
				.collect(Collectors.groupingBy(Subscription::getUserId, Collectors.toList()));
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
