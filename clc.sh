#!/bin/bash

if [[ $1 == "-i" ]]
   then java -jar ~/.clite/CliteInterpreter.jar $2
else 
   if [[ $1 == "-c" || $1 == "-cj"]] 
	then
	java -jar ~/.clite/CliteCompiler.jar $2 > /dev/null

	# Some bash string operations
	EXTEN=".cpp"
	EXTEN_LEN=${#EXTEN}

	# This operation cuts off the .cpp extension
	jasm=${2:0:$(expr ${#2} - $EXTEN_LEN)} 

	java -jar ~/.clite/jasmin.jar $jasm.j > /dev/null

	if [[ $1 == "-c" ]]
	    then 
	    rm $jasm.j
        fi

    else
   
	echo "$0: unrecognized option '$1'"
    fi
fi
