import csv
import string
import math
import operator
import re

k = 300

filelist = ['mi_cs.csv','mi_math.csv','mi_physics.csv','mi_stat.csv']
finalWord = set()

for name in filelist:
    data = dict()
    with open(name , 'rb') as f:
        reader = csv.reader(f, delimiter=',', quotechar='"')
        for line in reader:        
            data[line[0]] = float(line[1])


    sortKey = sorted(data, key = data.get , reverse = True)

    i = 0
    for key in sortKey :
        finalWord.add(key)
        i = i +1
        if i == k:
            break   

with open('bagofwords', 'wt') as endfile:
     for key in finalWord :
       endfile.write(key+'\n') 
endfile.close()

