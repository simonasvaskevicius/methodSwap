/*
 * Created by Simonas Vaskevicius on 7/17/19 4:02 PM.
 * Last modified 7/17/19 2:30 PM.
 */

package extensions;

public class MethodReplaceItem {

    public String methodTitle;
    public String replaceTo;
    public String className;

    public MethodReplaceItem() {
    }

    public MethodReplaceItem(String classPath, String methodTitle, String replaceTo, String className) {
        this.methodTitle = methodTitle;
        this.replaceTo = replaceTo;
        this.className = className;
    }

    public String getClassPath() {
        String path = className.replace('.', '/');
        path = path + ".class";
        return path;
    }

    public String getMethodTitle() {
        return methodTitle;
    }

    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    public String getReplaceTo() {
        return replaceTo;
    }

    public void setReplaceTo(String replaceTo) {
        this.replaceTo = replaceTo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
