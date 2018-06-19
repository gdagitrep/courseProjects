import csv
import sys
import string
import math
import operator

data = []
with open('/home/sidious/Desktop/train_input.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    next(reader, None) 
    for line in reader:        
        data.append(line)

results = []
with open('/home/sidious/Desktop/train_output.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    next(reader, None) 
    for line in reader:        
        results.append(line)

stop_words = set()
with open('/home/sidious/ml/stop_words', 'rb') as csvfile:
    reader = csvfile.readlines()
    for line in reader: 
        stop_words.add(line.strip())


all_words=set()
wordlist = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
docstats={'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
numtopic={'cs':0, 'math':0,'stat':0,'physics':0,'category':0}
overall={}
i=0
otherfeatures = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
for topic in otherfeatures:
    otherfeatures[topic]['dollars'] = {}
    otherfeatures[topic]['uppercase'] = {}
    otherfeatures[topic]['length'] = {}
    otherfeatures[topic]['punc'] = {}
    otherfeatures[topic]['digits'] = {}
    otherfeatures[topic]['vars'] = {}

for ([num,text],[ordinal,topic]) in zip(data, results):
    otherfeatures[topic]['dollars'][num]= text.count('$')
    otherfeatures[topic]['uppercase'][num] = sum(1 for char in text if char.isupper())
    otherfeatures[topic]['length'][num] = len(text.split())
    otherfeatures[topic]['punc'][num] =sum(1 for char in text if char in string.punctuation)
    otherfeatures[topic]['digits'][num] =sum(1 for char in text if char.isdigit())
    otherfeatures[topic]['vars'][num] =sum(1 for word in text.split() if len(word)==1 and word[0] != 'a' and word[0].islower())
    text =text.translate(None, string.punctuation)
    docstats[topic][num]=set()
    numtopic[topic]= numtopic[topic]+1
    for word in text.strip().split(' '): 
        word = word.lower()
        if word in stop_words or len(word)==0:
            continue
        if word in overall:
            overall[word]=overall[word]+1
        else:
            overall[word]=1
        if word in wordlist[topic]:
            wordlist[topic][word] =wordlist[topic][word]+1
        else:
            wordlist[topic][word] = 1
        docstats[topic][num].add(word)
        all_words.add(word)
      

pi = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}

for metric in ['dollars','uppercase','length','punc','digits','vars']:
    for topic in otherfeatures:
       mean = float(sum(otherfeatures[topic][metric].values()))/numtopic[topic]
       var = sum(map(lambda x: math.pow(x- mean,2), otherfeatures[topic][metric].values()))/numtopic[topic]
  


for topic in docstats:
    if topic == 'category':
        continue
    for word in all_words:
        docContain=0
        for num in docstats[topic]:
            if word in docstats[topic][num]:
                docContain = docContain+1
        pi[topic][word]= float(docContain)/numtopic[topic]

i=0

overall_sorted = sorted(overall.items(), key=operator.itemgetter(1),reverse=True)
with open('/home/sidious/Desktop/wordlist'+str(i), 'wt') as endfile:
        for (key,value) in overall_sorted:
            endfile.write(key+","+str(value)+"\n")

for cat in wordlist:
    i=i+1
    cat_sorted = sorted(wordlist[cat].items(), key=operator.itemgetter(1),reverse=True)
    with open('/home/sidious/Desktop/wordlist'+cat, 'wt') as endfile:
         for (key,value) in cat_sorted:
            if key in pi[cat]:
                endfile.write(key+","+str(value)+","+str(pi[cat][key])+"\n")
            else:
                endfile.write(key+","+str(value)+",0\n")

