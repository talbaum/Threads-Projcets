package bgu.spl.a2.sim;

import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.Deferred;


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
	AtomicInteger driversCount;
	AtomicInteger hammersCount;
	AtomicInteger pliersCount;
	ConcurrentLinkedQueue <Deferred<Tool>> waitingDrivers;
	ConcurrentLinkedQueue <Deferred<Tool>> waitingPliers;
	ConcurrentLinkedQueue<Deferred<Tool>> waitingHammers;
	ConcurrentLinkedQueue<ManufactoringPlan> plans;

	/**
	 * Constructor
	 */
	public Warehouse() {
		driversCount=new AtomicInteger(0);
		hammersCount=new AtomicInteger(0);
		pliersCount=new AtomicInteger(0);
		waitingDrivers= new ConcurrentLinkedQueue<>();
		waitingPliers= new ConcurrentLinkedQueue<>();
		waitingHammers=new ConcurrentLinkedQueue<>();
		plans= new ConcurrentLinkedQueue<>();
	}

	/**
	 * Tool acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @param type - string describing the required tool
	 * @return a deferred promise for the  requested tool
	 */
	public Deferred<Tool> acquireTool(String type) {
		Deferred<Tool> ans = new Deferred<>();
		switch (type) {
			case "gs-driver":
				if (driversCount.intValue() > 0) {
					driversCount.set(driversCount.intValue() - 1);
					ans.resolve(new GcdScrewDriver());
					return ans;
				}
				else {
				//need to add Callback..whenresolved at Man task maybe
						waitingDrivers.add(ans);
				}
				break;

			case "np-hammer":
				if (hammersCount.intValue() > 0) {
					hammersCount.set(hammersCount.intValue() - 1);
					ans.resolve(new NextPrimeHammer());
					return ans;
				}
				else {

					waitingHammers.add(ans);
				}
				break;

			case "rs-pliers":
				if (pliersCount.intValue() > 0) {
					pliersCount.set(pliersCount.intValue() - 1);
					ans.resolve(new RandomSumPliers());
					return ans;
				}
				else {

					waitingPliers.add(ans);
				}
				break;
		}
		return null;
	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	 *
	 * @param tool - The tool to be returned
	 */
	public void releaseTool(Tool tool){
		switch (tool.getType()) {
			case "gs-driver":
				driversCount.set(driversCount.intValue()+1);
					if(!waitingDrivers.isEmpty()){
					waitingDrivers.poll().resolve(new GcdScrewDriver());
						driversCount.set(driversCount.intValue()-1);
					}
				break;

			case "np-hammer":
				hammersCount.set(hammersCount.intValue()+1);
				if(!waitingHammers.isEmpty()){
					waitingHammers.poll().resolve(new NextPrimeHammer());
					hammersCount.set(hammersCount.intValue()-1);
				}
				break;

			case "rs-pliers":
				pliersCount.set(pliersCount.intValue()+1);
				if(!waitingPliers.isEmpty()){
					waitingPliers.poll().resolve(new RandomSumPliers());
					pliersCount.set(pliersCount.intValue()-1);
				}
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
				driversCount.set(driversCount.intValue()+qty);
				break;

			case "np-hammer":
					hammersCount.set(hammersCount.intValue()+qty);
				break;

			case "rs-pliers":
					pliersCount.set(pliersCount.intValue()+qty);
				break;
		}
	}
}
