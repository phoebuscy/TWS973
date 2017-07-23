if ""%1"" == """" goto end
set otherjar=%otherjar%;%1
shift
rem Process the remaining arguments
:setArgs
if ""%1"" == """" goto doneSetArgs
set otherjar=%otherjar% %1
shift
goto setArgs
:doneSetArgs
:end