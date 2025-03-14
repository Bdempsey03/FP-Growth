package code;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FPGrowth {
    public static void main(String[] args) {
        float startTime = System.nanoTime();
        float endTime;
        float elapsed;

        if (args.length < 2) {
            System.out.println("Usage: java FPGrowth <file_path> <min_support>");
            return;
        }
        String dataset = args[0];
        String minsup = args[1];
        
        File file = new File("Data/"+dataset);
        try(Scanner sc = new Scanner(file)) {
            Double numTransactions = Double.parseDouble(sc.nextLine());
            double minSupport = (Double.parseDouble(args[1])/100) * numTransactions; 
            FPGrowthAlgo(dataset, minSupport);
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        
        endTime = System.nanoTime();
        elapsed = endTime - startTime;
        System.out.println(elapsed/1000000000);

    }

    public static void FPGrowthAlgo(String dataset, double minSupport){
        ArrayList<int[]> frequentItemsets = new ArrayList<int[]>();
        Tree tree = new Tree(1);
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
    }
}
