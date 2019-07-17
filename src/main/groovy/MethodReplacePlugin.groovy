
import extensions.MethodReplaceExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.TransformTask

class MethodReplacePlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        def extension = target.extensions.create('methodReplace', MethodReplaceExtension)
        def android = target.extensions.findByName("android")
        android.registerTransform(new TransformTask(extension))
    }
}