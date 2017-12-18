import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Future;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static akka.dispatch.Futures.future;

/**
 * Created by topgame on 2017/3/30.
 */
public class JdkBlockingQueueTest {
    public static final Logger log = LoggerFactory.getLogger(JdkBlockingQueueTest.class);

    private static LinkedBlockingQueue<String> concurrentLinkedQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        final int n = 500_0000;
        CountDownLatch countDownLatch = new CountDownLatch(n + 1);
        final ActorSystem actorSystem = ActorSystem.create();
        long t1 = System.currentTimeMillis();

        //消费者
        Future<String> f2 = future(new Callable<String>() {
            public String call() {
                for (int i = 0; i < n; i++) {
                    try {
                        concurrentLinkedQueue.take();
                    } catch (InterruptedException e) {
                    }
                }
                countDownLatch.countDown();
                return "1";
            }
        }, actorSystem.dispatcher());
        long t2 = System.currentTimeMillis();
        log.info("invoke consume cost {} ms", t2 - t1);

        //生产者
        int msgNumPerFuture = 1;
        for (int i = 0; i < n / msgNumPerFuture; i++) { //akka fork-join 缺省并行线程64个
            Future<String> f1 = future(new Callable<String>() { //多生产者
                public String call() {
                    for (int i = 0; i < msgNumPerFuture; i++) {
                        try {
                            concurrentLinkedQueue.put("1");
                        } catch (InterruptedException e) {
                        }
                        countDownLatch.countDown();
                    }
                    return "1";
                }
            }, actorSystem.dispatcher());
        }
        //Future<String> f1 = future(new Callable<String>() {
        //    public String call() {
        //        for (int i = 0; i < n; i++) {
        //            try {
        //                concurrentLinkedQueue.put("1");
        //            } catch (InterruptedException e) {
        //            }
        //            countDownLatch.countDown();
        //        }
        //        return "1";
        //    }
        //}, actorSystem.dispatcher());
        long t3 = System.currentTimeMillis();
        log.info("invoke produce cost {} ms. concurrentLinkedQueue.size()={}", t3 - t2, concurrentLinkedQueue.size());


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
        }
        long t4 = System.currentTimeMillis();
        final long l = t4 - t1;
        log.info("produce consume {}, total cost {} ms, speed: {}/s", n, l, 1000L * n / l);

        actorSystem.terminate();
    }
}
