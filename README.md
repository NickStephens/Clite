Clite
-----

Clite is a small subset of C. There are no structured or derived types, only the 
primitives Int, Float, Bool, and Char (the inclusion of Bool, technically means 
that Clite is not a subset of C).

Included with the homemade bytecode extension is a bash schellscript, clc.sh. This
script has a number of options:
_output from clc.sh's help file..._
<pre> 
usage: ./clc.sh [-h|-i|-c|-k|-a] clitefile
example: ./clc.sh -k recFib.cpp

OPTIONS:
	-h, --help		 output this help file
	-i, --interpret		 interpret the clite input file
	-c, --compile		 compile the clite input to Java Bytecode
	-k, --compile-and-keep  compile the clite input to Java Bytecode and keep the the produced jasmin assembly
	-a, --assembly		 produce the jasmin assembly of the clite input file
</pre>

> * It's best to run clc.sh from the directory it comes in. It uses relative paths
>   to find the right jars to call. This implementation also assumes that the
>   Jasmin jar file is located in Clite's bin.
>   
> * Note: Clite was not designed by me and was assigned as a school project. Clite 
> comes as a skeleton. You can find the skeleton 
> <a href="http://highered.mcgraw-hill.com/sites/0072866098/student_view0/clite_interpreter_.html">here</a>, it goes hand-in-hand with the book 
> <u>Programming Languages: Priniciple and Paradigms</u> by Allen B. Tucker and 
> Robert E. Noonan. However, the JVM ByteCode compilation extension and _CliteF_ 
> interpretation is largely of my own design.

__Dependencies:__ <a href="http://jasmin.sourceforge.net/">Jasmin</a>
