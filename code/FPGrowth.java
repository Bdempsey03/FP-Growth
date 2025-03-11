package code;
public class FPGrowth {
    public static void main(String[] args) {
        
        // Scan DB
        FPGrowthAlgo("data.txt");


    }

    public static void FPGrowthAlgo(String dataset){
        // ParsedTable parsedTable = new ParsedTable(dataset);
        // parsedTable.printTable();
        Tree tree = new Tree();
        tree.findSingletons(dataset); //scan DB to find support of singletons
        tree.sortTable(); //sort singletons
        tree.printTable();
        tree.makeGlobalTree(dataset);

        // Step 1: Scan DB DONE!
        
        // Step 2: Sort items in decreasing order of frequency DONE!

        // Step 3: Construct FP-tree
        // Step 4: Mine FP-tree
    }
}
