import com.android.build.gradle.BaseExtension;
import extensions.MethodReplaceExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import tasks.TransformTask;

public class MethodReplacePlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        MethodReplaceExtension extension = target.getExtensions().create("methodReplace", MethodReplaceExtension.class);
        BaseExtension ext = (BaseExtension) target.getExtensions().findByName("android");
        ext.registerTransform(new TransformTask(extension));
    }
}