import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class NewsAgency implements NewsPublisher {

    private final Map<Subscriber, Set<NewsCategory>> subscribers = new ConcurrentHashMap<>();
    private final ExecutorService executor; // null => deliver synchronously

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
        subscribers.put(requireSubscriber(subscriber), copyCategories(categories));
    }

    @Override
    public void updatePreferences(Subscriber subscriber, Set<NewsCategory> categories) {
        requireSubscriber(subscriber);
        Set<NewsCategory> copy = copyCategories(categories);
        // Atomic: only replaces when already subscribed, never re-adds a concurrently removed subscriber.
        if (subscribers.computeIfPresent(subscriber, (s, old) -> copy) == null) {
            throw new IllegalStateException(subscriber.getName() + " is not subscribed");
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        if (subscriber != null) {
            subscribers.remove(subscriber);
        }
    }

    @Override
    public void publish(News news) {
        Objects.requireNonNull(news, "news must not be null");
        for (Map.Entry<Subscriber, Set<NewsCategory>> entry : subscribers.entrySet()) {
            // BREAKING news always goes to everyone; other news only to interested subscribers.
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
            } catch (Exception e) {
                // One failing subscriber must not affect the others.
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

    private static Subscriber requireSubscriber(Subscriber subscriber) {
        return Objects.requireNonNull(subscriber, "subscriber must not be null");
    }

    private static Set<NewsCategory> copyCategories(Set<NewsCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("At least one category must be provided");
        }
        return EnumSet.copyOf(categories);
    }
}