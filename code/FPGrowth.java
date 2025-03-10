package code;
public class FPGrowth {
    public static void main(String[] args) {
        
        // Scan DB
        FPGrowthAlgo("retail.txt");


    }

    public static void FPGrowthAlgo(String dataset){
        // ParsedTable parsedTable = new ParsedTable(dataset);
        // parsedTable.printTable();
        Tree tree = new Tree();
        tree.findSingletons(dataset); //scan DB to find support of singletons
        tree.sortTable(); //sort singletons
        tree.printTable();

        // Step 1: Scan DB
        
        // Step 2: Sort items in decreasing order of frequency
        // Step 3: Construct FP-tree
        // Step 4: Mine FP-tree
    }
}
