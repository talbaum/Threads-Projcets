/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;
import java.util.Timer;
import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;
import com.sun.javafx.tk.Toolkit;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    @Override
    protected void start() {

        if (array.length>1){

            int[] firstHalf = Arrays.copyOfRange(array,0,array.length/2);
            int[] secondHalf = Arrays.copyOfRange(array,array.length/2,array.length);
            MergeSort task1 = new MergeSort(firstHalf);
            MergeSort task2 = new MergeSort(secondHalf);

            spawn(task1,task2);

            List<MergeSort> list = new ArrayList<MergeSort>();
            list.add(task2);
            list.add(task1);
            whenResolved(list, () ->{
                this.complete(merge(task1.getResult().get(),task2.getResult().get()));
            });
        }
        else {
            complete(array);
        }

    }

    public static int[] merge(int[] a, int[] b) {

        int[] answer = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;

        while (i < a.length && j < b.length)
        {
            if (a[i] < b[j])
                answer[k++] = a[i++];

            else
                answer[k++] = b[j++];
        }

        while (i < a.length)
            answer[k++] = a[i++];


        while (j < b.length)
            answer[k++] = b[j++];

        return answer;
    }

    public static void main(String[] args) throws InterruptedException {
            for(int p=0;p<100;p++){
            long tStart = System.currentTimeMillis();
            WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
            int n = 1000; //you may check on different number of elements if you like
            int[] array = new Random().ints(n).toArray();
            //int[] array = new int[n];
            //for (int i=0;i<n;i++)
            //    array[i]=i;

            MergeSort task = new MergeSort(array);

            CountDownLatch l = new CountDownLatch(1);
            pool.start();
            pool.submit(task);
            task.getResult().whenResolved(() -> {
                //warning - a large print!! - you can remove this line if you wish
                //System.out.println(Arrays.toString(task.getResult().get()));
                l.countDown();
            });

            l.await();

            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;

            System.out.println(elapsedSeconds + " seconds");
            pool.shutdown();
            System.out.println("great success " +p);
        }
        System.out.println("finished with " +Thread.activeCount() + " alive threads (should be 0)" );

    }

}
