package ru.ifmo.ctddev.agapov.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 18.02.14
 * Time: 23:05
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String> words = new ArrayList<String>();
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-")) {
                String line;
                try {
                    while ((line = stdin.readLine()) != null) {
                        words.add(line);
                    }
                } catch (IOException e) {
                    System.err.println("Error occurred while reading words from stdin");
                    e.printStackTrace();
                }
            } else words.add(args[i]);
        }
        String[] wordsArray = new String[words.size()];
        words.toArray(wordsArray);
        new Processor(wordsArray).process();
    }
}
