:: pour ouvrir les fichiers XML avec Jaxe

assoc .xml=xmlfile

set diresc=%cd:\=\\%

> "%Temp%.\JaxeAssoc.reg" echo Windows Registry Editor Version 5.00
>>"%Temp%.\JaxeAssoc.reg" echo.
>>"%Temp%.\JaxeAssoc.reg" echo [HKEY_CLASSES_ROOT\xmlfile]
>>"%Temp%.\JaxeAssoc.reg" echo @="XML Document"
>>"%Temp%.\JaxeAssoc.reg" echo.
>>"%Temp%.\JaxeAssoc.reg" echo [HKEY_CLASSES_ROOT\xmlfile\DefaultIcon]
>>"%Temp%.\JaxeAssoc.reg" echo @="\"%diresc%\\Jaxe.exe\",1"
>>"%Temp%.\JaxeAssoc.reg" echo.
>>"%Temp%.\JaxeAssoc.reg" echo [HKEY_CLASSES_ROOT\xmlfile\shell]
>>"%Temp%.\JaxeAssoc.reg" echo @="edit_with_jaxe"
>>"%Temp%.\JaxeAssoc.reg" echo.
>>"%Temp%.\JaxeAssoc.reg" echo [HKEY_CLASSES_ROOT\xmlfile\shell\edit_with_jaxe]
>>"%Temp%.\JaxeAssoc.reg" echo @="Edit with Jaxe"
>>"%Temp%.\JaxeAssoc.reg" echo.
>>"%Temp%.\JaxeAssoc.reg" echo [HKEY_CLASSES_ROOT\xmlfile\shell\edit_with_jaxe\command]
>>"%Temp%.\JaxeAssoc.reg" echo @="\"%diresc%\\Jaxe.exe\" \"%%1\""
>>"%Temp%.\JaxeAssoc.reg" echo.

START /WAIT REGEDIT /S "%Temp%.\JaxeAssoc.reg"

del "%Temp%.\JaxeAssoc.reg"
