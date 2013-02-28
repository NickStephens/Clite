#! /bin/bash

java CodeGen $1 > /dev/null

# Some bash string operations
EXTEN=".cpp"
EXTEN_LEN=${#EXTEN}

# This operation cuts off the .cpp extension
jasm=${1:0:$(expr ${#1} - $EXTEN_LEN)} 

java -jar ~/bin/jasmin.jar $jasm.j > /dev/null
