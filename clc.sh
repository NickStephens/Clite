#!/bin/bash -e

#CLite Compile (clc)

usage() {
	echo """
usage: clc [-i|-c[j]|-a] clitefile
example: clc -cj recFib.cpp

OPTIONS:
	-h, --help		 output this help file
	-i, --interpret		 interpret the clite input file
	-c, --compile		 compile the clite input to Java Bytecode
	-cj, --compile-and-keep  compile the clite input to Java Bytecode and keep the the produced jasmin assembly
	-a, --assembly		 produce the jasmin assembly of the clite input file
	"""
	exit 1
}

compile() {
	java -jar ~/.clite/CliteCompiler.jar $file > /dev/null
	
	EXTEN=".cpp"
	EXTEN_LEN=${#EXTEN}

	jasm=${file:0:$(expr ${#file} - $EXTEN_LEN)}
	
	java -jar ~/.clite/jasmin.jar $jasm.j > /dev/null
}

interpret() {
	java -jar ~/.clite/CliteInterpreter.jar $file 	
}

cleanup() {
	rm $jasm.j
}

#resetting any env vars
jasm=""
file=""

case $1 in
	-h | --help | -\? | "")
		usage
		;;
	-i | --interpret)
		file=$2
		interpret
		;;
	-c | --compile)
		file=$2
		compile
		cleanup	
		;;
	-cj | --compile-and-keep)
		file=$2
		compile
		;;
	-a | --assembly)
		java -jar ~/.clite/CliteCompiler.jar $2 > /dev/null
		mv $jasm.j .
		;;
	-*)
		echo "unknown option: $1"
		usage
		;;
	*)
		file=$1
		compile
		cleanup
		;;
esac	