public class FunctionMap {
	private Type t;
	private TypeMap params;

	public FunctionMap(Type t, TypeMap params) {
		this.t = t; this.params = params;
	}

	public Type getType() {
		return t;
	}

	public TypeMap getParams() {
		return params;
	}

	public String toString() {
		return " ," + t + " ," + params + ">";
	}
}
