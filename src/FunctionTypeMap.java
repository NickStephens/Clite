import java.util.*;

/* This is my hacky solution to the order problem
   where one cannot statically type check the arguments
   passed to a function without the functions map of params
   having an order
*/

public class FunctionTypeMap {
	
	private class Pair {
		VariableRef key;
		Object value;
		
		public Pair(VariableRef k, Object v) {
			key = k; value = v;
		}
		
		public void update(Object v) {
			value = v;
		}
	}

	ArrayList<Pair> members;
		
	public FunctionTypeMap() {
		members = new ArrayList<Pair>();
	}

	public FunctionTypeMap(VariableRef k, Object v) {
		members = new ArrayList<Pair>();

		members.add(new Pair(k, v));
	}

	public void put(VariableRef k, Object v) {
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

	public Object get(VariableRef k) {
		for (int i=0; i < members.size(); i++) {
			if (members.get(i).equals(k))
				return members.get(i).value; 
		}
		throw new IllegalArgumentException("none existent key");
	}
}
		
