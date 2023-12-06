package com.xcite.javatest.action.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class UserStat {

	private static boolean isTest = false;
	private static final int NUMBER_OF_USERS_IN_TEST = 10;
	private static final int HEADER_ROW_INDEX = 0;
	private static final int HEADER_ROWS = 1;

	public static void main(String[] args) {
		stat1();
	}

	private static void stat1() {

    	Map<String, Integer> baseStat =
        	getUsersFromFile().stream()
            	.collect(Collectors.toMap(User::getEmailDomain, u -> 1, Integer::sum));

		printEntrySubscriptions(baseStat);
	}

	private static void printEntrySubscriptions(Map<String, Integer> subscriptionAmounts) {
		for (Entry<String, Integer> entry : subscriptionAmounts.entrySet()) {
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
