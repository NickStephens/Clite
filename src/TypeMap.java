import java.util.*;

// public class TypeMap extends HashMap<VariableRef, Type> {
public class TypeMap extends HashMap<VariableRef, Object> { 

// TypeMap is implemented as a Java HashMap.  
// Plus a 'display' method to facilitate experimentation.

   public void display() {
	String tm = "{ ";
	Iterator it = entrySet().iterator();
	while(it.hasNext()) {
		tm += it.next() + " ,";
	}
	tm = tm.substring(0, tm.length() - 2) + " }";
	System.out.println(tm);
  } 
}
