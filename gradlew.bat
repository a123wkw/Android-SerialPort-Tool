@echo off
@rem #################################################################
@rem 
@rem  增强版 Gradle Windows 启动脚本
@rem  新增功能: 自动检测JDK版本并显示构建环境信息
@rem
@rem #################################################################

:: 启用延迟变量扩展和错误检查
if "%OS%"=="Windows_NT" setlocal enabledelayedexpansion

:: 初始化配置
set DEFAULT_JVM_OPTS=
set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

:: 新增功能: 显示环境信息
call :show_build_info

:: 查找Java可执行文件
if defined JAVA_HOME (
    call :find_java_from_home
) else (
    call :find_java_in_path
)

if "%ERRORLEVEL%"=="0" goto execute

:: 执行Gradle
:execute
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

:: 新增: 构建开始时间记录
set BUILD_START_TIME=%TIME%

"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% ^
    "-Dorg.gradle.appname=%APP_BASE_NAME%" ^
    -classpath "%CLASSPATH%" ^
    org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%

:: 新增: 构建耗时计算
call :calculate_build_time

goto end

:: **************************
:: 新增功能函数区域
:: **************************

:show_build_info
    echo.
    echo [构建环境信息]
    ver | find "Version"
    echo Gradle项目目录: %APP_HOME%
    goto :eof

:find_java_from_home
    set JAVA_HOME=%JAVA_HOME:"=%
    set JAVA_EXE=%JAVA_HOME%\bin\java.exe
    
    if exist "%JAVA_EXE%" (
        "%JAVA_EXE%" -version 2>nul
        if "%ERRORLEVEL%"=="0" (
            echo 使用的JDK路径: %JAVA_EXE%
            goto :eof
        )
    )
    
    echo.
    echo ERROR: JAVA_HOME指向无效的JDK目录: %JAVA_HOME%
    goto fail

:find_java_in_path
    set JAVA_EXE=java.exe
    %JAVA_EXE% -version 2>nul
    
    if "%ERRORLEVEL%"=="0" (
        for /f "tokens=2 delims==" %%a in ('%JAVA_EXE% -XshowSettings:properties -version 2^>^&1 ^| find "java.home"') do (
            set JAVA_HOME=%%a
            echo 自动发现的JDK路径: !JAVA_HOME!
        )
        goto :eof
    )
    
    echo.
    echo ERROR: 未设置JAVA_HOME且在PATH中找不到java命令
    goto fail

:calculate_build_time
    set BUILD_END_TIME=%TIME%
    
    :: 计算构建耗时
    call :time_diff "%BUILD_START_TIME%" "%BUILD_END_TIME%"
    echo 构建耗时: !DIFF_RESULT!
    goto :eof

:time_diff
    :: 时间差计算函数
    setlocal
    call :parse_time %1
    set /a START=(%HH%*3600 + %MM%*60 + %SS%)
    
    call :parse_time %2
    set /a END=(%HH%*3600 + %MM%*60 + %SS%)
    
    set /a DIFF=%END%-%START%
    
    set /a DIFF_H=%DIFF%/3600
    set /a DIFF_M=(%DIFF%-%DIFF_H%*3600)/60
    set /a DIFF_S=%DIFF%-%DIFF_H%*3600-%DIFF_M%*60
    
    endlocal & set DIFF_RESULT=%DIFF_H%小时 %DIFF_M%分钟 %DIFF_S%秒
    goto :eof

:parse_time
    :: 解析时间格式
    for /f "tokens=1-3 delims=:.," %%a in ("%~1") do (
        set HH=%%a
        set MM=%%b
        set SS=%%c
    )
    goto :eof

:: **************************
:: 原有功能保持不变
:: **************************

:fail
    if not "" == "%GRADLE_EXIT_CONSOLE%" exit 1
    exit /b 1

:end
    if "%OS%"=="Windows_NT" endlocal
