@REM Maven Wrapper for Windows
@REM This downloads Maven if not present and runs it

@echo off
setlocal

set "WRAPPER_DIR=%~dp0.mvn\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
set "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
)

set "JAVA_EXE=java"
if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java"
)

"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%~dp0." -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
