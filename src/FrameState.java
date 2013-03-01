import java.util.*;

public class FrameState extends HashMap<VariableRef, Value> { 
    // Defines the set of variables and their associated values 
    // that are active during interpretation
    
    public FrameState( ) { }
    
    public FrameState(VariableRef key, Value val) {
        put(key, val);
    }
    
    public FrameState onion(VariableRef key, Value val) {
        put(key, val);
        return this;
    }
    
    public FrameState onion (FrameState t) {
        for (VariableRef key : t.keySet( ))
            put(key, t.get(key));
        return this;
    }

   public void display() {
	String tm = "{ ";
	Iterator it = entrySet().iterator();
	while(it.hasNext()) {
		tm += it.next() + " ,";
	}
	tm = tm.substring(0, tm.length() - 2) + " }";
	System.out.println(tm);
  } 

}
