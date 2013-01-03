import java.util.*;

public class StackFrame { // Activation Record
	
	private String name;
	private StackFrame slink;
	private StackFrame dlink;
	private FrameState frame_state; // This may have to change to a keyless data structure, if I'm not able to distinguish globals ahead of time.	
	// Return Address
	// Saved Frame Pointer

	public StackFrame (String frame_name) {
		name = frame_name;
		slink = null;
		dlink = null;
		frame_state = new FrameState();
	}

	public StackFrame (String frame_name, StackFrame static_link, StackFrame dynamic_link, Declarations decls) {
		name = frame_name;
		slink = static_link; dlink = dynamic_link;
		frame_state = new FrameState();
		for (Declaration di : decls) 
			frame_state.onion(di.v, Value.mkValue(di.t));
	}	

	public StackFrame (String frame_name, StackFrame static_link, StackFrame dynamic_link) {
		name = frame_name;
		slink = static_link; dlink = dynamic_link;
		frame_state = new FrameState();
	}

	/* updates the StackFrames state given a VariableRef and Value
	   returns a reference to the StackFrame (itself) */
	public StackFrame onion (VariableRef var, Value val) {
		frame_state.onion(var, val);
		return this;
	}

	/* updates the StackFrame's state given a StackFrame 
	   returns a reference to the modified StackFrame (itself) */
	public StackFrame onion (StackFrame st)  {
		frame_state.onion(st.frame_state);
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

