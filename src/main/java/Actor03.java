import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Actor03 extends UntypedActor {
	long t1 = 0;
	int i=0;
	public void onReceive(Object arg0) throws Exception {
		i++;
		switch (i){
			//case 100000:
			//	getContext().stop(self());
			//	break;
			case 1:
				t1 = System.currentTimeMillis();
				break;
			case 5000000:
				//getContext().stop(getSelf());
				//Thread.sleep(3000L);
				getContext().system().terminate();
				break;
			default:
		}
		//if(arg0 instanceof String)
		//	System.out.println(Thread.currentThread() +" "+getSelf() + " received: 3-------------->"+arg0 + " from " + getSender());
		getSender().tell(i, ActorRef.noSender());
		//Thread.sleep(2000L);
	}


	@Override
	public void postStop() throws Exception {
		System.err.println("i="+i);
		System.err.println("elapse "+(System.currentTimeMillis() - t1));
		super.postStop();
	}
}