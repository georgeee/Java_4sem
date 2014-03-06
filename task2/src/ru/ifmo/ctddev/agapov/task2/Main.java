package ru.ifmo.ctddev.agapov.task2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NavigableSet;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 06.03.14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String [] args){
        ImmutableArraySet<Integer> arraySet = new ImmutableArraySet<Integer>(Arrays.asList(new Integer[]{1,6,22,2,3,30,87,443,22,5}));
//        arraySet.addAll();
        NavigableSet<Integer> set = arraySet.tailSet(3, false).headSet(30, true).descendingSet();
//        set.add(28);
//        set.add(15);
//        set.add(4);
//        set.pollFirst();
        System.out.println(set);
        System.out.println(arraySet);
    }
}
