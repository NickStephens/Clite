public class Stack {
	
	private StackFrame head;
	private Stack body;

	public Stack ( ) {
		head = null;
		head = null;
	}
	
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
	public StackFrame pop () {
		// getting the proper frame
		StackFrame ret = head; 

		// setting up the stack
		if (body == null) { 
			head = null;
			this.body = null;
		} else {
			head = body.pop();
			this.body = this.body.body;
		}

		return ret;
	}	

	/* pushes a new StackFrame onto the stack
	   returns a reference to the stack */
	public Stack push (StackFrame stk_frm) {
		body = this;
		head = stk_frm;
		return  this;
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
