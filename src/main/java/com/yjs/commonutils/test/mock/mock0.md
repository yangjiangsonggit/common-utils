使用MockRestServiceServer测试REST客户端
发布者： 乔尔帕特里克Llosa 在 春天  2017年9月8日 0  7509意见

本文是MockRestServiceServer用于测试REST客户端的示例。MockRestServiceServer是Spring库的一部分，用于测试。它是客户端REST测试的主要入口点。它提供了来自预期请求的模拟响应RestTemplate。它消除了实际服务器的使用，从而加快了测试过程。

通过其操作，我们可以验证是否已执行所有请求。我们可以重置内部状态，从而消除所有期望和要求。我们可以为单个或多个HTTP请求设置期望。
 
 
 

想掌握Spring Framework吗？订阅我们的时事通讯并立即下载Spring Framework Cookbook ！为了帮助您掌握领先和创新的Java框架，我们编写了一个带有所有主要功能和用例的kick-ass指南！除了在线学习，您可以下载PDF格式的电子书！现在下载！
1.假设
本文直接讨论了如何使用MockRestServiceServer测试REST客户端。假设读者熟悉以下一些概念：

弹簧
Mars Eclipse或任何IDE
JUnit的
Apache Maven
2.代码示例
ExampleRestService在com.javacodegeeks.example.service主包中找到。

ExampleRestService.java


package com.javacodegeeks.example.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
 
@Service
public class ExampleRestService {
     
    @Autowired
    private RestTemplate restTemplate;
     
    public String getRootResource() {
        String result = restTemplate.getForObject("http://localhost:8080", String.class);
        System.out.println("getRootResource: " + result);
         
        return result;
    }
     
    public String addComment(String comment) {
        String result = null;
        try {
            result = restTemplate.postForObject("http://localhost/add-comment", comment, String.class);
            System.out.println("addComment: " + result);
        } catch (HttpClientErrorException e) {
            result = e.getMessage();
        }
         
        return result;
    }
 
}
的ExampleRestService类是一个服务层类。使用服务层的公共层是表示层。提供两种服务ExampleRestService，getRootResource和addComment。该getRootResource操作使用URL RestTemplate与URL通信，并将结果返回给表示层。名为的第二个操作addComment接受来自表示层的注释，然后通过它将其发布到URL RestTemplate。然后，它将结果返回到表示层，指定它是否成功。试想一下，该addComment操作是在Facebook帖子上添加评论。

为了测试ExampleRestService，我们有ExampleRestServiceTestViaRestGateway和ExampleRestServiceTest在com.javacodegeeks.example.service测试包。

ExampleRestServiceTestViaRestGateway.java


package com.javacodegeeks.example.service;
 
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
 
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;
 
import com.javacodegeeks.example.service.ExampleRestService;
 
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExampleRestServiceTestViaRestGateway {
     
    @Autowired
    RestTemplate restTemplate;
 
    @Autowired
    ExampleRestService service;
 
    private MockRestServiceServer mockServer;
 
    @Before
    public void setUp() {
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(gateway);
    }
     
    @Test
    public void testGetRootResourceOnce() {
        mockServer.expect(once(), requestTo("http://localhost:8080"))
            .andRespond(withSuccess("{message : 'under construction'}", MediaType.APPLICATION_JSON));
 
        String result = service.getRootResource();
        System.out.println("testGetRootResourceOnce: " + result);
 
        mockServer.verify();
        assertEquals("{message : 'under construction'}", result);
    }
 
}
ExampleRestServiceTestViaRestGateway该类将ExampleRestService通过模拟REST服务器来测试该类。如图所示ExampleRestService，REST服务器是localhost。模拟服务器不是运行实际的REST服务器，而是用于模拟实际的REST服务器。虚假服务器是使用Spring创建的MockRestServiceServer。这使得测试便宜且快速。回归测试可以每天进行几次。

该@RunWith注解意味着，而不是使用内置的JUnit测试运行，SpringRunner.class将成为测试运行。SpringRunner是新的名字SpringJUnit4ClassRunner。该@SpringBootTest办法还增加了Spring的引导支持的测试（如XML配置）。该@Autowired注解告诉Spring去哪里应该发生的注入。例如，Spring将自动创建一个类型的bean RestTemplate并将其注入到restTemplate字段中。该@Before注解告诉测试运行，它应该每次测试之前调用。对于每个测试，RestGatewaySupport都会创建一个new 并创建一个new MockRestServiceServer。@Test方法的注释将其标记为单个测试。这是实际测试。testGetRootResourceOnceREST服务器ExampleRestService将与via进行通信的模拟RestTemplate。此测试期望ExampleRestService仅调用REST服务器（HTTP请求）一次。它期望REST服务器的URL为http：// localhost：8080，并将使用JSON消息成功响应。如果不满足这些期望，验证方法将无法通过此测试。

该线“String result = service.getRootResource();”测试的getRootResource方法ExampleRestService。assertEquals下面的行检查预期结果是否等于getRootResource方法返回的实际结果。如果测试相同，则测试通过。

ExampleRestServiceTest.java


package com.javacodegeeks.example.service;
 
import static org.junit.Assert.assertEquals;
 
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
 
import com.javacodegeeks.example.service.ExampleRestService;
 
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.ExpectedCount.times;
 
@RunWith(SpringRunner.class) //SpringRunner is an alias for the SpringJUnit4ClassRunner
@SpringBootTest
public class ExampleRestServiceTest {
 
    @Autowired
    RestTemplate restTemplate;
 
    @Autowired
    ExampleRestService service;
 
    private MockRestServiceServer mockServer;
 
    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
 
    @Test
    public void testGetRootResource() {
        mockServer.expect(requestTo("http://localhost:8080")).andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
 
        String result = service.getRootResource();
        System.out.println("testGetRootResource: " + result);
 
        mockServer.verify();
        assertEquals("hello", result);
    }
     
    @Test
    public void testGetRootResourceOnce() {
        mockServer.expect(once(), requestTo("http://localhost:8080"))
            .andRespond(withSuccess("{message : 'under construction'}", MediaType.APPLICATION_JSON));
 
        String result = service.getRootResource();
        System.out.println("testGetRootResourceOnce: " + result);
 
        mockServer.verify();
        assertEquals("{message : 'under construction'}", result);
    }
     
    @Test
    public void testGetRootResourceTimes() {
        mockServer.expect(times(2), requestTo("http://localhost:8080"))
            .andRespond(withSuccess("{message : 'under construction'}", MediaType.APPLICATION_JSON));
 
        String result = service.getRootResource();
        System.out.println("testGetRootResourceTimes: " + result);
 
        mockServer.verify(); // should fail because this test expects RestTemplate.getForObject to be called twice 
        assertEquals("{message : 'under construction'}", result);
    }
     
    @Test
    public void testAddComment() {
        mockServer.expect(requestTo("http://localhost/add-comment")).andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{post : 'success'}", MediaType.APPLICATION_JSON));
 
        String result = service.addComment("cute puppy");
        System.out.println("testAddComment: " + result);
 
        mockServer.verify();
        assertEquals("{post : 'success'}", result);
    }
     
    @Test
    public void testAddCommentClientError() {
        mockServer.expect(requestTo("http://localhost/add-comment")).andExpect(method(HttpMethod.POST))
            .andRespond(withBadRequest());
 
        String result = service.addComment("cute puppy");
        System.out.println("testAddCommentClientError: " + result);
 
        mockServer.verify();
        assertEquals("400 Bad Request", result);
    }
     
    @Test
    public void testReset() {
        mockServer.expect(requestTo("http://localhost/add-comment")).andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{post : 'success'}", MediaType.APPLICATION_JSON));
 
        String result = service.addComment("cute puppy");
        System.out.println("testReset 1st: " + result);
 
        mockServer.verify();
        assertEquals("{post : 'success'}", result);
         
        mockServer.reset();
         
        mockServer.expect(requestTo("http://localhost:8080")).andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
 
        result = service.getRootResource();
        System.out.println("testReset 2nd: " + result);
 
        mockServer.verify();
        assertEquals("hello", result);
    }
}
该ExampleRestServiceTest还测试ExampleRestService。与前面示例的不同之处在于此测试用于RestTemplate创建服务器而不是RestGateWaySupport。此类中使用的注释与前一个示例相同。该类包含六个测试，其中一个测试旨在失败。

在该testGetRootResource方法中，如果未指定预期计数（例如，once（）），则默认情况下它期望单个HTTP请求。

的testGetRootResourceTimes，因为它需要两个HTTP请求到REST服务器，但将失败ExampleRestService只调用RestTemplate’s getForObject方法一次。

该testAddComment方法模拟HTTP POST请求。模拟服务器有两个期望，一个指定的URL和某些HTTP请求方法。

在testAddCommentClientError，模拟客户端错误。模拟服务器返回表示HTTP客户端错误的HTTP状态代码（例如，格式错误的请求）。

在该testReset方法中，服务被调用两次。模拟服务器没有通过测试失败，因为进行了重置（即，mockServer.reset()在每次单个调用之间调用）。该MockRestServiceServer复位操作删除所有的期望和要求记录。

3.异步代码示例
AsyncExampleRestService在com.javacodegeeks.example.service主包中找到。

AsyncExampleRestService.java


package com.javacodegeeks.example.service;
 
import java.util.concurrent.ExecutionException;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpServerErrorException;
 
@Service
public class AsyncExampleRestService {
 
    @Autowired
    private AsyncRestTemplate asyncRestTemplate;
     
    public String deleteAllSuspendedUsers() {
        ListenableFuture future = asyncRestTemplate.delete("http://localhost/delete-all-suspended-users");
        // doing some long process here...
        Object result = null;
        String returnValue = "";
        try {
            result = future.get(); //The Future will return a null result upon completion.
            if (result == null) {
                returnValue = "{result:'success'}";
            } else {
                returnValue = "{result:'fail'}";
            }
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof HttpServerErrorException) {
                returnValue = "{result: 'server error'}";
            }
        }
        System.out.println("deleteAllSuspendedUsers: " + result);
         
        return returnValue;
    }
}
AsyncExampleRestService使用Spring的AsyncRestTemplate类来访问REST服务器。AsyncRestTemplate类似于RestTemplate并用于异步客户端HTTP访问。此服务中的操作是模拟删除所有可能需要大量时间的已暂停用户。这就是我们以异步方式进行的原因。它不需要等待或阻塞来执行下一行代码。如果调用完成，future.get()语句将阻塞并返回null， AsyncRestTemplate.delete或者Exception在出现错误时抛出该语句。

为了测试AsyncExampleRestService，我们AsyncExampleRestServiceTest在com.javacodegeeks.example.service测试包中。

AsyncExampleRestServiceTest.java


package com.javacodegeeks.example.service;
 
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
 
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.AsyncRestTemplate;
 
import com.javacodegeeks.example.service.AsyncExampleRestService;
 
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncExampleRestServiceTest {
 
    @Autowired
    AsyncRestTemplate asyncRestTemplate;
 
    @Autowired
    AsyncExampleRestService service;
 
    private MockRestServiceServer mockServer;
 
    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(asyncRestTemplate);
    }
 
    @Test
    public void testDeleteAllSuspendedUsers() {
        mockServer.expect(requestTo("http://localhost/delete-all-suspended-users")).andExpect(method(HttpMethod.DELETE))
            .andRespond(withServerError());
 
        String result = service.deleteAllSuspendedUsers();
        System.out.println("testDeleteAllSuspendedUsers: " + result);
 
        mockServer.verify();
        assertEquals("{result: 'server error'}", result);
    }
 
}
该testDeleteAllSuspendedUsers方法与其他测试方法类似。不同之处在于模拟服务器需要HTTP DELETE请求和服务器错误响应。服务层返回JSON字符串，而不是HTTP状态代码5xx（例如，500 - 内部服务器错误）来指示服务器错误。由... HttpServerErrorException处理AsyncExampleRestService，然后返回一个JSON字符串来表示错误。该withServerError调用触发虚假服务器生成服务器错误。

4.运行测试
测试输出应如下所示：
 
ExampleRestServiceTest Output

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v1.5.1.RELEASE)
 
2017-09-07 14:10:49.438  INFO 7916 --- [           main] c.j.e.service.ExampleRestServiceTest     : Starting ExampleRestServiceTest on asus_k43s with PID 7916 (started by jpllosa in D:\javacodegeeks_com\mockrestserviceserver\mockrestserviceserver-example)
2017-09-07 14:10:49.441  INFO 7916 --- [           main] c.j.e.service.ExampleRestServiceTest     : No active profile set, falling back to default profiles: default
2017-09-07 14:10:49.621  INFO 7916 --- [           main] o.s.w.c.s.GenericWebApplicationContext   : Refreshing org.springframework.web.context.support.GenericWebApplicationContext@22fcf7ab: startup date [Thu Sep 07 14:10:49 BST 2017]; root of context hierarchy
2017-09-07 14:10:52.386  INFO 7916 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration' of type [class org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2017-09-07 14:10:52.567  INFO 7916 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'validator' of type [class org.springframework.validation.beanvalidation.LocalValidatorFactoryBean] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2017-09-07 14:10:54.738  INFO 7916 --- [           main] s.w.s.m.m.a.RequestMappingHandlerAdapter : Looking for @ControllerAdvice: org.springframework.web.context.support.GenericWebApplicationContext@22fcf7ab: startup date [Thu Sep 07 14:10:49 BST 2017]; root of context hierarchy
2017-09-07 14:10:55.028  INFO 7916 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/]}" onto public java.lang.String com.javacodegeeks.example.MockRestServiceServerExample.getRootResource()
2017-09-07 14:10:55.048  INFO 7916 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error]}" onto public org.springframework.http.ResponseEntity<java.util.Map> org.springframework.boot.autoconfigure.web.BasicErrorController.error(javax.servlet.http.HttpServletRequest)
2017-09-07 14:10:55.052  INFO 7916 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error],produces=}" onto public org.springframework.web.servlet.ModelAndView org.springframework.boot.autoconfigure.web.BasicErrorController.errorHtml(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
2017-09-07 14:10:55.237  INFO 7916 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/webjars/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2017-09-07 14:10:55.238  INFO 7916 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2017-09-07 14:10:55.392  INFO 7916 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**/favicon.ico] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2017-09-07 14:10:55.839  INFO 7916 --- [           main] c.j.e.service.ExampleRestServiceTest     : Started ExampleRestServiceTest in 7.163 seconds (JVM running for 9.03)
getRootResource: {message : 'under construction'}
testGetRootResourceOnce: {message : 'under construction'}
addComment: {post : 'success'}
testReset 1st: {post : 'success'}
getRootResource: hello
testReset 2nd: hello
testAddCommentClientError: 400 Bad Request
getRootResource: hello
testGetRootResource: hello
getRootResource: {message : 'under construction'}
testGetRootResourceTimes: {message : 'under construction'}
addComment: {post : 'success'}
testAddComment: {post : 'success'}
2017-09-07 14:10:56.235  INFO 7916 --- [       Thread-3] o.s.w.c.s.GenericWebApplicationContext   : Closing org.springframework.web.context.support.GenericWebApplicationContext@22fcf7ab: startup date [Thu Sep 07 14:10:49 BST 2017]; root of context hierarchy
5.使用MockRestServiceServer测试REST客户端摘要
MockRestServiceServer用于测试客户端。我们应该RestTemplate使用我们的生产代码使用的实例来创建它的实例。我们不会RestTemplate在测试中创建新的。
在每次测试之后，verify必须在RestTemplate调用以运行MockRestServiceServer断言之后调用该方法。

6.下载源代码
 
这是MockRestServiceServer用于测试REST客户端的示例。