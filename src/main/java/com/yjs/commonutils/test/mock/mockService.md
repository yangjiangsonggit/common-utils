# 基于动态代理 Mock dubbo 服务的实现方案

序言
背景概述
公司目前 Java 项目提供服务都是基于 Dubbo 框架的，而且 Dubbo 框架已经成为大部分国内互联网公司选择的一个基础组件。

在日常项目协作过程中，其实会碰到服务不稳定、不满足需求场景等情况，很多开发都会通过在本地使用 Mocktio 等单测工具作为自测辅助。那么，在联调、测试等协作过程中怎么处理？

其实，Dubbo 开发者估计也是遇到了这样的问题，所以提供了一个提供泛化服务注册的入口。但是在服务发现的时候有个弊端，就说通过服务发现去请求这个 Mock 服务的话，在注册中心必须只有一个服务有效，否则消费者会请求到其他非Mock服务上去。

为了解决这个问题，Dubbo 开发者又提供了泛化调用的入口。既支持通过注册中心发现服务，又支持通过 IP+PORT 去直接调用服务，这样就能保证消费者调用的是 Mock 出来的服务了。

以上泛化服务注册和泛化服务调用结合起来，看似已经是一个闭环，可以解决 Dubbo 服务的 Mock 问题。但是，结合日常工作使用时，会出现一些麻烦的问题：

服务提供方使用公用的注册中心，消费方无法准确调用
消费者不可能更改代码，去直连 Mock 服务
使用私有注册中心能解决以上问题，但是 Mock 最小纬度为 Method，一个 Service 中被 Mock 的 Method 会正常处理，没有被 Mock 的 Method 会异常，导致服务方需要 Mock Service 的全部方法
在解决以上麻烦的前提下，为了能快速注册一个需要的 Dubbo 服务，提高项目协作过程中的工作效率，开展了 Mock 工厂的设计与实现。

功能概述
Mock Dubbo 服务
单个服务器，支持部署多个相同和不同的 Service
动态上、下线服务
非 Mock 的 Method 透传到基础服务
一、方案探索
1.1 基于 Service Chain 选择 Mock 服务的实现方式
1.1.1 Service Chain 简单介绍
在业务发起的源头添加 Service Chain 标识，这些标识会在接下来的跨应用远程调用中一直透传并且基于这些标识进行路由，这样我们只需要把涉及到需求变更的应用的实例单独部署，并添加到 Service Chain 的数据结构定义里面，就可以虚拟出一个逻辑链路，该链路从逻辑上与其他链路是完全隔离的，并且可以共享那些不需要进行需求变更的应用实例。

根据当前调用的透传标识以及 Service Chain 的基础元数据进行路由，路由原则如下：

当前调用包含 Service Chain 标识，则路由到归属于该 Service Chain 的任意服务节点，如果没有归属于该
Service Chain 的服务节点，则排除掉所有隶属于 Service Chain 的服务节点之后路由到任意服务节点
当前调用没有包含 Service Chain 标识，则排除掉所有隶属于 Service Chain 的服务节点之后路由到任意服务节点
当前调用包含 Service Chain 标识，并且当前应用也属于某个 Service Chain 时，如果两者不等则抛出路由异常
以 Dubbo 框架为例，给出了一个 Service Chain 实现架构图（下图来自有赞架构团队）



1.1.2 Mock 服务实现设计方案
方案一、基于 GenericService 生成需要 Mock 接口的泛化实现，并注册到 ETCD 上（主要实现思路如下图所示）。 image

方案二、使用 Javassist，生成需要mock接口的Proxy实现，并注册到 ETCD 上（主要实现思路如下图所示）。 image

1.1.3 设计方案比较
方案一优点：实现简单，能满足mock需求

继承 GenericService，只要实现一个 $invoke(String methodName, String[] parameterTypes, Object[] objects)，可以根据具体请求参数做出自定义返回信息。
接口信息只要知道接口名、protocol 即可。
即使该服务已经存在，也能因为 generic 字段，让消费者优先消费该 mock service。
缺点：与公司的服务发现机制冲突

由于有赞服务背景，在使用 Haunt 服务发现时，是会同时返回正常服务和带有 Service Chain 标记的泛化服务，所以必然存在两种类型的服务。导致带有 Service Chain 标记的消费者在正常请求泛化服务时报 no available invoke。 例：注册了 2个 HelloService：

正常的 ：generic=false&interface=com.alia.api.HelloService&methods=doNothing,say,age
泛化的：generic=true&interface=com.alia.api.HelloService&methods=*
在服务发现的时候，RegistryDirectory 中有个 map，保存了所有 Service 的注册信息。也就是说， method=* 和正常 method=doNothing,say,age 被保存在了一起。 image 客户端请求服务的时候，优先匹配到正常的服务的 method，而不会去调用泛化服务。 导致结果：访问时，会跳过 genericFilter，报 no available invoke。

方案二优点：Proxy 实现，自动生成一个正常的 Dubbo 接口实现

1.Javassist 有现成的方法生成接口实现字节码，大大简化了对用户代码依赖。例如：

返回 String、Json 等，对单 method 的 mock 实现，都无需用户上传实现类。
透传时统一由平台控制，不配置 mock 的方法默认就会进行透传，而且保留 Service Chain 标记。
2.Mock 服务注册 method 信息完整。 
3.生成接口 Proxy 对象时，严格按照接口定义进行生成，返回数据类型有保障。

缺点：

无优先消费选择功能。
字节码后台生成，不利于排查生成的 Proxy 中存在问题。
1.1.4 选择结果
由于做为平台，不仅仅需要满足 mock 需求，还需要减少用户操作，以及支持现有公司服务架构体系，所以选择设计方案二。

1.2 基于动态代理结合 ServiceConfig 实现动态上、下线服务
1.2.1 Dubbo 暴露服务的过程介绍
image

上图（来自 dubbo 开发者文档）暴露服务时序图： 首先 ServiceConfig 类拿到对外提供服务的实际类 ref（如：StudentInfoServiceImpl）,然后通过 ProxyFactory 类的 getInvoker 方法使用 ref 生成一个 AbstractProxyInvoker 实例。到这一步就完成具体服务到 Invoker 的转化。接下来就是 Invoker 转换到 Exporter 的过程,Exporter 会通过转化为 URL 的方式暴露服务。 从 dubbo 源码来看，dubbo 通过 Spring 框架提供的 Schema 可扩展机制，扩展了自己的配置支持。dubbo-container 通过封装 Spring 容器，来启动了 Spring 上下文，此时它会去解析 Spring 的 bean 配置文件（Spring 的 xml 配置文件），当解析 dubbo:service 标签时，会用 dubbo 自定义 BeanDefinitionParser 进行解析。dubbo 的 BeanDefinitonParser 实现为 DubboBeanDefinitionParser。 Spring.handlers 文件：http://code.alibabatech.com/schema/dubbo=com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler

    public class DubboNamespaceHandler extends NamespaceHandlerSupport {
      public DubboNamespaceHandler() {
      } 
      public void init() {
          this.registerBeanDefinitionParser("application", new DubboBeanDefinitionParser(ApplicationConfig.class, true));
          this.registerBeanDefinitionParser("module", new DubboBeanDefinitionParser(ModuleConfig.class, true));
          this.registerBeanDefinitionParser("registry", new DubboBeanDefinitionParser(RegistryConfig.class, true));
          this.registerBeanDefinitionParser("monitor", new DubboBeanDefinitionParser(MonitorConfig.class, true));
          this.registerBeanDefinitionParser("provider", new DubboBeanDefinitionParser(ProviderConfig.class, true));
          this.registerBeanDefinitionParser("consumer", new DubboBeanDefinitionParser(ConsumerConfig.class, true));
          this.registerBeanDefinitionParser("protocol", new DubboBeanDefinitionParser(ProtocolConfig.class, true));
          this.registerBeanDefinitionParser("service", new DubboBeanDefinitionParser(ServiceBean.class, true));
          this.registerBeanDefinitionParser("reference", new DubboBeanDefinitionParser(ReferenceBean.class, false));
          this.registerBeanDefinitionParser("annotation", new DubboBeanDefinitionParser(AnnotationBean.class, true));
      }
      static {
          Version.checkDuplicate(DubboNamespaceHandler.class);
      }
     }

DubboBeanDefinitionParser 会将配置标签进行解析,并生成对应的 Javabean，最终注册到 Spring Ioc 容器中。 对 ServiceBean 进行注册时，其 implements InitializingBean 接口，当 bean 完成注册后，会调用 afterPropertiesSet() 方法，该方法中调用 export() 完成服务的注册。在 ServiceConfig 中的 doExport() 方法中，会对服务的各个参数进行校验。

    if(this.ref instanceof GenericService) {
        this.interfaceClass = GenericService.class;
        this.generic = true;
    } else {
        try {
            this.interfaceClass = Class.forName(this.interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException var5) {
            throw new IllegalStateException(var5.getMessage(), var5);
        }
        this.checkInterfaceAndMethods(this.interfaceClass, this.methods);
        this.checkRef();
        this.generic = false;
    }
注册过程中会进行判断该实现类的类型。其中如果实现了 GenericService 接口，那么会在暴露服务信息时，将 generic 设置为 true，暴露方法就为*。如果不是，就会按正常服务进行添加服务的方法。此处就是我们可以实现 Mock 的切入点，使用 Javassist 根据自定义的 Mock 信息，写一个实现类的 class 文件并生成一个实例注入到 ServiceConfig 中。生成 class 实例如下所示，与一个正常的实现类完全一致，以及注册的服务跟正常服务也完全一致。

    package 123.com.youzan.api;
    import com.youzan.api.StudentInfoService;
    import com.youzan.pojo.Pojo;
    import com.youzan.test.mocker.internal.common.reference.ServiceReference;

    public class StudentInfoServiceImpl implements StudentInfoService {
        private Pojo getNoValue0;
        private Pojo getNoValue1;
        private ServiceReference service;
        public void setgetNoValue0(Pojo var1) {
            this.getNoValue0 = var1;
        }
        public void setgetNoValue1(Pojo var1) {
            this.getNoValue1 = var1;
        }
        public Pojo getNo(int var1) {
            return var1 == 1 ? this.getNoValue0 : this.getNoValue1;
        }
        public void setService(ServiceReference var1) {
            this.service = var1;
        }
        public double say() {
            return (Double)this.service.reference("say", "", (Object[])null);
        }
        public void findInfo(String var1, long var2) {
            this.service.reference("findInfo", "java.lang.String,long", new Object[]{var1, new Long(var2)});
        }
        public StudentInfoServiceImpl() {}
       }
使用 ServiceConfig 将自定义的实现类注入，并完成注册，实现如下：

    void registry(Object T, String sc) {
        service.setFilter("request")
        service.setRef(T)
        service.setParameters(new HashMap<String, String>())
        service.getParameters().put(Constants.SERVICE_CONFIG_PARAMETER_SERVICE_CHAIN_NAME, sc)
        service.export()
        if (service.isExported()) {
            log.warn "发布成功 : ${sc}-${service.interface}"
        } else {
            log.error "发布失败 : ${sc}-${service.interface}"
        }
    }
通过service.setRef(genericService)完成实现类的注入，最终通过service.export()完成服务注册。ref 的值已经被塞进来，并附带 ServiceChain 标记保存至 service 的 paramters 中。具体服务到 Invoker 的转化以及 Invoker 转换到 Exporter，Exporter 到 URL 的转换都会附带上 ServiceChain 标记注册到注册中心。

1.2.2 生成实现类设计方案
方案一、 支持指定 String(或 Json) 对单个 method 进行 mock。
功能介绍：根据入参 String or Json，生成代理对象。由 methodName 和 methodParams 获取唯一 method 定义。（指支持单个方法mock）。消费者请求到Mock服务的对应Mock Method时，Mock服务将保存的数据转成对应的返回类型，并返回。

方案二、 支持指定 String(或 Json) 对多个 method生成 mock。
功能介绍：根据入参 String or Json，生成代理对象。method 对应的 mock 数据由 methodMockMap 指定，由 methodName 获取唯一 method 定义，所以被 mock 接口不能有重载方法（只支持多个不同方法 mock）。消费者请求到 Mock 服务的对应 mock method 时，Mock 服务将保存的数据转成对应的返回类型，并返回。

方案三、 在使用 实现类(Impl) 的情况下，支持传入一个指定的 method 进行 mock。
功能介绍：根据入参的实现类，生成代理对象。由 methodName 和 methodParams 获取唯一 method 定义。（支持 mock 一个方法）。消费者请求到 Mock 服务的对应 mock method 时，Mock 服务调用该实现类的对应方法，并返回。

方案四、 在使用 实现类(Impl) 的情况下，支持传入多个 method 进行 mock。
功能介绍：根据入参的实现类，生成代理对象。由 methodName 获取唯一 method 定义，所以被 mock 接口不能有重载方法（只支持一个实现类 mock 多个方法）。消费者请求到 Mock 服务的对应 mock method 时，Mock 服务调用该实现类的对应方法，并返回。

方案五、 使用 Custom Reference 对多个 method 进行 mock。
功能介绍：根据入参 ServiceReference，生成代理对象。method 对应的自定义 ServiceReference 由 methodMockMap 指定，由 methodName 获取唯一method定义，所以被 mock 接口不能有重载方法（只支持多个不同方法 mock）。消费者请求到 Mock 服务的对应 mock method 时，Mock 服务会主动请求自定义的 Dubbo 服务。

1.2.3 设计方案选择
以上五种方案，其实就是整个 Mock 工厂实现的一个迭代过程。在每个方案的尝试中，发现各自的弊端然后出现了下一种方案。目前，在结合各种使用场景后，选择了方案二、方案五。

方案三、方案四被排除的主要原因：Dubbo 对已经发布的 Service 保存了实现类的 ClassLoader，相同 className 的类一旦注册成功后，会将实现类的 ClassLoader 保存到内存中，很难被删除。所以想要使用这两种方案的话，需要频繁变更实现类的 className，大大降低了一个工具的易用性。改用自定义 Dubbo 服务（方案五），替代自定义实现类，但是需要使用者自己起一个 Dubbo 服务，并告知 IP+PORT。

方案一其实是方案二的补集，能支持 Service 重载方法的 Mock。由于在使用时，需要传入具体 Method 的签名信息，增加了用户操作成本。由于公司内部保证一个 Service 不可能有重载方法，且为了提高使用效率，不开放该方案。后期如果出现这样的有重载方法的情况，再进行开放。

1.2.4 遇到的坑
基础数据类型需要特殊处理
使用 Javassist 根据接口 class 写一个实现类的 class 文件，遇到最让人头疼的就是方法签名和返回值。如果方法的签名和返回值为基础数据类型时，那在传参和返回时需要做特殊处理。平台中本人使用了最笨的枚举处理方法，如果有使用 Javassist 的高手，有好的建议麻烦不吝赐教。代码如下：

    /** 参数存在基本数据类型时，默认使用基本数据类型
     * 基本类型包含：
     * 实数：double、float
     * 整数：byte、short、int、long
     * 字符：char
     * 布尔值：boolean
     * */
    private static CtClass getParamType(ClassPool classPool, String paramType) {
        switch (paramType) {
            case "char":
                return CtClass.charType
            case "byte":
                return CtClass.byteType
            case "short":
                return CtClass.shortType
            case "int":
                return CtClass.intType
            case "long":
                return CtClass.longType
            case "float":
                return CtClass.floatType
            case "double":
                return CtClass.doubleType
            case "boolean":
                return CtClass.booleanType
            default:
                return classPool.get(paramType)
        }
    }
1.3 非 Mock 的 Method 透传到基础服务
1.3.1 Dubbo 服务消费的过程介绍
image

在消费端：Spring 解析 dubbo:reference 时，Dubbo 首先使用 com.alibaba.dubbo.config.spring.schema.NamespaceHandler 注册解析器，当 Spring 解析 xml 配置文件时就会调用这些解析器生成对应的 BeanDefinition 交给 Spring 管理。Spring 在初始化 IOC 容器时会利用这里注册的 BeanDefinitionParser 的 parse 方法获取对应的 ReferenceBean 的 BeanDefinition 实例，由于 ReferenceBean 实现了 InitializingBean 接口，在设置了 Bean 的所有属性后会调用 afterPropertiesSet 方法。afterPropertiesSet 方法中的 getObject 会调用父类 ReferenceConfig 的 init 方法完成组装。ReferenceConfig 类的 init 方法调用 Protocol 的 refer 方法生成 Invoker 实例，这是服务消费的关键。接下来把 Invoker 转换为客户端需要的接口(如：StudentInfoService)。由 ReferenceConfig 切入，通过 API 方式使用 Dubbo 的泛化调用，代码如下：

Object reference(String s, String paramStr, Object[] objects) {
    if (StringUtils.isEmpty(serviceInfoDO.interfaceName) || serviceInfoDO.interfaceName.length() <= 0) {
        throw new NullPointerException("The 'interfaceName' should not be ${serviceInfoDO.interfaceName}, please make sure you have the correct 'interfaceName' passed in")
    }

    // set interface name
    referenceConfig.setInterface(serviceInfoDO.interfaceName)
    referenceConfig.setApplication(serviceInfoDO.applicationConfig)
    // set version
    if (serviceInfoDO.version != null && serviceInfoDO.version != "" && serviceInfoDO.version.length() > 0) {
        referenceConfig.setVersion(serviceInfoDO.version)
    }
    if (StringUtils.isEmpty(serviceInfoDO.refUrl) || serviceInfoDO.refUrl.length() <= 0) {
        throw new NullPointerException("The 'refUrl' should not be ${serviceInfoDO.refUrl} , please make sure you have the correct 'refUrl' passed in")
    }
    //set refUrl
    referenceConfig.setUrl(serviceInfoDO.refUrl)
    reference.setGeneric(true)// 声明为泛化接口

     //使用com.alibaba.dubbo.rpc.service.GenericService可以代替所有接口引用
    GenericService genericService = reference.get()

    String[] strs = null

    if(paramStr != ""){
        strs = paramStr.split(",")
    }

    Object result = genericService.$invoke(s, strs, objects)

     // 返回值类型不定，需要做特殊处理
    if (result.getClass().isAssignableFrom(HashMap.class)) {
        Class dtoClass = Class.forName(result.get("class"))
        result.remove("class")
        String resultJson = JSON.toJSONString(result)

        return JSON.parseObject(resultJson, dtoClass)
    }

    return result
}
如上代码所示，具体业务 DTO 类型，泛化调用结果非仅结果数据，还包含 DTO 的 class 信息，需要特殊处理结果，取出需要的结果进行返回。

1.3.2 记录dubbo服务请求设计方案
方案一、捕获请求信息
服务提供方和服务消费方调用过程拦截，Dubbo 本身的大多功能均基于此扩展点实现，每次远程方法执行，该拦截都会被执行。Provider 提供的调用链，具体的调用链代码是在 ProtocolFilterWrapper 的 buildInvokerChain 完成的，具体是将注解中含有 group=provider 的 Filter 实现，按照 order 排序，最后的调用顺序是 EchoFilter->ClassLoaderFilter->GenericFilter->ContextFilter->ExceptionFilter->TimeoutFilter->MonitorFilter->TraceFilter。 其中：EchoFilter 的作用是判断是否是回声测试请求，是的话直接返回内容。回声测试用于检测服务是否可用，回声测试按照正常请求流程执行，能够测试整个调用是否通畅，可用于监控。ClassLoaderFilter 则只是在主功能上添加了功能，更改当前线程的 ClassLoader。

在 ServiceConfig 继承 AbstractInterfaceConfig，中有 filter 属性。以此为切入点，给每个 Mock 服务添加 filter,记录每次 dubbo 服务请求信息（接口、方法、入参、返回、响应时长）。

方案二、记录请求信息
将请求信息保存在内存中，一个接口的每个被 Mock 的方法保存近 10次 记录信息。使用二级缓存保存，缓存代码如下：

    @Singleton(lazy = true)
    class CacheUtil {
        private static final Object PRESENT = new Object()
        private int maxInterfaceSize = 10000    // 最大接口缓存数量
        private int maxRequestSize = 10         // 最大请求缓存数量
        private Cache<String, Cache<RequestDO, Object>> caches = CacheBuilder.newBuilder()
                .maximumSize(maxInterfaceSize)
                .expireAfterAccess(7, TimeUnit.DAYS)    // 7天未被请求的接口，缓存回收
                .build()
    }   
如上代码所示，二级缓存中的一个 Object 是被浪费的内存空间，但是由于想不到其他更好的方案，所以暂时保留该设计。

1.3.3 遇到的坑
泛化调用时参数对象转换
使用 ReferenceConfig 进行服务直接调用，绕过了对一个接口方法签名的校验，所以在进行泛化调用时，最大的问题就是 Object[] 内的参数类型了。每次当遇到数据类型问题时，本人只会用最笨的办法，枚举解决。代码如下：

    /** 参数存在基本数据类型时，默认使用基本数据类型
     * 基本类型包含：
     * 实数：double、float
     * 整数：byte、short、int、long
     * 字符：char
     * 布尔值：boolean
     * */
    private Object getInstance(String paramType, String value) {
        switch (paramType) {
            case "java.lang.String":
                return value
            case "byte":
            case "java.lang.Byte":
                return Byte.parseByte(value)
            case "short":
                return Short.parseShort(value)
            case "int":
            case "java.lang.Integer":
                return Integer.parseInt(value)
            case "long":
            case "java.lang.Long":
                return Long.parseLong(value)
            case "float":
            case "java.lang.Float":
                return Float.parseFloat(value)
            case "double":
            case "java.lang.Double":
                return Double.parseDouble(value)
            case "boolean":
            case "java.lang.Boolean":
                return Boolean.parseBoolean(value)
            default:
                JSONObject jsonObject = JSON.parseObject(value) // 转成JSONObject
                return jsonObject
        }
    }
如以上代码所示，是将传入参数转成对应的包装类型。当接口的签名如果为 int,那么入参对象是 Integer 也是可以的。因为 $invoke(String methodName, String[] paramsTypes, Object[] objects)，是由 paramsTypes 检查方法签名，然后再将 objects 传入具体服务中进行调用。

ReferenceConfig 初始化优先设置 initialize 为 true
使用泛化调用发起远程 Dubbo 服务请求，在发起 invoke 前，有 GenericService genericService = referenceConfig.get() 操作。当 Dubbo 服务没有起来，此时首次发起调用后，进行 ref 初始化操作。ReferenceConfig 初始化 ref 代码如下:

    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<dubbo:reference interface=\"\" /> interface not allow null!");
        }
        // 获取消费者全局配置
        checkDefault();
        appendProperties(this);
        if (getGeneric() == null && getConsumer() != null) {
            setGeneric(getConsumer().getGeneric());
        }
        ...
    }
结果导致：由于第一次初始化的时候，先把 initialize 设置为 true，但是后面未获取到有效的 genericService，导致后面即使 Dubbo 服务起来后，也会泛化调用失败。

解决方案：泛化调用就是使用 genericService 执行 invoke 调用，所以每次请求都使用一个新的 ReferenceConfig，当初始化进行 get() 操作时报异常或返回为 null 时，不保存；直到初始化进行 get() 操作时获取到有效的 genericService 时，将该 genericService 保存起来。实现代码如下：

    synchronized (hasInit) {
        if (!hasInit) {
            ReferenceConfig referenceConfig = new ReferenceConfig();
            // set interface name
            referenceConfig.setInterface(serviceInfoDO.interfaceName)
            referenceConfig.setApplication(serviceInfoDO.applicationConfig)
            // set version
            if (serviceInfoDO.version != null && serviceInfoDO.version != "" && serviceInfoDO.version.length() > 0) {
                referenceConfig.setVersion(serviceInfoDO.version)
            }
            if (StringUtils.isEmpty(serviceInfoDO.refUrl) || serviceInfoDO.refUrl.length() <= 0) {
                throw new NullPointerException("The 'refUrl' should not be ${serviceInfoDO.refUrl} , please make sure you have the correct 'refUrl' passed in")
            }
            referenceConfig.setUrl(serviceInfoDO.refUrl)
            referenceConfig.setGeneric(true)// 声明为泛化接口
            genericService = referenceConfig.get()
            if (null != genericService) {
                hasInit = true
            }
        }
    }
1.4 单个服务器，支持部署多个相同和不同的Service
根据需求，需要解决两个问题：1.服务器运行过程中，外部API的Jar包加载问题；2.注册多个相同接口服务时，名称相同的问题。

1.4.1 动态外部Jar包加载的设计方案
方案一、为外部 Jar 包生成单独的 URLClassLoader,然后在泛化注册时使用保存的 ClassLoader，在回调时进行切换 currentThread 的 ClassLoader，进行相同 API 接口不同版本的 Mock。

不可用原因： JavassistProxyFactory 中 final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type); 
wapper 获取的时候，使用的 makeWrapper 中默认使用的是ClassHelper.getClassLoader(c);导致一直会使用 AppClassLoader。API 信息会保存在一个 WapperMap 中，当消费者请求过来的时候，会优先取这个 Map 找对应的 API 信息。

导致结果：

1.由于使用泛化注册，所以 class 不在 AppClassLoader 中。设置了 currentThread 的 ClassLoader 不生效。
2.由于 dubbo 保存 API 信息只有一个 Map，所以导致发布的服务的 API 也只能有一套。
解决方案：

使用自定义 ClassLoader 进行加载外部 Jar 包中的 API 信息。
一台 Mock 终端存一套 API 信息，更新 API 时需要重启服务器。
方案二、在程序启动时，使用自定义 TestPlatformClassLoader。还是给每个 Jar 包生成对应的 ApiClassLoader，由 TestPlatformClassLoader 统一管理。
不可用原因：

在 Mock 终端部署时，使用 -Djava.system.class.loader 设置 ClassLoader 时，JVM 启动参数不可用。因为，TestPlatformClassLoader 不存在于当前 JVM 中，而是在工程代码中。详细参数如下：

-Djava.system.class.loader=com.youzan.test.mocker.internal.classloader.TestPlatformClassLoader

解决方案：（由架构师汪兴提供）

使用自定义 Runnable()，保存程序启动需要的 ClassLoader、启动参数、mainClass 信息。
在程序启动时，新起一个 Thread，传入自定义 Runnable()，然后将该线程启动。
方案三、使用自定义容器启动服务
应用启动流程，如下图所示（下图来自有赞架构团队）



Java 的类加载遵循双亲委派的设计模式，从 AppClassLoader 开始自底向上寻找，并自顶向下加载，所以在没有自定义 ClassLoader 时，应用的启动是通过 AppClassLoader 去加载 Main 启动类去运行。

自定义 ClassLoader 后，系统 ClassLoader 将被设置成容器自定义的 ClassLoader，自定义 ClassLoader 重新去加载 Main 启动类运行，此时后续所有的类加载都会先去自定义的 ClassLoader 里查找。

难点：应用默认系统类加载器是 AppClassLoader，在 New 对象时不会经过自定义的 ClassLoader。

巧妙之处：Main 函数启动时，AppClassLoader 加载 Main 和容器，容器获取到 Main class，用自定义 ClassLoader 重新加载Main，设置系统类加载器为自定义类加载器，此时 New 对象都会经过自定义的 ClassLoader。

1.4.2 设计方案选择
以上三个方案，其实是实践过程中的一个迭代。最终结果：

方案一、保留为外部Jar包生成单独的 URLClassLoader。
方案二、保留自定义 TestPlatformClassLoader，使用 TestPlatformClassLoader 保存每个 Jar 包中 API 与其 ClassLoader 的对应关系。
方案三、采用自定义容器启动，新起一个线程，并设置其 concurrentThreadClassLoader 为 TestPlatformClassLoader，用该线程启动 Main.class。
1.4.3 遇到的坑
使用 Javassist 生成的 Class 名称相同
使用 Javassist 生成的 Class，每个 Class 有单独的 ClassName 以 Service Chain + className 组成。在重新生成相同名字的 class 时，即使使用 new ClassPool() 也不能完全隔离。因为生成 Class 的时候 Class<?> clazz = ctClass.toClass() 默认使用的是同一个 ClassLoader，所以会报“attempted duplicate class definition for name:**”。

解决方案：基于 ClassName 不是随机生成的，所以只能基于之前的 ClassLoader 生成一个新的 SecureClassLoader(ClassLoader parent) 加载新的 class，旧的 ClassLoader 靠 Java 自动 GC。代码如下：

Class<?> clazz = ctClass.toClass(new SecureClassLoader(clz.classLoader))

PS：该方案目前没有做过压测，不知道会不会导致内存溢出。

二、方案实现
2.1 Mock 工厂整体设计架构
image

2.2 Mocker 容器设计图
image

2.3 二方包管理时序图
image

2.4 Mocker 容器服务注册时序图
image

三、支持场景
3.1 元素及名词解释


上图所示为基本元素组成，相关名词解释如下：

消费者：调用方发起 DubboRequest
Base 服务：不带 Service Chain 标识的正常服务
Mock 服务：通过 Mock 工厂生成的 dubbo 服务
ETCD：注册中心，此处同时注册着 Base 服务和 Mock 服务
默认服务透传：对接口中不需要 Mock 的方法，直接泛化调用 Base 服务
自定义服务（CF）：用户自己起一个泛化 dubbo 服务（PS：不需要注册到注册中心，也不需要 Service Chain 标识）
3.2 支持场景简述
场景1：不带 Service Chain 请求（不使用 Mock 服务时）
消费者从注册中心获取到 Base 环境服务的 IP+PORT，直接请求 Base 环境的服务。 

场景2、带 Service Chain 请求、Mock 服务采用 JSON 返回实现
消费者从注册中心获取到两个地址：1.Base 环境服务的 IP+PORT；2.带 Service Chain 标记服务（Mock服务）的 IP+PORT。根据 Service Chain 调用路由，去请求 Mock 服务中的该方法，并返回 Mock 数据。 

场景3、带 Service Chain 请求、Mock 服务没有该方法实现
消费者从注册中心获取到两个地址：1.Base 环境服务的 IP+PORT；2.带 Service Chain 标记服务（Mock 服务）的 IP+PORT。根据 Service Chain 调用路由，去请求 Mock 服务。由于 Mock 服务中该方法是默认服务透传，所以由 Mock 服务直接泛化调用 Base 服务，并返回数据。 

场景4、带 Service Chain 请求头、Mock 服务采用自定义服务（CR）实现
消费者从注册中心获取到两个地址：1.Base 环境服务的 IP+PORT；2.带 Service Chain 标记服务（Mock 服务）的 IP+PORT。根据 Service Chain 调用路由，去请求Mock服务。由于 Mock 服务中该方法是自定义服务（CF），所以由 Mock 服务调用用户的 dubbo 服务，并返回数据。 

场景5、带 Service Chain 请求头、Mock 服务没有该方法实现、该方法又调用带 Service Chain 的 InterfaceB 的方法
消费者调用 InterfaceA 的 Method3 时，从注册中心获取到两个地址：1.Base 环境服务的 IP+PORT；2.带 Service Chain 标记服务（Mock 服务）的 IP+PORT。根据 Service Chain 调用路由，去请求 InterfaceA 的 Mock 服务。由于 Mock 服务中该方法是默认服务透传，所以由 Mock 服务直接泛化调用 InterfaceA 的 Base 服务的Method3。

但是，由于 InterfaceA 的 Method3 是调用 InterfaceB 的 Method2，从注册中心获取到两个地址：1.Base 环境服务的 IP+PORT；2.带 Service Chain 标记服务（Mock 服务）的 IP+PORT。由于 Service Chain 标识在整个请求链路中是一直被保留的，所以根据Service Chain调用路由，最终请求到 InterfaceB 的 Mock 服务，并返回数据。 

场景6、带 Service Chain 请求头、Mock已经存在的 Service Chain 服务
由于不能同时存在两个相同的 Service Chain 服务，所以需要降原先的 Service Chain 服务进行只订阅、不注册的操作。然后将Mock服务的透传地址，配置为原 Service Chain 服务（即订阅）。 消费者在进行请求时，只会从 ETCD 发现 Mock 服务，其他同场景2、3、4、5。 

四、结束语
Mock平台实践过程中，遇到很多的难题，此处需要特别感谢架构组何炜龙、汪兴的友情支持。后续还有很多需要完善的，希望大家能多提宝贵意见（邮箱：zhongyingying@youzan.com）。