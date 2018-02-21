attrib -R desktop\build\jfx\native /d /s
attrib -R desktop\build\jfx\native\GhostJumpers\GhostJumpers.exe
mt.exe -manifest "GhostJumpers.manifest" -outputresource:"desktop\build\jfx\native\GhostJumpers\GhostJumpers.exe;#1"