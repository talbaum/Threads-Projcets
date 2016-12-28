package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;
import java.math.BigInteger;

/**
 * Created by באום on 28/12/2016.
 */
public class GCDScrewdriver implements Tool {

    @Override
    public String getType() {
        return "GCD Screwdriver";
    }

    @Override
    public long useOn(Product p) {
        long myId=p.getStartId();
        long reversedId=0;

        while (myId != 0) {
            reversedId = reversedId * 10 + myId % 10;
            myId = myId / 10;
        }

        BigInteger b1 = BigInteger.valueOf(p.getStartId());
        BigInteger b2 = BigInteger.valueOf(reversedId);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();

    }
}
