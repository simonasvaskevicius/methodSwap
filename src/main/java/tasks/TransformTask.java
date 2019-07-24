/*
 * Taken from https://vincent.bernat.ch/en/blog/2016-android-build-time-patch
 * Converted to Java by Simonas Vaskevicius on 7/17/19 4:02 PM.
 * Last modified 7/12/19 5:05 PM.
 */

package tasks;

import com.android.annotations.NonNull;
import com.android.build.api.transform.*;
import extensions.MethodReplaceExtension;
import extensions.MethodReplaceItem;
import javassist.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class TransformTask extends Transform {

    private Logger logger;
    private MethodReplaceExtension extensions;
    private String name = "methodReplace";

    public TransformTask(MethodReplaceExtension extensions) {
        logger = org.slf4j.LoggerFactory.getLogger("methodReplace-logger");
        this.extensions = extensions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES);
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return Collections.singleton(QualifiedContent.Scope.EXTERNAL_LIBRARIES);
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(@NonNull TransformInvocation transformInvocation)
            throws TransformException, IOException {
        for (TransformInput input : transformInvocation.getInputs()) {
            for (JarInput jarInput : input.getJarInputs()) {
                String jarName = jarInput.getName();
                File src = jarInput.getFile();
                File dest = transformInvocation.getOutputProvider().getContentLocation(jarName, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                Status status = jarInput.getStatus();

                if (status == Status.REMOVED) FileUtils.deleteDirectory(dest);
                else if (!transformInvocation.isIncremental() || status != Status.NOTCHANGED) {
                    try {
                        handleLibrary(src, dest);
                    } catch (NotFoundException | CannotCompileException e) {
                        throw new TransformException(e);
                    }
                } else FileUtils.copyFile(src, dest);
            }
        }
    }

    void handleLibrary(File src, File dest) throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        ClassPool fakePool = new ClassPool();

        pool.insertClassPath(src.getPath());
        fakePool.insertClassPath(src.getPath());

        HashMap<CtClass, MethodReplaceItem> list = new HashMap<>();
        for (MethodReplaceItem extension : extensions.getMethodReplaceItems()) {
            if (getCtcClass(fakePool, extension) != null) {
                CtClass ctc = getCtcClass(pool, extension);
                list.put(ctc, extension);
                removeMethod(ctc, extension);
                ctc.addMethod(CtNewMethod.make(extension.getReplaceTo(), ctc));
                logger.info("Replaced: " + extension.getMethodTitle() + " in " + extension.getClassName());
            }
        }

        JarFile input = new JarFile(src);
        JarOutputStream output = new JarOutputStream(new FileOutputStream(dest));
        Enumeration<JarEntry> entries = input.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!containsClass(entry.getName(), extensions.getMethodReplaceItems())) {
                InputStream s = input.getInputStream(entry);
                output.putNextEntry(new JarEntry(entry.getName()));
                IOUtils.copy(s, output);
                s.close();
            }
        }

        for (Map.Entry<CtClass, MethodReplaceItem> item : list.entrySet()) {
            output.putNextEntry(new JarEntry(item.getValue().getClassPath()));
            output.write(item.getKey().toBytecode());
        }

        output.close();
    }

    private void removeMethod(CtClass ctc, MethodReplaceItem extension) throws NotFoundException {
        try {
            CtMethod ctm = ctc.getDeclaredMethod(extension.getMethodTitle());
            ctc.removeMethod(ctm);
        } catch (NotFoundException e) {
            throw new NotFoundException("Method '\n" + extension.getMethodTitle() + "'\n does not exists");
        }
    }

    private boolean containsClass(String entryName, MethodReplaceItem[] extensions) {
        for (MethodReplaceItem extension : extensions) {
            if (entryName.equals(extension.getClassPath())) return true;
        }
        return false;
    }

    private CtClass getCtcClass(ClassPool pool, MethodReplaceItem extension) {
        try {
            return pool.get(extension.getClassName());
        } catch (NotFoundException e) {
            return null;
        }
    }
}
