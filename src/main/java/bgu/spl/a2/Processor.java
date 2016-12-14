package bgu.spl.a2;

import java.util.Deque;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;
    Deque myTasks;
    int numOfTasks=0;
    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    }

    @Override
    public void run() {
        // pool.start(); - Old implement

        if (myTasks.isEmpty()) {
            steal();
        } else {
            Task doMe;
            while (!myTasks.isEmpty()) {
                doMe = (Task) myTasks.getFirst();
                doMe.start();
            }
        }
    }

    void steal(){
        Processor nextProcessor=pool.myProccesors[(id+1)%pool.myProccesors.length];

        while(nextProcessor.isEmpty() || nextProcessor.numOfTasks==1)
            nextProcessor=pool.myProccesors[(id+1)%pool.myProccesors.length];

        int numOfTasksToSteal=nextProcessor.numOfTasks/2;
        int count=0;
        while(count<numOfTasksToSteal){
            addTask((nextProcessor.removeTask());
            count++;
        }
    }
    void addTask(Task task){
        myTasks.add(task);
        numOfTasks++;
    }
    Task removeTask(){
         numOfTasks--;
        return (Task) myTasks.getLast();
    }

    WorkStealingThreadPool getPool(){
        return pool;
    }

    boolean isEmpty(){
        return numOfTasks==0;
    }
}

