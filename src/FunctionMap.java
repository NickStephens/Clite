public class FunctionMap {
	private Type t;
	private FunctionTypeMap params;

	public FunctionMap(Type t, FunctionTypeMap params) {
		this.t = t; this.params = params;
	}

	public Type getType() {
		return t;
	}

	public FunctionTypeMap getParams() {
		return params;
	}

	public String toString() {
		return " ," + t + " ," + params + ">";
	}
}
