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
    Deque <Task> myTasks;
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
            while (!myTasks.isEmpty()) {
                myTasks.pollFirst().handle(this);
            }
        }
    }

    void steal(){
        Processor nextProcessor=pool.myProcessors[(id+1)%pool.myProcessors.length];

        while(nextProcessor.isEmpty() || nextProcessor.myTasks.size()==1) {
            nextProcessor = pool.myProcessors[(nextProcessor.id + 1) % pool.myProcessors.length];

           // if(nextProcessor.id==this.id)
              //should sleep and be notified when new task are inserted
        }

        int numOfTasksToSteal=nextProcessor.myTasks.size()/2;
        int stealCount=0;
        while(stealCount<numOfTasksToSteal){
            try {
                addTask(nextProcessor.removeTask());
                stealCount++;
            }
            catch (Exception e){
                break; //no more tasks to remove
            }
        }
    }

    void addTask(Task task){
        myTasks.add(task);

    }

    Task removeTask() throws Exception{

        Task last= myTasks.pollLast();
        if(last!=null)
            return last;
        else
            throw new Exception("no tasks to remove. exception at remove task!");
    }

    WorkStealingThreadPool getPool(){
        return pool;
    }

    boolean isEmpty(){
        return myTasks.size()==0;
    }
}

