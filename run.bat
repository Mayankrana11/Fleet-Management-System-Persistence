@echo off
echo ===============================================
echo     Compiling Fleet Management System (A2)
echo ===============================================

if exist out (
    echo Cleaning old build files...
    rmdir /s /q out
)
mkdir out

echo.
echo Compiling Source Code...

javac -d out ^
src\fleet\*.java ^
src\vehicles\*.java ^
src\interfaces\*.java ^
src\exceptions\*.java ^
src\util\*.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo =====================
    echo  COMPILATION FAILED!
    echo =====================
    echo Please check the errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Compilation Successful.
echo.
echo ===============================================
echo     Running Fleet Management System...
echo ===============================================
echo.

java -cp out fleet.Main

echo.
echo Program finished. Press any key to exit.
pause >nul
