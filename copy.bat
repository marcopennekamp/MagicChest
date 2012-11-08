xcopy src D:\Projects\mcp\src\minecraft /S /Y

cd D:\Projects\mcp
call recompile.bat <nul
call reobfuscate.bat <nul

cd D:\Projects\MagicChest
xcopy data D:\Projects\mcp\reobf\minecraft /S /Y /EXCLUDE:excludes

cd D:\Projects\mcp\reobf\minecraft
7z a MagicChest.zip mcmod.info MagicChest org
move MagicChest.zip C:\Users\marco_000\AppData\Roaming\.minecraft\mods