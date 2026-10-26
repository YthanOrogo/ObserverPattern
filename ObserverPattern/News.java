import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class News {
    private final String headline;
    private final String content;
    private final NewsCategory category;
    private final LocalDateTime publishedAt;

    public News(String headline, String content, NewsCategory category) {
        this.headline = headline;
        this.content = content;
        this.category = category;
        this.publishedAt = LocalDateTime.now();
    }

    public String getHeadline() { return headline; }
    public String getContent() { return content; }
    public NewsCategory getCategory() { return category; }
    public LocalDateTime getPublishedAt() { return publishedAt; }

    @Override
    public String toString() {
        return "[" + category + "] " + headline + " - " + content
                + " (" + publishedAt.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")";
    }
}