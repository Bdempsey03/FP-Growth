package code;

import java.util.ArrayList;

public class FPGrowth {
    public static void main(String[] args) {
        
        // Scan DB
        FPGrowthAlgo("data.txt");


    }

    public static void FPGrowthAlgo(String dataset){
        // ParsedTable parsedTable = new ParsedTable(dataset);
        // parsedTable.printTable();
        ArrayList<int[]> frequentItemsets = new ArrayList<int[]>();
        Tree tree = new Tree(1);
        tree.findSingletons(dataset); //Step 1: scan DB to find support of singletons
        tree.sortTable(); //Step 2: sort singletons
        tree.printTable();
        // tree.printTree();
        tree.makeGlobalTree(dataset); //Step 3: construct FP-tree
        tree.printLinkedLists();

        tree.projectSubtrees();

        //Print all paths from leaves back to the root node.
        tree.newPrintTree();
        //tree.printTree();

        frequentItemsets = tree.getFrequentItemsets();

        
// Need to iteratively call projectSubtrees() and getFrequentItemsets() until the tables associated 
        for(int[] itemset : frequentItemsets){
            System.out.print("[");
            for(int item : itemset){
                System.out.print( item + " ");
            }
            System.out.println("]");
        }

        // Step 1: Scan DB DONE!
        
        // Step 2: Sort items in decreasing order of frequency DONE!

        // Step 3: Construct FP-tree
        // Step 4: Mine FP-tree
    }
}
