package bgu.spl.a2;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * an abstract class that represents a task that may be executed using the
 * {@link WorkStealingThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the task result type
 */
 public abstract class Task<R> {
    private ConcurrentLinkedQueue<Task<?>> childTasks;
    Deferred<R> myTaskDeferred= new Deferred<>();
    Processor myProcessor;
    /**
     * start handling the task - note that this method is protected, a handler
     * cannot call it directly but instead must use the
     * {@link #handle(bgu.spl.a2.Processor)} method
     */
    protected abstract void start();

    /**
     *
     * start/continue handling the task
     *
     * this method should be called by a processor in order to start this task
     * or continue its execution in the case where it has been already started,
     * any sub-tasks / child-tasks of this task should be submitted to the queue
     * of the handler that handles it currently
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * @param handler the handler that wants to handle the task
     */
    /*package*/ final synchronized void handle(Processor handler) {
        boolean DoneYet=true;
        ///////we need a boolean for start and to see what come first (maybe start?) and check that start runs only once.
        if (!myTaskDeferred.isResolved()) {
            myProcessor = handler;
            ConcurrentLinkedQueue<Task<?>> childTasks2 = new ConcurrentLinkedQueue<>();
            while (!childTasks.isEmpty()) {
                Task<?> tmp = childTasks.poll();
                if (!tmp.getResult().isResolved()) {
                    //Runnable callback = () -> myProcessor.addTask(this);
                    //tmp.getResult().whenResolved(callback);
                    spawn(tmp);
                    childTasks2.add(tmp);
                    DoneYet=false;
                    //myProcessor.run();
                }
            }
            childTasks.addAll(childTasks2);
            //exit here only when there is no more childtasks
            if (DoneYet)
            start();
        }
    }

    /**
     * This method schedules a new task (a child of the current task) to the
     * same processor which currently handles this task.
     *
     * @param task the task to execute
     */
    protected final void spawn(Task<?>... task) {  //maybe sync
        for (Task<?> curTask:task){
        myProcessor.addTask(curTask);
        }
    }

    /**
     * add a callback to be executed once *all* the given tasks results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given tasks completed.
     *
     * @param tasks
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void whenResolved(Collection<? extends Task<?>> tasks, Runnable callback) {
        //write how to check if all the given tasks are resolved!
        //
        //
        Iterator<? extends Task<?>> E = tasks.iterator();
        while (E.hasNext()){
            Task<?> tmp =E.next();
            if (!tmp.getResult().isResolved()) {
                childTasks.add(tmp);
            }
            E.remove();
        }
        myTaskDeferred.whenResolved(callback);
    }

    /**
     * resolve the internal result - should be called by the task derivative
     * once it is done.
     *
     * @param result - the task calculated result
     */
    protected final void complete(R result) {
        myTaskDeferred.resolve(result);
    }

    /**
     * @return this task deferred result
     */
    public final Deferred<R> getResult() {
        return myTaskDeferred;
        //TODO: replace method body with real implementation
    }
}
