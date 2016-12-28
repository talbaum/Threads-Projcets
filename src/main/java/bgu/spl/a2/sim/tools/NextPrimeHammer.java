package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.util.Random;

/**
 * an interface to be implemented by all tools
 */
class NextPrimeHammer implements Tool{
	/**
	* @return tool name as string
	*/
    public String getType() {
    	return "Next Prime Hammer";
	}
	/** Tool use method
	* @param p - Product to use tool on
	* @return a long describing the result of tool use on Product package
	*/
    public long useOn(Product p){
    	boolean foundPrime=false;
    	long num=p.getStartId();
    	while (!foundPrime){
			foundPrime=true;
			num++;
    		for (int i=2;i<=num/2;i++){
    			if (num%i==0){
					foundPrime=false;
				}
			}
		}
		return num;
	}
}
