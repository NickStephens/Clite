public class Print extends Statement {
	
	Expression to_print;

	protected Print(Expression expr) {
		to_print = expr;
	}
}
