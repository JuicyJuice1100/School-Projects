/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab10;
import java.util.Scanner;
import java.util.Random;
/**
 *
 * @author Juice
 */
public class Driver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double firstRow = 1;
        System.out.println("Enter number of entries for first timing iteration");
        int iterations = new Scanner(System.in).nextInt();
        System.out.printf("%10s %10s %30s %25s %20s \n", "n", "bigO", "elapsed time in nanoSeconds", "elapsed time / bigO", "normalized");
        for(int j = 0; j < 4; j++){
            LPHashTable<Integer> table = new LPHashTable<>();
            for(int i = 0; i < iterations; i++){
                int random = new Random().nextInt( Integer.MAX_VALUE ) + 1;
                table.insert(random, random);
            }
            long startSearch = System.nanoTime();
            for(int i = 0; i < iterations; i++){
                int random = new Random().nextInt( Integer.MAX_VALUE ) + 1;
                table.search(random);
            }
            long endSearch = System.nanoTime();
            long elapsedTime = endSearch - startSearch;
            if(j == 0)
                firstRow = ((double)elapsedTime)/((double)iterations);
            double timeOverBigO = ((double)elapsedTime)/((double)iterations);
            System.out.printf("%10d %10d %30d %25.4f %20.2f \n", iterations, iterations, elapsedTime, timeOverBigO, timeOverBigO/firstRow);
            iterations *= 2;
        }     
    } 
}
