#!/bin/bash

if [[ $1 == "-i" ]]
   then java Semantics $2
else 
if [[ $1 == "-c" ]] 
   then
   java CodeGen $2 > /dev/null

   # Some bash string operations
   EXTEN=".cpp"
   EXTEN_LEN=${#EXTEN}

   # This operation cuts off the .cpp extension
   jasm=${2:0:$(expr ${#2} - $EXTEN_LEN)} 

   java -jar ~/bin/jasmin.jar $jasm.j > /dev/null
else
   echo "$0: unrecognized option '$1'"
fi
fi
