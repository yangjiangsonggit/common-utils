##oracle和mysql的区别
    MySQL和Oracle都是流行的关系数据库管理系统（RDBMS），在世界各地广泛使用；大多数数据库以类似的方式工作，但MySQL和Oracle的这里和那里总是存在一些差异的。本篇文章就给大家比较Oracle和MySQL，介绍Oracle和MySQL之间的区别，希望对你们有所帮助。
    
    
    MySQL和Oracle有什么区别？两个数据库的特性是不同的，所以与Oracle相比，MySQL的使用方式不同；与MySQL相比，Oracle的使用情况有所不同。它们的特点也是不同的。下面我们就来具体看看MySQL和Oracle的区别有哪些。
    
    1、本质的区别
    
        Oracle数据库是一个对象关系数据库管理系统（ORDBMS）。它通常被称为Oracle RDBMS或简称为Oracle，是一个收费的数据库。
        MySQL是一个开源的关系数据库管理系统（RDBMS）。它是世界上使用最多的RDBMS，作为服务器运行，提供对多个数据库的多用户访问。它是一个开源、免费的数据库。
    
    2、数据库安全性
    
        MySQL使用三个参数来验证用户，即用户名，密码和位置；Oracle使用了许多安全功能，如用户名，密码，配置文件，本地身份验证，外部身份验证，高级安全增强功能等。
    
    3、SQL语法的区别
    
        Oracle的SQL语法与MySQL有很大不同。Oracle为称为PL / SQL的编程语言提供了更大的灵活性。Oracle的SQL * Plus工具提供了比MySQL更多的命令，用于生成报表输出和变量定义。
    
    4、存储上的区别：
    
        与Oracle相比，MySQL没有表空间，角色管理，快照，同义词和包以及自动存储管理。
    
    5、对象名称的区别：
    
        虽然某些模式对象名称在Oracle和MySQL中都不区分大小写，例如列，存储过程，索引等。但在某些情况下，两个数据库之间的区分大小写是不同的。
        Oracle对所有对象名称都不区分大小写；而某些MySQL对象名称（如数据库和表）区分大小写（取决于底层操作系统）。
    
    6、运行程序和外部程序支持：
    
        Oracle数据库支持从数据库内部编写，编译和执行的几种编程语言。此外，为了传输数据，Oracle数据库使用XML。
        MySQL不支持在系统内执行其他语言，也不支持XML。
    
    7、MySQL和Oracle的字符数据类型比较：
    
        两个数据库中支持的字符类型存在一些差异。对于字符类型，MySQL具有CHAR和VARCHAR，最大长度允许为65,535字节（CHAR最多可以为255字节，VARCHAR为65.535字节）。
        而，Oracle支持四种字符类型，即CHAR，NCHAR，VARCHAR2和NVARCHAR2; 所有四种字符类型都需要至少1个字节长; CHAR和NCHAR最大可以是2000个字节，NVARCHAR2和VARCHAR2的最大限制是4000个字节。可能会在最新版本中进行扩展。
    
    8、MySQL和Oracle的额外功能比较：
    
        MySQL数据库不支持其服务器上的任何功能，如Audit Vault。另一方面，Oracle支持其数据库服务器上的几个扩展和程序，例如Active Data Guard，Audit Vault，Partitioning和Data Mining等。
    
    9、临时表的区别：
    
        Oracle和MySQL以不同方式处理临时表。
        在MySQL中，临时表是仅对当前用户会话可见的数据库对象，并且一旦会话结束，这些表将自动删除。
        Oracle中临时表的定义与MySQL略有不同，因为临时表一旦创建就会存在，直到它们被显式删除，并且对具有适当权限的所有会话都可见。但是，临时表中的数据仅对将数据插入表中的用户会话可见，并且数据可能在事务或用户会话期间持续存在。
    
    10、MySQL和Oracle中的备份类型：
    
        Oracle提供不同类型的备份工具，如冷备份，热备份，导出，导入，数据泵。Oracle提供了最流行的称为Recovery Manager（RMAN）的备份实用程序。使用RMAN，我们可以使用极少的命令或存储脚本自动化我们的备份调度和恢复数据库。
        MySQL有mysqldump和mysqlhotcopy备份工具。在MySQL中没有像RMAN这样的实用程序。
    
    11、Oracle和MySQL的数据库管理：
    
        在数据库管理部分，Oracle DBA比MySQL DBA更有收益。与MySQL相比，Oracle DBA有很多可用的范围。
    
    12、数据库的认证：
    
        MySQL认证比Oracle认证更容易。
        与Oracle（设置为使用数据库身份验证时）和大多数仅使用用户名和密码对用户进行身份验证的其他数据库不同，MySQL在对用户进行身份验证location时会使用其他参数。此location参数通常是主机名，IP地址或通配符。
        使用此附加参数，MySQL可以进一步将用户对数据库的访问限制为域中的特定主机或主机。此外，这还允许根据进行连接的主机为用户强制实施不同的密码和权限集。因此，从abc.com登录的用户scott可能与从xyz.com登录的用户scott相同或不同。