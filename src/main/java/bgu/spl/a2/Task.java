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
    ConcurrentLinkedQueue<Task<?>> childTasks = new ConcurrentLinkedQueue<Task<?>>();
    Deferred<R> myTaskDeferred= new Deferred<>();
    Processor myProcessor;
    boolean hasStarted=false;
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
        myProcessor = handler;
        if (!hasStarted) {
            start();
            hasStarted = true;
        } else {
            if (!myTaskDeferred.isResolved()) {
                int i = childTasks.size();
                if (i > 0) {
                    while ((i > 0) & (!childTasks.isEmpty())) {
                        i--;
                        Task<?> tmp = childTasks.poll();
                        if ((tmp != null) && (!tmp.getResult().isResolved())) {
                            spawn(tmp);
                        }
                    }
                    myProcessor.addTask(this);
                }
            } else {
                try {
                    for (Runnable callback : myTaskDeferred.doAfterResolve) {
                        Runnable tmp = myTaskDeferred.doAfterResolve.poll();
                        tmp.run();
                    }
                } catch (Exception e) {
                    System.out.println("running all the callbacks null exception (in task class) ");
                }
            }

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
            childTasks.add(curTask);
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
        boolean foundOne = false;
        Object[] taskArr = tasks.toArray();
        for (int i = 0; i < taskArr.length; i++) {
            Task<?> tmp = (Task<?>) taskArr[i];
            if (tmp.getResult().isResolved()) {
                taskArr[i] = 0;
            } else if (!foundOne) {
                Runnable callback2 = () -> whenResolved(tasks, callback);
                tmp.myTaskDeferred.whenResolved(callback2);
                foundOne = true;
            }
        }
        if (!foundOne) {
            callback.run();
        }
    }
     /*   Iterator<? extends Task<?>> IT = tasks.iterator();
        while ((!foundOne)&(IT.hasNext())){
            Task<?> tmp = IT.next();
            if (tmp.getResult().isResolved()){
                IT.remove();
            }
            else if (!foundOne){
                Runnable callback2 = () -> whenResolved(tasks, callback);
                tmp.myTaskDeferred.whenResolved(callback2);
                foundOne=true;
            }
        }
        if (!foundOne){
            callback.run();
        }
    }*/




    /**
     * resolve the internal result - should be called by the task derivative
     * once it is done.
     *
     * @param result - the task calculated result
     */
    protected  synchronized final void complete(R result) {
        //myTaskDeferred.resolve(result);
       Runnable callCompleteAgain = () -> this.complete(result);

        Iterator<Task<?>> I = childTasks.iterator();

        while (I.hasNext()){
            if (I.next().getResult().isResolved())
                I.remove();
        }
        if (childTasks.isEmpty()){
            if (!myTaskDeferred.isResolved()) {
                myTaskDeferred.resolve(result);
            }

        }
        else{
            if ((!myTaskDeferred.isResolved())&((!childTasks.isEmpty())))
            childTasks.peek().myTaskDeferred.whenResolved(callCompleteAgain);
        }
    }

    /**
     * @return this task deferred result
     */
    public final Deferred<R> getResult() {
        return myTaskDeferred;
        //TODO: replace method body with real implementation
    }
}
