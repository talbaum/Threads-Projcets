package bgu.spl.a2;

import bgu.spl.a2.WorkStealingThreadPool;

public class TestMatrix {

    public static void main(String[] args) throws InterruptedException {
        //for(int i=0;i<1000;i++){
        WorkStealingThreadPool pool = new WorkStealingThreadPool(10);
        int[][] array = {{1,1,23,1},{1,1,2,100},{1,1,1,1},{1,3,1,1}};

        SumMatrix myTask = new SumMatrix(array);
        pool.submit(myTask);
        pool.start();
        Thread.sleep(3000);
        pool.shutdown(); //stopping all the threads
        //	}


    }

}

