#!/bin/sh

SOURCE="$1"
EXEC="${SOURCE%.*}"
if gcc -Wall -o "$EXEC" "$SOURCE"; then
	./"$EXEC"
else
	ped "$SOURCE"
fi