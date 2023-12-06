package com.xcite.javatest.action.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserStat {

	private static boolean isTest = true;
	private static final int NUMBER_OF_USERS_IN_TEST = 10;
	private static final int HEADER_ROW_INDEX = 0;

	public static void main(String[] args) {
		stat1();
	}

	private static void stat1() {

		String[] subscriptionData = getFile("WebContent/data/newslettersubs.txt").split("\n");
		List<String> subscriptionRows = new ArrayList<>(Arrays.asList(subscriptionData));
		subscriptionRows.remove(HEADER_ROW_INDEX);
		Map<Integer, List<Subscription>> subscriptions = new HashMap<>();
		for (String subscriptionRow: subscriptionRows) {
			Subscription subscription = new Subscription(subscriptionRow.split(","));
			Integer userId = subscription.getUserId();
			if (subscriptions.containsKey(userId)) {
				List<Subscription> subscriptionsList = subscriptions.get(userId);
				subscriptionsList.add(subscription);
				subscriptions.put(userId, subscriptionsList); // TODO: is this necessary?
			} else {
			subscriptions.put(userId, new ArrayList<>(Collections.singletonList(subscription))); }
		}
//		Map<Integer, Subscription> subscriptions = subscriptionRows.stream()
//				.map(subscriptionRow -> new Subscription(subscriptionRow.split(",")))
//				.collect(Collectors.toMap(Subscription::getUserId, Function.identity()));

		Map<String, Integer> baseStat = new HashMap<>();
		String[] userData = getFile("WebContent/data/users.txt").split("\n");
		int numberOfParsedUsers = isTest ? NUMBER_OF_USERS_IN_TEST : userData.length - 1;
		for (int i = 1; i <= numberOfParsedUsers; i++) {
			User user = new User(userData[i].split(","));
			user.setSubscriptions(subscriptions.get(user.getId()));

			String key = user.getEmail().split("@")[1];
			Integer count = baseStat.get(key);
			if (count == null) {
				count = 0;
			}
			baseStat.put(key, count + 1);
		}

		printEntrySubscriptions(baseStat);
	}

	private static void printEntrySubscriptions(Map<String, Integer> subscriptionAmounts) {
		for (Entry<String, Integer> entry : subscriptionAmounts.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
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
