public class Stack {
	
	private StackFrame head;
	private Stack body;

	public Stack (StackFrame stk_frm) {
		head = stk_frm;
		body = null;
	}

	public Stack (StackFrame stk_frm, Stack stk) {
		head = stk_frm;
		body = stk;
	}

	/* Returns a reference to head */
	public StackFrame get ( ) {
		return head;
	}

	/* pops the current head off the stack
     	   returns a reference to body */
	public Stack pop () {
		return body; 
	}

	/* pushes a new StackFrame onto the stack
	   returns a reference to a new stack with the push */
	public Stack push (StackFrame stk_frm) {
		return new Stack ( stk_frm, this);
	}

	public void display() {
		if (body == null) 
			head.display();
		else {
			head.display();
			body.display();
		}
	}
}
