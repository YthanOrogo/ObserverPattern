package com.newsservice;

/** Observer: anything that wants to be notified about news. */
public interface Subscriber {
    String getName();

    /** Called by the publisher when new news is available. */
    void update(News news);
}
