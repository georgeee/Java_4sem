package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.*;
import java.util.ArrayList;

/**
 * Utility class, provides flexible and powerful interface to {@link Implementor}
 * <p/>
 * Usage:
 * <p/>
 * Syntax: java -jar [-debug] [-cp classPath] [-jar|-dir] class1, class2, .. [jarFile|outDir]
 * <p/>
 * Classpath format is just the same as for java utilite, look at it's '-cp' parameter
 * <p/>
 * To build jar file from classes' implementations:
 * <p/>
 * java -jar task3.jar -jar class1, class2, ... jarFile
 * <p/>
 * To put generated classes' implementations into outDir:
 * <p/>
 * java -jar task3.jar -dir class1, class2, ... outDir
 * <p/>
 * To put generated classes' implementations into current dir:
 * <p/>
 * java -jar task3.jar class1, class2, ...
 * <p/>
 * Hint: instead of class you can pass path to file with line-by-line list of classes.
 * To do so, pass @filePath as parameter in list of classes
 */
public class Runner {
    /**
     * Command line arguments
     */
    private String[] args;
    /**
     * Class names to process
     */
    private String[] classNames;

    /**
     * directory, to which we save generated implentations (code)
     */
    private File outDir = null;
    /**
     * file, to which we should save jar archive with built classes
     */
    private File jarFile = null;

    /**
     * true, if [-jar] argument was passed
     * Look at usage, described in {@link Runner} class description
     */
    private boolean jarMode = false;
    /**
     * true, if [-dirMode] argument was passed
     * Look at usage, described in {@link Runner} class description
     */
    private boolean dirMode = false;
    /**
     * true, if [-debug] argument was passed
     * Look at usage, described in {@link Runner} class description
     */
    private boolean debugMode = false;

    /**
     * classPath, which we should use for processing files
     */
    private String classPath = null;


    /**
     * Constructs new Runner
     *
     * @param args command arguments
     */
    public Runner(String[] args) {
        this.args = args;
    }

    /**
     * Reads arguments, initializes process parameters
     *
     * @throws InvalidUsageException if inappropriate amount of arguments passed
     * @throws IOException           if error occurs, while reading class list file
     */
    private void init() throws InvalidUsageException, IOException {
        if (args.length == 0) throw new InvalidUsageException();
        int i = 0;
        if (args[i].equals("-debug")) {
            debugMode = true;
            ++i;
        }
        if (args[i].equals("-cp")) {
            classPath = args[++i];
            ++i;
        }
        if (args[i].equals("-jar")) {
            ++i;
            jarMode = true;
        } else if (args[i].equals("-dir")) {
            ++i;
            dirMode = true;
        }
        int classCount = args.length - i - (jarMode || dirMode ? 1 : 0);
        if (classCount < 1) throw new InvalidUsageException();
        ArrayList<String> classNamesList = new ArrayList<>(classCount);

        for (int j = 0; j < classCount; ++j) {
            String arg = args[i + j];
            if (arg.charAt(0) == '@') {
                String filePath = arg.substring(1);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        classNamesList.add(line);
                    }
                } catch (FileNotFoundException e) {
                    throw new FileNotFoundException("File " + filePath + " from arguments was not found");
                } catch (IOException e) {
                    throw new IOException("Error occurred while reading file " + filePath + " (from arguments)", e);
                } finally {
                    if (br != null) br.close();
                }
            } else {
                classNamesList.add(arg);
            }
        }

        if (dirMode)
            outDir = new File(args[args.length - 1]);
        else if (!jarMode)
            outDir = new File(System.getProperty("user.dir"));

        if (jarMode)
            jarFile = new File(args[args.length - 1]);

        classNames = classNamesList.toArray(new String[classNamesList.size()]);
    }

    /**
     * Executes the utility
     */
    public void run() {
        try {
            init();
            if (debugMode) System.err.println("==== Debug mode on ====");
            Utility.loadClassPath(classPath);
            Implementor implementor = new Implementor();
            if (jarMode) {
                implementor.implementClassesJar(Utility.loadClasses(classNames), jarFile, classPath);
            } else {
                implementor.implementClasses(Utility.loadClasses(classNames), outDir);
            }
        } catch (ImplerException | ClassNotFoundException | IOException e) {
            System.err.println(e.getMessage());
            if (debugMode) e.printStackTrace();
        } catch (InvalidUsageException e) {
            printUsage();
        }
    }


    /**
     * Prints usage message
     */
    public void printUsage() {
        System.out.println("Usage:\n" +
                "Syntax: java -jar [-debug] [-cp classPath] [-jar|-dir] class1, class2, .. [jarFile|outDir]\n" +
                "Classpath format is just the same as for java utilite, look at it's '-cp' parameter\n\n" +
                "To build jar file from classes' implementations: \n" +
                "   java -jar task3.jar -jar class1, class2, ... jarFile\n" +
                "To put generated classes' implementations into outDir: \n" +
                "   java -jar task3.jar -dir class1, class2, ... outDir\n" +
                "To put generated classes' implementations into current dir: \n" +
                "   java -jar task3.jar class1, class2, ...\n\n" +
                "Hint: instead of class you can pass path to file with line-by-line list of classes.\n" +
                "To do so, pass @filePath as parameter in list of classes\n");
    }


    /**
     * Is being thrown, when invalid set of parameters is passed
     */
    protected static class InvalidUsageException extends Exception {

    }


}
