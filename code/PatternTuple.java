package code;

public class PatternTuple {
    private int[] freqPattern;
    private int support;



    public PatternTuple(int[] pattern, int sup){
        freqPattern = pattern;
        support = sup;
    }

    public int getSupport() {
        return support;
    }

    public int[] getFreqPattern() {
        return freqPattern;
    }

    public void setSupport(int sup){
        support = sup;
    }

    public void setFreqPattern(int[] pat){
        freqPattern = pat;
    }

    @Override
    public String toString(){
      String patternString = "";

      for(int i = 0; i < freqPattern.length; i++){
          patternString =  (patternString + freqPattern[i]);
      }

      return ("[" + patternString + "] : " + support);
    }

}
