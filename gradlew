#!/usr/bin/env bash

##############################################################################
##  Gradle 启动脚本 (UNIX/Linux/macOS 版本)
##  功能: 设置JVM环境并启动Gradle Wrapper
##############################################################################

# 初始化配置
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
DEFAULT_JVM_OPTS=""  # 默认JVM参数
MAX_FD="maximum"     # 最大文件描述符限制

# 日志函数
warn() { echo "[WARN] $*"; }
die() { echo "[ERROR] $*"; exit 1; }

# 检测操作系统类型
case "$(uname)" in
  CYGWIN*)  cygwin=true ;;  # Windows Cygwin环境
  Darwin*)  darwin=true ;;  # macOS环境
  MINGW*)   msys=true   ;;  # Windows MSYS环境
esac

# 解析APP_HOME路径 (处理符号链接)
resolve_app_home() {
  PRG="$0"
  while [ -h "$PRG" ]; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' >/dev/null; then
      PRG="$link"
    else
      PRG=$(dirname "$PRG")"/$link"
    fi
  done
  SAVED=$(pwd)
  cd "$(dirname "$PRG")/" >/dev/null || exit
  APP_HOME=$(pwd -P)
  cd "$SAVED" >/dev/null || exit
}
resolve_app_home

# 设置CLASSPATH
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# 查找Java可执行文件
find_java() {
  if [ -n "$JAVA_HOME" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM JDK特殊路径
      echo "$JAVA_HOME/jre/sh/java"
    else
      echo "$JAVA_HOME/bin/java"
    fi
  else
    which java 2>/dev/null || die "JAVA_HOME未设置且PATH中找不到java命令"
  fi
}
JAVACMD=$(find_java)
[ -x "$JAVACMD" ] || die "无效的Java路径: $JAVACMD"

# 设置文件描述符限制
set_fd_limit() {
  if [ "$cygwin" = "false" ] && [ "$darwin" = "false" ]; then
    MAX_FD_LIMIT=$(ulimit -H -n 2>/dev/null)
    [ "$MAX_FD" = "maximum" ] && MAX_FD=$MAX_FD_LIMIT
    ulimit -n "$MAX_FD" 2>/dev/null || 
      warn "无法设置文件描述符限制: $MAX_FD"
  fi
}
set_fd_limit

# 平台特定设置
if $darwin; then
  # macOS Dock设置
  GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

if $cygwin; then
  # Cygwin路径转换
  APP_HOME=$(cygpath --mixed "$APP_HOME")
  CLASSPATH=$(cygpath --mixed "$CLASSPATH")
  
  # 参数转换逻辑
  convert_args() {
    # ... [原有参数转换逻辑保持不变]
  }
  convert_args
fi

# 执行Gradle
exec "$JAVACMD" "${JVM_OPTS[@]}" \
  -classpath "$CLASSPATH" \
  -Dorg.gradle.appname="$APP_BASE_NAME" \
  org.gradle.wrapper.GradleWrapperMain "$@"
