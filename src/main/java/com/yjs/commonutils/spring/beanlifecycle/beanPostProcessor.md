BeanPostProcessor和BeanFactoryProcessor浅析


1、简介
BeanPostProcessor和BeanFactoryProcessor是Spring中很重要的了两个接口。我们先来看看Spring的文档中对BeanPostProcessor的定义：

The BeanPostProcessor interface defines callback methods that you can 
implement to provide your own (or override the container’s default) 
instantiation logic, dependency-resolution logic, and so forth. If you want 
to implement some custom logic after the Spring container finishes 
instantiating, configuring,and initializing a bean, you can plug in one or 
more BeanPostProcessor implementations.
BeanPostProcessor 接口定义了一个你可以自己实现的回调方法，来实现你自己的实例化逻辑、依赖解决逻辑等，如果你想要在Spring完成对象实例化、配置、初始化之后实现自己的业务逻辑，你可以补充实现一个或多个BeanPostProcessor的实现。

我们再来看一下文档那个中对BeanFactoryPostProcessor的描述：

The semantics of this interface are similar to those of the 
BeanPostProcessor, with one major difference:BeanFactoryPostProcessor 
operates on the bean configuration metadata; that is, the Spring IoC 
container allows a BeanFactoryPostProcessor to read the configuration 
metadata and potentially change it before the container instantiates any 
beans other than BeanFactoryPostProcessors.
BeanFactoryPostProcessor的定义和BeanPostProcessor相似，有一个最主要的不同是：BeanFactoryPostProcessor可以对bean的配置信息进行操作；更确切的说Spring IOC容器允许BeanFactoryPostProcessor读取配置信息并且能够在容器实例化任何其他bean（所有的实现了BeanFactoryPostProcessor接口的类）之前改变配置信息

2、BeanPostProcessor和BeanFactoryProcessor的定义
BeanPostProcessor有两个方法：

public interface BeanPostProcessor {

    /**
     * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * @param bean bean的实例
     * @param beanName bean的name
     * @return 返回处理过后的bean,可以是最初的Bean或者是包装后的Bean；
     * 如果返回null，后续的BeanPostProcessor不会被执行
     * @throws org.springframework.beans.BeansException in case of errors
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
     * instance and the objects created by the FactoryBean (as of Spring 2.0). The
     * post-processor can decide whether to apply to either the FactoryBean or created
     * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
     * <p>This callback will also be invoked after a short-circuiting triggered by a
     * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
     * in contrast to all other BeanPostProcessor callbacks.
     * @param bean bean的实例
     * @param beanName bean的name
     * @return 返回处理过后的bean,可以是最初的Bean或者是包装后的Bean；
     * 如果返回null，后续的BeanPostProcessor不会被执行
     * {@code null}, no subsequent BeanPostProcessors will be invoked
     * @throws org.springframework.beans.BeansException in case of errors
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
     * @see org.springframework.beans.factory.FactoryBean
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

}
我们可以看到注释postProcessBeforeInitialization方法是在所有的bean的InitializingBean的afterPropertiesSet方法之前执行而postProcessAfterInitialization方法则是在所有的bean的InitializingBean的afterPropertiesSet方法之后执行的。

而BeanFactoryPOSTProcessor只有一个方法：

 */
public interface BeanFactoryPostProcessor {

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     * @param beanFactory the bean factory used by the application context
     * @throws org.springframework.beans.BeansException in case of errors
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
我们可以看到注释中写到的：postProcessBeanFactory可以在BeanFactory完成实例化后修改容器内部的BeanFactory。这时候所有的bean都被加载，但是没有bean被初始化。这就允许BeanFactoryPOSTProcessor重写或者添加配置，甚至可以提前初始化bean。
好了，说了这么多，也许大家对BeanPostProcessor和BeanFactoryProcessor还是有些迷惑，我们来看一个例子就能明白了：

3、示例
我们先创建一个类实现BeanPostProcessor：

public class TestBeanPostProcessor implements BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName + "-->" + "TestBeanPostProcessor->postProcessBeforeInitialization");
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName + "-->" + "TestBeanPostProcessor->postProcessAfterInitialization");
        return bean;
    }
}
在创建一个类实现BeanFactoryPostProcessor：

public class TestBeanFactoryPostPorcessor implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("TestBeanFactoryPostPorcessor->postProcessBeanFactory");
    }
}
然后创建一个普通的bean，实现了InitializingBean和DisposableBean接口：

public class TestBean implements InitializingBean, DisposableBean{
    private String name;
    private int age;

    public TestBean() {
        System.out.println("TestBean->constrcutor");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("TestBean->setter");
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        System.out.println("TestBean->setter");
        this.age = age;
    }

    public void destroy() throws Exception {
        System.out.println("TestBean->destroy");
    }

    public void afterPropertiesSet() throws Exception {
        System.out.println("TestBean->afterPropertiesSet");
    }
}
然后在xml文件中注册这三个bean：

    <bean id="testBeanPostProcessor" class="wbx.test_spring_noweb.beans.TestBeanPostProcessor"></bean>
    <bean id="testBeanFactoryPostProcessor" class="wbx.test_spring_noweb.beans.TestBeanFactoryPostPorcessor"></bean>
    <bean id="testBean" class="wbx.test_spring_noweb.beans.TestBean">
        <property name="name" value="丑星星"></property>
        <property name="age" value="18"></property>
    </bean>
最后写测试类运行：

public class Test {
    public static void main(String[] args) {
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("classpath:wbx/test_spring_noweb/beans.xml");
        TestBean testBean = (TestBean)context.getBean("testBean");
        context.destroy();

    }
}
我们可以看到控制台的运行结果，这里截图一部分：

测试运行结果

我们可以看到Bean的生命周期：
1、解析xml文件，解析出BeanDefinition
2、Spring容器创建BeanFactoryPostProcessor实例
3、调用BeanFactoryPostProcessor的postProcessBeanFactory方法
4、Spring容器创建BeanPostProcessor实例
5、在需要创建其他Bean实例的时候创建其他Bean
6、调用Bean的构造方法
7、调用Bean的setter方法为Bean属性赋值
8、调用BeanPostProcessor的postProcessBeforeInitialization方法
9、调用InitializingBean的afterPropertiesSet方法
10、调用BeanPostProcessor的postProcessAfterInitialization方法
11、容器销毁的时候调用DisposableBean的destroy方法

4、BeanPostProcessor在Spring内部的使用举例
我们都知道，当一个Bean实现了OoXxAware接口后，Spring就会自动将OoXx对象注入，例如：一个Bean实现了ApplicationContextAware接口，Spring会尝试将ApplicationContext对象注入到这个Bean中，这是怎么实现的呢？
当Spring容器初始化时，会先解析Bean的定义，将定义解析成BeanDefinition对象，并且由BeanDefinitionHolder包装一层，注册到BeanFactory中。我们可以看一下容器刷新的方法（AbstractApplicationContext类中的）：

public void refresh() throws BeansException, IllegalStateException {
        synchronized (this.startupShutdownMonitor) {
            // Prepare this context for refreshing.
            prepareRefresh();

            // Tell the subclass to refresh the internal bean factory.
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            // Prepare the bean factory for use in this context.
            prepareBeanFactory(beanFactory);

            try {
                // Allows post-processing of the bean factory in context subclasses.
                postProcessBeanFactory(beanFactory);

                // Invoke factory processors registered as beans in the context.
                invokeBeanFactoryPostProcessors(beanFactory);

                // Register bean processors that intercept bean creation.
                registerBeanPostProcessors(beanFactory);

                // Initialize message source for this context.
                initMessageSource();

                // Initialize event multicaster for this context.
                initApplicationEventMulticaster();

                // Initialize other special beans in specific context subclasses.
                onRefresh();

                // Check for listener beans and register them.
                registerListeners();

                // Instantiate all remaining (non-lazy-init) singletons.
                finishBeanFactoryInitialization(beanFactory);

                // Last step: publish corresponding event.
                finishRefresh();
            }

            catch (BeansException ex) {
                // Destroy already created singletons to avoid dangling resources.
                destroyBeans();

                // Reset 'active' flag.
                cancelRefresh(ex);

                // Propagate exception to caller.
                throw ex;
            }
        }
    }
解析Bean在ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();中完成。有兴趣的同学可以自己看一下。然后调用postProcessBeanFactory(beanFactory);方法，我们看一下这个方法：

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // Tell the internal bean factory to use the context's class loader etc.
        beanFactory.setBeanClassLoader(getClassLoader());
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

        // Configure the bean factory with context callbacks.
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);

        // BeanFactory interface not registered as resolvable type in a plain factory.
        // MessageSource registered (and found for autowiring) as a bean.
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);

        // Detect a LoadTimeWeaver and prepare for weaving, if found.
        if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            // Set a temporary ClassLoader for type matching.
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }

        // Register default environment beans.
        if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
        }
        if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
        }
        if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
        }
    }
这个方法其实就是为BeanFactory初始化一些属性的值。
我们可以看到有一个行代码：beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
ApplicationContextAwareProcessor实现了BeanPostProcessor接口。这行代码将ApplicationContextAwareProcessor注册到beanFactory的BeanPostProcessor集合中。我们再来看一下ApplicationContextAwareProcessor的部分代码：

@Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        AccessControlContext acc = null;

        if (System.getSecurityManager() != null &&
                (bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
                        bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
                        bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)) {
            acc = this.applicationContext.getBeanFactory().getAccessControlContext();
        }

        if (acc != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    invokeAwareInterfaces(bean);
                    return null;
                }
            }, acc);
        }
        else {
            invokeAwareInterfaces(bean);
        }

        return bean;
    }

    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                ((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
            }
            if (bean instanceof EmbeddedValueResolverAware) {
                ((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(
                        new EmbeddedValueResolver(this.applicationContext.getBeanFactory()));
            }
            if (bean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
            }
            if (bean instanceof ApplicationEventPublisherAware) {
                ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
            }
            if (bean instanceof MessageSourceAware) {
                ((MessageSourceAware) bean).setMessageSource(this.applicationContext);
            }
            if (bean instanceof ApplicationContextAware) {
                ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
            }
        }
    }