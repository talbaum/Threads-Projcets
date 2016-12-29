package bgu.spl.a2.sim;

import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.Deferred;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {

	ConcurrentLinkedQueue <Deferred<Tool>> drivers;
	ConcurrentLinkedQueue <Deferred<Tool>> pliers;
	ConcurrentLinkedQueue<Deferred<Tool>> hammers;
	AtomicInteger driversNum;
	AtomicInteger hammersNum;
	AtomicInteger pliersNum;
	ConcurrentLinkedQueue<ManufactoringPlan> plans;

	/**
	 * Constructor
	 */
	public Warehouse() {
		drivers= new ConcurrentLinkedQueue<>();
		pliers= new ConcurrentLinkedQueue<>();
		hammers=new ConcurrentLinkedQueue<>();
		driversNum=new AtomicInteger(0);
		hammersNum=new AtomicInteger(0);
		pliersNum=new AtomicInteger(0);
		plans= new ConcurrentLinkedQueue<>();
	}

	/**
	 * Tool acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @param type - string describing the required tool
	 * @return a deferred promise for the  requested tool
	 */
	public Deferred<Tool> acquireTool(String type){
        switch (type) {
            case "gs-driver":
                if(driversNum.intValue()>0) {
                    driversNum.set(driversNum.intValue() - 1);
                    return drivers.poll();
                }
                else {

                }
                break;

            case "np-hammer":
                hammersNum.set(hammersNum.intValue()-1);
                break;

            case "rs-pl-iers":
                pliersNum.set(pliersNum.intValue()-1);
                break;
        }

    }

	/**
	 * Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	 *
	 * @param tool - The tool to be returned
	 */
	public void releaseTool(Tool tool){
		switch (tool.getType()) {
			case "gs-driver":
				driversNum.set(driversNum.intValue()+1);
				break;

			case "np-hammer":
				hammersNum.set(hammersNum.intValue()+1);
				break;

			case "rs-pliers":
				pliersNum.set(pliersNum.intValue()+1);
				break;
		}
	}


	/**
	 * Getter for ManufactoringPlans
	 *
	 * @param product - a string with the product name for which a ManufactoringPlan is desired
	 * @return A ManufactoringPlan for product
	 */
	public ManufactoringPlan getPlan(String product) {
		for(ManufactoringPlan p: plans){
			if(p.getProductName().equals(product))
				return p;
		}
		return null;
	}

	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 *
	 * @param plan - a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan) {
			plans.add(plan);
			}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later retrieval
	 *
	 * @param tool - type of tool to be stored
	 * @param qty  - amount of tools of type tool to be stored
	 */
	public void addTool(Tool tool, int qty) {
    	switch (tool.getType()) {
			case "gs-driver":
				driversNum.set(qty);
				for (int i=0;i<qty;i++)
                {

                }

				break;

			case "np-hammer":
					hammersNum.set(qty);
				break;

			case "rs-pliers":
					pliersNum.set(qty);
				break;
		}
	}
}
