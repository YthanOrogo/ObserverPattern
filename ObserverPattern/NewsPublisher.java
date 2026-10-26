import java.util.Set;

public interface NewsPublisher {
    void subscribe(Subscriber subscriber);
    void subscribe(Subscriber subscriber, Set<NewsCategory> categories);
    void updatePreferences(Subscriber subscriber, Set<NewsCategory> categories);
    void unsubscribe(Subscriber subscriber);
    void publish(News news);
}