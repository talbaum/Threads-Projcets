package bgu.spl.a2;

import java.util.ArrayList;
import java.util.List;


public class SumMatrix extends Task<int[]>{
    private int[][] array;
    public SumMatrix(int[][] array) {
        this.array = array;
    }

    protected void start(){

        List<Task<Integer>> tasks = new ArrayList<>();
        int rows = array.length;
        for(int i=0;i<rows;i++){
            SumRow newTask=new SumRow(array,i);
            spawn(newTask);
            tasks.add(newTask);
        }
       // System.out.println("almost GOT INTO LAMBDA");

        whenResolved(tasks,()->{
            System.out.println("GOT INTO LAMBDA");
                         int sum=0;
                    int[] res = new int[rows];
                    for(int j=0; j< rows; j++){
                        res[j] = tasks.get(j).getResult().get();
                      // System.out.println(res[j]);
                    }

                    complete(res);
                    for(int i=0;i<myTaskDeferred.get().length;i++){
                        sum+=myTaskDeferred.get()[i];
                    }
            System.out.println("The sum of the matrix is "+sum);
                }
        );
    }
}