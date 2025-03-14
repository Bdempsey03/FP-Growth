package code;

import java.util.ArrayList;

public class ProjectedTree extends Tree{
    public ArrayList<EntryTuple> treeTable;
    public ArrayList <TreeNode> tree;
    public ArrayList<int[]> frequent = new ArrayList<>();
    private TreeNode start;
    private EntryTuple lastEntryInPrevTable;

    private ArrayList<TreeNode> prevTree;
    private ArrayList<EntryTuple> prevTable;
    
    public ProjectedTree(ArrayList<TreeNode> prevTree, ArrayList<EntryTuple> prevTable, int projectVal){

        //init variables
        this.prevTable = prevTable;
        this.prevTree = prevTree;
        lastEntryInPrevTable = prevTable.get(prevTable.size()-1);

        
        System.out.println(lastEntryInPrevTable);
        buildProjectedTree();

    }
    public void buildProjectedTree(){
        //construct FP projected tree from previous tree and projected value
        frequent.addAll(minePatterns());
    }
    public ArrayList<int[]> minePatterns(){
        ArrayList<int[]> frequent = new ArrayList<>();

        //add frequent patterns

        return frequent;
    }

}
