package bgu.spl.a2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by amitu on 10/12/2016.
 */
public class VersionMonitorTest {
    private VersionMonitor tester;
    /**
     * Set up for a test.
     @throws Exception
     @pre: none.
     @post: tester!=null
     */
    @Before
    public void setUp() throws Exception {
       tester = new VersionMonitor();
    }
    /**
     * destroy after the test.
     @throws Exception
     @pre: tester!=null
     @post: tester==null
     */
    @After
    public void tearDown() throws Exception {
    tester=null;
    }
    /**
     * returns the  current version.
     * @throws Exception
     * @inv: version>=0
     */
    @Test
    public void getVersion() throws Exception {
        try {
            assertTrue("version should be 0", tester.getVersion() == 0);
        }
        catch(Exception e){
            assertNull("getVersion returned null", tester.getVersion());
        }
    }

    /**
     * increase the version value by 1.
     * @throws Exception
     * @pre: version>=0
     * @post: version()==@pre (version())+1
     */
    @Test
    public void inc() throws Exception {
        try {
            assertFalse("version shouldnt be 1", tester.getVersion()==1);
            assertTrue("version should be 0", tester.getVersion()==0);
            tester.inc();
            assertTrue("version should be 1", tester.getVersion() == 1);
            assertFalse("version shouldnt be 0", tester.getVersion()==0);
        }
        catch(Exception e) {
            System.out.println("Unexpected inc error");
        }
    }

    /**
     * wait until this version number changes.
     @throws Exception
     @pre: none.
     @post: version()= (@pre version())+1
     */
    @Test
    public void await() throws Exception {
        int myVersion=tester.getVersion();
        try{
            tester.await(myVersion);
            tester.inc();
        }
        catch(Exception e){
            System.out.println("Unexpected await error");
        }
    assertFalse("should be different versions",  myVersion==tester.getVersion());
    }
}