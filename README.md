Clite is a small subset of C.

There are no structured or derived types of the primitives Int, Float, Bool, and Char.

There is some simple type coercion between Floats and Ints with arithmentic operators.

Currently the only branch I've pushed is titled CodeGen. CodeGen is a .class file which reduces Clite to Jasmin, an assembly language for the Java Virtual Machine. Also included is a shellscript, clc.sh, or CLite Compile. This shellscript, provided you have the right utilities in the right places (I'll make this more generic, I'm not expert BASH scripter) will first reduce your Clite program in to assembly then assemble it into JVM bytecode using the Jasmin assembler, producing a new .class file in your current directory!

