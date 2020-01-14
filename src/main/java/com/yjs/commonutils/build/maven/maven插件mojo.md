Maven 插件编写 之 Mojo


1.Maven
Maven 作为一个优秀的项目管理工具，其插件机制为其功能扩展提供了非常大的便捷性。虽然说大多数情况下，我们可能不太会自己去编写 Maven 插件，但不排除在某些特殊的情况下，我们需要去完成一个自己的插件，来协助我们处理某些比较通用的事情。

2.Maven 插件的命名规范
一般来说，我们会将自己的插件命名为<myplugin>-maven-plugin，而不推荐使用maven-<myplugin>-plugin，因为后者是 Maven 团队维护官方插件的保留命名方式，使用这个命名方式会侵犯 Apache Maven 商标。

3.什么是 Mojo？
Mojo 就是Maven plain Old Java Object。每一个 Mojo 就是 Maven 中的一个执行目标（executable goal），而插件则是对单个或多个相关的 Mojo 做统一分发。一个 Mojo 包含一个简单的 Java 类。插件中多个类似 Mojo 的通用之处可以使用抽象父类来封装。

4.创建 Mojo 工程
mojo项目
这里，我们使用 idea 作为开发工具进行讲解，创建工程选择 Maven，然后在模板中找到 maven-archetype-plugin，点击下一步，输入对应的参数，如：io.fredia/test-maven-plugin/1.0-SNAPSHOT，最后点击完成即可创建一个简单的 Mojo 工程。

创建完成后，工程内会生成对应的 pom.xml 文件。其内容比较简单，与普通 Maven 工程的 pom.xml 基本一致，只是自动添加了对 maven-plugin-api 的依赖，这个依赖里面会包含一些 Mojo 的接口与抽象类，在后续编写具体的 Mojo 时再进行详细讲解。

<dependency>
  <groupId>org.apache.maven</groupId>
  <artifactId>maven-plugin-api</artifactId>
  <version>2.0</version>
</dependency>
<dependency>
   <groupId>org.apache.maven.plugin-tools</groupId>
   <artifactId>maven-plugin-annotations</artifactId>
   <version>3.2</version>
</dependency>
注意打包方式：
<packaging>maven-plugin</packaging>

5.Mojo 的创建
工程创建完毕后，创建一个简单MoJo

package io.fredia.test;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal which touches a timestamp file.
 *
 * @deprecated Don't use!
 */
@Mojo(name = "hello", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MyMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    /*
     * @Parameter(defaultValue = "${project.build.directory}", property =
     * "outputDir", required = true) private File outputDirectory;
     */

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("hello world");
    }
}
解释一下这个类，我们发现它继承了 AbstractMojo 这个抽象类，并实现了 execute() 方法，该方法就是用来定义这个 Mojo 具体操作内容，我们只需要根据自己的需要来编写自己的实现即可。

Mojo 操作的实现我们了解了，那怎么让 Maven 知道这是一个 Mojo 而不是一个普通的 Java 类呢？这里，就需要说一下 Mojo 的查找机制了，在处理源码的时候，plugin-tools 会把使用了 @Mojo 注解或 Javadoc 里包含 @goal 注释的类来当作一个 Mojo 类。在上面的例子中，我们使用了 @MoJo 的方法来声明一个 Mojo。同样我们也可以使用 Javadoc 注解来进行声明：

/**
 * @goal hello
 */
public class MyMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("hello fredia");
    }

}
6.如何运行自定义 Plugin
与使用其它插件类似，我们需要在 pom.xml 文件中引入插件：

<build>
    <plugins>
        <plugin>
            <groupId>io.fredia</groupId>
            <artifactId>test-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
        </plugin>
    </plugins>
</build>
mvn命令行执行如下：

mvn io.fredia:test-maven-plugin:1.0-SNAPSHOT:hello
即可看到输出：

[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building test-maven-plugin Maven Mojo 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- test-maven-plugin:1.0-SNAPSHOT:hello (default-cli) @ test-maven-plugin ---
hello fredia
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.249 s
[INFO] Finished at: 2017-11-22T12:59:47+08:00
[INFO] Final Memory: 6M/123M
[INFO] ------------------------------------------------------------------------
7.缩短执行命令
在刚才运行插件的时候，我们使用全量的插件指引，但这个实在是太长太繁琐了，那我们是否可以缩短我们的执行命令呢？答案肯定是可以的，如果你想要执行的是你本地库中最新版本的插件，那么你可以删除掉版本号；如果你的命名满足前面提及的两种命令方式，你可以直接使用插件名及 goal 名来运行对应的插件，如：

mvn test:hello
结果一样

8.绑定 Maven 执行周期
你还可以将插件配置为将特定目标，从而附加到构建生命周期中的某个特定阶段。如：

<build>
    <plugins>
        <plugin>
            <groupId>io.fredia</groupId>
            <artifactId>test-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>hello</goal>
                    </goals>
                    <phase>package</phase>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
最后附加一个自己基于maven-plugin开发的代码生成器，实现高度的代码复用和全自动化
gitee地址：https://gitee.com/fredia/code-factory/