#! /bin/bash
for i in `find ./flow-master/src/main/java/jaxe  -type f -name '*.java'` ;
do
        echo $i
        iconv -f utf-8 -t utf-8  $i
#        enca -x utf-8 $i
        #mv ${i}.tmp $i;
done


