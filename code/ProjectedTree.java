package code;

import java.util.ArrayList;

public class ProjectedTree {
    public ArrayList<EntryTuple> treeTable;
    public ArrayList <TreeNode> tree;
    public ArrayList<int[]> frequent = new ArrayList<>();
    
    public ProjectedTree(ArrayList<TreeNode> prevTree, int projectVal){
        //init variables

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
