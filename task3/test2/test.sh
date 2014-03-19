#!/bin/bash
java -cp `for i in lib/* *.jar; do echo -n "$i"':'; done`'../task3.jar' info.kgeorgiy.java.advanced.implementor.Tester class ru.ifmo.ctddev.agapov.task3.Implementor
