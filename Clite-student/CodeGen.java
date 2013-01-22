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
		M(p.body, symtable, assem_out);

		assem_out.writeout();
    }
  
    SymbolTable init_symboltable (Declarations d) {
        SymbolTable symtable = new SymbolTable();
		// We must increment all local #s by one, because #0 is reserved 
		// (I think for the receiver object)
        for (int i=0; i < d.size(); i++) 
			symtable.put(d.get(i).v, new Pair(d.get(i).t, (i + 1)));
        return symtable;
    }

    void M (Statement s, SymbolTable symtable, JasminFile jfile) throws IOException {
        if (s instanceof Skip) { 
			M((Skip)s, symtable, jfile);
			return;
        } if (s instanceof Assignment) { 
			M((Assignment)s, symtable, jfile);
			return;
        } if (s instanceof Conditional) { 
			M((Conditional)s, symtable, jfile);
			return;
        } if (s instanceof Loop) { 
			M((Loop)s, symtable, jfile);
			return;
        } if (s instanceof Block) { 
			M((Block)s, symtable, jfile);
			return;
		}
        throw new IllegalArgumentException("should never reach here");
    }
  
    void M (Skip s, SymbolTable symtable, JasminFile jfile) {
		return;
    }
  
    void M (Assignment a, SymbolTable symtable, JasminFile jfile) throws IOException {
		// write the meaning of the source expression

		M(a.source, symtable, jfile); // this should write the expression 
									  // onto the stack

		Type target_type = symtable.getType((Variable) a.target);
		String store;
		if (target_type.equals(Type.INT)) {
			store = "istore";
		} else if (target_type.equals(Type.FLOAT)) {
			store = "fstore";
		} else {
		// if (target_type.equals(Type.CHAR))
			throw new IllegalArgumentException("should never reach here");
		}
		
		jfile.writeln(store + " " + symtable.getIndex((Variable) a.target));
    }
  
    void M (Block b, SymbolTable symtable, JasminFile jfile) throws IOException {
		// for each statement in the block write the assembly of the statement
        for (Statement s : b.members) {
            M (s, symtable, jfile);
		}
        return;
    }

	/*
  
    void M (Conditional c, State state) {
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

	*/

    void applyBinary (Operator op, JasminFile jfile) throws IOException {
        if (op.val.equals(Operator.INT_PLUS)) {
			jfile.writeln("iadd");
			return;
        } if (op.val.equals(Operator.INT_MINUS)) { 
			jfile.writeln("isub");
			return;
        } if (op.val.equals(Operator.INT_TIMES)) {
			jfile.writeln("imul");
			return;
        } if (op.val.equals(Operator.INT_DIV)) {
			jfile.writeln("idiv");
            return; 
		}
        // student exercise
		/*
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
		*/

		if (op.val.equals(Operator.FLOAT_PLUS)) { 
			jfile.writeln("fadd");
            return;
		} if (op.val.equals(Operator.FLOAT_MINUS)) {
			jfile.writeln("fsub");
            return; 
        } if (op.val.equals(Operator.FLOAT_TIMES)) {
			jfile.writeln("fmul");
            return; 
        } if (op.val.equals(Operator.FLOAT_DIV)) {
			jfile.writeln("fdiv");
            return; 
		}
        throw new IllegalArgumentException("should never reach here");
	
    } 
    
	/* ignoring Unary ops for the time being
    void applyUnary (Operator op, Value v) {
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
    } */ 

    void M (Expression e, SymbolTable symtable, JasminFile jfile) throws IOException { 
        if (e instanceof Value) {
			jfile.writeln("ldc " + (Value)e);
            return; 
		} if (e instanceof Variable) { 
			Variable v = (Variable) e;
			Type v_type = symtable.getType(v);
			String load = "null";
			if (v_type.equals(Type.INT)) {
				load = "iload";
			} else if (v_type.equals(Type.FLOAT)) {
				load = "fload";
			} else {
				throw new IllegalArgumentException("should never reach here");
			}
			jfile.writeln(load + symtable.get(v));
            return;
	    }
        if (e instanceof Binary) {
			// I think applyBinay should handle the work here (I don't know if this is good design?)
            Binary b = (Binary)e;
			M(b.term1, symtable, jfile);
			M(b.term2, symtable, jfile);
            applyBinary (b.op, jfile);
			return;
        }
		/*
        if (e instanceof Unary) {
			// I think applyUnary works similarly to applyBinary
            Unary u = (Unary)e;
            return applyUnary(u.op, M(u.term, state));
        }
		*/
        throw new IllegalArgumentException("should never reach here");
    }

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
		System.out.println("\nReducing into Jasmin Instructions...");
		codegen.M(out, args[0]);
	}	
}
