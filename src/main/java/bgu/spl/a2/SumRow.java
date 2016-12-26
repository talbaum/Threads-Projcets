package bgu.spl.a2;

/**
 * Created by באום on 25/12/2016.
 */
public class SumRow extends Task<Integer> {
    private int[][] array;
    private int r;

    public SumRow(int[][] array,int r) {
        this.array = array;
        this.r=r;
    }
    protected void start(){
        int sum=0;
        for(int j=0 ;j<array[1].length;j++)
            sum+=array[r][j];

        System.out.println(sum+ " is the sumRow");
        complete(sum);
    }
}