package code;

//The entries in the table and a part of the nodes
public class EntryTuple {
    private int item;
    private int support;
    private EntryTuple next;

    public EntryTuple(int item){
        this.item = item;
    }
    public EntryTuple(int item, int support){
        this.item = item;
        this.support = support;
    }
    public int getItem(){
        return item;
    }
    public int getSupport(){
        return support;
    }
    public void setSupport(int i){
        support = i;
    }

    public String toString(){
        return "(" + item + " " + support + ")";
    }
    public EntryTuple getNext(){
        return next;
    }
    public void setNext(EntryTuple next){
        this.next = next;
    }
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EntryTuple e = (EntryTuple) obj;
        return item == e.item;
    }
    //HELLOOOOo
}
