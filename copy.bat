xcopy src C:\Users\Marco\mcp\src\minecraft /S /Y

cd C:\Users\Marco\mcp
call recompile.bat <nul
call reobfuscate.bat <nul

cd C:\Users\Marco\workspace\MagicChest
xcopy data C:\Users\Marco\mcp\reobf\minecraft /S /Y /EXCLUDE:excludes