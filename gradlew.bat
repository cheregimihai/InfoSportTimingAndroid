
@if EXIST "%~dp0\gradle\wrapper\gradle-wrapper.jar" (
    @echo off
    setlocal
    set DIRNAME=%~dp0
    if "%DIRNAME%" == "" set DIRNAME=.
    set APP_BASE_NAME=%~n0
    set APP_HOME=%DIRNAME%

    rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass any JVM options to this script.
    set DEFAULT_JVM_OPTS=

    rem Find java.exe
    if defined JAVA_HOME goto findJavaFromJavaHome

    set JAVA_EXE=java.exe
    %JAVA_EXE% -version >NUL 2>&1
    if "%ERRORLEVEL%" == "0" goto execute

    echo.
    echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
    echo.
    echo Please set the JAVA_HOME variable in your environment to match the
    echo location of your Java installation.

    goto fail

    :findJavaFromJavaHome
    set JAVA_HOME=%JAVA_HOME:"=%
    set JAVA_EXE=%JAVA_HOME%/bin/java.exe

    if exist "%JAVA_EXE%" goto execute

    echo.
    echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
    echo.
    echo Please set the JAVA_HOME variable in your environment to match the
    echo location of your Java installation.

    goto fail

    :execute
    rem Setup the command line
    set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

    rem Execute Gradle
    "%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

    :end
    rem End local scope for the variables with values derived from the command line
    endlocal
    exit /b %ERRORLEVEL%

    :fail
    rem Exit with failure message
    exit /b 1

)
