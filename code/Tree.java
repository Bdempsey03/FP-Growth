package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Tree{
    private TreeNode root;
    private ArrayList<TreeNode> nodesInTree; //bucket of all nodes in the tree
    private ArrayList<EntryTuple> treeTable; //This is the table associated with a tree

    public Tree(){
        root = new TreeNode(true);
        treeTable = new ArrayList<EntryTuple>();
    }
    public void findSingletons(String filename){
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

                for(int i = 2; i < tokens.length; i++) {//igore TiD and number of items

                    if(!tableContains(Integer.parseInt(tokens[i]))){
                        treeTable.add(new EntryTuple(Integer.parseInt(tokens[i]), 1));
                    }
                    else{
                        incrementSupport(Integer.parseInt(tokens[i]));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
            
        }
        public void incrementSupport(int item){ //seems slow
            for(int i = 0; i < treeTable.size(); i++){
                if(treeTable.get(i).getItem()==item)
                    treeTable.get(i).setSupport(treeTable.get(i).getSupport()+1);
            }
        }
        public boolean tableContains(int item){
            for(EntryTuple e: treeTable){
                if(e.getItem()==item)
                    return true;
            }
            return false;
        }

        public void sortTable(){
        treeTable.sort(Comparator.comparingInt(EntryTuple::getSupport).reversed());
        }
        public void printTable(){
            int i= 0;
            for(EntryTuple e: treeTable){
                if(i++>1000)
                    break;
                System.out.println(e);
            }
        }
        
    }

