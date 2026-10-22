package com.newsservice;

/** Concrete Observer: receives news as a push notification. */
public class MobileAppSubscriber implements Subscriber {
    private final String name;
    private final String deviceId;

    public MobileAppSubscriber(String name, String deviceId) {
        this.name = name;
        this.deviceId = deviceId;
    }

    @Override
    public String getName() { return name; }

    @Override
    public void update(News news) {
        System.out.println("PUSH to " + name + " (device " + deviceId + "): " + news);
    }
}
