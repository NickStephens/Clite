import java.util.*;

public class State extends HashMap<VariableRef, Value> { 
    // Defines the set of variables and their associated values 
    // that are active during interpretation
    
    public State( ) { }
    
    public State(VariableRef key, Value val) {
        put(key, val);
    }
    
    public State onion(VariableRef key, Value val) {
        put(key, val);
        return this;
    }
    
    public State onion (State t) {
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
