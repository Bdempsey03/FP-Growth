package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Tree {
    private TreeNode root;
    private ArrayList<TreeNode> nodesInTree; // bucket of all nodes in the tree
    private ArrayList<EntryTuple> treeTable; // This is the table associated with a tree
    private ArrayList<TreeNode> projectedTree;
    private ArrayList<EntryTuple> projectedTable;
    private int minsup;

    private ArrayList<int[]> frequentItemsets = new ArrayList<int[]>();

    public Tree(int minsup) {
        root = new TreeNode(true);
        treeTable = new ArrayList<EntryTuple>();
        nodesInTree = new ArrayList<TreeNode>();
        nodesInTree.add(root);
        this.minsup = minsup;
    }

    public void findSingletons(String filename) {
        Scanner sc;
        File file = new File("Data\\" + filename);
        try {
            sc = new Scanner(file);

            treeTable = new ArrayList<EntryTuple>();// First line of the file is the number of transactions
            // sc.nextLine(); // Skip the rest of the first line
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] tokens = line.split("\\s+");
                if (tokens.length < 2) {
                    continue; // Skip lines that do not have at least TiD and one item
                }

                for (int i = 2; i < tokens.length; i++) {// igore TiD and number of items

                    if (!tableContains(Integer.parseInt(tokens[i]))) {
                        treeTable.add(new EntryTuple(Integer.parseInt(tokens[i]), 1));
                    } else {
                        incrementSupport(Integer.parseInt(tokens[i]));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void incrementSupport(int item) {
        for (int i = 0; i < treeTable.size(); i++) {
            if (treeTable.get(i).getItem() == item)
                treeTable.get(i).setSupport(treeTable.get(i).getSupport() + 1);
        }
    }

    private boolean tableContains(int item) {
        for (EntryTuple e : treeTable) {
            if (e.getItem() == item)
                return true;
        }
        return false;
    }

    public void sortTable() {
        treeTable.sort(Comparator.comparingInt(EntryTuple::getSupport).reversed());
        while(treeTable.get(treeTable.size()-1).getSupport() < minsup){
            treeTable.remove(treeTable.size()-1);
        }
    }

    public void makeGlobalTree(String filename) {

        /*
         * For all transactions in parsedTable insert all the items
         * 
         * Start at root and check if any of root.getChildren() contain an item in the
         * transaction.
         * If yes -> increment support and go down that path.
         * If no -> add a new branch with the remainder of the transaction.
         * 
         * Go back to root and repeat for next transaction.
         */

        // Start at root:
        Scanner sc;
        TreeNode currentNode = root; // item in root is NULL

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
                            // if(!currentNode.isRoot)
                            // currentNode.entry.setSupport(currentNode.entry.getSupport()+1);
                            nodesInTree.add(currentNode.getChildren().get(currentNode.getChildren().size() - 1));
                            currentNode = currentNode.getChildren().get(currentNode.getChildren().size() - 1);
                        }

                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // System.out.println(this.toStringFreq(1));
    }

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
    public void projectSubtrees() {
        for (int i = treeTable.size()-1; i > 0; i--) {
            frequentItemsets.addAll(buildProjectedTree(treeTable.get(i).getItem()));
        }

    }

    public ArrayList<int[]> buildProjectedTree(int val) {

        ArrayList<int[]> frequentItemsets = new ArrayList<int[]>();

        boolean noRoot = true;
        TreeNode projectedRoot;
        projectedTree = new ArrayList<TreeNode>();
        projectedTable = new ArrayList<EntryTuple>();


        EntryTuple start = getSingletonWithValueOf(val); // This is the table entry that we are going to project on
        TreeNode firstNode = getNodeOfEntry(start.getNext()); // First node in tree with this value
        // Add all nodes in the path to the root to the projected tree



        // GOOD UNTIL HERE

        TreeNode currentNode = firstNode;
        int sup = firstNode.getEntryTuple().getSupport();
        while (start != null) {
            while (currentNode != null) {
                if (currentNode.getParent() == null && noRoot) {
                    projectedRoot = new TreeNode(true);
                    projectedTree.add(projectedRoot);
                    noRoot = false;
                    break;
                } else {
                    if (currentNode.getParent() == null) {
                        break;
                    }
                }
                if (!currentNode.getParent().isRoot) {
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
            currentNode = getNodeOfEntry(start);
            // System.out.println(start + "!!");
        }
        System.out.println(projectedTable + ", "+val);
        for(EntryTuple e : projectedTable){
            if(e.getItem()!=-1){//skip root
                // System.out.println(val+","+e.getItem() + "!");
                frequentItemsets.add(new int[]{val, e.getItem()});
            }
        }
        
        return frequentItemsets;

    }

    public TreeNode getNodeOfEntry(EntryTuple e) {
        for (TreeNode n : nodesInTree) {
            if (n.getEntryTuple().equals(e))
                return n;
        }

        System.out.println("Cannot find node with entry: " + e);
        return null;
    }

    public EntryTuple getSingletonWithValueOf(int val) {
        for (EntryTuple e : treeTable) {
            if (e.getItem() == val)
                return e;
        }
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



        /*
        System.out.println("\n New String Tree");
        System.out.print(root.getStringNode());
        for(int i = 0; i < root.getChildren().size(); i++){
            System.out.print(root.getChildren().get(i).getStringNode());
        }
        System.out.println("\n");

         */
    }



    public void printLinkedLists() {
        boolean first = true;
        EntryTuple ptr;
        for (EntryTuple e : treeTable) {
            first = true;
            ptr = e;
            while (ptr != null) {
                System.out.print((first ? (ptr+"|") : (getNodeOfEntry(ptr)+"") + " -> "));
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
