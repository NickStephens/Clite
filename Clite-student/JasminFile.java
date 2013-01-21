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

	/* I'm not quite sure how to statically determine stack and locals limits
		Stack limit is the maximum amount of items the stack will hold, this includes
		function calls (I think)
		Locals limit is how many variable we need to store in a call, keep in mind
		the JVM passes parameters through locals 0,1,2, and so on
	*/
	public void allocate(Declarations dec) throws IOException {
		write(".method public static main([Ljava/lang/String;)V\n");
		write(".limit stack" + " " +  dec.size() + " ; information gathered from declarations\n");
		write(".limit locals" + " " + dec.size() + "\n");
		write("\n");
	}	
}
