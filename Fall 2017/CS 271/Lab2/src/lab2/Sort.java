/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab2;

/**
 *
 * @author Justin Espiritu
 */
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sort{
    int[] numberArrayOne, numberArrayClone;
    int size;
    
    public Sort(int size){
        this.size = size;
        numberArrayOne = RandomArray();
        numberArrayClone = numberArrayOne.clone();
        long startSelectionSort = System.currentTimeMillis();
        SelectionSort(numberArrayOne);
        long endSelectionSort = System.currentTimeMillis();
        if(!Verify(numberArrayOne)){
            try {
                throw new InvalidSort();
            } catch (InvalidSort ex) {
                Logger.getLogger(Sort.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0);
            }
        }
        long startArraySort = System.currentTimeMillis();
        Arrays.sort(numberArrayClone);
        long endArraySort = System.currentTimeMillis();
    System.out.printf("%-10d %-20d %-20d \n", size, endSelectionSort, endArraySort);
        }
    
    public static void SelectionSort(int[] arr) {
        int i, j;
        int indexSmallest;
        int temp;
        for(i = 0; i < arr.length; i++){
            indexSmallest = i;
            for(j = i + 1; j < arr.length; j++){
                if(arr[j] < arr[indexSmallest]){
                    indexSmallest = j;
                }
            }
            temp = arr[i];
            arr[i] = arr[indexSmallest];
            arr[indexSmallest] = temp;
        }
    }
    
    public static boolean Verify(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++){
            if (arr[i] > arr[i + 1]){
                return false;
            }
        }
        return true;
    }
    
    public int[] RandomArray () {
        Random random = new Random(542);
        int[] array = new int[size];
        for(int i=0; i<10; i++){
            array[i] = random.nextInt();
        }
        return array;
    }
    
    class InvalidSort extends Exception{
        public InvalidSort() {
        }
    }
}
