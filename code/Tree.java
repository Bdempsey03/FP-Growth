package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Tree {
    private TreeNode root;
    private ArrayList<TreeNode> nodesInTree; // bucket of all nodes in the tree
    private ArrayList<EntryTuple> treeTable; // This is the table associated with a tree
    private ArrayList<TreeNode> projectedTree; // Stores a projected tree's "bucket" of nodes
    private ArrayList<EntryTuple> projectedTable; // a proejcted tree's table
    private int minsup; //set by user

    private ArrayList<int[]> frequentItemsets = new ArrayList<int[]>(); //this is constantly added to
    private ArrayList<int[]> fPatterns = new ArrayList<>();

    public Tree(int minsup) {
        //init variables
        root = new TreeNode(true);
        treeTable = new ArrayList<EntryTuple>();
        nodesInTree = new ArrayList<TreeNode>();
        nodesInTree.add(root);//add root to tree's nodes
        this.minsup = minsup;
    }

    /*
     * This method finds all itemsets of size 1 and their frequencies.
     * The singletons are added to treeTable which is the table for the global tree.
     * The table is not sorted and singletons with support < minsup ARE still included (dealt with later)
     */
    public void findSingletons(String filename) {
        Scanner sc;
        File file = new File("Data\\" + filename);
        try {
            sc = new Scanner(file);
            treeTable = new ArrayList<EntryTuple>();// First line of the file is the number of transactions
            while (sc.hasNextLine()) {//continue until end of file
                String line = sc.nextLine().trim();
                String[] tokens = line.split("\\s+");//split transaction into individual tokens
                if (tokens.length < 2) {
                    continue; // Skip lines that do not have at least TiD and one item
                }

                for (int i = 2; i < tokens.length; i++) {// ignore TiD and number of items


                    if (!tableContains(Integer.parseInt(tokens[i]))) {
                        //if the tree table does not already have this token then we need to add it
                        treeTable.add(new EntryTuple(Integer.parseInt(tokens[i]), 1));
                    } else {
                        //if the Tree Table already has this token in it then we increase the support rather than adding a new token
                        incrementSupport(Integer.parseInt(tokens[i]));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /*
     * Helper method that just adds 1 to the support of an item
     */
    private void incrementSupport(int item) {
        for (int i = 0; i < treeTable.size(); i++) {
            if (treeTable.get(i).getItem() == item)
                treeTable.get(i).setSupport(treeTable.get(i).getSupport() + 1);
        }
    }

    public ArrayList<int[]> getfPatterns(){
        return fPatterns;
    }

    /*
     * Helper method that checks every tuple in the table to see if it is contained in the table
     * @returns boolean "does the table contain item?"
     */
    private boolean tableContains(int item) {
        for (EntryTuple e : treeTable) {
            if (e.getItem() == item)
                return true;
        }
        return false;
    }

    /*
     * Sorts list of singletons and removes ones with support less than the minsup
     */
    public void sortTable() {
        treeTable.sort(Comparator.comparingInt(EntryTuple::getSupport).reversed());
        while(treeTable.get(treeTable.size()-1).getSupport() < minsup){
            treeTable.remove(treeTable.size()-1);
        }
    }


    /**
     * Parses a file and constructs a global m-way FP tree.
     *
     * For every transaction t in the transaction DB we sort the transaction in order of most frequent singletons,
     * next we construct the tree from the root downwards.
     * First, we add the first element of t1 as the child of the root,
     * next we add the second element of t1 as the child of the first,
     * repeat until t1 is added.
     *
     * Next, if t2's first element is the same as t1 we follow down that branch increasing the support
     * until we find an element that was NOT in t1. Now we split into a new branch inserting t2. Repeat for
     * t3, and t4, ...
     *
     * @param filename
     */
    public void makeGlobalTree(String filename) {

        // Start at root:
        Scanner sc;
        TreeNode currentNode = root; // item in root is -1

        // Read the file and create the table
        File file = new File("Data\\" + filename);
        for(EntryTuple e : treeTable)
            frequentItemsets.add(new int[]{e.getItem()});

        try {
            sc = new Scanner(file);
            Transaction t;
            ArrayList<Integer> itemset;
            sc.nextLine(); // Skip the first line since it just the transaction count
            while (sc.hasNextLine()) {
                itemset = new ArrayList<Integer>(); // create a new empty itemset for next transaction
                String line = sc.nextLine().trim();
                String[] tokens = line.split("\\s+");
                if (tokens.length < 2) { // This is just incase the file is formatted poorly
                    continue; // Skip lines that do not have at least TiD and one item
                }
                for (int i = 2; i < tokens.length; i++) {// igore TiD and number of items
                    itemset.add(Integer.parseInt(tokens[i]));
                }
                ArrayList<Integer> singletons = new ArrayList<Integer>(); //Construct a list of just the integer singletons above minsup



                for (EntryTuple e : treeTable) {
                    singletons.add(e.getItem());
                }
                itemset.retainAll(singletons); //remove all items not in the singletons list from transaction before adding to tree
                Collections.sort(itemset, new ItemComparator(treeTable));
                t = new Transaction(Integer.parseInt(tokens[0]), itemset);

                /* Build global tree */

                /*
                 * Start at root and check if any of root.getChildren() contain an item in the
                 * transaction.
                 * If yes -> increment support and go down that path.
                 * If no -> add a new branch with the remainder of the transaction.
                 */

                boolean found;
                currentNode = root;
                while (t.getItemset().size() > 0) {
                    found = false;
                    if (currentNode.getChildren().size() == 0) {// No children (true if root and tree is empty or if
                                                                // leaf node)
                        while (t.getItemset().size() > 0) { // if currentNode is a leaf node then we make a new branch
                                                            // with this whole remaining transaction
                            currentNode.addChild(new TreeNode(new EntryTuple(t.getItemset().get(0), 1), currentNode,
                                    new ArrayList<TreeNode>()));
                            // ADD POINTER
                            addPointer(currentNode.getChildren().get(0), t.getItemset().get(0));
                            // PTR ADDED

                            nodesInTree.add(currentNode.getChildren().get(0));
                            t.getItemset().remove(0);// shrink itemset (TODO: REVISIT BECAUSE SLOW)
                            currentNode = currentNode.getChildren().get(0);
                        }
                    }
                    if (currentNode.getChildren().size() > 0) {

                        for (int i = 0; i < currentNode.getChildren().size(); i++) {

                            if (!t.getItemset().isEmpty() && currentNode.getChildren().get(i).getEntryTuple()
                                    .getItem() == t.getItemset().get(0)) {// if the item is in the children of
                                                                          // currentNode traverse that branch
                                currentNode.getChildren().get(i).getEntryTuple()
                                        .setSupport(currentNode.getChildren().get(i).getEntryTuple().getSupport() + 1);
                                currentNode = currentNode.getChildren().get(i);
                                t.getItemset().remove(0);// shrink itemset (TODO: REVISIT BECAUSE SLOW)
                                found = true;
                            }
                        }
                        if (found == false) {
                            currentNode.addChild(new TreeNode(new EntryTuple(t.getItemset().get(0), 1), currentNode,
                                    new ArrayList<TreeNode>()));
                            // ADD POINTER
                            addPointer(currentNode.getChildren().get(currentNode.getChildren().size() - 1),
                                    t.getItemset().get(0));
                            // POINTER ADDED
                            t.getItemset().remove(0);// shrink itemset (TODO: REVISIT BECAUSE SLOW)

                            nodesInTree.add(currentNode.getChildren().get(currentNode.getChildren().size() - 1));
                            currentNode = currentNode.getChildren().get(currentNode.getChildren().size() - 1);
                        }

                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * Helper method that adds the appropriate pointer to a node in a tree.
     *
     * INNEFFICIENT :(
     */
    private void addPointer(TreeNode newNode, int item) {
        EntryTuple ptr;
        EntryTuple next;

        ptr = getSingletonWithValueOf(item);
        next = ptr.getNext();
        while (next != null) {
            ptr = next;
            next = ptr.getNext();
        }
        ;
        ptr.setNext(newNode.getEntryTuple());

    }













    /*
     * This method SHOULD iterate through all subtrees and subtrees of subtrees but it current just creates
     * the x-projected trees for the singletons in the DB
     */

    public void projectSubtrees() {
        for (int i = treeTable.size()-1; i > 0; i--) {

            System.out.println("Tree Table: " + treeTable.get(i));

            ArrayList<int[]> foundPatterns = (getProjectedPatterns(treeTable, nodesInTree, treeTable.get(i).getItem()));
            for(int j = 0; j < foundPatterns.size(); j++){

                if (frequentItemsets.contains(foundPatterns.get(j))){
                   frequentItemsets.add(foundPatterns.get(j)); // Add a newly found pattern if it's not already in frequentItemsets
                }
            }



        }

    }


    private TreeNode combineNodes(TreeNode n1, TreeNode n2){

        if(n1 == null){
            return n2;
        }

        if(n2 == null){
            return n1;
        }

        EntryTuple tup1 = n1.getEntryTuple();
        EntryTuple tup2 = n2.getEntryTuple();
        EntryTuple ctup = new EntryTuple(tup1.getItem(), (tup1.getSupport() + tup2.getSupport()) ); //tuples should have the same item; sum support

        ArrayList<TreeNode> children1 = n1.getChildren();
        ArrayList<TreeNode> children2 = n2.getChildren();
        children1.addAll(children2); //combine bot sets of children
        //Should only combine nodes that have the same parent
        TreeNode cNode = new TreeNode(ctup, n1.getParent(),children1);

        return cNode;
    }


    
    private ArrayList<TreeNode> mergePaths(ArrayList<ArrayList<TreeNode>> paths){

        ArrayList<TreeNode> mergedPaths = new ArrayList<>();

        TreeNode combinedRoot = null;
        for(ArrayList<TreeNode> t: paths){
            TreeNode pRoot = t.get(t.size()-1); //Last element in the path will be the root.
            t.remove(pRoot);
            mergedPaths.addAll(t);
            combinedRoot = combineNodes(combinedRoot, pRoot);
        }

        mergedPaths.add(combinedRoot); //Add one root node to the merged paths.

        ArrayList<TreeNode> rootKids = combinedRoot.getChildren();
        for(int i = 0; i < rootKids.size(); i++){
            for(int j = 0; j < rootKids.size(); j++){
                if(rootKids.get(i).getEntryTuple().getItem() == rootKids.get(j).getEntryTuple().getItem() && i != j){
                    //I don't think combine method modifies in place so manually need to remove and add new node. I could be wrong though.
                    TreeNode combined = combineNodes(rootKids.get(i), rootKids.get(j));
                    mergedPaths.remove(rootKids.get(i));
                    mergedPaths.remove(rootKids.get(j));
                    mergedPaths.add(combined);// put combined in the merged list
                    //TODO Merges Are possible between children of merged nodes. Need to add those.
                }
            }
        }



        return mergedPaths;
    }




    public ArrayList<TreeNode> buildPTreeFromPaths(ArrayList<TreeNode> tree, TreeNode pNode) {
        ArrayList<TreeNode> path = new ArrayList<TreeNode>();
        ArrayList<TreeNode> pTree = new ArrayList<>();
        ArrayList<ArrayList<TreeNode>> pathList = new ArrayList<>();

        TreeNode current;


        for (int i = 0; i < tree.size(); i++) {
            //skip root node
            if (tree.get(i).getParent() == null) {
                continue;
            }
            // If a node is a leaf get the path though parents back to the root
            if (tree.get(i).getChildren().isEmpty()) {
                current = tree.get(i);
                while (current.getParent() != null) {
                    path.add(current);
                    current = current.getParent();
                }
                path.add(current); // place root node as last node in the path
                pathList.add(path);//add full path to the pathList
                path.clear(); //empty path
            }
        }


        // For each path in the list
        for(int i = 0; i < pathList.size(); i++){
            if(pathList.get(i).contains(pNode) == false){
                pathList.remove(pathList.get(i)); //remove paths that do not contain the item being projected.
            }

            current = pathList.get(i).get(0); //Get the leaf node of the path


            while(current.getParent() != null){
                //Set the support of each element between the leaf and the root to the value of the support value of the leaf
                current.getParent().getEntryTuple().setSupport(current.getEntryTuple().getSupport());
                current = current.getParent();
            }

            pTree = mergePaths(pathList);

        }

        return pTree;
    }

    public ArrayList<ArrayList<TreeNode>> buildPTree(ArrayList<TreeNode> tree, TreeNode pNode){

        ArrayList<TreeNode> path = new ArrayList<TreeNode>();
        ArrayList <ArrayList<TreeNode>> pathList = new ArrayList<>();

        TreeNode current;

        for(int i = 0; i < tree.size(); i++){
            //skip root node
            if(tree.get(i).getParent() == null){
                continue;
            }
            // If a node is a leaf get the path though parents back to the root
            if(tree.get(i).getChildren().isEmpty()) {
                current = tree.get(i);
                while (current.getParent() != null) {
                    path.add(current);
                    current = current.getParent();
                }
                path.add(current); // place root node as last node in the path

                pathList.add(path);
                path.clear(); //empty path
            }
        }

        return pathList;
    }



    public ArrayList<int[]> getProjectedPatterns(ArrayList<EntryTuple> prevTable, ArrayList<TreeNode> prevTree, int val){

        ArrayList<int[]> projectedPatterns = new ArrayList<int[]>();

        boolean noRoot = true;
        TreeNode projectedRoot;
        projectedTree = new ArrayList<TreeNode>();
        projectedTable = new ArrayList<EntryTuple>();

        EntryTuple start = getSingletonWithValueOf(val); // This is the table entry that we are going to project on

        TreeNode firstNode = getNodeOfEntry(prevTree, start.getNext()); // First node in tree with a value that matches the side table

        // Add all nodes in the path to the root to the projected tree

        TreeNode currentNode = firstNode;

        int sup = firstNode.getEntryTuple().getSupport(); // The support of all nodes between this node and the root are the same as this one
        start = start.getNext();


        while (start != null) {//This loop goes "laterally" from one node to its NEXT POINTER.

            while (currentNode != null) {//This loops traverses UP a tree branch and stops when we reach the root


                if (currentNode.getParent() == null && noRoot) {//If this projected tree doesn't have a root and we have reached the top we add a root
                    projectedRoot = new TreeNode(true);
                    projectedTree.add(projectedRoot);
                    noRoot = false;
                    break;
                } else {
                    if (currentNode.getParent() == null) {//if there is a root and weve reached the parent being NULL then we have reached the top
                        break;
                    }
                }


                if (!currentNode.getParent().isRoot) {//if we arent in the root then we add the current node and continue upwards
                    //add a new node with the value of the parent and the support of the firstNode
                    projectedTree.add(new TreeNode(new EntryTuple(currentNode.getParent().getEntryTuple().getItem(), sup), currentNode.getParent().getParent(), new ArrayList<TreeNode>()));
                    projectedTable.add(projectedTree.get(projectedTree.size() - 1).getEntryTuple());
                    currentNode = currentNode.getParent();
                }
                else{

                    // I think this was leading to multiple root nodes in the tree. First if-block should add the root -Rohan
                    /*
                    //The parent is the root
                    projectedTree.add(new TreeNode(new EntryTuple(currentNode.getParent().getEntryTuple().getItem(), 0), currentNode.getParent(), //TODO: I think this should be the same root as above
                            new ArrayList<TreeNode>())); //Root support should always be zero
                    projectedTable.add(projectedTree.get(projectedTree.size() - 1).getEntryTuple());

                     */

                    currentNode = currentNode.getParent();

                }
            }
            start = start.getNext();//Move laterally
            if(start!=null)
                currentNode = getNodeOfEntry(prevTree, start);

            System.out.println("Tree: " + projectedTree);
            System.out.println("Table: " + projectedTable);
            printPTree(projectedTree);

        }







        return projectedPatterns;
    }













    /*
     * This method makes the projected subtrees from a tree (arraylist of nodes) and a tree table with a value to project (val)
     */

    /*
    public ArrayList<int[]> buildProjectedTree(ArrayList<EntryTuple> prevTable, ArrayList<TreeNode> prevTree, int val) {

        ArrayList<int[]> frequentItemsets = new ArrayList<int[]>();

        boolean noRoot = true;
        TreeNode projectedRoot;
        projectedTree = new ArrayList<TreeNode>();
        projectedTable = new ArrayList<EntryTuple>();


        EntryTuple start = getSingletonWithValueOf(val); // This is the table entry that we are going to project on
        TreeNode firstNode = getNodeOfEntry(prevTree, start.getNext()); // First node in tree with this value
        // Add all nodes in the path to the root to the projected tree


        TreeNode currentNode = firstNode;//rename to a more useful name

        //TODO:
        //This assumption is not true if the projected tree has branches that join higher up. Need to revisit!

        int sup = firstNode.getEntryTuple().getSupport();//the support of all nodes between this node and the root are the same as this one
        start=start.getNext();

        while (start != null) {//This loop goes "laterally" from one node to its NEXT POINTER.

            while (currentNode != null) {//THis loops traverses UP a tree branch and stops when we reach the root
                if (currentNode.getParent() == null && noRoot) {//If this projected tree doesnt have a root and we have reached the top we add a root
                    projectedRoot = new TreeNode(true);
                    projectedTree.add(projectedRoot);
                    noRoot = false;
                    break;
                } else {
                    if (currentNode.getParent() == null) {//if there is a root and weve reached the parent being NULL then we have reached the top
                        break;
                    }
                }
                if (!currentNode.getParent().isRoot) {//if we arent in the root then we add the current node and continue upwards
                    projectedTree.add(new TreeNode(new EntryTuple(currentNode.getParent().getEntryTuple().getItem(),
                            sup), currentNode.getParent(),
                            new ArrayList<TreeNode>()));
                    projectedTable.add(projectedTree.get(projectedTree.size() - 1).getEntryTuple());
                    currentNode = currentNode.getParent();
                }else{
                    projectedTree.add(new TreeNode(new EntryTuple(currentNode.getParent().getEntryTuple().getItem(),
                            0), currentNode.getParent(), //TODO: I think this should be the same root as above
                            new ArrayList<TreeNode>())); //Root support should always be zero
                    projectedTable.add(projectedTree.get(projectedTree.size() - 1).getEntryTuple());
                    currentNode = currentNode.getParent();
                }
            }
            start = start.getNext();//Move laterally
            if(start!=null)
                currentNode = getNodeOfEntry(prevTree, start);

        }

        //THIS IS PROBABLY WHERE THE RECURIVE CALL SHOULD BE(?)

        for(EntryTuple e : projectedTable){ //add discovered frequent items to set
            if(e.getItem()!=-1){//dont add the root in a set
                frequentItemsets.add(new int[]{val, e.getItem()});//add the prefix (the thing we projected) + every frequent item in the table for the projected tree
            }
        }
        //TODO: Filter out all entries with sup < minsup

        return frequentItemsets;

    }
 */













    /*
     * Helper method to find the node in the tree
     */

    public TreeNode getNodeOfEntry(ArrayList<TreeNode> nodesInTree, EntryTuple e) {
        for (TreeNode n : nodesInTree) {
            if (n.getEntryTuple().equals(e)){
                return n;
            }
        }

        System.out.println("Cannot find node with entry: " + e +"\n");
        // new Exception().printStackTrace();
        return null;
    }

    public EntryTuple getSingletonWithValueOf(int val) {
        for (EntryTuple e : treeTable) {
            if (e.getItem() == val)
                return e;
        }
        System.out.println("Didnt find singleton with value of "+val);
        return null;
    }

    public String toString() {
        String tree = "";
        int ctr = 0;
        for (TreeNode n : nodesInTree) {
            if (ctr++ > 1000)
                break;
            tree += n.toString() + ", ";
        }

        // return nodesInTree.toString();
        return tree;
    }

    public String toStringFreq(int minsup) {
        String tree = "";
        int ctr = 0;
        int numOfFreqItems = 0;
        for (TreeNode n : nodesInTree) {
            if (ctr++ > 1000)
                break;
            if (n.getEntryTuple().getSupport() >= minsup) {
                tree += n.toString() + ", ";
                numOfFreqItems++;
            }

        }
        return tree + "\nNumber of nodes above minsup: " + numOfFreqItems;
    }

    public void printTable() {
        int i = 0;
        for (EntryTuple e : treeTable) {
            if (i++ > 1000)
                break;
            System.out.println(e);
        }
    }

    public void printTree() {
        TreeNode currentNode = root;
        for (int i = 0; i < currentNode.getChildren().size(); i++) {
            System.out.println(currentNode.getChildren().get(i));
        }

    }

    public void printPTree(ArrayList<TreeNode> nodes){

        ArrayList<TreeNode> path = new ArrayList<TreeNode>();
        TreeNode current;
        TreeNode projRoot;
        System.out.print("\nPrinting all paths from leaves to root in a P-Tree: \n");
        for(int i = 0; i < nodes.size(); i++){
            //skip root node
            if(nodes.get(i).getParent() == null){
                continue;
            }
            // If a node is a leaf get the path though parents back to the root
            if(nodes.get(i).getChildren().isEmpty()) {
                current = nodes.get(i);
                while (current.getParent() != null) {
                    path.add(current);
                    current = current.getParent();
                }
                path.add(current); // place root node as last node in the path

                for (int j = path.size() - 1; j >= 0; j--) {
                    System.out.print(path.get(j).getStringNode()); //print path in order from root to leaf
                }
                path.clear(); //empty path
                System.out.print("\n");
            }
        }

        System.out.println("\n");


    }

    public void newPrintTree(){

        ArrayList<TreeNode> path = new ArrayList<TreeNode>();
        TreeNode current;
        System.out.print("\nPrinting all paths from leaves to root: \n");
        for(int i = 0; i < nodesInTree.size(); i++){
            //skip root node
            if(nodesInTree.get(i).isRoot){
                continue;
            }
            // If a node is a leaf get the path though parents back to the root
            if(nodesInTree.get(i).getChildren().isEmpty()) {
                current = nodesInTree.get(i);
                while (current.isRoot == false) {
                    path.add(current);
                    current = current.getParent();
                }
                path.add(root); // place root node as last noe in the path

                for (int j = path.size() - 1; j >= 0; j--) {
                    System.out.print(path.get(j).getStringNode()); //print path in order from root to leaf
                }
                path.clear(); //empty path
                System.out.print("\n");
            }
        }

        System.out.println("\n");
    }



    public void printLinkedLists() {
        boolean first = true;
        EntryTuple ptr;
        for (EntryTuple e : treeTable) {
            first = true;
            ptr = e;
            while (ptr != null) {
                System.out.print((first ? (ptr+"|") : (getNodeOfEntry(nodesInTree,ptr)+"") + " -> "));
                first = false;
                ptr = ptr.getNext();
            }
            System.out.println();
        }
    }
    public ArrayList<int[]> getFrequentItemsets(){
        return frequentItemsets;
    }

}
