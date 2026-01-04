@echo off
chcp 65001 >nul
title 企业文档管理系统 - 合并启动脚本
cls
setlocal enabledelayedexpansion
cd /d "%~dp0"
echo.
echo ==========================================
echo 企业文档管理系统 - 合并启动工具
echo ==========================================
echo.
:menu
echo 请选择要执行的操作：
echo  [1] 快速启动服务器
echo  [2] 完整检查并启动
echo  [3] 重启服务器
echo  [4] 快速构建WAR
echo  [5] 详细构建过程
echo  [6] 修复CSS链接
echo  [7] 修复数据库编码
echo  [8] 退出
echo.
set /p opt=输入选项编号并回车: 
if "%opt%"=="1" goto quick_run
if "%opt%"=="2" goto run_full
if "%opt%"=="3" goto restart_server
if "%opt%"=="4" goto quick_build
if "%opt%"=="5" goto final_build
if "%opt%"=="6" goto fix_css
if "%opt%"=="7" goto fix_db_charset
if "%opt%"=="8" exit /b 0
echo.
echo 无效选项，请重新输入。
echo.
goto menu

:quick_run
echo 正在启动服务器...
java -version >nul 2>&1
if %errorlevel% neq 0 (
  echo 未检测到Java，請安裝或配置JAVA_HOME
  pause
  goto menu
)
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
  echo 未检测到Maven，請安裝並將其加入PATH
  pause
  goto menu
)
echo 正在以內嵌Tomcat運行（端口: 8081，路徑: /document-management）...
mvn tomcat7:run
if %errorlevel% neq 0 (
  echo 啟動失敗，請查看上方錯誤信息
  pause
)
goto menu

:run_full
java -version 2>&1
if %errorlevel% neq 0 (
  echo Java环境错误
  pause
  goto menu
)
mvn -version 2>&1
if %errorlevel% neq 0 (
  echo Maven环境错误
  pause
  goto menu
)
mvn clean compile
if %errorlevel% neq 0 (
  echo 构建失败
  pause
  goto menu
)
echo 構建成功，開始啟動服務器...
mvn tomcat7:run
if %errorlevel% neq 0 (
  echo 啟動失敗，請查看上方錯誤信息
  pause
)
goto menu

:restart_server
echo 正在清理並重啟服務器...
java -version >nul 2>&1
if %errorlevel% neq 0 (
  echo 未检测到Java，請安裝或配置JAVA_HOME
  pause
  goto menu
)
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
  echo 未检测到Maven，請安裝並將其加入PATH
  pause
  goto menu
)
mvn clean
if %errorlevel% neq 0 (
  echo 清理失敗
  pause
  goto menu
)
mvn tomcat7:run
if %errorlevel% neq 0 (
  echo 重啟失敗
  pause
)
goto menu

:quick_build
echo 正在快速打包WAR（跳過測試）...
mvn clean package -DskipTests
if %errorlevel% neq 0 (
  echo 打包失敗
  pause
  goto menu
)
echo 打包完成：請查看 target 目錄
pause
goto menu

:final_build
java -version 2>&1
if %errorlevel% neq 0 (
  echo Java环境错误
  pause
  goto menu
)
mvn -version 2>&1
if %errorlevel% neq 0 (
  echo Maven环境错误
  pause
  goto menu
)
echo 正在執行詳細構建（輸出完整信息）...
mvn clean package -DskipTests -e -X
if %errorlevel% neq 0 (
  echo 詳細構建失敗
  pause
  goto menu
)
echo 詳細構建成功
pause
goto menu

:fix_css
echo 已准备本地CSS：src\main\webapp\css\bootstrap-local.css
echo 如无样式显示，请重启服务器并强制刷新
goto menu

:fix_db_charset
set /p confirm=执行数据库编码修复将改动数据，继续？(y/N): 
if /i not "%confirm%"=="y" goto menu
mysql -u root -p -e "source %~dp0fix-database-encoding.sql"
goto menu
