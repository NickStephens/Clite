// Following is the semantics class:
// The meaning M of a Statement is a State
// The meaning M of a Expression is a Value

public class CodeGen {

    State M (Program p) { 

	// void M (Program p, String filename) 

		// New class required symbol table to map variable names to numbers
		// SymbolTable symtable = new SymbolTable(p.decpart);
		// The constructor for SymbolTable must tie numbers to symbols, (ie <local_0, a>, <local_1, b> ...) Parameters will have to be accounted for

		// Intiliaze file to write to here.
		// Call the M (p.body, initialState(p.decpart));

		// jfile = some operation on the filename removing the '.cpp' extension

		// File assem_out = new JasminFile(jfile + ".j")

		// assem_out.JVMBoiler()

		// allocate write the function type signature, then allocates stack and local space
		// assem_out.preamble(p.decpart);

		// text goes through the body and writes the instructions
		// write loosely corresponds to M
		// write(p.body, symtable, assem_out);

        return M (p.body, initialState(p.decpart)); 
    }
  
    State initialState (Declarations d) {
    	// This should be modified for arrayRefs
        State state = new State();
        Value intUndef = new IntValue();
        for (Declaration decl : d)
	    if (decl instanceof ArrayDecl) {
	    	ArrayDecl adecl = (ArrayDecl) decl;
	    	for (int i=0; i<(adecl.size.intValue());i++)
			state.put(new ArrayRef(adecl.v.toString(), new IntValue(i)), Value.mkValue(adecl.t));
		}
	    else 
            	state.put(decl.v, Value.mkValue(decl.t));
        return state;
    }
  
    State M (Statement s, State state) {
        if (s instanceof Skip) return M((Skip)s, state);
        if (s instanceof Assignment)  return M((Assignment)s, state);
        if (s instanceof Conditional)  return M((Conditional)s, state);
        if (s instanceof Loop)  return M((Loop)s, state);
        if (s instanceof Block)  return M((Block)s, state);
        throw new IllegalArgumentException("should never reach here");
    }
  
    State M (Skip s, State state) {
		// return
        return state;
    }
  
    State M (Assignment a, State state) {
		// write the meaning of the source expression
		// M(assess_out, a.source, symtable)
		// assess_out.writeln("xstore " + symtable.get(a.target));

    	if (a.target instanceof ArrayRef) {
		ArrayRef b = (ArrayRef) a.target;
		ArrayRef r = new ArrayRef(b.id, M(b.index, state));
        	State st = state.onion(r, M (a.source, state));
		return st;
	}
	State st = state.onion(a.target, M(a.source, state));
	return st;
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
}
