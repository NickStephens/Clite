#! /bin/bash
java CodeGen $1

# Some bash string operations
EXTEN=".cpp"
EXTEN_LEN=${#EXTEN}

# This operation cuts off the .cpp extension
jasm=${1:0:$(expr ${#1} - $EXTEN_LEN)} 

jasmin $jasm.j 
