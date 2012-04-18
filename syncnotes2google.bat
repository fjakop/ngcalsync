@echo off
rem The path to the notes installation
set notes-path=C:\Program Files\Lotus\Notes
rem The relative path to the Notes.jar from the notes installation. ex. Notes.jar or jvm/lib/ext/Notes.jar
set notes-jar=notes.jar

set cPath=%CLASSPATH%;.\syncnotes2google.jar
set cPath=%cPath%;"%notes-path%\%notes-jar%"
set cPath=%cPath%;.\lib\domingo-1.5.1.jar
set cPath=%cPath%;.\lib\gdata-calendar-2.0.jar
set cPath=%cPath%;.\lib\gdata-calendar-meta-2.0.jar
set cPath=%cPath%;.\lib\gdata-client-1.0.jar
set cPath=%cPath%;.\lib\gdata-client-meta-1.0.jar
set cPath=%cPath%;.\lib\gdata-core-1.0.jar
set cPath=%cPath%;.\lib\gdata-media-1.0.jar
set cPath=%cPath%;.\lib\google-collect-1.0-rc1.jar

set path=%path%;%notes-path%
java -classpath %cPath% com.googlecode.syncnotes2google.SyncNotes2Google
