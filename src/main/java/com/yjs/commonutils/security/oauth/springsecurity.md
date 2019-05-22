#Spring Security

> 概念：https://www.jianshu.com/p/cb886f995e86

	Spring Security是一套安全框架，可以基于RBAC（基于角色的权限控制）对用户的访问权限进行控制，核心思想是通过一系列的filter chain来进行拦截过滤，以下是ss中默认的内置过滤器列表，当然你也可以通过custom-filter来自定义扩展filter chain列表

	这里面最核心的就是FILTER_SECURITY_INTERCEPTOR，通过FilterInvocationSecurityMetadataSource来进行资源权限的匹配，AccessDecisionManager来执行访问策略。


##认证与授权（Authentication and Authorization）

	一般意义来说的应用访问安全性，都是围绕认证（Authentication）和授权（Authorization）这两个核心概念来展开的。即首先需要确定用户身份，在确定这个用户是否有访问指定资源的权限。认证这块的解决方案很多，主流的有CAS、SAML2、OAUTH2等（不巧这几个都用过-_-），我们常说的单点登录方案（SSO）说的就是这块，授权的话主流的就是spring security和shiro。shiro我没用过，据说是比较轻量级，相比较而言spring security确实架构比较复杂。


## JWT介绍
	终于来到了著名的JWT部分了，JWT全称为Json Web Token，最近随着微服务架构的流行而越来越火，号称新一代的认证技术。今天我们就来看一下，jwt的本质到底是什么。
	我们先来看一下OAuth2的token技术有没有什么痛点，相信从之前的介绍中你也发现了，token技术最大的问题是不携带用户信息，且资源服务器无法进行本地验证，每次对于资源的访问，资源服务器都需要向认证服务器发起请求，一是验证token的有效性，二是获取token对应的用户信息。如果有大量的此类请求，无疑处理效率是很低的，且认证服务器会变成一个中心节点，对于SLA和处理性能等均有很高的要求，这在分布式架构下是很要命的。
	JWT就是在这样的背景下诞生的，从本质上来说，jwt就是一种特殊格式的token。普通的oauth2颁发的就是一串随机hash字符串，本身无意义，而jwt格式的token是有特定含义的，分为三部分：

	头部Header

	载荷Payload

	签名Signature


	这三部分均用base64进行编码，当中用.进行分隔，一个典型的jwt格式的token类似xxxxx.yyyyy.zzzzz。关于jwt格式的更多具体说明，不是本文讨论的重点，大家可以直接去官网查看官方文档，这里不过多赘述。

## JWT适用场景与不适用场景
	就像布鲁克斯在《人月神话》中所说的名言一样：“没有银弹”。JWT的使用上现在也有一种误区，认为传统的认证方式都应该被jwt取代。事实上，jwt也不能解决一切问题，它也有适用场景和不适用场景。
	适用场景：

	一次性的身份认证
	api的鉴权

	这些场景能充分发挥jwt无状态以及分布式验证的优势
	不适用的场景：

	传统的基于session的用户会话保持