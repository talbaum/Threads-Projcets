package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;
import java.util.Random;
/**
 * an interface to be implemented by all tools
 */
class RandomSumPliers implements Tool{
	/**
	* @return tool name as string
	*/
    public String getType(){
    	return "RandomSumPliers";
	}
	/** Tool use method
	* @param p - Product to use tool on
	* @return a long describing the result of tool use on Product package
	*/
    public long useOn(Product p){
    	long sum=0;
		Random Num = new Random(p.getStartId());
		for (int i=0;i<p.getStartId()%10000;i++)
			sum=sum+Num.nextLong();

		return sum;
	}
}
