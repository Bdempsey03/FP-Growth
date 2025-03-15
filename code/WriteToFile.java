package code;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteToFile{
    private FileWriter writer;

    public WriteToFile(String dataset, ArrayList<int[]> frequentItemsets){
        try{
            writer = new FileWriter("MiningResults_"+dataset);
            writer.write("FPs = "+frequentItemsets.size()+"\n");
            for (int[] itemset : frequentItemsets){
                for(int item : itemset){
                    writer.write( item + " ");
                }
                writer.write(": \n");
            }
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}