
set current_dir=%~dp0%
set install_root=%current_dir%
set java_home=%install_root%jdk1.8.0_131\
set path=%java_home%jre\bin;%path%
set CLASSPATH=.;%java_home%lib\dt.jar;%java_home%lib\tools.jar;%install_root%utils\utils.par\utils.jar;%install_root%spy\spy.par\twsapi-spy.jar;%install_root%apidemo\apidemo.par\twsapi-apidemo.jar;%install_root%javaclient\javaclient.par\twsapi-javaclient.jar;
set java="%java_home%bin\java.exe"
set otherjar=%install_root%utils\utils.par\utils.jar;%install_root%spy\spy.par\twsapi-spy.jar;%install_root%apidemo\apidemo.par\twsapi-apidemo.jar;%install_root%javaclient\javaclient.par\twsapi-javaclient.jar;

for %%i in (%install_root%lib\*.*) do call %install_root%cpappend.bat %%i


set port=8788

set jvm_opts=-Xdebug -Xrunjdwp:transport=dt_socket,address=%port%,server=y,suspend=n -XXaltjvm=dcevm -javaagent:C:\hotswap\hotswap-agent-1.1.0-SNAPSHOT.jar -Xbootclasspath/p:%otherjar%

set x=%~dp0%spy\spy.par\twsapi-spy.jar
java  -jar %jvm_opts%  %x% %*
