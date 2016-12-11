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
            System.out.println("getVersion returned null");
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
            tester.inc();
            assertTrue("version should be 1", tester.getVersion() == 1);
            tester.inc();
            assertTrue("version should be 2", tester.getVersion() == 2);
        }
        catch(Exception e)
        System.out.println("Unexcpeted inc error");
        }

    @Test
    public void await() throws Exception {

    }

}