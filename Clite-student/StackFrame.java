import java.util.*;

public class StackFrame { // Activation Record
	
	private String name;
	private StackFrame slink;
	private StackFrame dlink;
	private FrameState frame_state; // This may have to change to a keyless data structure, if I'm not able to distinguish globals ahead of time.	
	// Return Address
	// Saved Frame Pointer


	public StackFrame (String frame_name, Declarations globals) {
		name = frame_name;
		slink = null;
		dlink = null;
		frame_state = new FrameState();
		for (Declaration di : globals) 
			frame_state.onion(di.v, Value.mkValue(di.t));
	}

	/* This constructor assumes that all StackFrames are generated by function calls,
	    and each stack_frame is named after the function being called. */
	public StackFrame (String frame_name, State st) {
		name = frame_name;
		slink = st.get_dataseg();
		try {
			dlink = st.get_stack_top();
		} catch (IllegalArgumentException e) { // stack underflow
			dlink = null;
		}
		frame_state = new FrameState();
		for (Declaration di : st.get_func_vars(frame_name)) 
			frame_state.onion(di.v, Value.mkValue(di.t));
	}
	
	public StackFrame (String frame_name, StackFrame static_link, StackFrame dynamic_link, Declarations params, Declarations locals) {
		name = frame_name;
		slink = static_link; dlink = dynamic_link;
		frame_state = new FrameState();
		for (Declaration di : params) 
			frame_state.onion(di.v, Value.mkValue(di.t));
		for (Declaration di : locals) 
			frame_state.onion(di.v, Value.mkValue(di.t));
	}	

	public StackFrame (String frame_name, StackFrame static_link, StackFrame dynamic_link) {
		name = frame_name;
		slink = static_link; dlink = dynamic_link;
		frame_state = new FrameState();
	}

	public Value get(VariableRef var) {
		return frame_state.get(var);
	}

	/* sets the var to the val, if not found in current stack frame
	   it sets the var in slink 
	   To make Clite interpret the program with dynamic typing, switch
	   slink to dlink */
	public StackFrame set(VariableRef var, Value val) {
		if (frame_state.containsKey(var))
			onion(var, val);
		else
			slink.onion(var, val);

		return this;
	}

	/* Retrieves dlink */ 
	public StackFrame get_dlink( ) {
		return dlink;
	}
	
	/* Retrieves slink */
	public StackFrame get_slink( ) {
		return slink;
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

	public void display( ) {
		String tm = "Vars: { ";
		Iterator it = frame_state.entrySet().iterator();
		while(it.hasNext()) {
			tm += it.next() + " ,";
		}

		int sub;
		if (tm.length() > 8)
			sub = 2;	
		else
			sub = 1;
			
		tm = tm.substring(0, tm.length() - sub) + " }";
		System.out.print(tm);
	}
		

	/* displays useful debug information */
	public void debug() {
		String acc = "";
		String frame_name = "stk_frame name: " + name + "\n";

		String tm = "Vars: { ";
		Iterator it = frame_state.entrySet().iterator();
		while(it.hasNext()) {
			tm += it.next() + " ,";
		}

		int sub;
		if (tm.length() > 8)
			sub = 2;	
		else
			sub = 1;
		tm = tm.substring(0, tm.length() - sub) + " }\n";

		if (slink != null)
			acc += "slink: " + slink.get_name() + "\n";
		if (dlink != null)
			acc += "dlink: " + dlink.get_name() + "\n";	
		System.out.println(frame_name + tm + acc);
  	}
}

