@echo off

setlocal
FOR /F "tokens=*" %%i in ('type %HOMEPATH%\.ngcalsync\env.properties') do SET "%%i"

set PATH=%NOTES_HOME%;%PATH%
set NOTES_JAR=%NOTES_HOME%\jvm\lib\ext\Notes.jar

start javaw -cp *;"%NOTES_JAR%" de.jakop.ngcalsync.StartApplication
endlocal
