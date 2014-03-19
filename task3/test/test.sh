#!/bin/bash
rm -Rf gen test.jar

java -jar ../task3.jar -cp '../simple_tester/simple_tester.jar:../task3.jar' -jar @classes_list.txt test.jar
java -jar ../task3.jar -cp '../simple_tester/simple_tester.jar:../task3.jar' -dir @classes_list.txt gen


