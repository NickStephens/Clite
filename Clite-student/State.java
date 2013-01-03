public class State {
	
	private Functions text;
	private Stack stack;
	// Heap
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

	/* returns the instruction of the function specified by id */
	public Block get_func_intrs(String id) {
		return text.get(id).body;
	}

	/* pushes a StackFrame onto the stack */
	public State push(StackFrame stk_frm) {
		stack.push(stk_frm);
		return this;
	}

	/* returns StackFrame on top of stack, alters state's stack in process */
	public StackFrame pop( ) {
		return stack.pop();
	}

	public void display ( ) {
		System.out.println("Functions: (not yet implemented in display()) \n");
		stack.display();
	}
}
