XML中DTD,XSD的区别与应用


<?xml version="1.0" encoding="utf-8"?>

1.DTD(Documnet Type Definition)
DTD即文档类型定义，是一种XML约束模式语言，是XML文件的验证机制,属于XML文件组成的一部分。
DTD 是一种保证XML文档格式正确的有效方法，可以通过比较XML文档和DTD文件来看文档是否符合规范，元素和标签使用是否正确。
一个 DTD文档包含：
元素的定义规则，元素间关系的定义规则，元素可使用的属性，可使用的实体或符号规则。 DTD和XSD相比：DTD 是使用非 XML 语法编写的。 DTD 不可扩展,不支持命名空间,只提供非常有限的数据类型 .

*没有深入1.0 跟 2.0的区别 *

<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN //EN" 
"http://www.springframework.org/dtd/spring-beans.dtd">
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN"
"http://www.springframework.org/dtd/spring-beans-2.0.dtd">
**2.XSD(XML Schemas Definition) **
XML Schema语言也就是XSD。XML Schema描述了XML文档的结构。 可以用一个指定的XML Schema来验证某个XML文档，以检查该XML文档是否符合其要求。文档设计者可以通过XML Schema指定一个XML文档所允许的结构和内容，并可据此检查一个XML文档是否是有效的。XML Schema本身是一个XML文档，它符合XML语法结构。可以用通用的XML解析器解析它。 一个XML Schema会定义：文档中出现的元素、文档中出现的属性、子元素、子元素的数量、子元素的顺序、元素是否为空、元素和属性的数据类型、元素或属性的默认 和固定值。

XSD是DTD替代者的原因，一是据将来的条件可扩展，二是比DTD丰富和有用，三是用XML书写，四是支持数据类型，五是支持命名空间。

XML Schema的优点:

XML Schema基于XML,没有专门的语法
XML Schema可以象其他XML文件一样解析和处理
XML Schema比DTD提供了更丰富的数据类型.
XML Schema提供可扩充的数据模型。
XML Schema支持综合命名空间
XML Schema支持属性组。
<beans xmlns="http://www.springframework.org/schema/beans"  
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"   
    xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans-2.5.xsd>  