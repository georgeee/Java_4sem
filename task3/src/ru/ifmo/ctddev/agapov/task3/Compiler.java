package ru.ifmo.ctddev.agapov.task3;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by georgeee on 16.03.14.
 */
public class Compiler {

    public static int runCompiler(File srcDir, List<String> files, File outDir, String classPath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> args = new ArrayList<String>();
        args.addAll(files);
        args.add("-cp");
        if (classPath != null && !classPath.isEmpty())
            classPath = classPath.concat(System.getProperty("path.separator")).concat(srcDir.getPath());
        else
            classPath = srcDir.getPath();
        args.add(classPath);
        args.add("-d");
        args.add(outDir.getPath());
        int exitCode = compiler.run(null, null, null, args.toArray(new String[args.size()]));
        return exitCode;
    }


}
