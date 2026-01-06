package chen.linfeng;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import

        java.util.concurrent.Executors;




/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        ObservableOnSubscribe<String> handler = emitter -> {

            Future<Object> future = executor.schedule(() -> {
                emitter.onNext("Hello");
                emitter.onNext("World");
                emitter.onComplete();
                return null;
            }, 1, TimeUnit.SECONDS);

            emitter.setCancellable(() -> future.cancel(false));
        };

        Observable<String> observable = Observable.create(handler);

        observable.subscribe(item -> System.out.println(item), error -> error.printStackTrace(),
                () -> System.out.println("Done"));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
    }
}
