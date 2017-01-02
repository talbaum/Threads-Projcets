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
    static int testingVal=0;
    boolean flag=false;
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
            assertTrue("version should be 1", tester.getVersion() == 1);
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
            assertTrue("version should be 1", tester.getVersion()==1);
            assertFalse("version shouldnt be "+tester.getVersion(), tester.getVersion()==0);
            tester.inc();
            assertTrue("version should be 2", tester.getVersion() == 2);
            assertFalse("version shouldnt be 1", tester.getVersion()==1);
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
        Runnable check= ()->{
            try {
                int theVersion=tester.getVersion();
                flag=true;
                tester.await(theVersion);
                VersionMonitorTest.testingVal=40;
            }
            catch (Exception e){
                System.out.println("Tester got Interupted" + e.toString());
            }
        };

        Runnable check2= ()->{
            while (!flag){
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tester.inc();
        };


        Thread t1= new Thread(check);
        Thread t2=new Thread(check2);

        t1.start();
        t2.start();
        assertTrue("testingVal should be 40", testingVal==40);
    }
}