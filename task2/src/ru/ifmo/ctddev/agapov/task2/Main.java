package ru.ifmo.ctddev.agapov.task2;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 06.03.14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String [] args){
        List<Integer> list = Arrays.asList(new Integer[]{-1858898787, 1389573307, -829648055, -1316693806, -1662316621, -1113798478, -1991682604, -184471203, -843874012, 241311289});
        int value = 1723939396;
        ArraySet<Integer> set = new ArraySet<Integer>(list);
        TreeSet<Integer> treeSet = new TreeSet<Integer>(list);
        System.out.println(set);
        System.out.println(treeSet);
        System.out.println(set.lower(value));
        System.out.println(treeSet.lower(value));
        System.out.println(set.ceiling(value));
        System.out.println(treeSet.ceiling(value));
    }

    public static void test1(){
        ArraySet<Integer> arraySet = new ArraySet<Integer>(Arrays.asList(new Integer[]{1,6,22,2,3,30,87,443,22,5}));
//        arraySet.addAll();
        NavigableSet<Integer> set = arraySet.tailSet(3, false).headSet(30, true).descendingSet();
//        set.add(28);
//        set.add(15);
//        set.add(4);
//        set.pollFirst();
        for (int i = 0; i < 4; i++) {
            System.out.println(i+": "+set.subSet(300, i % 2 == 1, 50, i / 2 == 1));
        }
        System.out.println(set);
        System.out.println(arraySet);
    }
}
