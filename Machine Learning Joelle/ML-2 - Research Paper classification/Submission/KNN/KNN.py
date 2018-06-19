import csv
import math
import concurrent.futures

# k nearest neighbours
k = 40

#outputlist

output_list = []
# id : 1 0 1 0 1
def dist_between_example(test_example,train_example):
    #read their features
    distsq=0
    for [i,j] in zip(test_example[1:], train_example[1:-1]):
        m=float(int(i)-int(j))
        distsq = distsq +  m*m
    return distsq

def initCatDict():
    return {'cs':{},'math':{},'physics':{},'stat':{}}

def initCatCounter():
    return {'cs':0,'math':0,'physics':0,'stat':0}

def initConfusionDict():
    result = initCatDict()
    for cat in result:
        result[cat] = initCatCounter()
    return result



#for top k entries from list and gives them weights accordingly, and uses those weights to calculate sum for each category
def selectclass(sorted_listofdistance):
    summGaussian={"cs":0, "math":0,"stat":0, "physics":0}
    for each in sorted_listofdistance:
        #using inverse of distance as weights (can be replaces with gaussian also)
        summGaussian[each[1]] += math.exp(-float(each[0])/float(2))
    #print summ[each[1]]

    sorted_sum_gaussian = sorted(summGaussian, key=summGaussian.get, reverse =True)
    
    return sorted_sum_gaussian[0]


def eachexample(test_example):
    listofdistance=[]

    for train_example in trainingset:
        if train_example[-1] == "category":
            continue
        listofdistance.append([dist_between_example(test_example,train_example),train_example[-1]])
   
    sorted_listofdistance = sorted(listofdistance, key = lambda listofdistance: listofdistance[0])

    predictedclassSumm, predictedclassGaussian = selectclass(sorted_listofdistance[0:k])    #print "predictedclass= " +predictedclass

    print test_example[0]+','+predictedclassSumm+','+predictedclassGaussian+'\n'
    output_list.append(test_example[0]+','+predictedclassSumm+','+predictedclassGaussian+'\n')

l = 0
# id featurs category
trainingset=[]

with open('Features_train.csv', 'rb') as train_input:
    reader = csv.reader(train_input, delimiter=',')
    next(train_input,None)
    for data in reader:
        trainingset.append(data)



    # id feature
    with open('Features_test.csv', 'rb') as test_input:
        testset=csv.reader(test_input, delimiter=',')
        next(testset,None)
        #take an example out of test set
        with concurrent.futures.ThreadPoolExecutor(max_workers=7) as executor:
                for test_example in testset:
                    future  = executor.submit(eachexample, test_example)
        print("done")
    test_input.close()
train_input.close()




with open('Feature_output_all.csv','w') as test_output:
    test_output.write('id'+","+'categorysumm'+','+'categorygaussian'+'\n')
    for each in output_list:
        test_output.write(each)
test_output.close()
