package testing;

import org.cubic.esys.EventBus;
import org.cubic.esys.EventDispatcher;
import org.cubic.esys.EventHook;
import org.cubic.esys.Subscribe;

@SuppressWarnings("ALL")
public class App {

    public static void main(String[] args) {
        EventBus eventBus = EventDispatcher.builder()
                                           .searchFields()
                                           .name("EventBus")
                                           .build();
        //eventBus.subscribe(App::onMain1);
        //eventBus.unsubscribe(App::onMain1);
        //eventBus.subscribe(eventHook);
        // load all of the needed classes before doing benchmark
        //eventBus.subscribe(App::onMain3);
        //eventBus.post(new Object());
        //eventBus.subscribe(App.class);
        //eventBus.post(new App());
        Test test = new Test();
        test.init();
    }

    public static <T> boolean doEventHooksEqual(EventHook<T> hook1, EventHook<T> hook2){
        return hook1 == hook2;
    }

    @Subscribe(type = App.class, priority = 100)
    private static final EventHook<App> eventHook = event -> {
        System.out.println("event hook");
    };

    public boolean filterTest(){
        return true;
    }

    @Subscribe private static void onMain1(App main){
        System.out.println("onMain1");
    }
    @Subscribe private static void onMain2(App main){
        //System.out.println("onMain2");
    }
    @Subscribe(priority = 101)
    private static void onMain3(App main){
        System.out.println("onMain3");
    }
    @Subscribe private static void onMain4(App main){
        //System.out.println("onMain4");
    }
    @Subscribe private static void onMain5(App main){
        //System.out.println("onMain5");
    }
    @Subscribe private static void onMain6(App main){
        //System.out.println("onMain6");
    }
    @Subscribe private static void onMain7(App main){
        //System.out.println("onMain7");
    }
    @Subscribe private static void onMain8(App main){
        //System.out.println("onMain8");
    }
    @Subscribe private static void onMain9(App main){
        //System.out.println("onMain9");
    }
    @Subscribe private static void onMain10(App main){
        //System.out.println("onMain10");
    }
    @Subscribe private static void onMain11(App main){
        //System.out.println("onMain11");
    }
    @Subscribe private static void onMain12(App main){
        //System.out.println("onMain12");
    }
    @Subscribe private static void onMain13(App main){
        //System.out.println("onMain13");
    }
    @Subscribe private static void onMain14(App main){
        //System.out.println("onMain14");
    }
    @Subscribe private static void onMain15(App main){
        //System.out.println("onMain15");
    }
    @Subscribe private static void onMain16(App main){
        //System.out.println("onMain16");
    }
}
