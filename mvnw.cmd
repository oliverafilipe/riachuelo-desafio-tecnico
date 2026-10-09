@REM Maven wrapper script for Windows with support for 'run' shortcut (maps to spring-boot:run)
@echo off
if "%1"=="run" (
    shift
    mvn spring-boot:run %*
) else (
    mvn %*
)
