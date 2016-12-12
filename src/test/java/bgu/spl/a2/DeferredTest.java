package bgu.spl.a2;
import static org.hamcrest.Matcher.*;

import javafx.geometry.Point2D;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;


import static org.junit.Assert.*;

/**
 * Created by amitu on 10/12/2016.
 */
public class DeferredTest {
    //need to build constractor or function that does this and setUp is sent to it?
    private Deferred<Integer> tester1;
    private Deferred<String> tester2;
    private Deferred<int[]> tester3;
    static int a=30;

    @Before
    public void setUp() throws Exception {
    tester1 = new Deferred<Integer>();
    tester2 = new Deferred<String>();
    tester3 = new Deferred<int[]>();

    }

    @After
    public void tearDown() throws Exception {
    tester1=null;
    tester2=null;
    tester3=null;
    }

     /*   try{
        //... all your code
    } catch (Exception e){
        // check your nested clauses
        if(e.getCause() instanceof FooException){
            // pass
        } else {
            Assert.fail("unexpected exception");
        }
    }*/

    @Test
    public void get() throws Exception {
        try{
            tester1.get();
        }
        catch (Exception e){
            if(e instanceof UnsupportedOperationException){
                //pass
            } else {
                fail("unexpected exception: " + e.getMessage());
            }

            assertFalse("resolved should be false",tester1.isResolved());
            Integer value = 3;
            tester1.resolve(value);
            assertEquals(value,tester1.get());
            assertTrue("resolved should be true",tester1.isResolved());
        }

        int[] a = {1,2,3,4,5};
        tester3.resolve(a);
        assertEquals(a[2],tester3.get()[2]);
    }


    /**
     * return if the deferred has been resolved.
     @throws Exception
     @pre: none.
     @post: none.
     */
    @Test
    public void isResolved() throws Exception {
    assertFalse("resolved should be false",tester1.isResolved());
    //assertEquals("the function isnt returning the right value",tester1.Resolved,tester1.isResolved());
    Integer value = 3;
    tester1.resolve(value);
    assertTrue("resolved should be true",tester1.isResolved());
    //assertEquals("the function isnt returning the right value",tester1.Resolved,tester1.isResolved());
    }

  /*public  boolean getResolvedBoolean(Deferred<T> tester){
        return tester1.Resolved;
    }*/

    /**
     * return if the deferred has been resolved.
     @throws Exception
     @pre: tester1.isResolved()=false
     @post: tester1.isResolved()=true //not sure here!
     */
    @Test
    public void resolve() throws Exception {
        // check if tester.get() gives a null ecxption
    assertFalse("should be false",tester1.isResolved());
    Integer value = 5;
    tester1.resolve(value);
    assertTrue("resolved should be true",tester1.isResolved());
    assertEquals(value,tester1.get());
        //  thrown.expect(IllegalStateException); to implement
        //tester1.resolve(value);

        assertFalse("should be false",tester2.isResolved());
        String s =null;
        tester2.resolve(s); //should

        //do the same with array!

    }

    /**
     * add a runnable to the deffered to run when it has been resolved.
     @throws Exception
     @pre: tester1.isResolved()=false
     @post: tester1.isResolved()=true //not sure here!
     */
    @Test
    public void whenResolved() throws Exception {
        int a;
    Runnable testing = () -> { DeferredTest.a=40;};
    }

    }

}