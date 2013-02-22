// Abstract syntax for the language C++Lite,
// exactly as it appears in Appendix B.
import java.util.*;

class Program {
    // Program = Declarations decpart ; Block body

    private static final String TAB = "  ";	

    Declarations decpart;
    Block body;

    Program (Declarations d, Block b) {
        decpart = d;
        body = b;
    }
    
    void display() {
	String prefix = TAB + "Declarations:\n";
	String declars = TAB + TAB + "{";
		for (int i=0; i<decpart.size();i++) {
			declars += decpart.get(i) + ", ";
		}
	declars = declars.substring(0,declars.length()-2) + "}\n";
	System.out.print(prefix + declars + inner_display( TAB, TAB, body));
    } 

    String inner_display(String spc, String spcing, Object node) {
	if (node instanceof Unary) {
		Unary u_node = (Unary) node;
		String prefix = spcing + "Unary:\n";
		String branch1 = inner_display(spc, spc + spcing, u_node.op);
		String branch2 = inner_display(spc, spc + spcing, u_node.term);
		return prefix + branch1 + branch2; 
		
	} if (node instanceof Binary) {
		Binary b_node = (Binary) node;
		String prefix = spcing + "Binary:\n";
		String branch1 = inner_display(spc, spc + spcing, b_node.op);
		String branch2 = inner_display(spc, spc + spcing, b_node.term1);
		String branch3 = inner_display(spc, spc + spcing, b_node.term2);
		return prefix + branch1 + branch2 + branch3;
	} if (node instanceof Assignment) {
		Assignment a_node = (Assignment) node;
		String prefix = spcing + "Assignment:\n";
		String branch1 = inner_display(spc, spc + spcing, a_node.target);
		String branch2 = inner_display(spc, spc + spcing, a_node.source);
		return prefix + branch1 + branch2;	
	} if (node instanceof Block) {
		Block blk_node = (Block) node;
		String prefix = spcing + "Block:\n";
		String blk_string = "";
		for (int i=0; i<blk_node.members.size(); i++) {
			blk_string += inner_display(spc, spc + spcing, blk_node.members.get(i));
		}
		return prefix + blk_string;
	} if (node instanceof Conditional) {
		Conditional con_node = (Conditional) node;
		String prefix = spcing + "Conditional:\n";
		String test = inner_display(spc, spc + spcing, con_node.test);
		String then_branch = inner_display(spc, spc + spcing, con_node.thenbranch);
		String else_branch = inner_display(spc, spc + spcing, con_node.elsebranch);
		return prefix + test + then_branch + else_branch;
	} if (node instanceof Loop) {
		Loop l_node = (Loop) node;
		String prefix = spcing + "Loop:\n";
		String test = inner_display(spc, spc + spcing, l_node.test);
		String body = inner_display(spc, spc + spcing, l_node.body); 	
		return prefix + test + body;
	} if (node instanceof Print) {
		Print p_node = (Print) node;
		String prefix = spcing + "Print:\n";
		String expr = inner_display(spc, spc + spcing, p_node.to_print);
		return prefix + expr;

	} else {
		String prefix = spcing + (node.getClass() + "").substring(6) + ": ";
		String value = node + "\n"; 
		return prefix + value; 
	}
    }
}

class Declarations extends ArrayList<Declaration> {
    // Declarations = Declaration*
    // (a list of declarations d1, d2, ..., dn)

}

abstract class Declaration {
// Declaration = VariableDecl | ArrayDecl
	Variable v;
	Type t;

}

class VariableDecl extends Declaration {
// VariableDecl = Variable V; Type t

    VariableDecl (Variable var, Type type) {
        super.v = var; super.t = type;
    } // declaration */

    public String toString( ) {
	return "<" + v + ", " + t + ">";
    }
}


class ArrayDecl extends Declaration {
// ArrayDecl = Variable v; Type t; Integer size

	IntValue size;
	
	ArrayDecl (Variable var, Type type, IntValue alloc) {
	   super.v = var; super.t = type; size = alloc;
	}	
	
	public String toString( ) {
	   return "<" + v + ", " + t + ", size: " + size + ">";
	}
}

class Type {
    // Type = int | bool | char | float 
    final static Type INT = new Type("int");
    final static Type BOOL = new Type("bool");
    final static Type CHAR = new Type("char");
    final static Type FLOAT = new Type("float");
    // final static Type UNDEFINED = new Type("undef");
    
    private String id;

    private Type (String t) { id = t; }

    public String toString ( ) { return id; }
}

abstract class Statement {
    // Statement = Skip | Block | Assignment | Conditional | Loop

}

class Skip extends Statement {
}

class Block extends Statement {
    // Block = Statement*
    //         (a Vector of members)
    public ArrayList<Statement> members = new ArrayList<Statement>();
}

class Assignment extends Statement {
    // Assignment = VariableRef target; Expression source
    VariableRef target;
    Expression source;

    Assignment (VariableRef t, Expression e) {
        target = t;
        source = e;
    }
}

class Conditional extends Statement {
// Conditional = Expression test; Statement thenbranch, elsebranch
    Expression test;
    Statement thenbranch, elsebranch;
    // elsebranch == null means "if... then"
    
    Conditional (Expression t, Statement tp) {
        test = t; thenbranch = tp; elsebranch = new Skip( );
    }
    
    Conditional (Expression t, Statement tp, Statement ep) {
        test = t; thenbranch = tp; elsebranch = ep;
    }
    
}

class Loop extends Statement {
// Loop = Expression test; Statement body
    Expression test;
    Statement body;

    Loop (Expression t, Statement b) {
        test = t; body = b;
    }
    
}

abstract class Expression {
    // Expression = VariableRef | Value | Binary | Unary
}

abstract class VariableRef extends Expression {
   // VariableRef = Variable | ArrayRef
	String id;

	public boolean equals (Object obj) {
	    String v = ((VariableRef) obj).id;
	    System.out.println("VarRef equals called");
	    return id.equals(v);
	}
}

class Variable extends VariableRef {
    // Variable = String id

    Variable (String s) { super.id = s; }

    public String toString( ) { return id; }
    
    public boolean equals (Object obj) {
    	try {
        	String s = ((Variable) obj).id;
        	return id.equals(s); // case-sensitive identifiers
	}
	catch (ClassCastException e) {
		return false;
	}
    }
    
    public int hashCode ( ) { return id.hashCode( ); }
}

class ArrayRef extends VariableRef {
    // ArrayRef = String id; Expression index
	
    Expression index;

    ArrayRef (String s, Expression e) {
	super.id = s; index = e;
    }

    public String toString( ) { return id + "[" + index + "]"; }

    public boolean equals (Object obj) {
    	try {
	    ArrayRef a = (ArrayRef) obj;
	    String aid = a.id;
	    Expression aindex = a.index;
	    return id.equals(aid) && index.equals(aindex); 
        }
	catch (ClassCastException e) {
		return false; 
	}
    }

    public int hashCode( ) {
    	IntValue ival = (IntValue) index;
    	return id.hashCode() + ival.intValue(); 
    }
    // I think the reason the ArrayRefs are not being matched. Is because I have not implemented the function hashCode, for ArrayRef
}
	
abstract class Value extends Expression {
    // Value = IntValue | BoolValue |
    //         CharValue | FloatValue
    protected Type type;
    protected boolean undef = true;

    int intValue ( ) {
        assert false : "should never reach here";
        return 0;
    }
    
    boolean boolValue ( ) {
        assert false : "should never reach here";
        return false;
    }
    
    char charValue ( ) {
        assert false : "should never reach here";
        return ' ';
    }
    
    float floatValue ( ) {
        assert false : "should never reach here";
        return 0.0f;
    }

    boolean isUndef( ) { return undef; }

    Type type ( ) { return type; }

    static Value mkValue (Type type) {
        if (type == Type.INT) return new IntValue( );
        if (type == Type.BOOL) return new BoolValue( );
        if (type == Type.CHAR) return new CharValue( );
        if (type == Type.FLOAT) return new FloatValue( );
        throw new IllegalArgumentException("Illegal type in mkValue");
    }
}

class IntValue extends Value {
    private int value = 0;

    IntValue ( ) { type = Type.INT; }

    IntValue (int v) { this( ); value = v; undef = false; }

    int intValue ( ) {
        assert !undef : "reference to undefined int value";
        return value;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }

    public boolean equals(Object obj) {
    	IntValue iv = (IntValue) obj;
	return iv.value == this.value;
    }

}

class BoolValue extends Value {
    private boolean value = false;

    BoolValue ( ) { type = Type.BOOL; }

    BoolValue (boolean v) { this( ); value = v; undef = false; }

    boolean boolValue ( ) {
        assert !undef : "reference to undefined bool value";
        return value;
    }

    int intValue ( ) {
        assert !undef : "reference to undefined bool value";
        return value ? 1 : 0;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }

}

class CharValue extends Value {
    private char value = ' ';

    CharValue ( ) { type = Type.CHAR; }

    CharValue (char v) { this( ); value = v; undef = false; }

    char charValue ( ) {
        assert !undef : "reference to undefined char value";
        return value;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }

}

class FloatValue extends Value {
    private float value = 0;

    FloatValue ( ) { type = Type.FLOAT; }

    FloatValue (float v) { this( ); value = v; undef = false; }

    float floatValue ( ) {
        assert !undef : "reference to undefined float value";
        return value;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }

}

class Binary extends Expression {
// Binary = Operator op; Expression term1, term2
    Operator op;
    Expression term1, term2;

    Binary (Operator o, Expression l, Expression r) {
        op = o; term1 = l; term2 = r;
    } // binary

}

class Unary extends Expression {
    // Unary = Operator op; Expression term
    Operator op;
    Expression term;

    Unary (Operator o, Expression e) {
        op = o; term = e;
    } // unary

}

class Operator {
    // Operator = BooleanOp | RelationalOp | ArithmeticOp | UnaryOp
    // BooleanOp = && | ||
    final static String AND = "&&";
    final static String OR = "||";
    // RelationalOp = < | <= | == | != | >= | >
    final static String LT = "<";
    final static String LE = "<=";
    final static String EQ = "==";
    final static String NE = "!=";
    final static String GT = ">";
    final static String GE = ">=";
    // ArithmeticOp = + | - | * | /
    final static String PLUS = "+";
    final static String MINUS = "-";
    final static String TIMES = "*";
    final static String DIV = "/";
    // UnaryOp = !    
    final static String NOT = "!";
    final static String NEG = "-NEG";
    // CastOp = int | float | char
    final static String INT = "int";
    final static String FLOAT = "float";
    final static String CHAR = "char";
    // Typed Operators
    // RelationalOp = < | <= | == | != | >= | >
    final static String INT_LT = "INT<";
    final static String INT_LE = "INT<=";
    final static String INT_EQ = "INT==";
    final static String INT_NE = "INT!=";
    final static String INT_GT = "INT>";
    final static String INT_GE = "INT>=";
    // ArithmeticOp = + | - | * | /
    final static String INT_PLUS = "INT+";
    final static String INT_MINUS = "INT-";
    final static String INT_TIMES = "INT*";
    final static String INT_DIV = "INT/";
    // UnaryOp = !    
    final static String INT_NEG = "INT-NEG";
    // RelationalOp = < | <= | == | != | >= | >
    final static String FLOAT_LT = "FLOAT<";
    final static String FLOAT_LE = "FLOAT<=";
    final static String FLOAT_EQ = "FLOAT==";
    final static String FLOAT_NE = "FLOAT!=";
    final static String FLOAT_GT = "FLOAT>";
    final static String FLOAT_GE = "FLOAT>=";
    // ArithmeticOp = + | - | * | /
    final static String FLOAT_PLUS = "FLOAT+";
    final static String FLOAT_MINUS = "FLOAT-";
    final static String FLOAT_TIMES = "FLOAT*";
    final static String FLOAT_DIV = "FLOAT/";
    // UnaryOp = !    
    final static String FLOAT_NEG = "FLOAT-NEG";
    // RelationalOp = < | <= | == | != | >= | >
    final static String CHAR_LT = "CHAR<";
    final static String CHAR_LE = "CHAR<=";
    final static String CHAR_EQ = "CHAR==";
    final static String CHAR_NE = "CHAR!=";
    final static String CHAR_GT = "CHAR>";
    final static String CHAR_GE = "CHAR>=";
    // RelationalOp = < | <= | == | != | >= | >
    final static String BOOL_LT = "BOOL<";
    final static String BOOL_LE = "BOOL<=";
    final static String BOOL_EQ = "BOOL==";
    final static String BOOL_NE = "BOOL!=";
    final static String BOOL_GT = "BOOL>";
    final static String BOOL_GE = "BOOL>=";
    // Type specific cast
    final static String I2F = "I2F";
    final static String F2I = "F2I";
    final static String C2I = "C2I";
    final static String I2C = "I2C";
    
    String val;
    
    Operator (String s) { val = s; }

    public String toString( ) { return val; }
    public boolean equals(Object obj) { return val.equals(obj); }
    
    boolean BooleanOp ( ) { return val.equals(AND) || val.equals(OR); }
    boolean RelationalOp ( ) {
        return val.equals(LT) || val.equals(LE) || val.equals(EQ)
            || val.equals(NE) || val.equals(GT) || val.equals(GE);
    }
    boolean ArithmeticOp ( ) {
        return val.equals(PLUS) || val.equals(MINUS)
            || val.equals(TIMES) || val.equals(DIV);
    }
    boolean NotOp ( ) { return val.equals(NOT) ; }
    boolean NegateOp ( ) { return val.equals(NEG) ; }
    boolean intOp ( ) { return val.equals(INT); }
    boolean floatOp ( ) { return val.equals(FLOAT); }
    boolean charOp ( ) { return val.equals(CHAR); }

    final static String intMap[ ] [ ] = {
        {PLUS, INT_PLUS}, {MINUS, INT_MINUS},
        {TIMES, INT_TIMES}, {DIV, INT_DIV},
        {EQ, INT_EQ}, {NE, INT_NE}, {LT, INT_LT},
        {LE, INT_LE}, {GT, INT_GT}, {GE, INT_GE},
        {NEG, INT_NEG}, {FLOAT, I2F}, {CHAR, I2C}
    };

    final static String floatMap[ ] [ ] = {
        {PLUS, FLOAT_PLUS}, {MINUS, FLOAT_MINUS},
        {TIMES, FLOAT_TIMES}, {DIV, FLOAT_DIV},
        {EQ, FLOAT_EQ}, {NE, FLOAT_NE}, {LT, FLOAT_LT},
        {LE, FLOAT_LE}, {GT, FLOAT_GT}, {GE, FLOAT_GE},
        {NEG, FLOAT_NEG}, {INT, F2I}
    };

    final static String charMap[ ] [ ] = {
        {EQ, CHAR_EQ}, {NE, CHAR_NE}, {LT, CHAR_LT},
        {LE, CHAR_LE}, {GT, CHAR_GT}, {GE, CHAR_GE},
        {INT, C2I}
    };

    final static String boolMap[ ] [ ] = {
        {EQ, BOOL_EQ}, {NE, BOOL_NE}, {LT, BOOL_LT},
        {LE, BOOL_LE}, {GT, BOOL_GT}, {GE, BOOL_GE},
	{OR, OR}, {AND, AND}, {NOT, NOT}
    };

    final static private Operator map (String[][] tmap, String op) {
        for (int i = 0; i < tmap.length; i++)
            if (tmap[i][0].equals(op))
                return new Operator(tmap[i][1]);
        assert false : "should never reach here";
        return null;
    }

    final static public Operator intMap (String op) {
        return map (intMap, op);
    }

    final static public Operator floatMap (String op) {
        return map (floatMap, op);
    }

    final static public Operator charMap (String op) {
        return map (charMap, op);
    }

    final static public Operator boolMap (String op) {
        return map (boolMap, op);
    }

}
