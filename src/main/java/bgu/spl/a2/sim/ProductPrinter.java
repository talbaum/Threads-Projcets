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
        FileInputStream fis = new FileInputStream("C:\\Users\\amitu\\Downloads\\spl-a2-2017\\src\\main\\java\\bgu\\spl\\a2\\sim\\result.ser");
        //FileInputStream fis = new FileInputStream("C:\\Users\\באום\\Desktop\\SPL\\Intelij Projects\\SPL2\\spl-a2-2017\\result.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        q = (ConcurrentLinkedQueue<Product>) ois.readObject();
        //  System.out.println(q.poll().name);

        for (Product p : q) {
            printProduct(p);
            System.out.println();
        }

    }
}