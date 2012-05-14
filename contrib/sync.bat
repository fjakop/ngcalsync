@echo off
rem your Lotus Notes installation directory
set NOTES_HOME=c:\programme\ibm\lotus\notes

rem do not modify anything below this line
rem ------------------------------------------------------------

set PATH=%PATH%;%NOTES_HOME%
set NOTES_JAR=%NOTES_HOME%\jvm\lib\ext\Notes.jar

java -cp ngcalsync.jar;%NOTES_JAR% de.jakop.ngcalsync.StartApplication