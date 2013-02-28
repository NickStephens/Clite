Clite
-----

Clite is a small subset of C. There are no structured or derived types, only the 
primitives Int, Float, Bool, and Char (the inclusion of Bool, technically means 
that Clite is not a subset of C).

Currently the only branch I've pushed is titled CodeGen. CodeGen is a .class file 
which reduces Clite to Jasmin, an assembly language for the Java Virtual Machine. 
Also included is a shellscript, clc.sh, or CLite Compile. This shellscript, 
provided you have the right utilities in the right places (I'll make this more 
generic, I'm not expert BASH scripter) will first reduce your Clite program in to 
assembly then assemble it into JVM bytecode using the Jasmin assembler, producing a 
new .class file in your current directory!

> clc.sh assumes that the jasmin.jar is located in <code>~/bin</code> and that the
> .class files are located in the current directory 

Dependencies: <a href="http://jasmin.sourceforge.net/" >Jasmin</a> 
