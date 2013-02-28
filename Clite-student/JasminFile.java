import java.io.*;

public class JasminFile extends FileWriter {

	private String filename; 

	public JasminFile(String pathname) throws IOException {
		super(pathname); // passes filename, and tells writer to append
		filename = pathname;
	}

	private String sanitize_path( ) {
		// Go backwards through the string and remove anything after the first
		// extension and once the UNIX sperator is reached output string
		boolean past_ext = false;
		String san_str_rev = "";
		for (int i=filename.length( )-1; i >= 0; i--) {
			char cur = filename.charAt(i);
			if (past_ext) {
				if (cur != '/')
					san_str_rev += cur;
				else
					break;
			} else {
				if (cur == '.')
					past_ext = true;
			}
		}
		String san_str = "";
		for (int i=san_str_rev.length( )-1; i >= 0; i--) 
			san_str += san_str_rev.charAt(i);

		return san_str;
	}
	
	public void write_globals(Declarations globals) throws IOException{
		for (Declaration global : globals) {
			String global_sig = ".field public static ";
			global_sig += global.v.id;
			if (global.t.equals(Type.INT) || global.t.equals(Type.BOOL)
				|| global.t.equals(Type.CHAR))
				global_sig += " I";	
			else
				global_sig += " V";
			writeln(global_sig);
		}
	}
		
				
	public void JVMBoiler() throws IOException {
		write(".class public" + " " + sanitize_path() + "\n");
		write(".super java/lang/Object\n");
		write("\n");
		write(".method public <init>()V\n");
		write("\taload_0\n");
		write("\n");
		write("\tinvokespecial java/lang/Object/<init>()V\n");
		write("\treturn\n");
		write(".end method\n");
		write("\n");
	}

	public void main_preamble(Declarations locals) throws IOException {

		writeln(".method public static main([Ljava/lang/String;)V");
		writeln("\t.limit stack" + " " + "14" + " ;(Hack!) Although it is possible to statically determine the size of the stack based off counting expression information \n\t\t\t;in a function it's too much work for now. ");
		// due to how Clite's expressions over operators are evaluated
		// we can safely limit the stack to two elements; however this will
		// change when we're generating code for CliteF and we need to push
		// the arguments to a function on the stack
		writeln("\t.limit locals" + " " + (locals.size() + 1) 
		+ " " +  "; #0 is reserved information is statically determined by counting declarations");
		writeln();
	}
		

	/*
		Locals limit is how many variables we need to store in a call, keep in mind
		the JVM passes parameters through locals 0,1,2, and so on, so the locals 
		limit should be the number of parameters plus the numbers of local variables declared
		(How will globals work?)
	*/
	public void function_preamble(String func_name, Type return_type, Declarations params, Declarations locals) throws IOException {

		// Determing the method's signature
		String method_sig = ".method public static ";
		method_sig += func_name;
		method_sig += "(";
		for (Declaration param : params) {
			if (param.t.equals(Type.INT) || param.t.equals(Type.CHAR) ||				param.t.equals(Type.BOOL))
			method_sig += "I";
			else
			method_sig += "F";
		}	
		method_sig += ")";
		if (return_type.equals(Type.INT) || return_type.equals(Type.CHAR) 
			|| return_type.equals(Type.BOOL))
			method_sig += "I";
		else if (return_type.equals(Type.FLOAT)) 
			method_sig += "F";
		else // return_type is of type void
			method_sig += "V";
		
		writeln(method_sig);
		writeln("\t.limit stack" + " " + "14" + " ;(Hack!) Although it is possible to statically determine the size of the stack based off counting expression information \n\t\t\t;in a function it's too much work for now. ");
		// due to how Clite's expressions over operators are evaluated
		// we can safely limit the stack to two elements; however this will
		// change when we're generating code for CliteF and we need to push
		// the arguments to a function on the stack
		writeln("\t.limit locals" + " " + (params.size() + locals.size() + 1) 
		+ " " +  "; #0 is reserved information is statically determined by counting declarations");
		writeln();
	}	
	
	public void writeln(String to_write) throws IOException {
		write("\t");
		write(to_write);
		write("\n");
	}

	public void writeln() throws IOException {
		write("\n");
	}

	public void write_relop_body(int branch_cnt) throws IOException {
		write("TRUE" + branch_cnt + "\n");
		writeln();
		writeln("FALSE" + branch_cnt + ":");
		writeln("\tbipush 0");
		writeln("\tgoto COMPLETED" + branch_cnt);
		writeln();
		writeln("TRUE" + branch_cnt + ":");
		writeln("\tbipush 1");
		writeln();
		writeln("COMPLETED" + branch_cnt + ":");
	}

	public void function_writeout(Type return_type) throws IOException {
		String t_prefix;
		if (return_type.equals(Type.INT) || return_type.equals(Type.BOOL)
		 || return_type.equals(Type.CHAR)) 
			t_prefix = "i";
		else // it's of type float	
			t_prefix = "f";
		writeln("\n" + t_prefix + "return");
		write(".end method\n");
		close();
	}
}
