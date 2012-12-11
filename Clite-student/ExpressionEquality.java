public class ExpressionEquality {
	public static void main(String args[]) {
		ArrayRef ar = new ArrayRef("a", new Binary(new Operator(Operator.INT_PLUS), new IntValue(0), new IntValue(0)));
		ArrayRef br = new ArrayRef("a", new IntValue(0));

		State state = new State();
		Variable some = new Variable("a");
		ArrayRef key = new ArrayRef(some.toString(), new IntValue(0));
			state.put(key, new IntValue());

	//	ArrayRef anew = new ArrayRef(ar.id, Semantics.M(ar.index, 
		System.out.println("Variable(\"a\").equals(\"a\")? " + some.toString().equals("a"));
		Semantics semantics = new Semantics();
	
		ArrayRef lifted_ar = new ArrayRef(ar.id, (IntValue) semantics.M(ar.index, state));

		IntValue zero = new IntValue(0);

		System.out.println("Semantics.M(Binary(0+0)) ?= IntValue(0) " + zero.equals(semantics.M(ar.index, state)));

		System.out.println("key ?= lifted_ar " + lifted_ar.equals(key));
		System.out.println("lifted_ar ?= key " + key.equals(lifted_ar));

		// lifted_ar = key
		System.out.println("Does lifted_ar's contents equal key's contents? "
			+ (key.id.equals(lifted_ar.id) && key.index.equals(lifted_ar.index)));

		System.out.println();
		System.out.println("state.get(key) " + state.get(key));
		System.out.println("state.get(lifted_ar) " + state.get(lifted_ar));
		
		ArrayRef key_reference = key;
		ArrayRef key_extracted = new ArrayRef(key.id, key.index);

		System.out.println();

		System.out.println("key.id ?= key_extracted.id " + key_extracted.id.equals(key.id));
		System.out.println("key.index ?= key_extracted.index " + key_extracted.index.equals(key.index));
		System.out.println("key ?= key_extracted " + key_extracted.equals(key));

		System.out.println();

		System.out.println("key_ref " + state.containsKey(key_reference));
		System.out.println("key_extracted " + state.containsKey(key_extracted));
		System.out.println("lifted_ar ?e state " + state.containsKey(lifted_ar));
		}
	}
			
