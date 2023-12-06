package com.xcite.javatest.action.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class UserStat {

	private static boolean isTest = false;

	public static void main(String[] args) {
		stat1();
	}

	private static void stat1() {

		String[] subscriptionData = getFile("WebContent/data/newslettersubs.txt").split("\n");
		Map<Integer, Subscription> subscriptions = new HashMap<>();
		for (int i = 1; i < subscriptionData.length; i++) {
			Subscription subscription = new Subscription(subscriptionData[i].split(","));
			subscriptions.put(subscription.getUserId(), subscription);
		}

		Map<String, Integer> baseStat = new HashMap<>();
		String[] userData = getFile("WebContent/data/users.txt").split("\n");
		int numberOfParsedUsers = isTest ? 10 : userData.length - 1;
		for (int i = 1; i <= numberOfParsedUsers; i++) {
			User user = new User(userData[i].split(","));
			user.setSubscription(subscriptions.get(user.getId()));

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
				sb.append(line + "\n");
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
