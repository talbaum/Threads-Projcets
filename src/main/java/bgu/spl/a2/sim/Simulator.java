/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import static java.lang.Math.toIntExact;
import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import org.json.simple.*;
import org.json.simple.parser.*;

import javax.swing.text.html.HTMLDocument;
//import org.json.simple.parser.JSONParser;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	static WorkStealingThreadPool pool;
	//static JSONParser parser;
	/**
	* Begin the simulation
	* Should not be called before attachWorkStealingThreadPool()
	*/
    public static ConcurrentLinkedQueue<Product> start(){
		ConcurrentLinkedQueue<Product> finishedProducts = new ConcurrentLinkedQueue<>();
		JSONParser parser = new JSONParser();
		Warehouse myWare = new Warehouse();
		try {

			//Object obj = parser.parse(new FileReader("C:\\Users\\amitu\\Downloads\\spl-a2-2017\\src\\main\\java\\bgu\\spl\\a2\\sim\\simulation[2].json"));
			Object obj = parser.parse(new FileReader("C:\\Users\\באום\\Desktop\\SPL\\Intelij Projects\\SPL2\\spl-a2-2017\\src\\main\\java\\bgu\\spl\\a2\\sim\\simulation[2].json"));
			JSONObject jsonObject = (JSONObject) obj;

			//number of threads

			Long Threads = (long)jsonObject.get("threads");
			Integer t = Integer.valueOf(Threads.intValue());
			System.out.println("Threads: "+t);

			//start checking for tools and their number
			JSONArray tools = (JSONArray)jsonObject.get("tools");
			Iterator i = tools.iterator();
			while (i.hasNext()){
				JSONObject tmp = (JSONObject)i.next();
				String name = (String)tmp.get("tool");

				Long num = (long)tmp.get("qty");
				Integer numOf = Integer.valueOf(num.intValue());
				//int numOf = (int)tmp.get("qty");
				if (name.equals("gs-driver")){
					System.out.println("tool: "+name + " num: "+numOf);
					myWare.addTool(new GcdScrewDriver(),numOf);
				}
				else if (name.equals("np-hammer")){
					System.out.println("tool: "+name + " num: "+numOf);
					myWare.addTool(new NextPrimeHammer(),numOf);
				}
				else if (name.equals("rs-pliers")){
					System.out.println("tool: "+name + " num: "+numOf);
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
				Iterator k = toolsInProduct.iterator();
				int index=0;
				while (k.hasNext()){
				tmpTools[index]=(String)k.next();
				}
				JSONArray partsInProduct = (JSONArray)tmp.get("parts");
				String[] tmpParts = new String[partsInProduct.size()];
				k = partsInProduct.iterator();
				index=0;
				while (k.hasNext()){
					tmpParts[index]=(String)k.next();
				}

				//create new plan here
				ManufactoringPlan tmpPlan = new ManufactoringPlan(name,tmpParts,tmpTools);
				System.out.println("name:"+name+ " parts:"+ Arrays.toString(tmpParts)+ " tools:"+Arrays.toString(tmpTools));
				myWare.addPlan(tmpPlan);
			}


			//JSONArray Waves = (JSONArray)jsonObject.get("waves");
			//i = plans.iterator();



		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return finishedProducts;

	}

	/**
	* attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	* @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	*/
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		pool=myWorkStealingThreadPool;
	}
	
	public static void main(String [] args) {
		start();
	}
}
