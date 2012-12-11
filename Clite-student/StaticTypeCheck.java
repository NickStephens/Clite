// StaticTypeCheck.java

import java.util.*;

// Static type checking for Clite is defined by the functions 
// V and the auxiliary functions typing and typeOf.  These
// functions use the classes in the Abstract Syntax of Clite.

// Consider turning arraydecls into variable decls for the purposes of typechecking.
// This may have consequences when we get past the TypeTransformer. Hopefully
// information will not be lost.

public class StaticTypeCheck {

    public static TypeMap typing (Declarations d) {
        TypeMap map = new TypeMap();
        for (Declaration di : d) {
            map.put (di.v, di.t);
	}
        return map;
    }

    public static void check(boolean test, String msg) {
        if (test)  return;
        System.err.println(msg);
        System.exit(1);
    }

    public static void V (Declarations d) {
        for (int i=0; i<d.size() - 1; i++)
            for (int j=i+1; j<d.size(); j++) {
                Declaration di = d.get(i);
                Declaration dj = d.get(j);
                check( ! (di.v.equals(dj.v)),
                       "duplicate declaration: " + dj.v);
            }
    } 

    public static void V (Program p) {
        V (p.decpart);
        V (p.body, typing (p.decpart));
    } 

    public static Type typeOf (Expression e, TypeMap tm) {
        if (e instanceof Value) return ((Value)e).type;
        if (e instanceof Variable) {
            Variable v = (Variable)e;
            check (tm.containsKey(v), "undefined variable: " + v);
            return (Type) tm.get(v);
	}
	if (e instanceof ArrayRef) {
	    ArrayRef a = (ArrayRef)e;
	    Variable key = new Variable(a.id);
	    check (tm.containsKey(key), "undefined arrayref: " + a);
	    return (Type) tm.get(key);
	}
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            if (b.op.ArithmeticOp( ))
                if (typeOf(b.term1,tm)== Type.FLOAT)
                    return (Type.FLOAT);
                else return (Type.INT);
            if (b.op.RelationalOp( ) || b.op.BooleanOp( )) 
                return (Type.BOOL);
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            if (u.op.NotOp( ))        return (Type.BOOL);
            else if (u.op.NegateOp( )) return typeOf(u.term,tm);
            else if (u.op.intOp( ))    return (Type.INT);
            else if (u.op.floatOp( )) return (Type.FLOAT);
            else if (u.op.charOp( ))  return (Type.CHAR);
        }
        throw new IllegalArgumentException("should never reach here");
    } 

    public static void V (Expression e, TypeMap tm) {
        if (e instanceof Value) 
            return;
	if (e instanceof ArrayRef) {
	    ArrayRef a = (ArrayRef)e;
	    check( tm.containsKey(new Variable(a.id))
		   , "undeclared variable: " + a);
	    Type typ = typeOf(a.index, tm);
	    check ( typ == Type.INT
		    , " non-int expression as index for " + a);
	    return;
	}
        if (e instanceof Variable) { 
            Variable v = (Variable)e;
            check( tm.containsKey(v)
                   , "undeclared variable: " + v);
            return;
        }
	if (e instanceof Binary) {
            Binary b = (Binary) e;
            Type typ1 = typeOf(b.term1, tm);
            Type typ2 = typeOf(b.term2, tm);
            V (b.term1, tm);
            V (b.term2, tm);
            if (b.op.ArithmeticOp( )) {
		if (typ1 == Type.FLOAT && typ2 == Type.INT) 
			check( true, "should never reach here");
		else if (typ1 == Type.INT && typ2 == Type.FLOAT)		
			check( true, "should never reach here");
		else
                	check( typ1 == typ2 &&
                       		(typ1 == Type.INT || typ1 == Type.FLOAT)
                       		, "type error for " + b.op);
            } else if (b.op.RelationalOp( )) 
                check( typ1 == typ2 , "type error for " + b.op);
            else if (b.op.BooleanOp( )) 
                check( typ1 == Type.BOOL && typ2 == Type.BOOL,
                       b.op + ": non-bool operand");
            else
                throw new IllegalArgumentException("should never reach here");
            return;
        }
        // student exercise Unary
	
	if (e instanceof Unary) {
	    Unary u = (Unary) e;
	    Type typ = typeOf(u.term, tm); 
	    V (u.term, tm);
	    if (u.op.NotOp( ))
		check( typ == Type.BOOL, u.op + ": non-bool operand");
	    else if (u.op.NegateOp( ))
		check( typ == Type.INT || typ == Type.FLOAT, "type error for " + u.op);
	    else if (u.op.intOp( ) || u.op.floatOp( ) || u.op.charOp( ))
		check( typ != Type.BOOL, u.op + ": bool operand");
	    else
		throw new IllegalArgumentException("should never reach here");
	    return;
	} 
        throw new IllegalArgumentException("should never reach here");
    }

    public static void V (Statement s, TypeMap tm) {
        if ( s == null )
            throw new IllegalArgumentException( "AST error: null statement");
        else if (s instanceof Skip) return;
        else if (s instanceof Assignment) {
            Assignment a = (Assignment)s;
	    Variable target;
	    if (a.target instanceof ArrayRef) {
	    	target = new Variable(a.target.id);
	    }
	    else { 
	        target = (Variable) a.target;
	    }
	    check( tm.containsKey(target)
		   , " undefined arrayref target in assignment: " + a.target);
            V(a.source, tm);
            Type ttype = (Type)tm.get(target);
            Type srctype = typeOf(a.source, tm);
            if (ttype != srctype) {
                if (ttype == Type.FLOAT)
                    check( srctype == Type.INT
                           , "mixed mode assignment to " + target);
                else if (ttype == Type.INT)
                    check( srctype == Type.CHAR
                           , "mixed mode assignment to " + target);
                else
                    check( false
                           , "mixed mode assignment to " + target);
            }
            return;
        } 
	// student exercise
	if (s instanceof Conditional) {
	    Conditional c = (Conditional) s;
	    V(c.test, tm);
	    V(c.thenbranch, tm);
	    V(c.elsebranch, tm);
	    Type ttype = typeOf(c.test, tm);
	    check( ttype == Type.BOOL, "test expression not of type bool: " + c.test);
	    return;
	}
	if (s instanceof Loop) {
	    Loop l = (Loop) s;
	    V(l.test, tm);
	    V(l.body, tm);
	    Type ttype = typeOf(l.test, tm);
	    check(ttype == Type.BOOL, "test expression not of type bool: " + l.test);	
	    return;
	}
	if (s instanceof Block) {
	    Block b = (Block) s;
	    for (int i=0; i<b.members.size(); i++)
		V(b.members.get(i), tm);
	    return;
	}
	throw new IllegalArgumentException("should never reach here");
    }

    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();           // student exercise
        System.out.println("\nBegin type checking...");
        System.out.println("Type map:");
        TypeMap map = typing(prog.decpart);
        map.display();   // student exercise
        V(prog);
    } //main

} // class StaticTypeCheck

