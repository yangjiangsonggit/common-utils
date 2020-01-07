Spring中@进出口的各种用法以及ImportAware接口
@导入注解
@Import注解提供了和XML中<import/>元素等价的功能，实现引入的一个或多个配置类。@Import即可以在类上使用，也可以作为元注解使用。

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	/**
	 * {@link Configuration}, {@link ImportSelector}, {@link ImportBeanDefinitionRegistrar}
	 * or regular component classes to import.
	 */
	Class<?>[] value();

}

注解中只有一个value();。支持引入@Configuration标注的配置类，实现ImportSelector接口的类，实现ImportBeanDefinitionRegistrar接口的类和普通的@component类。

作为元注解使用
@Import可以作为元注解使用，可以在@Import的继承上封装一层。我的理解是，从而不会对外（使用方）暴露我内部的具体实现细节。

举个例子：例如@EnableAspectJAutoProxy注解。

@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

@EnableAspectJAutoProxy就是被@Import这个元注解所标志了，我们（程序员）通过使用@EnableAspectJAutoProxy来开启AspectJAutoProxy，而Spring可以通过@Import引入相应的配置类来实现的。

导入实现ImportSelector接口的类
先来看一下ImportSelector接口，该接口中只有一个方法：

public interface ImportSelector {
	String[] selectImports(AnnotationMetadata importingClassMetadata);
}

ImportSelector，输入选择器。该接口就是根据根据给定的条件，选择导入某种配置类。

举个例子：例如@EnableTransactionManagement注解。

@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {

在@EnableTransactionManagement注解中使用了@Import(TransactionManagementConfigurationSelector.class)注解，其中TransactionManagementConfigurationSelector类就是实现了ImportSelector接口。

public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {
	@Override
	protected String[] selectImports(AdviceMode adviceMode) {
		switch (adviceMode) {
			case PROXY:
				return new String[] {AutoProxyRegistrar.class.getName(),
						ProxyTransactionManagementConfiguration.class.getName()};
			case ASPECTJ:
				return new String[] {
						TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME};
			default:
				return null;
		}
	}
}

方法的内部实现逻辑也很简单，就是根据不同的AdviceMode引入不同的配置类，来实现事务管理。

导入实现ImportBeanDefinitionRegistrar接口的类
ImportBeanDefinitionRegistrar接口中也只有一个方法：

public interface ImportBeanDefinitionRegistrar {
	void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);
}

该接口允许我们根据所给的注解元数据，按需注册额外的BeanDefinition。

举个例子：例如@EnableAspectJAutoProxy注解。

@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

@EnableAspectJAutoProxy注解约会了AspectJAutoProxyRegistrar.class类，这个类就是实现了ImportBeanDefinitionRegistrar接口。

class AspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(
			AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);

		AnnotationAttributes enableAspectJAutoProxy =
				AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
		if (enableAspectJAutoProxy != null) {
			if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
			}
			if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
				AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
			}
		}
	}
}

registerBeanDefinitions中调用了AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);方法，这个方法就是在往之上的BeanDefinitionRegistry registry中注册BeanDefinition。注册了BeanDefinition之后，Spring就会去实例化这个Bean，从而达到AspectJAutoProxy作用。

导入@Configuration类
这次@Import最常见的是使用方法。我们可以拆分配置类，然后在程序中按需导入相应的配置。

举个例子：例如@EnableRetry注解。使用这个注解可以开启retry功能。

@EnableAspectJAutoProxy(proxyTargetClass = false)
@Import(RetryConfiguration.class)
public @interface EnableRetry {

其内部就是引入了RetryConfiguration这个配置类。

ImportAware接口
ImportAware关联是需要和@Import一起使用的。在@Import作为元注解使用时，通过@Import引入的配置类如果实现了ImportAware接口就可以获取到导入该配置类接口的数据配置。有点绕，我们直接上代码。

举个例子：@EnableAsync注解。

@Import(AsyncConfigurationSelector.class)
public @interface EnableAsync {

//AsyncConfigurationSelector源码
public class AsyncConfigurationSelector extends AdviceModeImportSelector<EnableAsync> {

	private static final String ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME =
			"org.springframework.scheduling.aspectj.AspectJAsyncConfiguration";
	@Override
	@Nullable
	public String[] selectImports(AdviceMode adviceMode) {
		switch (adviceMode) {
			case PROXY:
				return new String[] {ProxyAsyncConfiguration.class.getName()};
			case ASPECTJ:
				return new String[] {ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME};
			default:
				return null;
		}
	}
}

默认情况下使用AdviceMode为PROXY，导入了ProxyAsyncConfiguration类。

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyAsyncConfiguration extends AbstractAsyncConfiguration {

	@Bean(name = TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public AsyncAnnotationBeanPostProcessor asyncAdvisor() {
		Assert.notNull(this.enableAsync, "@EnableAsync annotation metadata was not injected");
		AsyncAnnotationBeanPostProcessor bpp = new AsyncAnnotationBeanPostProcessor();
		Class<? extends Annotation> customAsyncAnnotation = this.enableAsync.getClass("annotation");
		if (customAsyncAnnotation != AnnotationUtils.getDefaultValue(EnableAsync.class, "annotation")) {
			bpp.setAsyncAnnotationType(customAsyncAnnotation);
		}
		if (this.executor != null) {
			bpp.setExecutor(this.executor);
		}
		if (this.exceptionHandler != null) {
			bpp.setExceptionHandler(this.exceptionHandler);
		}
		bpp.setProxyTargetClass(this.enableAsync.getBoolean("proxyTargetClass"));
		bpp.setOrder(this.enableAsync.<Integer>getNumber("order"));
		return bpp;
	}
}

在ProxyAsyncConfiguration的asyncAdvisor方法中需要电子杂志到@EnableAsync上的一些设置值，例如：this.enableAsync.getBoolean("proxyTargetClass")，this.enableAsync.<Integer>getNumber("order")。

this.enableAsync是其父类AbstractAsyncConfiguration的属性。AbstractAsyncConfiguration实现了ImportAware接口，从而就可以获取到@EnableAsync上的信息了。

// AbstractAsyncConfiguration#setImportMetadata 源码
public void setImportMetadata(AnnotationMetadata importMetadata) {
	this.enableAsync = AnnotationAttributes.fromMap(
			importMetadata.getAnnotationAttributes(EnableAsync.class.getName(), false));
	if (this.enableAsync == null) {
		throw new IllegalArgumentException(
				"@EnableAsync is not present on importing class " + importMetadata.getClassName());
	}
}
