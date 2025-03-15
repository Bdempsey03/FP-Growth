package code;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.Math;

public class FPGrowth {
   private static double minSupportPercent;
    private static int minSupport;


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java FPGrowth <file_path> <min_support>");
            return;
        }

        String dataset = args[0]; // getting the file path
        File file = new File("Data\\"+dataset);
        try(Scanner sc = new Scanner(file)) {
            minSupportPercent = Double.parseDouble(args[1]);
            int numTransactions = Integer.parseInt(sc.nextLine());
            minSupport = (int) Math.floor((minSupportPercent/100) * numTransactions); 
            FPGrowthAlgo(dataset, minSupport);
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        
    }

    public static void FPGrowthAlgo(String dataset, int minsup){
        float startTime = System.nanoTime();
        ArrayList<int[]> frequentItemsets = new ArrayList<int[]>();
        Tree tree = new Tree(minsup);
        tree.findSingletons(dataset); //Step 1: scan DB to find support of singletons
        tree.sortTable(); //Step 2: sort singletons
        // tree.printTable();
        // tree.printTree();
        tree.makeGlobalTree(dataset); //Step 3: construct FP-tree
        // tree.printLinkedLists();

        try {
            tree.projectSubtrees();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Print all paths from leaves back to the root node.
        tree.newPrintTree();
        //tree.printTree();

        frequentItemsets = tree.getFrequentItemsets();
        float endTime = System.nanoTime();
        WriteToFile writer = new WriteToFile(dataset, tree.getFrequentTuples());

        
// Need to iteratively call projectSubtrees() and getFrequentItemsets() until the tables associated 

        // for(int[] itemset : frequentItemsets){
        //     System.out.print("[");
        //     for(int item : itemset){
        //         System.out.print( item + " ");
        //     }
        //     System.out.println("]");
        // }

        // Step 1: Scan DB DONE!
        
        // Step 2: Sort items in decreasing order of frequency DONE!

        // Step 3: Construct FP-tree
        // Step 4: Mine FP-tree
        // System.out.println("Frequent Itemsets: ");
        // for(int[] itemset : frequentItemsets){
        //     System.out.print("[");
        //     for(int item : itemset){
        //         System.out.print( item + " ");
        //     }
        //     System.out.println("]");
        // }

        // Print stopwatch time and minsupport info
        float ellapsedTime = (endTime - startTime)/1000000000;
        System.out.println("minsup = "+minSupportPercent+"% = "+minSupport);
        System.out.println("|FPs| = "+frequentItemsets.size());
        System.out.println("Total Runtime = "+ellapsedTime+" seconds");
    }
}
