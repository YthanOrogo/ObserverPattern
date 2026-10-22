package com.newsservice;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Concrete Subject. Knows subscribers only through the Subscriber interface
 * (loose coupling). Thread-safe, so subscribers can join/leave at any time
 * without disrupting delivery to others. An optional ExecutorService lets
 * notifications run in parallel for scalability.
 */
public class NewsAgency implements NewsPublisher {

    private final Map<Subscriber, Set<NewsCategory>> subscribers = new ConcurrentHashMap<>();
    private final ExecutorService executor; // null => synchronous delivery

    public NewsAgency() {
        this(null);
    }

    public NewsAgency(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribe(subscriber, EnumSet.allOf(NewsCategory.class));
    }

    @Override
    public void subscribe(Subscriber subscriber, Set<NewsCategory> categories) {
        if (subscriber == null || categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("Subscriber and categories must be provided");
        }
        subscribers.put(subscriber, EnumSet.copyOf(categories));
    }

    @Override
    public void updatePreferences(Subscriber subscriber, Set<NewsCategory> categories) {
        if (!subscribers.containsKey(subscriber)) {
            throw new IllegalStateException(subscriber.getName() + " is not subscribed");
        }
        subscribe(subscriber, categories);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void publish(News news) {
        for (Map.Entry<Subscriber, Set<NewsCategory>> entry : subscribers.entrySet()) {
            // BREAKING news goes to everyone; otherwise match preferences
            if (news.getCategory() == NewsCategory.BREAKING
                    || entry.getValue().contains(news.getCategory())) {
                deliver(entry.getKey(), news);
            }
        }
    }

    private void deliver(Subscriber subscriber, News news) {
        Runnable task = () -> {
            try {
                subscriber.update(news);
            } catch (Exception e) { // one failing subscriber must not affect others
                System.err.println("Failed to notify " + subscriber.getName() + ": " + e.getMessage());
            }
        };
        if (executor != null) {
            executor.execute(task);
        } else {
            task.run();
        }
    }

    public int getSubscriberCount() {
        return subscribers.size();
    }
}
