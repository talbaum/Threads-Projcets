 package bgu.spl.a2;
import java.util.LinkedList;

/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T> the result type
 */
public class Deferred<T> {

    private T myObject;
    private Object Lock;
    LinkedList<Runnable> doAfterResolve = new LinkedList<>();
    boolean resolved = false;

    /**
     * @return the resolved value if such exists (i.e., if this object has been
     * {@link #resolve(java.lang.Object)}ed yet
     * @throws IllegalStateException in the case where this method is called and
     *                               this object is not yet resolved
     */
    public synchronized T get() throws IllegalStateException {
        if (myObject != null)
            return myObject;
        else
            throw new IllegalStateException("the object has not resolved yet!");
    }

    /**
     * @return true if this object has been resolved - i.e., if the method
     * {@link #resolve(java.lang.Object)} has been called on this object before.
     */
    public synchronized boolean isResolved() {
        return resolved;
    }

    /**
     * resolve this deferred object - from now on, any call to the method
     * {@link #get()} should return the given value
     * <p>
     * Any callbacks that were registered to be notified when this object is
     * resolved via the {@link #whenResolved(java.lang.Runnable)} method should
     * be executed before this method returns
     *
     * @param value - the value to resolve this deferred object with
     * @throws IllegalStateException in the case where this object is already
     *                               resolved
     */
    public synchronized void resolve(T value) {
        //TODO: replace method body with real implementation
        //synchronized (Lock) {
        if (value == null) {
            throw new IllegalStateException("resolve has got a null value!");
        } else {
            if (isResolved()) {
                throw new IllegalStateException("this object has already been resolved!");
            } else {
                myObject = value;
                resolved = true;
                if (!doAfterResolve.isEmpty()) {
                    while (!doAfterResolve.isEmpty()) {
                        doAfterResolve.getFirst().run();
                        doAfterResolve.removeFirst();
                    }
                }
            }
        }
    }

    /**
     * add a callback to be called when this object is resolved. if while
     * calling this method the object is already resolved - the callback should
     * be called immediately
     * <p>
     * Note that in any case, the given callback should never get called more
     * than once, in addition, in order to avoid memory leaks - once the
     * callback got called, this object should not hold its reference any
     * longer.
     *
     * @param callback the callback to be called when the deferred object is
     *                 resolved
     */
    public void whenResolved(Runnable callback) {
        if (callback == null) {
            throw new IllegalStateException("the callback sent to deferred is null!");
        } else {
            if (isResolved()) {
                callback.run();
            } else {
                doAfterResolve.add(callback);
            }
        }
    }
}
   /* public static void main(String[] args){
        Deferred<Integer> tester= new Deferred<Integer>();
        System.out.println("isResolved should be false: "+tester.isResolved());
        try {
            Integer checkIfNull = tester.get();
            System.out.println("Not Null value, test failed");
        }
        catch (Exception e){
            System.out.println("Null value , passed test!");
        }
       *//*
        try{
            tester.resolve(null);
        }
        catch (Exception e){
            System.out.println("passed test. null was inserted");
        }
System.out.println("isResolved should be false: "+tester.isResolved());

*//*
        Integer val=1;
        tester.resolve(val);
        System.out.println("isResolved should be true: "+tester.isResolved());
        System.out.println("isResolved should be 1: "+tester.get());

        try {
            tester.resolve(2);
        }
        catch(Exception e){
            System.out.println("tried resolving twice! " + e.getMessage());
        }
        Runnable callback=null;
        try {
            tester.whenResolved(callback);
        }
        catch (Exception e){
            System.out.println("callback is null!" + e.getMessage());
        }
callback=()->{
    System.out.println("IM THE GOOD CALLBACK");
};
        System.out.println("should print right away");
        tester.whenResolved(callback); //
        Deferred<Integer> tester2= new Deferred<Integer>();
        System.out.println("should add the callback tot he list and print it after i call to resolve");
        tester2.whenResolved(callback);
        callback=()->{
            System.out.println("IM THE GOOD CALLBACK2");
        };
        tester2.whenResolved(callback);
        callback=()->{
            System.out.println("IM THE GOOD CALLBACK3");
        };
        tester2.whenResolved(callback);
        System.out.println("Call to resolve:");
        tester2.resolve(5);
    }*/

