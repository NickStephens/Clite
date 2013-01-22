import java.io.*;

public class JasminFile extends FileWriter {

	private String filename; 

	public JasminFile(String pathname) throws IOException {
		super(pathname); // passes filename, and tells writer to append
		filename = pathname;
	}

	public void JVMBoiler() throws IOException {
		write(".class public" + " " + filename + "\n");
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

	/*
		Locals limit is how many variables we need to store in a call, keep in mind
		the JVM passes parameters through locals 0,1,2, and so on, so the locals 
		limit should be the number of parameters plus the numbers of local variables declared
		(How will globals work?)
	*/
	public void preamble(Declarations dec) throws IOException {
		write(".method public static main([Ljava/lang/String;)V\n");
		write(".limit stack" + " " + "2" + " " + " ; information gathered from declarations\n");
		// due to how Clite's expressions over operators are evaluated
		// we can safely limit the stack to two elements; however this will
		// change when we're generating code for CliteF and we need to push
		// the arguments to a function on the stack
		write(".limit locals" + " " + dec.size() + "\n");
		write("\n");
	}	
	
	public void writeln(String to_write) throws IOException {
		write("\t");
		write(to_write);
		write("\n");
	}

	public void writeout( ) throws IOException {
		write("\n.end method\n");
		close();
	}
}
