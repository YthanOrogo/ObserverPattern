# Real-time News Subscription Service

## Problem Statement

**Background:** You're tasked with designing a real-time news subscription service for a media company. The system needs to notify subscribers about breaking news as soon as it's published by the news agency.

**Scenario Description:** The system comprises two main components: `NewsAgency` and `Subscriber`.

- **NewsAgency:** Represents the central hub for publishing news. It maintains a list of subscribers and notifies them whenever new news is available.
- **Subscriber:** Represents users or entities subscribed to the news service. Subscribers receive immediate notifications about any breaking news published by the agency.

**Requirements:**

1. **Subscription Management:**
   - Users should be able to subscribe to the news service.
   - Subscribers should be notified promptly when new news is published.
2. **Dynamic Subscription Updates:**
   - The system should allow for dynamic subscription updates. Subscribers can subscribe, unsubscribe, or modify their preferences without disrupting other subscribers.
3. **Decoupled Communication:**
   - Ensure that the communication between the news agency and subscribers is decoupled. Subscribers shouldn't directly request news updates but should be notified by the news agency when new information is available.

**Expected Behavior:**

- When the news agency publishes breaking news, all subscribed users should receive immediate updates.
- Subscribers can join or leave the service without affecting the delivery of news to other subscribers.
- The system should provide flexibility for future enhancements, such as personalized subscriptions or categorization of news updates.

**Constraints:**

- The system should be scalable to handle a growing number of subscribers without compromising performance.
- Ensure that the implementation follows object-oriented design principles and promotes loose coupling between the news agency and subscribers.

## Solution: Observer Design Pattern

| Role | Class |
|------|-------|
| Subject (interface) | `NewsPublisher` |
| Concrete Subject | `NewsAgency` |
| Observer (interface) | `Subscriber` |
| Concrete Observers | `EmailSubscriber`, `MobileAppSubscriber` |
| Data | `News`, `NewsCategory` |

How the requirements are met:

- **Subscription management / dynamic updates:** `subscribe`, `unsubscribe`, `updatePreferences` on `NewsAgency`.
- **Decoupling:** `NewsAgency` only depends on the `Subscriber` interface; subscribers never poll, they get `update(News)` called.
- **Future enhancements:** `NewsCategory` + per-subscriber category sets already support personalized subscriptions. New subscriber types (SMS, Slack...) only need to implement `Subscriber`.
- **Scalability:** subscribers are stored in a `ConcurrentHashMap` (thread-safe), and `NewsAgency` accepts an optional `ExecutorService` so notifications run in parallel. A failing subscriber is isolated and doesn't block others.

## UML Class Diagram

```mermaid
classDiagram
    direction LR

    class NewsPublisher {
        <<interface>>
        +subscribe(Subscriber)
        +subscribe(Subscriber, Set~NewsCategory~)
        +updatePreferences(Subscriber, Set~NewsCategory~)
        +unsubscribe(Subscriber)
        +publish(News)
    }

    class NewsAgency {
        -subscribers : Map~Subscriber, Set~NewsCategory~~
        -executor : ExecutorService
        +subscribe(Subscriber)
        +subscribe(Subscriber, Set~NewsCategory~)
        +updatePreferences(Subscriber, Set~NewsCategory~)
        +unsubscribe(Subscriber)
        +publish(News)
        -deliver(Subscriber, News)
        +getSubscriberCount() int
    }

    class Subscriber {
        <<interface>>
        +getName() String
        +update(News)
    }

    class EmailSubscriber {
        -name : String
        -email : String
        +getName() String
        +update(News)
    }

    class MobileAppSubscriber {
        -name : String
        -deviceId : String
        +getName() String
        +update(News)
    }

    class News {
        -headline : String
        -content : String
        -category : NewsCategory
        -publishedAt : LocalDateTime
        +getHeadline() String
        +getContent() String
        +getCategory() NewsCategory
        +getPublishedAt() LocalDateTime
    }

    class NewsCategory {
        <<enumeration>>
        BREAKING
        POLITICS
        SPORTS
        TECHNOLOGY
        ENTERTAINMENT
        BUSINESS
    }

    NewsPublisher <|.. NewsAgency
    Subscriber <|.. EmailSubscriber
    Subscriber <|.. MobileAppSubscriber
    NewsAgency o-- "0..*" Subscriber : notifies
    NewsAgency ..> News : publishes
    Subscriber ..> News : receives
    News --> NewsCategory
```

## Project Structure

```
src/com/newsservice/
├── NewsCategory.java
├── News.java
├── Subscriber.java
├── NewsPublisher.java
├── NewsAgency.java
├── EmailSubscriber.java
├── MobileAppSubscriber.java
└── Main.java
```

## How to Run

```bash
mkdir out
javac -d out src/com/newsservice/*.java
java -cp out com.newsservice.Main
```

Requires Java 8 or later.
