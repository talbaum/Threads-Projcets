/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.VersionMonitor;
import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import bgu.spl.a2.sim.tasks.ManufactoringTask;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	static WorkStealingThreadPool pool;
	static int ProductsLeftThisWave;
	static VersionMonitor myVer = new VersionMonitor();
	static ConcurrentLinkedQueue<Product> finishedProducts = new ConcurrentLinkedQueue<>();
	static int productIndex=0;
	static String FileName;

	public Simulator(WorkStealingThreadPool p) {
		attachWorkStealingThreadPool(p);
	}

	/**
	 * Begin the simulation
	 * Should not be called before attachWorkStealingThreadPool()
	 */
	public static ConcurrentLinkedQueue<Product> start(){

		JSONParser parser = new JSONParser();
		Warehouse myWare = new Warehouse();
		boolean firstStart=true;

		try {

			Object obj = parser.parse(new FileReader(FileName));
			JSONObject jsonObject = (JSONObject) obj;

			//number of threads
			Long Threads = (long)jsonObject.get("threads");
			Integer t = Integer.valueOf(Threads.intValue());
			pool = new WorkStealingThreadPool(t);

			//start checking for tools and their number
			JSONArray tools = (JSONArray)jsonObject.get("tools");
			Iterator i = tools.iterator();
			while (i.hasNext()){
				JSONObject tmp = (JSONObject)i.next();
				String name = (String)tmp.get("tool");
				Long num = (long)tmp.get("qty");
				Integer numOf = Integer.valueOf(num.intValue());
				if (name.equals("gs-driver")){
					myWare.addTool(new GcdScrewDriver(),numOf);
				}
				else if (name.equals("np-hammer")){
					myWare.addTool(new NextPrimeHammer(),numOf);
				}
				else if (name.equals("rs-pliers")){
					myWare.addTool(new RandomSumPliers(),numOf);
				}
			}

			//create all the plans from the file read
			JSONArray plans = (JSONArray)jsonObject.get("plans");
			i = plans.iterator();
			while (i.hasNext()){
				JSONObject tmp = (JSONObject)i.next();
				String name = (String)tmp.get("product");

				JSONArray toolsInProduct = (JSONArray)tmp.get("tools");
				String[] tmpTools = new String[toolsInProduct.size()];
				for (int index1=0;index1<toolsInProduct.size();index1++){
					tmpTools[index1]=(String)toolsInProduct.get(index1);
				}

				JSONArray partsInProduct = (JSONArray)tmp.get("parts");
				String[] tmpParts = new String[partsInProduct.size()];
				for (int index1=0;index1<partsInProduct.size();index1++){
					tmpParts[index1]=(String)partsInProduct.get(index1);
				}

				//create new plan here
				ManufactoringPlan tmpPlan = new ManufactoringPlan(name,tmpParts,tmpTools);
				myWare.addPlan(tmpPlan);
			}

			JSONArray Waves = (JSONArray)jsonObject.get("waves");
			for (int d=0;d<Waves.size();d++){
				JSONArray Products = (JSONArray)Waves.get(d);

				ProductsLeftThisWave=0;
				for (int index=0;index<Products.size();index++){
					JSONObject tmpP = (JSONObject)Products.get(index);
					Long qty = (long)tmpP.get("qty");
					Integer count = Integer.valueOf(qty.intValue());
					String pPlanName = (String)tmpP.get("product");

					Long ID = (long)tmpP.get("startId");
					while (count>0){
						ManufactoringTask newTask = new ManufactoringTask(myWare,myWare.getPlan(pPlanName),ID);
						newTask.getMyProd().setIndex(productIndex);
						productIndex++;
						ID++;

						Runnable callback  = () -> {
							finishedProducts.add(newTask.getResult().get());
							oneMoreProduct();
						};
						newTask.getResult().whenResolved(callback);
						pool.submit(newTask);
						count--;
					}
					if (firstStart){
						pool.start();
						firstStart=false;
					}
					while (true){
						if (finishedProducts.size()==productIndex)
							break;
					}
				}
			}


			try {
				pool.shutdown();
			}
			catch (InterruptedException e){
				System.out.println("interrupted exception "+ e.getMessage());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}


		Product[] tmpFinal = new Product[productIndex];
		for (int i=0;i<productIndex;i++){
			Product FinalP = finishedProducts.poll();
			tmpFinal[FinalP.getIndex()] = FinalP;
		}

		ConcurrentLinkedQueue<Product> FinalJob = new ConcurrentLinkedQueue<Product>();
		for (int i=0;i<productIndex;i++){
			FinalJob.add(tmpFinal[i]);
		}
		return FinalJob;
	}
	/**
	 * needed synchronized because the counter isn't atomic, without sync it may hurt the function of this func.
	 */
	static synchronized void oneMoreProduct (){
		ProductsLeftThisWave++;
		if (ProductsLeftThisWave==productIndex-1){
			myVer.inc();
		}
	}

	/**
	 * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	 * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	 */
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		pool=myWorkStealingThreadPool;
	}

	public static void main(String [] args) {
		ConcurrentLinkedQueue<Product> SimulationResult;
		FileName = args[0];
		SimulationResult = start();
		try {
			Thread.sleep(1000);
		}
		catch (Exception e){}

		try {
			FileOutputStream fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(SimulationResult);

			FileInputStream fin = new FileInputStream("result.ser");
			ObjectInputStream ois = new ObjectInputStream(fin);
			ois.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}