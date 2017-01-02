package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * this class is an extention to Task which holds a product and his plan to be made.
 */
public class ManufactoringTask extends Task <Product> {
    Warehouse warehouse;
    ManufactoringPlan plan;
    long startId;
    public int index;
    Product myProd;
    ArrayList<ManufactoringTask> miniTasks;
    ArrayList<Deferred<Tool>> toolList;
    AtomicInteger numOfTools;


    /**
     * @return the product held
     */
    public Product getMyProd() {
        return myProd;
    }

    /**
     * @param warehouse
     * @param plan
     * @param startId
     */
    public ManufactoringTask(Warehouse warehouse, ManufactoringPlan plan, long startId) {
        this.warehouse = warehouse;
        this.plan = plan;
        this.startId = startId;
        myProd = new Product(startId, plan.getProductName());
        miniTasks = new ArrayList<ManufactoringTask>();
        toolList = new ArrayList<Deferred<Tool>>();
        numOfTools=new AtomicInteger(plan.getTools().length);
    }

    /**
     *  start reading the json file and create the products plans and tasks.
     */
    public void start() {

        if (plan.getParts().length > 0) {
            ManufactoringTask tmpTask;
            //create manifactured tasks for each part in the plan and then spawn themi
            long tmpy = startId + 1;
            for (String part : plan.getParts()) {
                tmpTask = new ManufactoringTask(warehouse, warehouse.getPlan(part), tmpy);
                miniTasks.add(tmpTask);
            }

            for (ManufactoringTask task : miniTasks) {
                if(task!=null)
                spawn(task);
            }

            //after all the mini tasks finished and got values at their deffered ,do this:
            whenResolved(miniTasks, () -> {
                for (ManufactoringTask task : miniTasks) {
                    myProd.addPart(task.getResult().get()); //task.getResult().get() = the value in deffered
                }

                if (plan.getTools().length > 0)
                    toolsCheck();
                else { //means no more tools , and all mini tasks were resolved
                    complete(myProd);
                }
            }); //end of lambda
        } else { // the first if. means num of parts is 0 - (plan.getParts().length == 0)
            if (plan.getTools().length > 0)
                toolsCheck();
            else //means 0 parts and 0 tools left
                complete(myProd);
        }
    }

    /**
     * checks whether any more tools are needed, and if so create the callback to use on the tool.
     */
    private void toolsCheck() {
        for (String toolName : plan.getTools()) {
            Deferred<Tool> requestedTool;
            requestedTool = warehouse.acquireTool(toolName);
            toolList.add(requestedTool);

            //after we finished using the tool, do that:
            requestedTool.whenResolved(() -> {
                long idAfterUse = requestedTool.get().useOn(myProd);
                long cur = myProd.getFinalId();
                myProd.setFinalId(cur+idAfterUse);
                warehouse.releaseTool(requestedTool.get());

                //if this was the last tool needed , complete and finish
                if (this.numOfTools.decrementAndGet() == 0)
                    complete(myProd);
            }); //end of lambda
        } //end of for
    }//end of toolCheck

    /**
     * @return index - the position of the product in the
     */
    int getIndex(){
        return index;
    }

}