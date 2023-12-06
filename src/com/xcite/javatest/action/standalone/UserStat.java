package com.xcite.javatest.action.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class UserStat {

	private static boolean isTest = true;

	public static void main(String[] args) {
		stat1();
	}

	private static void stat1() {
//		give names to 4 int variables
		int i, j, k, l;
//		copy file content to string array
		String[] userData = getFile("WebContent/data/users.txt").split("\n");
//		copy file content to string array
		String[] subscriptionData = getFile("WebContent/data/newslettersubs.txt").split("\n");

		Map<String, Integer> baseStat = new HashMap<String, Integer>();
//		iterate over user data lines

        for (i = 1; i < userData.length; i++) {
//			temp is the array of columns / fields of the user
			User user = new User(userData[i].split(","));

	//		iterate over user subscription lines
			for (i = 1; i < subscriptionData.length; i++) {
	//			temp is the array of columns / fields of the subscription
				Subscription subscription = new Subscription(subscriptionData[i].split(","));
//				wait for the users own subscription
				if (Objects.equals(user.getId(), subscription.getUserId())) {
					user.setSubscription(subscription);

					break;
				}
			}

			// key is the email domain
			String key = user.getEmail().split("@")[1];
			// get current count for the email domain
			Integer count = baseStat.get(key);
			// if the key is missing, then initialize the count with zero
			if (count == null) {
				count = 0;
			}

			baseStat.put(key, count + 1);
			if (isTest && i >= 10) {
				break;
			}
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
