package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;

/**
 * Created by baum on 29/12/2016.
 */
public class ManufactoringTask extends Task <Product>{
    Warehouse warehouse;
    ManufactoringPlan plan;
    long startId;


    public ManufactoringTask(Warehouse warehouse, ManufactoringPlan plan, long startId){
        this.warehouse=warehouse;
        this.plan=plan;
        this.startId=startId;
    }

    protected void start(){

    }
}
