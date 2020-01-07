ImportAware
       ImportAware的作用是用来处理自定义注解的，比如将注解的某些属性值赋值给其他bean的属性。有一点要注意的就是ImportAware也是要配合@Import()注解一起使用。

       我们通过一个简单的实例来说明，通过ImportAware来修改某个Bean的属性。把ChangeAttribute注解上的值设置给BeanImportAware组件的name属性。

/**
 * 注意这个类上的两个注解的使用
 * 1. @Import(BeanImportAware.class)，BeanImportAware类实现了ImportAware接口
 * 2. @ChangeAttribute是我们自定义的一个注解，用来带参数的。会在BeanImportAware类里面去获取这个主句
 */
@Configuration
@Import(BeanImportAware.class)
@ChangeAttribute(value = "tuacy")
public class ImportAwareConfig {
}
@Configuration
public class BeanImportAware implements ImportAware {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {

        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(ChangeAttribute.class.getName()));
        if (annoAttrs != null) {
            // 获取到ChangeAttribute注解里面value对应的值
            name = (String) annoAttrs.get("value");
        }

    }
}



https://www.jianshu.com/p/7a3aec3f5d1b
