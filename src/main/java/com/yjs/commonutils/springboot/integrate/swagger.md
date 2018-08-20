前言



前一章节介绍了mybatisPlus的集成和简单使用，本章节开始接着上一章节的用户表，进行Swagger2的集成。现在都奉行前后端分离开发和微服务大行其道，
分微服务及前后端分离后，前后端开发的沟通成本就增加了。所以一款强大的RESTful API文档就至关重要了。而目前在后端领域，基本上是Swagger的天下了。



Swagger2介绍



Swagger是一款RESTful接口的文档在线自动生成、功能测试功能框架。一个规范和完整的框架，用于生成、描述、调用和可视化RESTful风格的Web服务，
加上swagger-ui，可以有很好的呈现。



95150384



SpringBoot集成



这里选用的swagger版本为：2.8.0



0.pom依赖



<!--swagger -->

<dependency>

    <groupId>io.springfox</groupId>

    <artifactId>springfox-swagger2</artifactId>

    <version>2.8.0</version>

</dependency>

<dependency>

    <groupId>io.springfox</groupId>

    <artifactId>springfox-swagger-ui</artifactId>

    <version>2.8.0</version>

</dependency>



1. 编写配置文件(Swagger2Config.java)



主要是添加注解@EnableSwagger2和定义Docket的bean类。



@EnableSwagger2

@Configuration

public class SwaggerConfig {

 

    //是否开启swagger，正式环境一般是需要关闭的，可根据springboot的多环境配置进行设置

    @Value(value = "${swagger.enabled}")

    Boolean swaggerEnabled;

 

    @Bean

    public Docket createRestApi() {

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())

                // 是否开启

                .enable(swaggerEnabled).select()

                // 扫描的路径包

                .apis(RequestHandlerSelectors.basePackage("cn.lqdev.learning.springboot.chapter10"))

                // 指定路径处理PathSelectors.any()代表所有的路径

                .paths(PathSelectors.any()).build().pathMapping("/");

    }

 

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()

                .title("SpringBoot-Swagger2集成和使用-demo示例")

                .description("oKong | 趔趄的猿")

                // 作者信息

                .contact(new Contact("oKong", "https://blog.lqdev.cn/", "499452441@qq.com"))

                .version("1.0.0")

                .build();

    }

}





3.添加文档内容(一般上是在Controller，请求参数上进行注解，这里以上章节的UserController进行配置)



UserController



/**

 * 用户控制层 简单演示增删改查及分页

 * 新增了swagger文档内容 2018-07-21

 * @author oKong

 *

 */

@RestController

@RequestMapping("/user")

@Api(tags="用户API")

public class UserController {

 

    @Autowired

    IUserService userService;

 

    @PostMapping("add")

    @ApiOperation(value="用户新增")

    //正常业务时， 需要在user类里面进行事务控制，控制层一般不进行业务控制的。

    //@Transactional(rollbackFor = Exception.class)

    public Map<String,String> addUser(@Valid @RequestBody UserReq userReq){

 

        User user = new User();

        user.setCode(userReq.getCode());

        user.setName(userReq.getName());

        //由于设置了主键策略 id可不用赋值 会自动生成

        //user.setId(0L);

        userService.insert(user);

        Map<String,String> result = new HashMap<String,String>();

        result.put("respCode", "01");

        result.put("respMsg", "新增成功");

        //事务测试

        //System.out.println(1/0);

        return result;

    }

 

    @PostMapping("update")

    @ApiOperation(value="用户修改")    

    public Map<String,String> updateUser(@Valid @RequestBody UserReq userReq){

 

        if(userReq.getId() == null || "".equals(userReq.getId())) {

            throw new CommonException("0000", "更新时ID不能为空");

        }

        User user = new User();

        user.setCode(userReq.getCode());

        user.setName(userReq.getName());

        user.setId(Long.parseLong(userReq.getId()));        

        userService.updateById(user);

        Map<String,String> result = new HashMap<String,String>();

        result.put("respCode", "01");

        result.put("respMsg", "更新成功");

        return result;

    }

 

    @GetMapping("/get/{id}")

    @ApiOperation(value="用户查询(ID)")    

    @ApiImplicitParam(name="id",value="查询ID",required=true)

    public Map<String,Object> getUser(@PathVariable("id") String id){

        //查询

        User user = userService.selectById(id);

        if(user == null) {

            throw new CommonException("0001", "用户ID：" + id + "，未找到");

        }

        UserResp resp = UserResp.builder()

                .id(user.getId().toString())

                .code(user.getCode())

                .name(user.getName())

                .status(user.getStatus())

                .build();

        Map<String,Object> result = new HashMap<String,Object>();

        result.put("respCode", "01");

        result.put("respMsg", "成功");

        result.put("data", resp);

        return result;

    }

 

    @GetMapping("/page")

    @ApiOperation(value="用户查询(分页)")        

    public Map<String,Object> pageUser(int current, int size){

        //分页

        Page<User> page = new Page<>(current, size);

        Map<String,Object> result = new HashMap<String,Object>();

        result.put("respCode", "01");

        result.put("respMsg", "成功");

        result.put("data", userService.selectPage(page));

        return result;

    }

 

}



UserReq.java



@Data

@Builder

@NoArgsConstructor

@AllArgsConstructor

//加入@ApiModel

@ApiModel

public class UserReq {

 

    @ApiModelProperty(value="ID",dataType="String",name="ID",example="1020332806740959233")

    String id;

 

    @ApiModelProperty(value="编码",dataType="String",name="code",example="001")

    @NotBlank(message = "编码不能为空")

    String code;

 

    @ApiModelProperty(value="名称",dataType="String",name="name",example="oKong")

    @NotBlank(message = "名称不能为空")

    String name;

}



Swagger访问与使用



api首页路径：http://127.0.0.1:8080/swagger-ui.html



调试：点击需要访问的api列表，点击try it out!按钮，即可弹出一下页面：



16146099



执行：



92397999



结果：



64512351



大家可下载示例，查看自定义的字符出现的位置，这样可以对其有个大致了解，各字段的作用领域是哪里。



Swagger常用属性说明



Snipaste_2018-08-18_12-37-51



常用的注解@Api、@ApiOperation、@ApiModel、@ApiModelProperty示例中有进行标注，对于其他注解，大家可自动谷歌，毕竟常用的就这几个了。
有了swagger之后，原本一些post请求需要postman这样的调试工具来进行发起，而现在直接在页面上就可以进行调试了，是不是很爽！对于服务的调用者
而已，有了这份api文档也是一目了然，不需要和后端多少沟通成本，按着api说明进行前端开发即可。



总结



本章节主要是对Swagger的集成和简单使用进行了说明，详细的用法，可自行搜索相关资料下，这里就不阐述了。因为对于百分之八十之上的文档要求基本
能满足了。一些比如前端根据swagger的api-docs进行前端的快速开发，这就需要实际情况实际约定了，比如快速的生成表单页等也是很方便的事情。
最后，强烈建议在生产环境关闭swagger,避免不必要的漏洞暴露！