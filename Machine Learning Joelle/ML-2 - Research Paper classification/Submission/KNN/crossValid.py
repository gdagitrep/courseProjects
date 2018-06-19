__author__ = 'Shrey'
import csv
import math
import concurrent.futures

# k nearest neighbours
k = 40


def initCatDict():
    return {'cs':{},'math':{},'physics':{},'stat':{}}

def initCatCounter():
    return {'cs':0,'math':0,'physics':0,'stat':0}

def initConfusionDict():
    result = initCatDict()
    for cat in result:
        result[cat] = initCatCounter()
    return result

#outputlist

catlist = initConfusionDict()
# id : 1 0 1 0 1
def dist_between_example(test_example,train_example):
    #read their features
    distsq=0


    for [i,j] in zip(test_example[1:-1], train_example[1:-1]):
        m=float(int(i)-int(j))
        distsq = distsq +  m*m
    return distsq

#for top k entries from list and gives them weights accordingly, and uses those weights to calculate sum for each category
def selectclass(sorted_listofdistance):

    summGaussian={"cs":0, "math":0,"stat":0, "physics":0}
    for each in sorted_listofdistance:
        #using inverse of distance as weights (can be replaces with gaussian also)
        #summ[each[1]] += float(float(1.0)/float(each[0]))
        summGaussian[each[1]] += math.exp(-float(each[0])/float(2))


    sorted_sum_gaussian = sorted(summGaussian, key=summGaussian.get, reverse =True)

    return sorted_sum_gaussian[0]


def eachexample(test_example):
    listofdistance=[]

    for train_example in trainingset:
        if train_example[-1] == "1category":
            continue
        listofdistance.append([dist_between_example(test_example,train_example),train_example[-1]])

    sorted_listofdistance = sorted(listofdistance, key = lambda listofdistance: listofdistance[0])

    predictedclassGaussian = selectclass(sorted_listofdistance[0:k])
    catlist[test_example[-1]][predictedclassGaussian] +=1

l = 0
# id featurs category
trainingset=[]

with open('Features_train.csv', 'rb') as train_input:
    reader = csv.reader(train_input, delimiter=',')
    next(train_input,None)
    for data in reader:
        trainingset.append(data)
    print "dataread "
    testset = trainingset[:500]
    trainingset = trainingset[500:10501]
train_input.close()

i = 0
#take an example out of test set
for test_example in testset:
    eachexample(test_example)
    print i
    i +=1


precision={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0}
recall={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0}
TP ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0}
TN ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0}
FP ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0}
FN ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0}

#   cs  math    phy stat
#cs
#m
#ph
#st
for cat in catlist:
    TP[cat]=catlist[cat][cat]

    for notcat in catlist:
        if notcat!=cat:
            FP[cat]+=catlist[notcat][cat]
            FN[cat]+=catlist[cat][notcat]
        for notcatcol in catlist:
            if notcatcol!= cat and notcat!=cat:
                TN[cat]+=catlist[notcat][notcatcol]
    try:
        precision[cat] = TP[cat]/(TP[cat]+FP[cat])
        recall[cat] = TP[cat]/(TP[cat]+FN[cat])
    except Exception as err:
        print catlist
        print err

avgPrecision = 0.0
avgRecall = 0.0


for cat in precision:
    avgPrecision +=precision[cat]
    avgRecall += recall[cat]

avgPrecision = avgPrecision/4
avgRecall = avgRecall/4

Fscore = 2* (avgPrecision*avgRecall)/(avgPrecision+avgRecall)

print k
print "avgPrecision %f" % avgPrecision
print "avgRecall %f" % avgRecall
print "Fscore %f" % Fscore
