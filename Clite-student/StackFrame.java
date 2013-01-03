import java.util.*;

public class StackFrame { // Activation Record
	
	private String name;
	private StackFrame slink;
	private StackFrame dlink;
	private HashMap<VariableRef, Value> frame_state; // This may have to change to a keyless data structure, if I'm not able to distinguish globals ahead of time.	
	// Return Address
	// Saved Frame Pointer

	public StackFrame (String stack_name, StackFrame static_link, StackFrame dynamic_link) {
		name = stack_name;
		slink = static_link; dlink = dynamic_link;
		frame_state = new HashMap<VariableRef, Value>();
	}

	/* updates the StackFrame's state given a variable and value
	   returns a reference to the modified StackFrame (itself) */
	public StackFrame onion (VariableRef key, Value val) {
		frame_state.put(key, val);
		return this;
	}

	/* updates the StackFrame's state given a StackFrame 
	   returns a reference to the modified StackFrame (itself) */
	public StackFrame onion (StackFrame st) {
		for (VariableRef key : st.frame_state.keySet( ))
			frame_state.put(key, st.frame_state.get(key));
		return this;
	}

	/* gets the StackFrame's name */
	public String get_name() {
		return name;
	}

	public void display() {
	String stk_name = "stk_name: " + name + "\n";
	String static_link = "slink: " + slink.get_name() + "\n";
	String dyn_link = "dlink: " + dlink.get_name() + "\n";	
	String tm = "{ ";
	Iterator it = frame_state.entrySet().iterator();
	while(it.hasNext()) {
		tm += it.next() + " ,";
	}
	tm = tm.substring(0, tm.length() - 2) + " }";
	System.out.println(stk_name + static_link + dyn_link + tm);
  	}
}

