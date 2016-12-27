package bgu.spl.a2;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {
    Processor [] myProcessors;
    LinkedBlockingDeque<Task<?>>[] myQues;
    Thread[] myThreads;
    VersionMonitor monitor;
    boolean toShutDown=false;
    /**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
        myProcessors=new Processor[nthreads];
        myThreads = new Thread[nthreads];
        myQues = new LinkedBlockingDeque[nthreads];
        monitor= new VersionMonitor();

        for (int i=0;i<myProcessors.length;i++){
            myProcessors[i]=new Processor(i,this);
            myQues[i]=new LinkedBlockingDeque<>();
            myThreads[i]= new Thread(myProcessors[i]);
        }
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {
        int randProcess = (int)(Math.random()*(myProcessors.length-1));
        myProcessors[randProcess].addTask(task);
        //monitor.inc();
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
        //TODO: replace method body with real implementation
        toShutDown = true;

        for (int i = 0; i < myThreads.length; i++) {
            myThreads[i].interrupt();
        }
/*
        for (int i = 0; i < myThreads.length; i++) {
            if(myThreads[i].isInterrupted())
                myThreads[i].join();
        }*/
        monitor.inc();

   /*   for (int i = 0; i < myThreads.length; i++) {
            if(myThreads[i].isInterrupted()){
                System.out.println("before join for thread " + i);
                myThreads[i].join();
                System.out.println("after join for thread " + i);
             }
            }*/
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for (int i=0;i<myProcessors.length;i++){
            myThreads[i].start();
        }
    }

}
