package com.newsservice;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) {
        NewsAgency agency = new NewsAgency();

        Subscriber alice = new EmailSubscriber("Alice", "alice@mail.com");
        Subscriber bob = new MobileAppSubscriber("Bob", "DEV-123");
        Subscriber carol = new EmailSubscriber("Carol", "carol@mail.com");

        System.out.println("=== Alice (all), Bob (sports, tech), Carol (politics) subscribe ===");
        agency.subscribe(alice);
        agency.subscribe(bob, EnumSet.of(NewsCategory.SPORTS, NewsCategory.TECHNOLOGY));
        agency.subscribe(carol, EnumSet.of(NewsCategory.POLITICS));

        agency.publish(new News("Major earthquake hits coast", "Rescue teams deployed.", NewsCategory.BREAKING));
        agency.publish(new News("Local team wins finals", "A thrilling 3-2 victory.", NewsCategory.SPORTS));

        System.out.println("\n=== Bob unsubscribes; Carol adds TECHNOLOGY ===");
        agency.unsubscribe(bob);
        agency.updatePreferences(carol, EnumSet.of(NewsCategory.POLITICS, NewsCategory.TECHNOLOGY));

        agency.publish(new News("New AI chip announced", "Twice as fast as before.", NewsCategory.TECHNOLOGY));
        agency.publish(new News("Elections next month", "Candidates finalized.", NewsCategory.POLITICS));

        System.out.println("\nActive subscribers: " + agency.getSubscriberCount());
    }
}
