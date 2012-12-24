xcopy src C:\Users\marco_000\Work\mcp\src\minecraft /S /Y

cd /D C:\Users\marco_000\Work\mcp
call recompile.bat <nul
call reobfuscate.bat <nul

cd /D D:\Projects\MagicChest
xcopy data C:\Users\marco_000\Work\mcp\reobf\minecraft /S /Y /EXCLUDE:excludes

cd /D C:\Users\marco_000\Work\mcp\reobf\minecraft
7z a MagicChest.zip mcmod.info MagicChest org
move MagicChest.zip C:\Users\marco_000\AppData\Roaming\.minecraft\mods