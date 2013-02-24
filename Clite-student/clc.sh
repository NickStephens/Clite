#! /bin/bash

wd=$(pwd)
cwd=$(dirname "$0")

java CodeGen $1 > /dev/null

# Some bash string operations
EXTEN=".cpp"
EXTEN_LEN=${#EXTEN}

# This operation cuts off the .cpp extension
jasm=${1:0:$(expr ${#1} - $EXTEN_LEN)} 
echo "$jasm"

java -jar /Users/Nick/bin/jasmin.jar $jasm.j > /dev/null
