package code;

import java.util.ArrayList;

public class TreeNode {
    boolean isRoot = false;
    EntryTuple entry;
    TreeNode parent;
    ArrayList<TreeNode> children;

    public TreeNode(boolean isRoot){
        this.isRoot = isRoot;
    }
    public TreeNode(EntryTuple entry, TreeNode parent, ArrayList<TreeNode> children){
        this.entry = entry;
        this.parent = parent;
        this.children = children;
    }
    public void addChild(TreeNode child){
        children.add(child);
    }
    public EntryTuple getEntryTuple(){
        return entry;
    }

}
