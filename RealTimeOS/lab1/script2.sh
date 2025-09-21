#!/bin/sh

echo "$1\n"
pidin -F "%a %C %A" | grep -i "$1"
