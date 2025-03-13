package code;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

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
        tree.makeGlobalTree(dataset); //Step 3: construct FP-tree
        tree.printLinkedLists();

        tree.projectSubtrees();

        //Print all paths from leaves back to the root node.
        tree.newPrintTree();
        //tree.printTree();

        frequentItemsets = tree.getFrequentItemsets();

        
        // for(int i = 0; i < frequentItemsets.size(); i++){
        //     for(int j = 0; j < frequentItemsets.size(); j++){
        //        if(frequentItemsets.get(i).length == frequentItemsets.get(j).length){
        //            if(frequentItemsets.get(i)[0] == frequentItemsets.get(j)[0]){
        //             if(frequentItemsets.get(i).length == 2)
        //                if(frequentItemsets.get(i)[1] == frequentItemsets.get(j)[1]){
        //                    frequentItemsets.remove(i);
        //                }
        //            }
        //        }
        //     }
        // }
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
