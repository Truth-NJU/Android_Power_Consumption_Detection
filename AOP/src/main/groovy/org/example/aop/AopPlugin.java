package org.example.aop;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class AopPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        System.out.println("hello");
        project.getExtensions().findByType(AppExtension.class).registerTransform(new AopTransformer(project));
    }
}
