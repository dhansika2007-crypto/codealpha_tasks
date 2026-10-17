@echo off
setlocal
cd /d "%~dp0"
if not exist bin mkdir bin
javac -d bin src\hotel\model\*.java src\hotel\storage\*.java src\hotel\service\*.java src\hotel\util\*.java src\hotel\Main.java
if errorlevel 1 (
  echo Compile failed.
  exit /b 1
)
java -cp bin hotel.Main
