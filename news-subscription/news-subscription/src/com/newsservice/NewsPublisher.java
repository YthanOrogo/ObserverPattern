package com.newsservice;

import java.util.Set;

/** Subject: publishes news and manages subscriptions. */
public interface NewsPublisher {
    /** Subscribe to all categories. */
    void subscribe(Subscriber subscriber);

    /** Subscribe to specific categories only (personalized subscription). */
    void subscribe(Subscriber subscriber, Set<NewsCategory> categories);

    /** Modify the preferences of an existing subscriber. */
    void updatePreferences(Subscriber subscriber, Set<NewsCategory> categories);

    void unsubscribe(Subscriber subscriber);

    /** Publish news and notify all interested subscribers. */
    void publish(News news);
}
