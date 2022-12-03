package testing;

import org.cubic.esys.*;

public class Test {

    private static final EventBus eventBus = EventDispatcher.builder().name("Test").build();

    private int someValue = 32;

    public void init(){
        /*
        eventBus.subscribe(this);
        eventBus.subscribe(this::eventHookMethod);
        eventBus.subscribe(eventHook);
        eventBus.subscribe(new SubscribeInfo(100), eventHookWithInsertedPriority);
         */
        eventBus.subscribe(new SubscribeInfo("test"), eventHook);
        eventBus.post(new Test());
        Listener<Test> listener = eventBus.getListener("test");
        listener.sleepFilters(
                event -> event.test1(),
                event -> event.test2(),
                event -> event.someValue == 45
        );
        eventBus.post(new Test());
        listener.wakeUp();
        eventBus.post(new Test());
    }

    private int runCount = 0;

    private final EventHook<Test> eventHook = event -> {
        System.out.println("test: " + runCount);
        runCount++;
    };

    private final EventHook<Test> eventHookWithInsertedPriority = event -> {
    };

    @Subscribe
    private void method(Test event){

    }

    private void eventHookMethod(Test event){

    }

    //private final EventHook<Test> eventHook = event -> {
    //};

    @Subscribe(type = Test.class)
    private final EventHook<Test> eventHookSearched = event -> {
    };

    private boolean test1(){
        return false;
    }

    private boolean test2(){
        return true;
    }
}
