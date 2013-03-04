import java.util.*;

/* This is my hacky solution to the order problem
   where one cannot statically type check the arguments
   passed to a function without the functions map of params
   having an order
*/

public class FunctionTypeMap {
	
	private class Pair {
		VariableRef key;
		Type value;
		
		public Pair(VariableRef k, Type v) {
			key = k; value = v;
		}
		
		public void update(Type v) {
			value = v;
		}

		public String toString() {
			return "<" + key + ", " + value + ">";
		}
	}

	ArrayList<Pair> members;
		
	public FunctionTypeMap() {
		members = new ArrayList<Pair>();
	}

	public FunctionTypeMap(VariableRef k, Type v) {
		members = new ArrayList<Pair>();

		members.add(new Pair(k, v));
	}

	public void put(VariableRef k, Type v) {
		int index = -1;	
		for (int i=0; i < members.size(); i++) {
			if (members.get(i).key.equals(k))
				index = i;
		}
		if (index == -1)
			members.add(new Pair(k, v));
		else
			members.set(index, new Pair(k,v));
	}

	public Type get(VariableRef k) {
		for (int i=0; i < members.size(); i++) {
			if (members.get(i).equals(k))
				return members.get(i).value; 
		}
		throw new IllegalArgumentException("none existent key");
	}

	public ArrayList<Type> typeArray() {
		ArrayList<Type> ret = new ArrayList<Type>();
		for (Pair pi : members) {
			ret.add(pi.value);
		}
		return ret;
	}

	public String toString() {
		String ret = "[";
		for (Pair pi : members) 
			ret += pi.toString() + ", ";
		if (ret.length() > 2) 
			return ret.substring(0, ret.length()-2) + "]";
		return ret + "]";
	}
}
		
