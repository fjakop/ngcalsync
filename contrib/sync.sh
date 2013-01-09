#!/bin/bash

file=$HOME/.ngcalsync/env.properties

grep -v '^\s*$' $file | while read -r line
    do
	export "$line"
    done 

export PATH=$NOTES_HOME:$PATH
NOTES_JAR=%NOTES_HOME%\jvm\lib\ext\Notes.jar

java -cp ngcalsync.jar:$NOTES_JAR de.jakop.ngcalsync.StartApplication &

# EOF
