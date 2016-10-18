@echo off
start AutoHotkey %~dp0\volume_control.ahk
start javaw -cp %~dp0 com.perhac.superserver.SuperServer %*
