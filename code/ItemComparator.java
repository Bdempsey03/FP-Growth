package code;

import java.util.ArrayList;
import java.util.Comparator;

public class ItemComparator implements Comparator<Integer>{
    private ArrayList<EntryTuple> table;
    
    public ItemComparator(ArrayList<EntryTuple> table) {
        this.table = table;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        int support1 = 0;
        int support2 = 0;
        for (EntryTuple e : table) {
            if (e.getItem() == o1) {
                support1 = e.getSupport();
            }
            if (e.getItem() == o2) {
                support2 = e.getSupport();
            }
        }
        return support2 - support1;
    }
    


   }
