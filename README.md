# Esys
An event bus for java designed to be easy to use, fast and yet flexible.
Work is still in progress.

## Create an EventBus

To create a default event bus with a name:
```java
import org.cubic.esys.EventBus;

public class App {

    public static final EventBus EVENT_BUS = EventBus.buildDefault("the event bus");
}
```

## Subscribe to objects and classes

Subscribing to objects:
```java
import org.cubic.esys.EventBus;

public class App {

    public static final EventBus EVENT_BUS = EventBus.buildDefault("the event bus");

    public static void main(String[] args) {
        // our object we want to subscribe to
        App app = new App();
        EVENT_BUS.subscribe(app);
        EVENT_BUS.unsubscribe(app);
    }
}
```
Subscribing to classes:
```java
import org.cubic.esys.EventBus;

public class App {

    public static final EventBus EVENT_BUS = EventBus.buildDefault("the event bus");

    public static void main(String[] args) {
        EVENT_BUS.subscribe(App.class);
        EVENT_BUS.unsubscribe(App.class);
    }
}
```

## Listeners

A simple example:

```java
import org.cubic.esys.EventBus;
import org.cubic.esys.Subscribe;

public class App {

    public static final EventBus EVENT_BUS = EventBus.buildDefault("the event bus");

    public static void main(String[] args) {
        EVENT_BUS.subscribe(App.class);
        EVENT_BUS.unsubscribe(App.class);
    }

    @Subscribe
    public static void onEvent(SomeEvent event) {
        // handle the event...
    }
}
```

## Posting events

```java
import org.cubic.esys.EventBus;

public class App {

    public static final EventBus EVENT_BUS = EventBus.buildDefault("the event bus");

    public static void main(String[] args) {
        Event e = new Event(); // some event we want to post
        EVENT_BUS.post(e);
    }
}
```
