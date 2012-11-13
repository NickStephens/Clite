import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token + " at " + lexer.get_lineno() + ":" + lexer.get_col());
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token + " at " + lexer.get_lineno() + ":" + lexer.get_col());
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        match(TokenType.LeftBrace);
        // student exercise
		Declarations decpart = new Declarations();
		if(isType()) { decpart = declarations(); } 
		Block body = statements();

        match(TokenType.RightBrace);
        return new Program(decpart, body);  // student exercise
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
	Declarations dec = new Declarations();
	while (isType()) {
	 	declaration(dec);
	} 
	return dec;  // student exercise
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
	 Type t = type();
	 do {
		token = lexer.next();
		Variable identifier = new Variable(match(TokenType.Identifier));
		if (token.type().equals(TokenType.LeftBracket)) {
			// Messy array declaration stuff
			match(token.type());
			IntValue size = new IntValue();
			if (token.type().equals(TokenType.IntLiteral)) {
				size = (IntValue) literal();
			} else {
				error("IntLiteral");
			}
			match(TokenType.RightBracket);
			ds.add(new ArrayDecl(identifier, t, size));
		} else { 
			ds.add(new VariableDecl(identifier, t));
		}
	 } while (token.type().equals(TokenType.Comma));
	 match(TokenType.Semicolon);
        // student exercise
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char 
        Type t = null;
	if (token.type().equals(TokenType.Int)) {
		t = Type.INT;
	} else if (token.type().equals(TokenType.Bool)) {
		t = Type.BOOL;
	} else if (token.type().equals(TokenType.Char)) {
		t = Type.CHAR;
	} else if (token.type().equals(TokenType.Float)) {
		t = Type.FLOAT;
	} else error("int | bool | float | char");
        // student exercise
        return t;          
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
	if (token.type().equals(TokenType.LeftBrace)) {
		match(TokenType.LeftBrace);
		s = statements();
		match(TokenType.RightBrace);
	} else if (token.type().equals(TokenType.Identifier)) {
		s = assignment();
		match(TokenType.Semicolon);
	} else if (token.type().equals(TokenType.If)) {
		s = ifStatement();
	} else if (token.type().equals(TokenType.While)) {
		s = whileStatement();
	} else {
		match(TokenType.Semicolon);
	}
        return s;
    }
  
    private Block statements () {
        // Block --> '{' Statements '}'
        Block b = new Block();
	while (!token.type().equals(TokenType.RightBrace)) {	
		b.members.add(statement());
	} 
        // student exercise
        return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
	String id = match(TokenType.Identifier);
	VariableRef v = new Variable(id);
	if (token.type().equals(TokenType.LeftBracket)) {
		match(token.type());
		Expression e = expression();
		match(TokenType.RightBracket);
		v = new ArrayRef(id, e);
	}
	match(TokenType.Assign);
	Expression e2 = expression(); 
	
	return new Assignment(v, e2);  // student exercise
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
	match(token.type());
	match(TokenType.LeftParen);
	Expression test = expression();
	match(TokenType.RightParen);
	Statement tp = statement();
	if (token.type().equals(TokenType.Else)) {
		match(token.type());
		Statement ep = statement();
		return new Conditional(test, tp, ep);
	}
        return new Conditional(test, tp);  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
	match(token.type());
	match(TokenType.LeftParen);
	Expression test = expression();
	match(TokenType.RightParen);
	Statement st = statement();
        return new Loop(test, st);  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
	Expression e = conjunction();
	while (token.type().equals(TokenType.Or)) {
		Operator op = new Operator(match(TokenType.Or));
		Expression expr2 = conjunction();
		e = new Binary(op, e, expr2);
	}
	return e; 
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
	Expression e = equality();
	while (token.type().equals(TokenType.And)) {
		Operator op = new Operator(match(TokenType.And));
		Expression expr2 = equality();
		e = new Binary(op, e, expr2);
	}
        return e;  // student exercise
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
	Expression e = relation();
	if (isEqualityOp()) {
		Operator op = new Operator(match(token.type()));	
		Expression expr2 = relation();
		e = new Binary(op, e, expr2);
	}
        return e;  // student exercise
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition] 
	Expression e = addition();
	if (isRelationalOp()) {
		Operator op = new Operator(match(token.type()));
		Expression expr2 = relation();
		e = new Binary(op, e, expr2);
	}	
	return e; // student exercise
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier [ Expression ] | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
	    String id = match(token.type());
	    if (token.type().equals(TokenType.LeftBracket)) {
		match(token.type());
		Expression e2 = expression();
		match(TokenType.RightBracket);
		e = new ArrayRef(id, e2); 
	    } else {
            	e = new Variable(id);
	    }
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
	Value val;
	if (isBooleanLiteral()) {
		boolean b_val = Boolean.parseBoolean(match(token.type()));
		val = new BoolValue(b_val); //student exercise
	} else if (token.type().equals(TokenType.IntLiteral)) {
		int i_val = Integer.parseInt(match(token.type()));
		val = new IntValue(i_val);
	} else if (token.type().equals(TokenType.FloatLiteral)) {
		float f_val = Float.parseFloat(match(token.type()));
		val = new FloatValue(f_val);
	} else {
		char c_val = match(token.type()).charAt(0);
		val = new CharValue(c_val); 
	} 
	return val;
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0])); //Picks the file name and feeds it to the lexer.
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser
