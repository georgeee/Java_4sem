package ru.georgeee.itmo.java.sem4.task6.misc;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by georgeee on 02.04.14.
 */
public class Utility {
    private static final int DEFAULT_RANDOM_STRING_LENGTH = 24;
    public static String generateRandomString() {
        return generateRandomString(DEFAULT_RANDOM_STRING_LENGTH);
    }
    public static String generateRandomString(int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = (char) (ThreadLocalRandom.current().nextInt(26) + ((int) 'A'));
        }
        return new String(chars);
    }

}
