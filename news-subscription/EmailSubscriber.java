package com.newsservice;

/** Concrete Observer: receives news by email. */
public class EmailSubscriber implements Subscriber {
    private final String name;
    private final String email;

    public EmailSubscriber(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public String getName() { return name; }

    @Override
    public void update(News news) {
        System.out.println("EMAIL to " + name + " <" + email + ">: " + news);
    }
}
