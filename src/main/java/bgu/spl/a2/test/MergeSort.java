/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

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
            int[] secondHalf = Arrays.copyOfRange(array,(array.length/2)+1,array.length);
            MergeSort task1 = new MergeSort(firstHalf);
            MergeSort task2 = new MergeSort(secondHalf);
            //callback test () ->
            this.spawn(task1);
            this.spawn(task2);

            List<MergeSort> list = new ArrayList<MergeSort>();
            list.add(task2);
            task1.whenResolved(list, () ->{
                this.complete(merge(task1.getResult().get(),task2.getResult().get()));
            });

        }
        else complete(array);
        //TODO: replace method body with real implementation
        //throw new UnsupportedOperationException("Not Implemented Yet.");
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
        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        System.out.print("got here!");
        int n = 20; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();

        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
            System.out.println(Arrays.toString(task.getResult().get()));
            l.countDown();
        });

        //l.await();
        pool.shutdown();
    }

}
