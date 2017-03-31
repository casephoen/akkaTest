import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;


public class AskAndTellTest extends UntypedActor {
    static long t1 = 0;
    public static void main(String[] args) throws Exception {
        //System.out.println("AskAndTellTest World!");
        ActorSystem actorSystem = ActorSystem.create("akka");
        //System.out.println(actorSystem.settings());
        ActorRef rcActor = actorSystem.actorOf(Props.create(AskAndTellTest.class), "AskAndTellTest");
        System.out.println(rcActor);

        ActorRef actor3 = actorSystem.actorOf(Props.create(Actor03.class), "actor3");
        Thread.sleep(1000L);
        t1 = System.currentTimeMillis();
        int n = 5000000;
        for (int i = 0; i < n; i++) {
            actor3.tell("hello 33333!!",rcActor);
            //Future<Object> future = Patterns.ask(actor3, "hello 33333!!", 20000L);
            //FutureConverters.toJava(future).whenComplete((o, throwable) -> {
            //    //if (o.equals(n))
            //    //    actorSystem.terminate();
            //    System.out.println("whenComplete "+o);
            //    System.out.println(throwable);
            //});
        }
        //actor3.tell("tell msg", rcActor);
        //Future<Object> future = Patterns.ask(actor3, "hello 33333!!", 10000L);
        //ExecutionContextExecutor contextExecutor = actorSystem.dispatcher();
        //FutureConverters.toJava(future).whenComplete((o, throwable) -> {System.out.println("whenComplete "+o); System.out.println(throwable);});
        System.out.println(Thread.currentThread() + " " + "跨过无阻塞的future");
        //System.out.println(t1);
        //Await.result(future, Duration.create(60, TimeUnit.SECONDS));
        System.out.print("commit ");
        System.out.println(System.currentTimeMillis() - t1);
        //Thread.sleep(5000L);
        //actorSystem.terminate();
    }

    int i = 0;
    public void onReceive(Object message) throws Exception {
        i++;
        //System.out.println(Thread.currentThread() + " " + getSelf() + " received " + message + " from " + getSender());
    }

    @Override
    public void postStop() throws Exception {
        System.err.print("hello stopped "+i);
        System.out.println("cost "+(System.currentTimeMillis() - t1));
        super.postStop();
    }
}
