package bgu.spl.a2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.junit.rules;

import static org.junit.Assert.*;

/**
 * Created by amitu on 10/12/2016.
 */
public class DeferredTest {
    private Deferred<T> tester;
    @Before
    public void setUp() throws Exception {
    tester = new Deferred<T>();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void get() throws Exception {
        assertFalse("resolved should be false",getResolvedBoolean());
        T value = 3;
        tester.resolve(value);
      //  assertEquals(value,tester.get());
        assertTrue("resolved should be true",getResolvedBoolean());
      //  thrown.expect(IllegalStateException);
        tester.resolve(value);


    }



    @Test
    public void isResolved() throws Exception {
    assertFalse("resolved should be false",getResolvedBoolean());
    T value = 3;
    tester.resolve(value);
    assertTrue("resolved should be true",getResolvedBoolean());
    }

  public  boolean getResolvedBoolean(){
        return tester.Resolved;
    }

    @Test
    public void resolve() throws Exception {
    assertFalse("should be false",tester.isResolved());
    }

    @Test
    public void whenResolved() throws Exception {

    }

}