package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tasks.ManufactoringTask;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProductPrinter {
    static void printProduct(Product p) {
        System.out.println(String.format("ProductName: %s  Product Id = %d", p.getName(), p.getFinalId()));
        System.out.println(String.format("PartsList {"));

        for (Product part : p.getParts()) {
            printProduct(part);
        }

        System.out.println(String.format("}"));
    }



    public static void main(String args[]) throws Exception {
        ConcurrentLinkedQueue<Product> q;
        //amit - add your file path here!
        FileInputStream fis = new FileInputStream("result.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        q = (ConcurrentLinkedQueue<Product>) ois.readObject();

        for (Product p : q) {
            printProduct(p);
            System.out.println();
        }

    }
}