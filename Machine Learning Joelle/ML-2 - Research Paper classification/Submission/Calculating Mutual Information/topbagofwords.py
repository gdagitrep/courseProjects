import csv
import string
import math
import operator
import re
#
# k = 300
#
# filelist = ['mi_cs.csv','mi_math.csv','mi_physics.csv','mi_stat.csv']
# finalWord = set()
#
# for name in filelist:
#     data = dict()
#     with open(name , 'rb') as f:
#         reader = csv.reader(f, delimiter=',', quotechar='"')
#         for line in reader:
#             data[line[0]] = float(line[1])
#
#
#     sortKey = sorted(data, key = data.get , reverse = True)
#
#     i = 0
#     for key in sortKey :
#         finalWord.add(key)
#         i = i +1
#         if i == k:
#             break
#
# with open('bagofwords', 'wt') as endfile:
#      for key in finalWord :
#        endfile.write(key+'\n')
# endfile.close()

k = 788

filelist = ['mi_cs.csv','mi_math.csv','mi_physics.csv','mi_stat.csv']
finalWord = set()

datalist = []
stopword = []

with open('stop_words.csv' , 'rb') as f:
        reader = csv.reader(f, delimiter=',', quotechar='"')
        for line in reader:
            word = line[0].strip()
            stopword.append(word)

for name in filelist:
    with open(name , 'rb') as f:
        reader = csv.reader(f, delimiter=',', quotechar='"')
        for line in reader:
            if line[0] not in stopword:
                datalist.append((line[0],float(line[2])))

datalist = sorted(datalist, key=lambda datalist: datalist[1], reverse= False)

#sortKey = sorted(data, key = data.get , reverse = True)

sortedwordlist = []
i = 0
for key in datalist :
    if key[0] not in finalWord:
        finalWord.add(key[0])
        sortedwordlist.append(key[0])
        i = i +1
    if i == k:
        break

with open('bagofwords'+str(k), 'wt') as endfile:
     for key in sortedwordlist :
       endfile.write(key+'\n')
endfile.close()


