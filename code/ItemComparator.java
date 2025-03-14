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
        int location1 = 0;
        int location2 = 0;
        for (int i = 0; i < table.size(); i++) {

            if (table.get(i).getItem() == o1) {
                location1 = i;
            }
            if (table.get(i).getItem() == o2) {
                location2 = i;
            }
        }
        return location1 - location2;
    }
    


   }
