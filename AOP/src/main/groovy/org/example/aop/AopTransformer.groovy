package org.example.aop

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project;

public class AopTransformer extends Transform {
    def project
    // 缓存class字节码对象的容器
    def pool = ClassPool.getDefault()

    AopTransformer(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println "--------------------transform-------------------"


        // 1、查询输入，遍历inputs目录
        transformInvocation.inputs.each {
            // 1.1 jar包目录
            it.jarInputs.each {
                // 2.查询输出
                def destDir = transformInvocation.outputProvider.getContentLocation(
                        it.name,
                        it.contentTypes,
                        it.scopes,
                        Format.JAR)
                println "jar destDir----->" + destDir

                // 3.复制到下一环节
                FileUtils.copyFile(it.file, destDir);
            }

            // 1.2 class目录
            it.directoryInputs.each {

                def preFileName = it.file.absolutePath
                // 加载路径下的class文件
                pool.insertClassPath(preFileName)
                // project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
                pool.appendClassPath(project.android.bootClasspath[0].toString());
                // 引入android.os.Bundle包，因为onCreate方法参数有Bundle
                pool.importPackage("android.os.Bundle");

                println "========directoryInputs======== " + preFileName
                findTargetAndSettle(it.file, preFileName)

                // 2.查询输出
                def destDir = transformInvocation.outputProvider.getContentLocation(
                        it.name,
                        it.contentTypes,
                        it.scopes,
                        Format.DIRECTORY)
                println "class destDir----->" + destDir

                // 3.复制到下一环节
                FileUtils.copyDirectory(it.file, destDir);
            }
        }
    }

    // 找到需要处理的文件并处理
    // fileName D:\workplace_github\JavassistDemo\app\build\intermediates\javac\debug\classes
    private void findTargetAndSettle(File dir, String fileName) {
        if (dir.isDirectory()) {
            // 如果是目录，继续遍历
            dir.listFiles().each {
                findTargetAndSettle(it, fileName)
            }
        } else {
            def filePath = dir.absolutePath
            // 只处理class文件
            if (filePath.endsWith(".class")) {
                println "find class----->" + filePath
                // 修改文件
                filterClass(filePath, fileName)
            }
        }
    }

    // 过滤class
    private void filterClass(def filePath, String fileName) {
        // 过滤系统文件
        if (filePath.contains('R$')
                || filePath.contains('R.class')
                || filePath.contains("BuildConfig.class")) {
            return
        }

        // 获取className
        def className = filePath.replace(fileName, "")
                .replace("\\", ".")
                .replace("/", ".")
                .replace(".class", "")
                .substring(1)

        println "find className----->" + className

        // 获取CtClass对象，用来操作class
        CtClass ctClass = pool.get(className)
        addCode(ctClass, fileName)
    }

    // 添加代码
    private void addCode(CtClass ctClass, String fileName) {
        // 解冻
        ctClass.defrost()
        CtMethod[] methods = ctClass.getDeclaredMethods()
        for (method in methods) {
            println "method " + method.getName() + "参数个数  " + method.getParameterTypes().length
            if ("onCreate".equals(method.getName())) {
                method.insertBefore("{ System.out.println(\"调用了" + method.getName() + "\");}")
            }
        }

        // 将修改的文件写出去
        ctClass.writeFile(fileName)
        ctClass.detach()
    }

    // 在app/build/intermediates/transforms/路径下生成新的文件夹
    // 用来存储本次transform操作的数据
    @Override
    String getName() {
        return "monitor"
    }

    // 接收什么类型的数据
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 接收数据的范围
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 一般不修改
    @Override
    boolean isIncremental() {
        return false
    }
}
