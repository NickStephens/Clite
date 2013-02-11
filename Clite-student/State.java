public class State {
	
	private Functions text;
	private Function current_func;
	private Stack stack;
	private StackFrame data;
	// heap
	// bbs

	public State (Functions funcs, StackFrame globals) {
		text = funcs; 
		current_func = null;
		data = globals;
		stack = new Stack();
	}

	/* returns the top of the stack without popping it */
	public StackFrame get_stack_top( ) {
		return stack.get_top();
	}

	/* returns the data segment of state */
	public StackFrame get_dataseg( ) {
		return data; 
	}

	/* returns currents_func */
	public Function get_current_func( ) {
		return current_func;
	}

	/* Sets the current function to func */
	public State set_current_func(Function func) {
		current_func = func;
		return this;
	}

	/* returns the parameters and locals of the function specified by
	   id */
	public Declarations get_func_vars(String id) {
		Function f = text.get(id);
		Declarations acc = new Declarations();
		for (Declaration di : f.params)
			acc.add(di);
		for (Declaration di : f.locals)
			acc.add(di);
		return acc;
	}

	/* Gets the closes variable matching var's value */
	public Value get(VariableRef var) {
		return stack.get_top().get(var);
	}

	/* Sets the closest variable matching var to val */
	public State set(VariableRef var, Value val) {
		StackFrame top = stack.get_top();
		top.set(var, val);

		return this;
	}

	/* returns the current functions parameters */
	public Declarations get_params( ) {
		return current_func.params;
	}

	/* returns the instruction of the function specified by id */
	public Block get_instrs( ) {
		return current_func.body; 
	}

	/* pushes a StackFrame onto the stack 
	   and sets the current function to the owner of the stack frame */
	public State push(StackFrame stk_frm) {
		stack = stack.push(stk_frm);
		current_func = text.get(stk_frm.get_name());
		return this;
	}

	/* returns StackFrame on top of stack, alters state's stack in process */
	public StackFrame pop( ) {
		StackFrame below = stack.pop();
		if (!stack.isEmpty())
			current_func = text.get(below.get_dlink().get_name());
		else
			current_func = null;
		return below;
	}

	public void display( ) {
		System.out.print("\tGlobals: ");
		data.display( );	
		System.out.print("\n\tMain: ");
		stack.display( );
		System.out.print("\n");
	}

	/* prints useful debug information */
	public void debug(String tabs) {
		System.out.println(tabs + "[DEBUG] Functions: (not yet implemented in display()) \n");
		System.out.println(tabs + "[DEBUG] Globals: ");
		data.debug();
		System.out.println(tabs + "[DEBUG] Stack: ");
		stack.debug();
	}
}
