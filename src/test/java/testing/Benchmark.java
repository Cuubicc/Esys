package testing;

public class Benchmark {

    public static long countMillis(Runnable runnable){
        long millis = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        return end - millis;
    }

    public static long countNanos(Runnable runnable){
        long nanos = System.nanoTime();
        runnable.run();;
        long end = System.nanoTime();
        return end - nanos;
    }

    public static long countAverageNanos(int runCont, Runnable runnable){
        long runtime = 0;
        for(int i = 0; i < runCont; i++){
            long start = System.nanoTime();
            runnable.run();
            long end = System.nanoTime();
            runtime += end - start;
        }
        System.out.println("Runtime: " + runtime);
        return Math.round((double) runtime / (double) runCont);
    }

    public static long average(int runCount, Runnable runnable) {
        double runtime = 0;
        for (int i = 0; i < runCount; i++) {
            if (i < 10) {
                continue;
            }
            long start = System.nanoTime();
            runnable.run();
            long end = System.nanoTime();
            runtime += ((end - start) - runtime) / (i + 1.0);
        }
        return Math.round(runtime);
    }
}
