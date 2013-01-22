// Following is the semantics class:
// The meaning M of a Statement is a State
// The meaning M of a Expression is a Value
import java.util.*;
import java.io.*;

public class CodeGen {

	private class Pair {
			
			Type type;
			Integer index;

			public Pair(Type t, Integer i) {
				type = t; index = i;
			}

			public String toString ( ) {
				return "< " + type + ", " + index + ">";
			}

	}
	
	private class SymbolTable extends HashMap<Variable, Pair> { 

		Type getType (Variable v) {
			return get(v).type;
		}

		Integer getIndex (Variable v) {
			return get(v).index;
		}

		void display ( ) {
			String tm = "{ ";
			Iterator it = entrySet().iterator();
			while(it.hasNext()) {
				tm += it.next() + " ,";
			}
			tm = tm.substring(0, tm.length() - 2) + " }";
			System.out.println(tm);
		  } 
	
	}

	void M (Program p, String filename) throws IOException {

		// New class required symbol table to map variable names to numbers
		SymbolTable symtable = init_symboltable(p.decpart);
		// The constructor for SymbolTable must tie numbers to symbols, (ie <local_0, a>, <local_1, b> ...) Parameters will have to be accounted for

		// Intiliaze file to write to here.
		// Call the M (p.body, initialState(p.decpart));

		String jfile = filename.substring(0,filename.length()-4);

		JasminFile assem_out = new JasminFile(jfile + ".j");

		assem_out.JVMBoiler();

		// allocate write the function type signature, then allocates stack and local space
		assem_out.preamble(p.decpart);

		// text goes through the body and writes the instructions
		// write loosely corresponds to M
		// M(p.body, symtable, assem_out);

		assem_out.close();
    }
  
    SymbolTable init_symboltable (Declarations d) {
        SymbolTable symtable = new SymbolTable();
        for (int i=0; i < d.size(); i++) 
			symtable.put(d.get(i).v, new Pair(d.get(i).t, i));
        return symtable;
    }

    void M (Statement s, SymbolTable symtable, JasminFile jfile) {
        if (s instanceof Skip) return M((Skip)s, symtable, jfile);
        if (s instanceof Assignment)  return M((Assignment)s, symtable, jfile);
        if (s instanceof Conditional)  return M((Conditional)s, symtable, jfile);
        if (s instanceof Loop)  return M((Loop)s, symtable, jfile);
        if (s instanceof Block)  return M((Block)s, symtable, jfile);
        throw new IllegalArgumentException("should never reach here");
    }
  
    void M (Skip s, SymbolTable symtable, JasminFile jfile) {
		return;
    }
  
    void M (Assignment a, SymbolTable symtable, JasminFile jfile) {
		// write the meaning of the source expression

		M(a.source, symtable, jfile); // this should write the expression 
									  // onto the stack


		// assess_out.writeln("xstore " + symtable.get(a.target));
    }
  
    State M (Block b, State state) {
		// for each statement in the block write the assembly of the statement
        for (Statement s : b.members) {
            state = M (s, state);
	}
        return state;
    }
  
    State M (Conditional c, State state) {
		// translate conditional
		// 		translate the bodies of each conditional
        if (M(c.test, state).boolValue( ))
            return M (c.thenbranch, state);
        else
            return M (c.elsebranch, state);
    }
  
    State M (Loop l, State state) {
		// translate the conditional
		//		translate the body
        if (M (l.test, state).boolValue( ))
            return M(l, M (l.body, state));
        else return state;
    }

    Value applyBinary (Operator op, Value v1, Value v2) {
		//for each one of these:
		//	push the values of v1 and v2 (which I think are stored as strings)
        StaticTypeCheck.check( ! v1.isUndef( ) && ! v2.isUndef( ),
               "reference to undef value");
        if (op.val.equals(Operator.INT_PLUS)) 
            return new IntValue(v1.intValue( ) + v2.intValue( ));
			// asses_out.writeln("bipush " + v1);
			// asses_out.writeln("bipush " + v2);
			// asses_out.writeln("iadd");
        if (op.val.equals(Operator.INT_MINUS)) 
            return new IntValue(v1.intValue( ) - v2.intValue( ));
        if (op.val.equals(Operator.INT_TIMES)) 
            return new IntValue(v1.intValue( ) * v2.intValue( ));
        if (op.val.equals(Operator.INT_DIV)) 
            return new IntValue(v1.intValue( ) / v2.intValue( ));
        // student exercise
	if (op.val.equals(Operator.INT_LT))
	    return new BoolValue(v1.intValue() < v2.intValue());
	if (op.val.equals(Operator.INT_GT))
	    return new BoolValue(v1.intValue() > v2.intValue());
	if (op.val.equals(Operator.INT_EQ))
	    return new BoolValue(v1.intValue() == v2.intValue());
	if (op.val.equals(Operator.INT_NE))
	    return new BoolValue(v1.intValue() != v2.intValue());

	if (op.val.equals(Operator.FLOAT_LT))
	    return new BoolValue(v1.floatValue() < v2.floatValue());
	if (op.val.equals(Operator.FLOAT_GT))
	    return new BoolValue(v1.floatValue() > v2.floatValue());
	if (op.val.equals(Operator.FLOAT_EQ))
	    return new BoolValue(v1.floatValue() == v2.floatValue());
	if (op.val.equals(Operator.FLOAT_NE))
	    return new BoolValue(v1.floatValue() != v2.floatValue());

	if (op.val.equals(Operator.FLOAT_PLUS)) 
            return new FloatValue(v1.floatValue( ) + v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_MINUS)) 
            return new FloatValue(v1.floatValue( ) - v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_TIMES)) 
            return new FloatValue(v1.floatValue( ) * v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_DIV)) 
            return new FloatValue(v1.floatValue( ) / v2.floatValue( ));
        throw new IllegalArgumentException("should never reach here");
	
    } 
    
    Value applyUnary (Operator op, Value v) {
		// push the value onto the stack
		// asses_out.writeln("bipush " + v)
		// asses_out.writeln("i2f") ; or something similar

        StaticTypeCheck.check( ! v.isUndef( ),
               "reference to undef value");
        if (op.val.equals(Operator.NOT))
            return new BoolValue(!v.boolValue( ));
        else if (op.val.equals(Operator.INT_NEG))
            return new IntValue(-v.intValue( ));
        else if (op.val.equals(Operator.FLOAT_NEG))
            return new FloatValue(-v.floatValue( ));
        else if (op.val.equals(Operator.I2F))
            return new FloatValue((float)(v.intValue( ))); 
        else if (op.val.equals(Operator.F2I))
            return new IntValue((int)(v.floatValue( )));
        else if (op.val.equals(Operator.C2I))
            return new IntValue((int)(v.charValue( )));
        else if (op.val.equals(Operator.I2C))
            return new CharValue((char)(v.intValue( )));
        throw new IllegalArgumentException("should never reach here");
    } 

    Value M (Expression e, State state) {
        if (e instanceof Value) 
			// assess_out.writeln("bipush " + (Value)e);
            return (Value)e;
		if (e instanceof ArrayRef) {
			// xload symtable.get(a.id)
	    	ArrayRef a = (ArrayRef) e;
	    	ArrayRef key = new ArrayRef(a.id, M(a.index, state));
	    	return (Value)(state.get(key));
		}
        if (e instanceof VariableRef) { 
			// xload symtable.get(a.id)
            return (Value)(state.get(e));
	    }
        if (e instanceof Binary) {
			// I think applyBinay should handle the work here (I don't know if this is good design?)
            Binary b = (Binary)e;
            return applyBinary (b.op, 
                                M(b.term1, state), M(b.term2, state));
        }
        if (e instanceof Unary) {
			// I think applyUnary works similarly to applyBinary
            Unary u = (Unary)e;
            return applyUnary(u.op, M(u.term, state));
        }
        throw new IllegalArgumentException("should never reach here");
    }

    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();    // student exercise
        System.out.println("\nBegin type checking...");
        System.out.println("Type map:");
        TypeMap map = StaticTypeCheck.typing(prog.decpart);
        map.display();    // student exercise
        StaticTypeCheck.V(prog);
        Program out = TypeTransformer.T(prog, map);
        System.out.println("Output AST");
        out.display();    // student exercise
        CodeGen codegen = new CodeGen( );
		//codegen.M(out, args[0])
        State state = codegen.M(out);
        System.out.println("Final State");
        state.display( );  // student exercise
    }
	*/

	public static void main(String args[]) throws IOException {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();    // student exercise
        System.out.println("\nBegin type checking...");
        System.out.println("Type map:");
        TypeMap map = StaticTypeCheck.typing(prog.decpart);
        map.display();    // student exercise
        StaticTypeCheck.V(prog);
        Program out = TypeTransformer.T(prog, map);
        System.out.println("Output AST");
        out.display();    // student exercise
        CodeGen codegen = new CodeGen( );
		codegen.M(out, args[0]);
	}	
}
