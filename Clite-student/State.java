public class State {
	
	private Functions text;
	private Stack stack;
	// heap
	// bbs
	// data

	public State (StackFrame stk_frm, Functions funcs) {
		text = funcs; 
		stack = new Stack(stk_frm);
	}

	public State (Functions funcs) {
		text = funcs;
		stack = new Stack( );
	}

	public Declarations get_func_vars(String id) {
		Function f = text.get(id);
		Declarations acc = new Declarations();
		for (Declaration di : f.params)
			acc.add(di);
		for (Declaration di : f.locals)
			acc.add(di);
		return acc;
	}

	/* returns the instruction of the function specified by id */
	public Block get_func_intrs(String id) {
		return text.get(id).body;
	}

	/* pushes a StackFrame onto the stack */
	public State push(StackFrame stk_frm) {
		stack = stack.push(stk_frm);
		return this;
	}

	/* returns StackFrame on top of stack, alters state's stack in process */
	public StackFrame pop( ) {
		return stack.pop();
	}

	public void display ( ) {
		System.out.println("Functions: (not yet implemented in display()) \n");
		System.out.println("Stack: ");
		stack.display();
	}
}
