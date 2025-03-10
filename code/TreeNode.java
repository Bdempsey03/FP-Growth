package code;

public class TreeNode {
    boolean isRoot;
    EntryTuple entry;
    TreeNode parent;
    TreeNode[] children;

    public TreeNode(boolean isRoot){
        this.isRoot = isRoot;
    }

}
