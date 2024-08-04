# gcb-plugin
IDEA的插件使用Gradle作为构建工具, 要先从官网上下载Gradle,或使用IDEA自动下载的Gradle.
由于网络原因，最好移步[官网](https://gradle.org/install/).
Intellij提供了相对详细的[官方文档](https://plugins.jetbrains.com/docs/intellij/welcome.html), 但是在实际开发中还是会遇到很多无法解释的困难,
如果遇到文档中没有说明的情况,最好使用google进行搜索.

### 新建IDEA插件项目
在新建项目时选择Gradle->Java+Intellij Platform Plugins,生成目录结构如下
```
.
└─src
    └─main
        ├─java
        │  └─com
        └─resources
            └─META-INF
```
根目录下有两个重要文件

1. build.gradle
```groovy
plugins {
    id 'java'
    id 'org.jetbrains.intellij' version '0.6.5'
}
sourceCompatibility=1.8 // 兼容1.8
group 'com.inspection'
version '1.0-SNAPSHOT'

repositories {
    maven {
        url 'https://maven.aliyun.com/repository/central'
    }
    maven {
        url 'https://maven.aliyun.com/repository/public'
    }  // 阿里的源
    mavenCentral()

}

dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.12'
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version 'IU-191.7479.19'    // 沙盒版本是官方旗舰版, 可以更换版本
    plugins = ['JavaScript']    // 增加JavaScript依赖
}
patchPluginXml {
    changeNotes """
    """
}
```
2. setting.gradle
```groovy
// 当IDEA无法从官方仓库中下载时,选用阿里源
pluginManagement {
    repositories {
        maven {
            url 'https://maven.aliyun.com/repository/gradle-plugin'
        }
        gradlePluginPortal()
    }
}
```

### 新增插件功能
本例中插件的功能为新增两条Inspection

新建项目的目录结构如下
```text
.
└─src
    └─main
        ├─java
        │  └─com
        │      └─inspection
        │          ├─java
        │          │  ├─rf
        │          │  ├─rl
        │          │  ├─rpl
        │          │  │  └─exception
        │          │  └─utils
        │          └─js
        └─resources
            ├─inspectionDescriptions
            └─META-INF
```

META-INF下有plugin.xml,是插件元文件
```xml
<idea-plugin>
    <id>com.inspection.my-plugins</id>
<!--    插件的名字-->
    <name>SonarFix</name>
<!--    插件作者or发布者-->
    <vendor email="likeyang@hotmail.com" url="http://www.acodingdog.com">ACodingDog</vendor>
<!--插件描述-->
    <description>Find out all methods whose count of rows exceed 100 and add </description>
    <!-- please see https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/plugin_compatibility.html
         on how to target different products -->
<!--    插件的依赖-->
    <depends>com.intellij.modules.platform</depends>
    <depends>com.intellij.modules.java</depends>
    <extensions defaultExtensionNs="com.intellij">
<!--        表明新增一条Inspection-->
        <localInspection language="JAVA"
                         displayName="Row counts exceeds the limit"
                         groupPath="Java"
                         groupBundle="messages.InspectionsBundle"
                         groupKey="group.names.probable.bugs"
                         enabledByDefault="true"
                         level="WARNING"
                         implementationClass="com.inspection.java.rl.RowCountLimitInspection">
        </localInspection>
        <localInspection language="JAVA"
                         displayName="If-statement should start with a new line"
                         groupPath="Java"
                         groupBundle="messages.InspectionsBundle"
                         groupKey="group.names.probable.bugs"
                         enabledByDefault="true"
                         level="WARNING"
                         implementationClass="com.inspection.java.rf.ReformatIfStmtInspection">
        </localInspection>

        <localInspection language="JAVA"
                         displayName="replace CommonDao with DBUtils"
                         groupPath="Java"
                         groupBundle="messages.InspectionsBundle"
                         groupKey="group.names.probable.bugs"
                         enabledByDefault="true"
                         level="WARNING"
                         implementationClass="com.inspection.java.rpl.CommonDaoUsageInspection">

        </localInspection>
    </extensions>

    <actions>
        <!-- Add your actions here -->
    </actions>
</idea-plugin>
```
可以看到,3条inspection都是针对java的语法校验,displayName是校验规则的名字;implementationClass是校验规则的实现类;
groupKey和groupPath是校验规则的路径,方便在IDEA->Editor->Inspections下找到.

新建的Inspection要继承AbstractBaseJavaLocalInspectionTool类
```java
public class ReformatIfStmtInspection extends AbstractBaseJavaLocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new IfStmtVisitor(holder);
    }
}
```
IDEA已经将项目中的Java文件解析成Psi树, 我们可以使用IDEA提供的API访问它,如同访问一个正常的Java语法树. 如果有需要,
我们可以打开PSI Viewer来查看这些内容, PSI Viewer不是默认开启的, 项目中必须有一个插件的module才会出现.

以"if条件语句必须被括号包裹"的校验规则为例, 要检查if条件语句, 返回的Visitor如下所示
```java
public class IfStmtVisitor extends JavaElementVisitor {
    private final ProblemsHolder problemsHolder;
    private final String DESCRIPTION_TEMPLATE = CrapTemplate.getCrapStmt("条件语句应该另起一行");

    public IfStmtVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }


    @Override
    public void visitIfStatement(PsiIfStatement statement) {
        // 如果是一个if分支，不处理
        if (statement.getParent() instanceof PsiIfStatement) {
            return;
        }
        if (!(statement.getPrevSibling() instanceof PsiWhiteSpace)) {
            problemsHolder.registerProblem(statement, DESCRIPTION_TEMPLATE, new ReformatIfStmtFixer());
            return;
        }
        PsiWhiteSpace whiteSpace = (PsiWhiteSpace) statement.getPrevSibling();
        if (!whiteSpace.textContains('\n')) {
            problemsHolder.registerProblem(statement, DESCRIPTION_TEMPLATE, new ReformatIfStmtFixer());
        }
    }

}
```
override了JavaElementVisitor访问if-statement的visitor方法. 遇到同语法校验规则不一致的地方,
在problemHolder里注册这个Java元素.当校验规则起作用时,会将这部分代码高亮.

我们要自定义一个修正方法
```java
public class ReformatIfStmtFixer implements LocalQuickFix {

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getName() {
        return "ReformatIfStmt";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiIfStatement ifStmt = (PsiIfStatement) descriptor.getPsiElement();
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiStatement substituteStmt = factory.createStatementFromText(ifStmt.getText(), ifStmt.getContext());
        ifStmt.replace(substituteStmt);
    }
}

```
将注册进problemHolder的if语句拿出来,借助Intellij自带的JavaPsiFacade类,生成一个符合规则的if条件语句, 将它与原句替换
就此修改成功