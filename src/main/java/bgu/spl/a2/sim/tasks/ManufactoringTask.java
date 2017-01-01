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
 * Created by baum on 29/12/2016.
 */
public class ManufactoringTask extends Task <Product> {
    Warehouse warehouse;
    ManufactoringPlan plan;
    long startId;
    Product myProd;
    ArrayList<ManufactoringTask> miniTasks;
    ArrayList<Deferred<Tool>> toolList;


    public ManufactoringTask(Warehouse warehouse, ManufactoringPlan plan, long startId) {
        this.warehouse = warehouse;
        this.plan = plan;
        this.startId = startId;
        myProd = new Product(startId, plan.getProductName());
        miniTasks = new ArrayList<ManufactoringTask>();
        toolList = new ArrayList<Deferred<Tool>>();
    }

    public void start() {

        if (plan.getParts().length > 0) {
            ManufactoringTask tmpTask;
            //create manifactured tasks for each part in the plan and then spawn themi
            for (String part : plan.getParts()) {
                tmpTask = new ManufactoringTask(warehouse, warehouse.getPlan(part), myProd.getStartId() + 1); //why +1 ?- this is how its defined in the task
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
                  /*  long sumOfAll = myProd.getFinalId();
                    for (ManufactoringTask task : miniTasks)
                    sumOfAll+=task.getResult().get().getFinalId();

                   myProd.setFinalId(sumOfAll); //need to make sure if needed*/
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

    private void toolsCheck() {
        for (String toolName : plan.getTools()) {
            Deferred<Tool> requestedTool;
            requestedTool = warehouse.acquireTool(toolName);
            toolList.add(requestedTool);

            //after we finished using the tool, do that:
            requestedTool.whenResolved(() -> {
                long idAfterUse = requestedTool.get().useOn(myProd);
                //myProd.setFinalId(myProd.getFinalId()+idAfterUse);
                warehouse.releaseTool(requestedTool.get());

                //if this was the last tool needed , complete and finish
                AtomicInteger numOfTools = new AtomicInteger(toolList.size());// (plan.getTools().length); ?
                if (numOfTools.decrementAndGet() == 0)
                    complete(myProd);
            }); //end of lambda
        } //end of for
    }//end of toolCheck


    public String printProduct() {
        String des = "ProductName: ";
        des += myProd.getName();
        des += " Product Id = " + myProd.getFinalId();
        des += "\n" + "PartsList {" + "\n";
        if (miniTasks != null) {
            for (ManufactoringTask p : this.miniTasks) {
                if (p != null) {
                    des += p.printProduct();
                }
                des += "}" + "\n";

            }
        }

        return des;
    }

}