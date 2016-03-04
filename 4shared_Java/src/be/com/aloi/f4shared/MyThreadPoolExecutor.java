package be.com.aloi.f4shared;
import java.util.concurrent.*;
import java.util.*;
 
public class MyThreadPoolExecutor
{
    int poolSize = 2;
 
    int maxPoolSize = 5;
 
    long keepAliveTime = 10;
 
    ThreadPoolExecutor threadPool = null;

	private int maxPoolArray = 2;
 
    final BlockingQueue<Runnable> queue = new LinkedBlockingQueue()  ;
 
    public MyThreadPoolExecutor()
    {
        threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
                keepAliveTime, TimeUnit.SECONDS, queue);
 
    }
 
    public synchronized void runTask(Runnable task)
    {
        // System.out.println("Task count.."+threadPool.getTaskCount() );
        // System.out.println("Queue Size before assigning the
        // task.."+queue.size() );
        threadPool.execute(task);
        // System.out.println("Queue Size after assigning the
        // task.."+queue.size() );
        // System.out.println("Pool Size after assigning the
        // task.."+threadPool.getActiveCount() );
        // System.out.println("Task count.."+threadPool.getTaskCount() );
        System.out.println("Task count.." + queue.size());
 
    }
 
    public void shutDown()
    {
        threadPool.shutdown();
    }
 
    public static void main(String args[])
    {
        MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor();
        // start first one
        
        for(int ifo=1;ifo<20;ifo++){
        	try{
        		Runnable exect = new entitySimple(ifo);
                mtpe.runTask(exect);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        System.out.println("Shutdown");
        mtpe.shutDown();
    }
 
}
class entitySimple implements Runnable{

	private  int ifo;
	private final Random r = new Random();

	public entitySimple(int ifol) {
		ifo=ifol;
	}

	@Override
    public void run()
    {
        for (int i = 0; i < 10; i++)
        {
            try
            {
                int sleep = (int) ((r.nextDouble()*9000)+1000);
                Thread.sleep(sleep);
				System.out.println(ifo+"º Task "+sleep );
            } catch (InterruptedException ie)
            {
            }
        }
    }
	
}