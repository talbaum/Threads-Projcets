package bgu.spl.a2;

import java.util.concurrent.LinkedBlockingDeque;

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
    int startVersion;
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
        while (!pool.toShutDown) {
            if (pool.myQues[id].isEmpty()) {
                startVersion = pool.monitor.getVersion();
                steal();
            } else {
                while (!pool.myQues[id].isEmpty()) {
                    Task<?> tmp = pool.myQues[id].pollFirst();
                    if (tmp!=null) {
                        tmp.handle(this);
                    }
                }
            }
        }
    }

    void steal() {
        int whereToSteal = (id + 1) % (pool.myProcessors.length);
        //int startVersion = pool.monitor.getVersion();
        boolean awake = false;

        while (!awake && (pool.myQues[whereToSteal].size() <= 1)) {
            whereToSteal = (whereToSteal + 1) % pool.myProcessors.length;

            if (whereToSteal == id) { //why we need the try and catch?? need to test the await function again.
                try {
                    pool.monitor.await(startVersion);
                    awake = true;
                } catch (Exception e) {
                 //   System.out.println(e.getMessage() + " STEALING PROBLEM");
                }
            }
        }
            //if there there is tasks to steal get here.

            if ((!awake && (whereToSteal != id))) {
            int numOfTasksToSteal = (pool.myQues[whereToSteal].size() / 2) - 1;
                int stealCount = 0;
                while (stealCount < numOfTasksToSteal && pool.myQues[whereToSteal].size()>1) {
                    try {
                        Task tmp=pool.myProcessors[whereToSteal].removeTask();
                        if(tmp!=null) {
                            addTask(tmp);
                            stealCount++;
                        }
                    } catch (Exception e) {
                        System.out.println("Stealing probleam" + e.getMessage());
                        break; //no more tasks to remove
                    }
                }
            }
    }
    void addTask(Task<?> task){
        if (task!=null) {
            pool.myQues[id].add(task);
            pool.monitor.inc(); //check  amit: i think its ok, it fits what we need
        }
    }

    Task<?> removeTask() throws Exception{
        return pool.myQues[id].pollFirst();

    }

    WorkStealingThreadPool getPool(){
        return pool;
    }

    boolean isEmpty(){
        return pool.myQues[id].size()==0;
    }
}

