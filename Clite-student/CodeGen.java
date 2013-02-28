// Following is the semantics class:
// The meaning M of a Statement is a State
// The meaning M of a Expression is a Value
import java.util.*;
import java.io.*;

public class CodeGen {

	private int branch_cnt = 0;
	private SymbolTable current_table;

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
	
	// The symbol table class allows us to map Clite Identifiers
	// to Jasmin numeric storage
	private class SymbolTable extends HashMap<Variable, Pair> { 

		Type getType (Variable v) {
			return get(v).type;
		}

		Integer getIndex (Variable v) {
			return get(v).index;
		}

		boolean contains_symbol(Variable symbol) {
			return containsKey(symbol);
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

		HashMap<String, SymbolTable> symtable_hash = new HashMap<String, SymbolTable>();
		// New class required symbol table to map variable names to numbers
		init_symboltables(symtable_hash, p.functions);
		// The constructor for SymbolTable must tie numbers to symbols, (ie <local_0, a>, <local_1, b> ...) Parameters will have to be accounted for

		// Intiliaze file to write to here.
		// Call the M (p.body, initialState(p.decpart));

		// truncating .cpp
		String jfile = filename.substring(0,filename.length()-4);

		JasminFile assem_out = new JasminFile(jfile + ".j");

		assem_out.write_class_sig();
		assem_out.write_globals(p.globals);
		assem_out.JVMBoiler();

		for (Function f : p.functions) {
			M (f, symtable_hash, assem_out);
		}
		
		assem_out.close();

    }
  
    void init_symboltables (HashMap<String, SymbolTable> symtable_hash, Functions f) {
	for (Function fi : f) {
		symtable_hash.put(fi.id, init_symboltable(fi.params, fi.locals));
	}

    }
	
    SymbolTable init_symboltable (Declarations params, Declarations locals) {
		// We must increment all local #s by one, because #0 is reserved 
		// (I think for the receiver object)
	SymbolTable symtable = new SymbolTable();
	int i = 0;
        while (i < params.size()) { 
			symtable.put(params.get(i).v, new Pair(params.get(i).t, (i + 1)));
			i++;
	}
	int j = 0;
	while (i < (params.size() + locals.size())) {
			symtable.put(locals.get(j).v, new Pair(locals.get(j).t, (i + 1)));
			i++;
			j++;
	}
        return symtable;
    }

    void M (Function f, HashMap<String, SymbolTable> symtable_hash, JasminFile jfile) throws IOException {
	if (! f.id.equals("main")) {	
		jfile.function_preamble(f.id, f.t, f.params, f.locals);
		M (f.body, symtable_hash.get(f.id), jfile);	
		jfile.function_writeout(f.t);	
	} else {
		jfile.main_preamble(f.locals);
		M (f.body, symtable_hash.get(f.id), jfile);	
		jfile.main_writeout();
	}
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
	} if (s instanceof Print) {
			M((Print)s, symtable, jfile);
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
		if (target_type.equals(Type.INT) || target_type.equals(Type.CHAR) || target_type.equals(Type.BOOL)) {
			store = "istore";
		} else if (target_type.equals(Type.FLOAT)) {
			store = "fstore";
		} else {
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

    void M (Conditional c, SymbolTable symtable, JasminFile jfile) throws IOException {
		// translate conditional
		// 		translate the bodies of each conditional
		//
	
		// the conditional will result in either 1 (true) or 0 (false)
		// being pushed to the stack
	int current_branch_cnt = branch_cnt;
	branch_cnt++;

	M(c.test, symtable, jfile);
	// We can expect a 0 or 1 to be here
	jfile.writeln("ifne TRUE" + current_branch_cnt);

	jfile.writeln("goto FALSE" + current_branch_cnt);

	jfile.writeln();

	jfile.writeln("TRUE" + current_branch_cnt + ":");
	M(c.thenbranch, symtable, jfile);	
	jfile.writeln("goto COMPLETE" + current_branch_cnt);
	
	jfile.writeln();

	jfile.writeln("FALSE" + current_branch_cnt + ":");
	M(c.elsebranch, symtable, jfile);

	jfile.writeln();
	
	jfile.writeln("COMPLETE" + current_branch_cnt + ":");
	

	/* from the days where Clite was just an interpreter
        if (M(c.test, state).boolValue( ))
            return M (c.thenbranch, state);
        else
            return M (c.elsebranch, state);
	*/
    }
   
    void M (Loop l, SymbolTable symtable, JasminFile jfile) throws IOException {
		// translate the conditional
		//		translate the body

	int current_branch_cnt = branch_cnt;
	branch_cnt++;

	jfile.writeln();

	jfile.writeln("LOOPTEST" + current_branch_cnt + ":");
	M(l.test, symtable, jfile);
	
	jfile.writeln();
	jfile.writeln("ifne LOOPBODY" + current_branch_cnt);
	jfile.writeln("goto LOOPEXIT" + current_branch_cnt); 

	jfile.writeln("LOOPBODY" + current_branch_cnt + ":");
	M(l.body, symtable, jfile);
	jfile.writeln("goto LOOPTEST" + current_branch_cnt);

	jfile.writeln("LOOPEXIT" + current_branch_cnt + ":");
	
	/*
        if (M (l.test, state).boolValue( ))
            return M(l, M (l.body, state));
        else return state;
	*/
    }

    void M (Print p, SymbolTable symtable, JasminFile jfile) throws IOException {
	jfile.writeln("getstatic java/lang/System/out Ljava/io/PrintStream;");

	M(p.to_print, symtable, jfile);

	String print_type;
	Type e_type = typeOf(p.to_print, symtable);
	
	if (e_type.equals(Type.FLOAT)) 
		print_type = "F";
	else if (e_type.equals(Type.INT))
		print_type = "I";
	else if (e_type.equals(Type.CHAR)) 
		print_type = "C";
	else //It's a Bool
		print_type = "Z";
	
	jfile.writeln("invokevirtual java/io/PrintStream/println(" + print_type + ")V");
    }

    private Type typeOf(Expression e, SymbolTable sym) {
        if (e instanceof Value) return ((Value)e).type;
        if (e instanceof Variable) {
            Variable v = (Variable)e;
            return (Type) sym.get(v).type;
	}
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            if (b.op.ArithmeticOp( ))
                if (typeOf(b.term1,sym)== Type.FLOAT)
                    return (Type.FLOAT);
                else return (Type.INT);
            if (b.op.RelationalOp( ) || b.op.BooleanOp( )) 
                return (Type.BOOL);
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            if (u.op.NotOp( ))        return (Type.BOOL);
            else if (u.op.NegateOp( )) return typeOf(u.term,sym);
            else if (u.op.intOp( ))    return (Type.INT);
            else if (u.op.floatOp( )) return (Type.FLOAT);
            else if (u.op.charOp( ))  return (Type.CHAR);
        }
        throw new IllegalArgumentException("should never reach here");
    }

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
	if (op.val.equals(Operator.INT_LT)) {
		jfile.write("\tif_icmplt ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.INT_GT)) {
		jfile.write("\tif_icmpgt ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.INT_EQ)) {
		jfile.write("\tif_icmpeq ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.INT_NE)) {
		jfile.write("\tif_icmpne ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.INT_GE)) {
		jfile.write("\tif_icmpge ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return; 
	} if (op.val.equals(Operator.INT_LE)) {
		jfile.write("\tif_icmple ");
		jfile.write_relop_body(branch_cnt++);
		branch_cnt++;
		return;
	}
	// inconsistencies among float and int operations

	/* fcmp? guide
	> x
	> y
	> fcmpl
	-- 1 iff y < x
	-- 0 iff y == 0
	-- -1 iff y > x

	> x
	> y
	> fcmpg
	-- 1 iff y > x
	-- 0 iff y == 0
	-- -1 iff y < x
	*/


	if (op.val.equals(Operator.FLOAT_LT)) {
		jfile.writeln("fcmpl");
		// 1 if its true
		// -1 if its false
		// 0 it its false

		jfile.writeln("bipush 0");
		jfile.write("\tif_icmplt ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.FLOAT_GT))  {
		jfile.writeln("fcmpg");
		// 1 if its true
		// -1 if its false
		// 0 it its false

		jfile.writeln("bipush 0");
		jfile.write("\tif_icmplt ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.FLOAT_EQ)) {
		jfile.writeln("fcmpg");
		// 1 if its true
		// -1 if its false
		// 0 it its false

		jfile.writeln("bipush 0");
		jfile.write("\tif_icmpeq ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.FLOAT_NE)) {
		jfile.writeln("fcmpg");
		// 1 if its true
		// -1 if its false
		// 0 it its false

		jfile.writeln("bipush 0");
		jfile.write("\tif_icmpne ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.FLOAT_GE)) {
		jfile.writeln("fcmpg");
		// 1 if its true
		// -1 if its false
		// 0 it its false

		jfile.writeln("bipush 0");
		jfile.write("\tif_icmpge ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.FLOAT_LE)) {
		jfile.writeln("fcmpg");
		// 1 if its true
		// -1 if its false
		// 0 it its false

		jfile.writeln("bipush 0");
		jfile.write("\tif_icmple ");
		jfile.write_relop_body(branch_cnt);
		branch_cnt++;
		return;
	} if (op.val.equals(Operator.FLOAT_PLUS)) { 
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
	// these are some boolean operators which Jasmin has no intructions for
	// It turns out it's simply more efficient to operate on 32bit ints!
	// guess it makes sense that its too big of a hassle address a single bit

	// These also rely on the fact that we're on storing booleans as:
	// True = the Int 1
	// False = the Int 0
	// As Sherri says, the only meaning we're ever ascribing to these
	// types of things lies in the encoding

	//at this point two ints should be on the stack which are either 1 or 0
	if (op.val.equals(Operator.OR)) {
		jfile.writeln("ior"); 
		return;
	} if (op.val.equals(Operator.AND)) {
		jfile.writeln("iand");
		return;
	} if (op.val.equals(Operator.NOT)) {
	//	jfile.writeln("negate");
		return;
	}
			
        throw new IllegalArgumentException("should never reach here");
	
    } 
    
    void applyUnary (Operator op, JasminFile jfile) throws IOException {
		// push the value onto the stack
		// asses_out.writeln("bipush " + v)
		// asses_out.writeln("i2f") ; or something similar

        if (op.val.equals(Operator.NOT)) {
			//jfile.writeln("ineg");
			//This will probably just be ineg, but I have not yet confirmed the boolean integer vals
			return;
        } else if (op.val.equals(Operator.INT_NEG)) {
			jfile.writeln("ineg");
			return;
        } else if (op.val.equals(Operator.FLOAT_NEG)) {
			jfile.writeln("fneg");
			return;
        } else if (op.val.equals(Operator.I2F)) {
			jfile.writeln("i2f");
			return;
        } else if (op.val.equals(Operator.F2I)) {
			jfile.writeln("f2i");
			return;
        } else if (op.val.equals(Operator.C2I)) {
			return; // do nothing, maybe mark this in the symbol table or something, it will affect printing.
        } else if (op.val.equals(Operator.I2C)) {
			return; // do nothing
		}
        throw new IllegalArgumentException("should never reach here");
    } 

    void M (Expression e, SymbolTable symtable, JasminFile jfile) throws IOException { 
        if (e instanceof Value) {
			if (e instanceof IntValue || e instanceof FloatValue) {
				jfile.writeln("ldc " + (Value)e);
            	return; 
			} if (e instanceof BoolValue) {
				BoolValue b = (BoolValue) e;
				jfile.writeln("ldc " + b.intValue());
				return;
			} if (e instanceof CharValue) {
				CharValue c = (CharValue) e;
				jfile.writeln("ldc " + (int)c.charValue());
				return;
			}
		} if (e instanceof Variable) { 
			Variable v = (Variable) e;
			Type v_type = symtable.getType(v);
			String load = "null";
			if (v_type.equals(Type.INT) || v_type.equals(Type.CHAR)
				|| v_type.equals(Type.BOOL)) {
				load = "iload";
			} else if (v_type.equals(Type.FLOAT)) {
				load = "fload";
			} else {
				throw new IllegalArgumentException("should never reach here");
			}
			jfile.writeln(load + " " +  symtable.getIndex(v));
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
        if (e instanceof Unary) {
            Unary u = (Unary)e;
			M(u.term, symtable, jfile);
            applyUnary(u.op, jfile);
			return;
        }
        throw new IllegalArgumentException("should never reach here");
    }

	public static void main(String args[]) throws IOException, InterruptedException {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();    // student exercise
        System.out.println("\nBegin type checking...");
        System.out.println("Type map:");
        TypeMap map = StaticTypeCheck.typing(prog.globals, prog.functions);
        map.display();    // student exercise
        StaticTypeCheck.V(prog);
        Program out = TypeTransformer.T(prog, map);
        System.out.println("Output AST");
        out.display();    // student exercise
        CodeGen codegen = new CodeGen( );
		System.out.println("\nReducing into Jasmin Instructions...");

		// Stupid shit for running a command through Java
		codegen.M(out, args[0]);

		System.out.println();

		System.out.println("Assembly Produced:");
		Process p = Runtime.getRuntime().exec("cat " +  args[0].substring(0, (args[0].length() - 4)) + ".j");
		p.waitFor();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = r.readLine();
		while(line != null) {
			System.out.println(line);
			line = r.readLine();
		}
	}	
}
