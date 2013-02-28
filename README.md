Clite
-----

Clite is a small subset of C. There are no structured or derived types, only the 
primitives Int, Float, Bool, and Char (the inclusion of Bool, technically means 
that Clite is not a subset of C).

Currently the only branch I've pushed is titled CodeGen. CodeGen is a .class file 
which reduces Clite to Jasmin, an assembly language for the Java Virtual Machine. 
Also included is a shellscript, clc.sh, or CLite Compile. This shellscript, 
provided you have the right utilities in the right places (I'll make this more 
generic, I'm not an expert BASH scripter) will first reduce your Clite program in to 
assembly then assemble it into JVM bytecode using the Jasmin assembler, producing a 
new .class file in your current directory!

> * clc.sh assumes that the jasmin.jar is located in <code>~/bin</code> and that the
>   .class files are located in the current directory 
> * Note: Clite was not designed by me and was assigned as a school project. Clite 
> comes as a skeleton. You can find the skeleton 
> <a href="http://highered.mcgraw-hill.com/sites/0072866098/student_view0/clite_
interpreter_.html">here</a>, it goes hand-in-hand with the book 
> <u>Programming Languages: Priniciple and Paradigms</u> by Allen B. Tucker and 
> Robert E. Noonan. However, the JVM ByteCode compilation extension and _CliteF_ 
> interpretation is of my own design.

__Dependencies:__ <a href="http://jasmin.sourceforge.net/">Jasmin</a>
