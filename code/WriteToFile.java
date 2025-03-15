package code;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteToFile{
    private FileWriter writer;

    public WriteToFile(String dataset, ArrayList<PatternTuple> frequentTuples){
        try{
            writer = new FileWriter("MiningResults_"+dataset);
            writer.write("FPs = "+frequentTuples.size()+"\n");
            for (PatternTuple pattern : frequentTuples){
                for(int i = 0; i < pattern.getFreqPattern().length - 1; i++){
                    writer.write(pattern.getFreqPattern()[i]+", ");
                }

                writer.write(pattern.getFreqPattern()[pattern.getFreqPattern().length - 1]+" : "+pattern.getSupport()+"\n");
            }
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}