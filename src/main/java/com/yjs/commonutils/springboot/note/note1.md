##SpringBoot全局异常与数据校验

异常处理是每个项目中都绕不开的话题，那么如何优雅的处理异常，是本文的话题。本文将结合SpringBoot框架一起和大家探讨下。

要思考的问题
在现在的前后端交互中，通常都规范了接口返回方式，如返回的接口状态（成功|失败）以及要返回的数据在那个字段取，或者说失败了以后提示信息从接口哪里返回，因此，如果想做全局异常，并且异常发生后能准确的返回给前端解析，那么需要异常发生时返回给前端的格式与正常失败场景的格式一致。

项目建立
利用idea 工具，很容易的搭建一个SpringBoot项目，要引入的maven依赖如下：

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
很简单，除了加入web功能还加入了我们需要用到的JSR-303校验框架。

定义成功失败 返回码
public class Code {

    /**
     * 成功
     */
    public static int SUCCESSED = 1;
    /**
     * 失败
     */
    public static int FAILED = -1;
}
定义接口返回响应实体
public class Response<T> implements Serializable{

   /**
    * 
    */
   private static final long serialVersionUID = 4250719891313555820L;
   /**
    * 返回结果集
    */
   private T result;
   /**
    * 返回消息
    */
   private String msg;
   /**
    * 响应码
    */
   private Integer code;
   //set get 略
}
全局异常拦截和验证
定义自定义业务异常
public class MyException extends RuntimeException {

    private static final long serialVersionUID = -5875371379845226068L;


    public MyException(){}

    public MyException(String msg){
        this.msg = msg ;
    }

    /**
     * 异常信息
     */
    private String msg ;

    /**
     * 具体异常码
     */
    private int code = Code.FAILED;
   get set 略 
编写全局异常控制器并对自定义异常做处理
@ControllerAdvice
public class GlobalExceptionHandler {
     private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
@ExceptionHandler(value = MyException.class)
@ResponseBody
public Response<String> myExceptionErrorHandler(MyException ex) throws Exception {
    logger.error("myExceptionErrorHandler info:{}",ex.getMessage());
    Response<String> r = new Response<>();
    r.setMsg(ex.getMsg());
    r.setCode(ex.getCode());
    return r;
}
编写controller模拟抛出业务异常
@RestController
@RequestMapping("/user")
public class UserController {
@PostMapping(value = "/update")
Response<Boolean> update(User user){

    //todo 此处为模拟异常抛出
    if(true){
        throw new MyException("更新失败");
    }
    //todo 此处为模拟返回
    Response<Boolean> response = new Response<>();
    response.setCode(Code.SUCCESSED);
    response.setResult(true);
    return  response;
}

}
postMan模拟请求接口，进行验证




数据绑定异常处理
通常我们操作数据的时候，不仅前端需要进行数据校验，后端也应当进行拦截和进行相应的错误提示，
JSR-303校验框架也是我们的一种选择。

编写实体`User`，并对属性进行注解控制
public class User {

    @NotNull(message = "用户名不能为空")
    private String userName;

    private  int age;
   //...
全局异常控制类加入拦截
@ControllerAdvice
public class GlobalExceptionHandler {
     private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

     @ExceptionHandler(value = BindException.class)
     @ResponseBody
     public Response<String> bindExceptionErrorHandler(BindException ex) throws Exception {
          logger.error("bindExceptionErrorHandler info:{}",ex.getMessage());
          Response<String> r = new Response<>();
          StringBuilder sb = new StringBuilder();
          FieldError fieldError = ex.getFieldError();
          sb.append(fieldError.getDefaultMessage());
          r.setMsg(sb.toString());
          r.setCode(Code.FAILED);
          return r;
     }
  //...
编写控制器
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping(value = "/add")
    Response<User> add(@Validated User user){

        //todo 此处为模拟返回
        Response<User> response = new Response<>();
        response.setCode(Code.SUCCESSED);
        response.setResult(new User());
        return  response;
    }
  //...
postMan模拟请求
不填写任何属性，模拟添加操作，准确进行拦截和报错



代码地址
代码地址：https://github.com/pengziliu/spring-boot-2.0-leaning
项目结构预览：



结尾
适合的才是最好的，每个团队都应摸索出自己的一套异常解决方案，本文所提仅针对业务异常，希望大家也能有所收获

推荐阅读
springboot整合RabbitMQ



从 Spring Cloud 看一个微服务框架的「五脏六腑」



从 Spring Cloud 看一个微服务框架的「五脏六腑」




微信扫一扫
关注该公众号