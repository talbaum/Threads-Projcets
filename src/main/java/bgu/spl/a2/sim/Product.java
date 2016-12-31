package bgu.spl.a2.sim;
import bgu.spl.a2.sim.tools.Tool;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product {
	private ConcurrentLinkedDeque<Tool> Tools;
	private ConcurrentLinkedDeque<Product> Parts;
	private long startId;
	private String name;
	private long finalID=0;

	/**
	 * Constructor
	 * @param startId - Product start id
	 * @param name - Product name
	 */
	public Product(long startId, String name){
		this.name=name;
		this.startId=startId;
		Tools = new ConcurrentLinkedDeque<>();
		Parts = new ConcurrentLinkedDeque<>();
	}

	/**
	 * @return The product name as a string
	 */
	public String getName(){
		return name;
	}

	/**
	 * @return The product start ID as a long. start ID should never be changed.
	 */
	public long getStartId(){
		return startId;
	}

	/**
	 * @return The product final ID as a long.
	 * final ID is the ID the product received as the sum of all UseOn();
	 */
	public long getFinalId(){
		return finalID;
	}
	public void setFinalId(long id)
	{
		finalID=id;
	}
	/**
	 * @return Returns all parts of this product as a List of Products
	 */
	public List<Product> getParts(){
		List<Product> partsList = new LinkedList<>();
		if (Parts!=null)  //fixed a problem but shouldnt be getting null anyway!
		for (Product part : Parts){
			partsList.add(part);
		}
		return partsList;
	}

	/**
	 * Add a new part to the product
	 * @param p - part to be added as a Product object
	 */
	public void addPart(Product p){
		Parts.add(p);
	}


}