Mac OS使用SSL常见问题 



M6.3R1之前版本的老方案随着mac版本更新，已经无法支持在mac上使用
6.9R1开始支持mac10.10.5
7.1开始支持mac10.11
7.3R3开始支持mac10.12 和mac10.13（SSL 7.1R1也支持）
M6.3R1-M7.1之间版本若要支持mac10.12以及mac10.13需要更新mac10.12补丁包
查看版本信息包含macOS10.12_SP标记则说明更新了mac10.12补丁包
MAC10.12的支持包实现对MAC10.13的支持，MAC10.12补丁包向下兼容更新后支持低版本的MAC系统接入

mac10.12补丁包传送门：
https://bbs.sangfor.com.cn/forum.php?mod=viewthread&tid=22358

MAC电脑使用SSL VPN常见问题
问题1、MAC系统登录一直提示未安装控件或者提示安装失败 安装器遇到了一个错误导致安装失败

  
  


排查步骤：
①确认SSLVPN设备是否支持该Mac OS版本，具体支持参考第一节，若设备不支持，将SSLVPN设备升级版本或者更新mac10.12补丁
②更新了mac10.12补丁包之后mac电脑在安装新版本插件之前，存在旧的EasyConnectPlugin磁盘文件（这种情况是最常见的问题）。
由于Mac系统自身的原因，如果同一个软件加载了两个磁盘，那么系统安装软件的时候会去重定向最早加载的磁盘。这就导致安装的插件依然是旧的版本，所以重新的登录的时候由于服务端的版本号和本地插件的版本号不对应进入到登录失败页面。
解决方法：打开系统自带的磁盘工具【finder->应用程序,搜索 disk打开磁盘工具】，将所有的磁盘镜像推出如下图所示，然后再重新安装最新下载的dmg文件即可。如果是比较老的版本的dmg文件，安装之后还需要重启一下Safari

  

③在第一次安装插件的时候，由于误点不信任插件导致插件被禁止
解决方法：Safari－>偏好设置－>安全性－>互联网插件，找到EASYCONNECT_PLUGIN允许插件
 

④更新插件之后没有重启Safari
由于打开Safari有可能是已经加载了插件，如果不去重启的话Safari还是与已经加载的插件交互。
解决方法：清除Safari缓存，并重启Safari

总结：
在遇到MAC系统登录SSL VPN一直提示未安装控件问题并且确认当前SSL VPN支持当前MAC系统情况下的处理步骤：
①打开系统自带的磁盘工具（disk Utility.app），将所有的磁盘镜像推出
②Safari－>偏好设置－>安全性－>互联网插件，允许VPN插件
③safari清除缓存并重启
④重新打开safari登录SSL VPN并按照提示下载安装控件，安装完后重启safari然后重新登录








问题2、MAC系统登录VPN后提示：Failed to read the SANGFOR SSL virtual NIC或者“未能正确打开SANGFOR SSL虚拟网”


   



2.1可能是用户使用了其他vpn软件，系统上存在其他虚拟网卡与SSL VPN虚拟网卡冲突导致的问题，卸载冲突的虚拟网卡登录SSLVPN
排查步骤：
①.在【终端】上执行命令sudo kextstat |grep tun，查找系统上是否存在其他虚拟网卡，执行命令后的截图如下图所示，存在一个net.sf.tuntaposx.tun的虚拟网卡
 

②与用户确认此虚拟网卡是否可以卸载，如果可以卸载就卸载掉再登录vpn，卸载命令如下：
sudo  kextunload -b net.sf.tuntaposx.tun
这种虚拟网卡一般是其他vpn和翻墙软件的，所以卸载前最好和用户确认是否还需要使用



2.2在mac10.13由于系统新的安全机制变化，也会出现这个问题

  

解决方法：

a.系统偏好设置-安全性与隐私-通用，勾选[任何来源]如下图所示：
如果没有显示[任何来源]，请参考
http://www.feng.com/apple/tutorial/2016-09-27/MacOS-Sierra-features---security-adjustment_658157.shtml
1.如果需要恢复允许“任何来源”的选项，即关闭 Gatekeeper，请打开终端

　　2.然后使用 spctl 命令：

　　sudo spctl --master-disable

 

b.若看到有SANGFOR Technologies Company Limited 点击允许  （TV远程的用户可能没权限修改无法修改，需要本地操作），如果无法修改，尝试重启电脑在本地修改下

 




2.3 可能是文件夹权限不足导致插件无法运行
分别执行如下两条命令是否有下图相关报错
sudo /Applications/EasyConnect.app/Contents/Resources/kext/do.sh
sudo /Applications/EasyConnect.app/Contents/Resources/kext/StartL3VPN.sh
 

从上图提示可以看到：
Sudo :/etc/sudoers is world writable
Sudo: no valid sudoeeers sources found
说明/etc/sudoers存在权限问题
处理方法：执行命令sudo  chmod 0440  /etc/sudoers  
如果命令不能执行成功也可以在Finder打开对应的文件夹，右键查属性修改成如下图所示

 

执行成功之后在执行前面的命令，是否还有相关报错，没有则正常登录
VPN
sudo /Applications/EasyConnect.app/Contents/Resources/kext/do.sh
sudo /Applications/EasyConnect.app/Contents/Resources/kext/StartL3VPN.sh





问题3、配置了内网域名解析，MAC电脑登录SSL VPN后无法解析域名，windows电脑正常解析

原因分析：MAC电脑不支持【接入计算机使用此DNS服务器作为首选的DNS服务器】，设备勾选了此功能导致mac上无法解析

 
解决方法：
内网域名解析不启用【接入计算机使用此DNS服务器作为首选的DNS服务器】功能，改为DNS规则，注意DNS规则不支持后面带*通配符即google.*这种，不支持单独*
若要求客户端登录VPN后所有域名解析都走VPN进行解析，则可以按照下图设置：

    

若只是部分域名或者个别域名需要走VPN解析，则可以按照下图设置：
 


问题4、MAC电脑登录后L3VPN资源访问不了
排查过程：
  ①在终端输入netstat -rn查看路由表，若存在到资源网段的路由则说明L3VPN服务控件没有问题，若不正常则检查虚拟网卡是否有问题，详见上节内容
  ②确认设备配置是否启用了C/S服务压缩功能，MAC电脑不支持此功能，可以禁用C/S服务压缩功能后看MAC电脑使用L3VPN资源是否正常
 

③检查关联的L3VPN资源当中是否存在某个域名无法解析导致该资源无法下发给mac，如下图所示设备无法解析www.fjelfj.com导致mac登录之后连192.168.2.5也无法访问，
解决方法：删除该域名资源，或者给域名资源配置系统hosts
 
 
 
 
 原文地址:https://bbs.sangfor.com.cn/forum.php?mod=viewthread&tid=38040
 
 个人的电脑如果选择升级，执行以上步骤进行处理才能在执行idcvpn
 
 总结一下：
 sudo spctl --master-disable
 system Preferences->Security->信任任何软件源
 重启safair，登陆idcvpn 处理事情
 处理完毕后，记得在命令行执行
 sudo spctl --master-enable