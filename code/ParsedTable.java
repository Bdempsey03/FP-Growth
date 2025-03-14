package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ParsedTable {
    private Scanner sc;

    private Transaction[] table;
    private int TiD;
    private ArrayList<Integer> itemset; //varying sizes

    private void makeTable(String filename) {
        // Read the file and create the table
        File file = new File("/Users/katelynkilburn/fpgrowth/FP-Growth/Data/" + filename);
        itemset = new ArrayList<Integer>();
        try {
            sc = new Scanner(file);

            table = new Transaction[Integer.parseInt(sc.next())];// First line of the file is the number of transactions
            int lineIndex = 0;
            // sc.nextLine(); // Skip the rest of the first line
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] tokens = line.split("\\s+");
                if (tokens.length < 2) {
                    continue; // Skip lines that do not have at least TiD and one item
                }
                TiD = Integer.parseInt(tokens[0]);
                for(int i = 2; i < tokens.length; i++) {//igore TiD and number of items
                    itemset.add(Integer.parseInt(tokens[i]));
                }
                table[lineIndex++] = new Transaction(TiD, itemset);
                itemset = new ArrayList<Integer>(); //Making new arraylist each time feels gross :(
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String toString() { //PRINTING IS VERRRRRY SLOW
        String tableString = "";
        for (int i = 0; i < table.length; i++) {
            tableString += table[i] + "\n";
        }
        return tableString;
    }

    public static void main(String[] args) {
        ParsedTable table = new ParsedTable();
        table.makeTable("data.txt");
        System.out.println(table);
    }
}
