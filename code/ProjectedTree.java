package code;

import java.util.ArrayList;
import java.util.Iterator;

public class ProjectedTree {
    public TreeNode root;
    public ArrayList<EntryTuple> treeTable;
    public ArrayList<TreeNode> tree;
    public ArrayList<int[]> frequent = new ArrayList<>();
    public ArrayList<EntryTuple> prevTable = new ArrayList<>();
    public ArrayList<TreeNode> prevTree;
    public ArrayList<Integer> projectVal;
    public ArrayList<PatternTuple> frequentTuples = new ArrayList<PatternTuple>();

    public ProjectedTree(ArrayList<TreeNode> prevTree, ArrayList<EntryTuple> prevTable, ArrayList<Integer> projectVal) {
        // init variables

        this.prevTree = prevTree;
        tree = new ArrayList<TreeNode>();
        treeTable = new ArrayList<EntryTuple>();
        root = new TreeNode(true);
        this.projectVal = projectVal;
        buildProjectedTree(prevTree, prevTable, projectVal);

    }

    public void buildProjectedTree(ArrayList<TreeNode> prevTree, ArrayList<EntryTuple> prevTable,
            ArrayList<Integer> projectVal) {
        // Must deepcopy arraylist (BADDDDDDDD)

        // while(prevTable.size()>1){
        // construct FP projected tree from previous tree and projected value
        EntryTuple lastEntryInPrevTree =
                // getSingletonWithValueOf(5, prevTable); //debugging
                prevTable.get(prevTable.size() - 1);
        TreeNode firstLeaf = getNodeOfEntry(prevTree, lastEntryInPrevTree);

        tree.add(root);

        EntryTuple currentLeafEntry = lastEntryInPrevTree.getNext();
        int leafSupport;

        while (currentLeafEntry != null) {
            // Go up the branch and build a new tree
            TreeNode correspondingNode = new TreeNode(
                    new EntryTuple(currentLeafEntry.getItem(), currentLeafEntry.getSupport()),
                    null, new ArrayList<TreeNode>());

            addOrUpdateTreeTable(correspondingNode.getEntryTuple());

            TreeNode currentNode = getNodeOfEntry(prevTree, currentLeafEntry);
            leafSupport = currentLeafEntry.getSupport();

            while (currentNode.getParent() != null) {

                TreeNode parent = currentNode.getParent(); // This is the parent of the node just inserted

                TreeNode correspondingParent = new TreeNode(
                        new EntryTuple(parent.getEntryTuple().getItem(), currentLeafEntry.getSupport()), null,
                        new ArrayList<TreeNode>());
                if (!parent.isRoot) {
                    addOrUpdateTreeTable(correspondingParent.getEntryTuple());
                }
                // if(getSingletonWithValueOf(correspondingNode.getEntryTuple().getItem(),
                // treeTable) == null){
                // treeTable.add(correspondingParent.getEntryTuple());
                // }else{
                // treeTable.get(treeTable.indexOf(getSingletonWithValueOf(correspondingNode.getEntryTuple().getItem(),
                // treeTable))).setSupport(treeTable.get(treeTable.indexOf(getSingletonWithValueOf(correspondingNode.getEntryTuple().getItem(),
                // treeTable))).getSupport() + leafSupport);
                // }
                if (parent.isRoot) {
                    // if the parent is the root, then the corresponding node is a child of the root
                    root.addChild(correspondingNode);
                    correspondingNode.setParent(root);
                    tree.add(correspondingNode);
                    break;
                }
                correspondingParent.addChild(correspondingNode);
                correspondingNode.setParent(correspondingParent);
                // Add the new node to the tree
                tree.add(correspondingNode);
                // Add the parent of the new node to the tree
                // System.out.println("Added node: " + correspondingNode.getStringNode() + " and
                // parent: "
                // + correspondingParent.getStringNode());
                currentNode = parent;
                correspondingNode = correspondingParent;
            }

            // System.out.println(tree);

            currentLeafEntry = currentLeafEntry.getNext();
        }
        mergeBranches();
        Iterator<EntryTuple> iterator = treeTable.iterator();
        while (iterator.hasNext()) {
            EntryTuple n = iterator.next();
            if (n.getItem() == lastEntryInPrevTree.getItem()) {
                iterator.remove(); // Safe way to remove during iteration
                tree.remove(getNodeOfEntry(prevTree, n));
            }
        }

        for(EntryTuple e : treeTable){
            if(e.getSupport() >= Tree.minsup){
                int[] minedItem = {projectVal.get(0), e.getItem()};
            frequent.add(minedItem);
            System.out.println("[" + projectVal.get(0) + ", " + e.getItem() + "] : " + e.getSupport()); //debugging
            frequentTuples.add(new PatternTuple(minedItem,e.getSupport()));
            }
            
        }
        Tree.frequentItemsets.addAll(frequent);
        Tree.frequentTuples.addAll(frequentTuples);

        // for(PatternTuple p : Tree.frequentTuples){
        //     int[] fpattern = p.getFreqPattern();
        //     for(int i = 0; i < fpattern.length; i ++){
        //         System.out.print(fpattern[i] + ", ");
        //     }
        // }
        
        // buildProjectedTree(tree, treeTable, projectVal);
        tree.clear();
        treeTable.clear();
        prevTable.remove(prevTable.size() - 1);
        // }
    }

    private void addOrUpdateTreeTable(EntryTuple entry) {
        EntryTuple existing = getSingletonWithValueOf(entry.getItem(), treeTable);
        if (existing == null) {
            // Item not in table, add it
            treeTable.add(new EntryTuple(entry.getItem(), entry.getSupport()));
        } else {
            // Item already in table, update its support
            existing.setSupport(existing.getSupport() + entry.getSupport());
        }
    }

    public void mergeBranches() {
        // We need to do multiple passes since merging can create new merge
        // opportunities
        boolean changesMade;
        System.out.println("Before merging branches:");
        newPrintTree();
        do {
            changesMade = false;

            // Process each node in the tree
            for (TreeNode n : tree) {
                if (n.getChildren().size() > 1) {
                    // Look at each child
                    for (int i = 0; i < n.getChildren().size(); i++) {
                        TreeNode childI = n.getChildren().get(i);

                        // Compare with every other child (using j = i+1 to avoid duplicate comparisons)
                        for (int j = i + 1; j < n.getChildren().size(); j++) {
                            TreeNode childJ = n.getChildren().get(j);

                            // If they have the same item, merge them
                            if (childI.getEntryTuple().getItem() == childJ.getEntryTuple().getItem()) {
                                // Add the support from childJ to childI
                                childI.getEntryTuple().setSupport(
                                        childI.getEntryTuple().getSupport() +
                                                childJ.getEntryTuple().getSupport());

                                // Transfer all children from childJ to childI
                                // We need to make a copy because we'll be modifying childJ's children list
                                ArrayList<TreeNode> childrenToTransfer = new ArrayList<>(childJ.getChildren());

                                for (TreeNode grandchild : childrenToTransfer) {
                                    // Update parent reference in the grandchild
                                    grandchild.setParent(childI);

                                    // Add the grandchild to childI
                                    childI.getChildren().add(grandchild);
                                }

                                // Remove childJ from the parent's children list
                                n.getChildren().remove(j);

                                // We made a change, remember that
                                changesMade = true;

                                // Since we removed an item at index j, we need to process j again
                                j--;
                            }
                        }
                    }
                }
            }
        } while (changesMade); // Keep going until no more changes are made du to cascading merges

        System.out.println("After merging branches:");
        newPrintTree();
        System.out.println(projectVal + " projected tree ^");
    }

    public ArrayList<PatternTuple> minePatterns(int projectVal){
        ArrayList<PatternTuple> frequent = new ArrayList<>();

        for(int i = 0; i < tree.size(); i++){

            TreeNode current = tree.get(i);
            int[] freqValue = {current.getEntryTuple().getItem(), projectVal};
            PatternTuple pat = new PatternTuple(freqValue, current.getEntryTuple().getSupport());
            frequent.add(pat);
        }

        return frequent;
    }

    public TreeNode getNodeOfEntry(ArrayList<TreeNode> tree, EntryTuple e) {
        for (TreeNode n : tree) {
            if (n.getEntryTuple().equals(e)) {
                return n;
            }
        }

        System.out.println("Cannot find node with entry: " + e + "\n");
        // new Exception().printStackTrace();
        return null;
    }

    public EntryTuple getSingletonWithValueOf(int val, ArrayList<EntryTuple> treeTable) {
        for (EntryTuple e : treeTable) {
            if (e.getItem() == val)
                return e;
        }
        System.out.println("Didnt find singleton with value of " + val);
        return null;
    }

    public void newPrintTree() {

        ArrayList<TreeNode> path = new ArrayList<TreeNode>();
        TreeNode current;
        System.out.print("\nPrinting all paths from leaves to root: \n");
        for (int i = 0; i < tree.size(); i++) {
            // skip root node
            if (tree.get(i).isRoot) {
                continue;
            }
            // If a node is a leaf get the path though parents back to the root
            if (tree.get(i).getChildren().isEmpty()) {
                current = tree.get(i);
                while (current.isRoot == false) {
                    path.add(current);
                    current = current.getParent();
                }
                path.add(root); // place root node as last noe in the path

                for (int j = path.size() - 1; j >= 0; j--) {
                    System.out.print(path.get(j).getStringNode()); // print path in order from root to leaf
                }
                path.clear(); // empty path
                System.out.print("\n");
            }
        }

        System.out.println("\n");


    }

}
