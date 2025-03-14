package code;

import java.util.ArrayList;

public class TreeNode {
    boolean isRoot = false;
    EntryTuple entry;
    TreeNode parent;
    ArrayList<TreeNode> children;

    public TreeNode(boolean isRoot){
        this.isRoot = isRoot;
        this.entry = new EntryTuple(-1);
        children = new ArrayList<TreeNode>();
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
    public ArrayList<TreeNode> getChildren(){
        return children;
    }
    public TreeNode getParent(){
        return parent;
    }
    public void setParent(TreeNode parent){
        this.parent = parent;
    }
    public String toString(){
        return entry.toString() + "<" + (parent==null?"null":parent.getEntryTuple().getItem())+">";
    }


    public String getStringNode(){
        if(isRoot == true){
            return ("[" + "root" + "]");
        }
        return ("[" + entry.getItem() + ":" + entry.getSupport() + "]");
    }
}
