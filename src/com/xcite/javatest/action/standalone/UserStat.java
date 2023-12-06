package com.xcite.javatest.action.standalone;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserStat {
	
	protected final FileReader fileReader;

	public UserStat() {
		fileReader = new CsvFileReader();
	}

	public static void main(String[] args) {
		UserStat userStat = new UserStat();
		userStat.stat1();
	    System.out.println("\t-\t-\t-");
		userStat.stat2();
		System.out.println("\t-\t-\t-");
		userStat.stat3();
		System.out.println("\t-\t-\t-");
		userStat.stat4();

	}

	void stat1() {
		printStatEntries(usersByEmailDomain());
	}

	void stat2() {
		printStatEntries(registrationsByMonth());
	}

	void stat3() {
		printStatEntries(subscriptionsByMonthByNewsletter());
	}

	void stat4() {
		printStatEntries(currentlyActiveSubscriptionsByNewsletterOfUsersRegisteredAfter2015());
	}

	private Map<String, Integer> usersByEmailDomain() {
		Stream<User> users = getFileReader().getUsersFromFile();

		return users
				.collect(Collectors.groupingBy(User::getEmailDomain, Collectors.summingInt(user -> 1)));
  	}

	private Map<YearMonth, Integer> registrationsByMonth() {
		Stream<User> users = getFileReader().getUsersFromFile();

		return users
				.collect(Collectors.groupingBy(User::getRegMonth, Collectors.summingInt(user -> 1)));
	}

	private Map<YearMonth, Map<Integer, Integer>> subscriptionsByMonthByNewsletter() {
		Stream<Subscription> subscriptions = getFileReader().getSubscriptionsFromFile();

		return subscriptions
				.collect(Collectors.groupingBy(
						Subscription::getCreateMonth,
						Collectors.groupingBy(
								Subscription::getListId,
								Collectors.summingInt(subscription -> 1))));
	}

	private Map<Integer, Integer> currentlyActiveSubscriptionsByNewsletterOfUsersRegisteredAfter2015() {
		Stream<User> users = getFileReader().getUsersFromFile();
		Stream<Subscription> subscriptions = getFileReader().getSubscriptionsFromFile();
		Map<Integer, List<Subscription>> activeSubscriptionsByUser = activeSubscriptionsByUser(subscriptions);

		return users
				.filter(user -> user.getRegDate().getYear() > 2015)
				.flatMap(
						user ->
								activeSubscriptionsByUser
										.getOrDefault(user.getId(), Collections.emptyList())
										.stream())
				.collect(
						Collectors.groupingBy(
								Subscription::getListId, Collectors.summingInt(subscription -> 1)));
	}

	private Map<Integer, List<Subscription>> activeSubscriptionsByUser(Stream<Subscription> subscriptions) {
		return subscriptions
				.filter(Subscription::getSubscribed)
				.collect(Collectors.groupingBy(Subscription::getUserId));
	}

	private <K, V> void printStatEntries(Map<K, V> map) {
		printStatEntries(map, "");
	}

	private <K, V> void printStatEntries(Map<K, V> map, String indent) {
		Map<K, V> sortedMap = new TreeMap<>(map);
		for (Map.Entry<K, V> entry : sortedMap.entrySet()) {
			System.out.print(indent + entry.getKey() + ": ");
			if (entry.getValue() instanceof Map<?, ?>) {
				System.out.println();
				printStatEntries((Map<?, ?>) entry.getValue(), indent + "\t");
			} else {
				System.out.println(entry.getValue());
			}
		}
	}

	public FileReader getFileReader() {
		return fileReader;
	}
}
