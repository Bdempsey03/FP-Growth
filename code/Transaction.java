package code;

import java.util.ArrayList;

public class Transaction {
    private int TiD;
    private ArrayList<Integer> itemset;

    public Transaction(int TiD, ArrayList<Integer> itemset) {
        this.TiD = TiD;
        this.itemset = itemset;
    }

    public int getTiD() {
        return TiD;
    }

    public ArrayList<Integer> getItemset() {
        return itemset;
    }
    public String toString() {
        return TiD + " " + itemset;
    }
}
