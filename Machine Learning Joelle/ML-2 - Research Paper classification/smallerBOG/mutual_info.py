import traceback
import csv
import sys
import string
import math
import operator
import re
from decimal import Decimal
import concurrent.futures



n11 = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n10 = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n00 = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n01 = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n0_ = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n1_ = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n_0 = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n_1 = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}
n = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}

mi = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}

def calculate_mi():
    for word in wordlist[topic]:
        n11[topic][word] = float(wordlist[topic][word])
        n10[topic][word] = float(numtopic[topic] - wordlist[topic][word])
        docNotClassWord = 0
        docNotClassNotWord = 0
        for topic1 in wordlist:
            if topic1 == 'category' and topic1 == topic:
                continue
            for word1 in wordlist[topic1]:
                if word1 != word:
                    docNotClassNotWord = docNotClassNotWord + wordlist[topic1][word1]
                else:
                    docNotClassWord = docNotClassWord + wordlist[topic1][word1]
        n00[topic][word] = float(docNotClassNotWord)
        n01[topic][word] = float(docNotClassWord)
        n1_[topic][word] = float(n10[topic][word] + n11[topic][word])
        n0_[topic][word] = float(n00[topic][word] + n01[topic][word])
        n_0[topic][word] = float(n10[topic][word] + n00[topic][word])
        n_1[topic][word] = float(n01[topic][word] + n11[topic][word])
        n[topic][word] = float(n01[topic][word] + n11[topic][word] + n10[topic][word] + n00[topic][word])
        term1 =-1
        term2 =-1
        term3 =-1
        term4 =-1
        # print "n11 " + topic +" "+word+" "+str(n11[topic][word])
        # print "n10 " + topic +" "+word+" "+str(n10[topic][word])
        # print "n00 " + topic +" "+word+" "+str(n00[topic][word])
        # print "n01 " + topic +" "+word+" "+str(n01[topic][word])
        # print "n1_ " + topic +" "+word+" "+str(n1_[topic][word])
        # print "n0_ " + topic +" "+word+" "+str(n0_[topic][word])
        # print "n_0 " + topic +" "+word+" "+str(n_0[topic][word])
        # print "n_1 " + topic +" "+word+" "+str(n_1[topic][word])

        try:
            if ((n[topic][word]*n11[topic][word])/(n1_[topic][word]*n_1[topic][word])) != 0.0  :
                term1 = float((n11[topic][word]/n[topic][word])*math.log(((n[topic][word]*n11[topic][word])/(n1_[topic][word]*n_1[topic][word])),2))
            else :
                term1 = 0
            if ((n[topic][word]*n01[topic][word])/(n0_[topic][word]*n_1[topic][word])) != 0.0 :
                term2 = float((n01[topic][word]/n[topic][word])*math.log(((n[topic][word]*n01[topic][word])/(n0_[topic][word]*n_1[topic][word])),2))
            else :
                term2 = 0
            if ((n[topic][word]*n10[topic][word])/(n1_[topic][word]*n_0[topic][word])) != 0.0 :
                term3 = float((n10[topic][word]/n[topic][word])*math.log(((n[topic][word]*n10[topic][word])/(n1_[topic][word]*n_0[topic][word])),2))
            else :
                term3 = 0
            if ((n[topic][word]*n00[topic][word])/(n0_[topic][word]*n_0[topic][word])) != 0.0 :
                term4 = float((n00[topic][word]/n[topic][word])*math.log(((n[topic][word]*n00[topic][word])/(n0_[topic][word]*n_0[topic][word])),2))
            else:
                term4 = 0
        except Exception, err:
            print traceback.format_exc()
            print float(((n[topic][word]*n01[topic][word])/(n0_[topic][word]*n_1[topic][word])))
        finally :
            mi[topic][word] = term1 + term2 +term3+term4
            #print "mi  "+str(mi[topic][word])
        




data = []
with open('train_input.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    next(reader, None) 
    for line in reader:        
        data.append(line)

results = []
with open('train_output.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    next(reader, None) 
    for line in reader:        
        results.append(line)

imp_words = set()
with open('important_words.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    next(reader, None) 
    for line in reader:        
        imp_words.add(line[0])

stop_words = set()
with open('stop_words.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    next(reader, None) 
    for line in reader:        
        stop_words.add(line[0])
    #print stop_words

#set of all words
all_words =set()

#number of documents the words occurs in a topic: wordlist[topic][word] = count   |   n11[poultry][export] = wordlist[topic][word] 
wordlist = {'cs':{}, 'math':{},'stat':{},'physics':{},'category':{}}

#number of documents in each topic n10[poultry][!export] = numtopic[poultry] - wordlist[topic][word]
numtopic={'cs':0, 'math':0,'stat':0,'physics':0,'category':0}

# n00[!poultry][!export] = sum (for each word != export && topic != poultry)
# n01[!poultry][export] = sum (for each word == export && topic != poultry)
# n10[poultry][!export] = numtopic[poultry] - wordlist[topic][word]
# n11[poultry][export] =  wordlist[poultry]['export']


i=0

for ([num,text],[ordinal,topic]) in zip(data, results):
    #text =text.translate(None, string.punctuation)
    text = re.sub('[^a-zA-Z,;!? ]','',text)
    text = re.sub("[,.;!?]",' ',text)
    text = text.lower()
    numtopic[topic]= numtopic[topic]+1
    for word in text.strip().split(' '):
        if word not in imp_words or word in stop_words:
            continue
        if word in wordlist[topic]:
            wordlist[topic][word] =wordlist[topic][word]+1
        else:
            wordlist[topic][word] = 1
        all_words.add(word)

with concurrent.futures.ThreadPoolExecutor(max_workers=7) as executor:
        for topic in wordlist:
            if topic == 'category':
                continue
            future  = executor.submit(calculate_mi)
            
print("done")


for topic in wordlist:
    with open('mi_'+topic+'.csv', 'wt') as endfile:
            for key in mi[topic]:
            	endfile.write(key+","+str(mi[topic][key])+"\n")
    endfile.close()
