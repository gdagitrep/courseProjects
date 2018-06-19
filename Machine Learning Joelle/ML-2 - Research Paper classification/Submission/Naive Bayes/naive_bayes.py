import utility
import operator
import string
import re
import csv
import sys
import random
class NaiveBayes:
    def __init__(self,data1, wordlist):
        self.data =data1
        self.features = wordlist
        self.py1={}
        self.train()

    def extractFeaturesRaw(self, examples,id):
        extraction={}
        
        for [text,num] in zip(examples,id):
            num = int(num)
            extraction[num] = []
            text=re.sub('[^a-zA-Z,;!? ]','',text)
            text=re.sub("[,.;!?]",' ',text)
            text=text.lower()
            texts= text.split(' ')
            for word in self.features:
                if word in texts:
                    extraction[num].append(1)
                else:
                    extraction[num].append(0)
        return extraction

    def extractFeaturesByClass(self, examples):
        extraction=utility.initCatDict()
        
        for (num, text, cat) in examples:
            num = int(num)
            extraction[cat][num] = []
            text=re.sub('[^a-zA-Z,;!? ]','',text)
            text=re.sub("[,.;!?]",' ',text)
            text=text.lower()
            texts=text.split(' ')
            for word in self.features:
                if word in texts:
                    extraction[cat][num].append(1)
                else:
                    extraction[cat][num].append(0)
        return extraction
                
    def train(self):
        #number of points
        self.points = len(self.data)

        #probability of being of class cs
        for cat in utility.getCats():        
            self.py1[cat] = sum(1 for (num, text,item) in self.data if item == cat)

        extraction = self.extractFeaturesByClass(self.data)

        self.pfgivenc = utility.initCatDict()
    
        for cat in self.pfgivenc:         
            for index in xrange(len(self.features)):
                self.pfgivenc[cat][index]= sum(extraction[cat][f][index] for f in extraction[cat])/float(self.py1[cat])
    

    def validate(self, testdata):
        id = []
        textlist = []
        category = dict()
        for idT, textlistT, categoryT  in testdata:
            id.append(idT)
            textlist.append(textlistT)
            category[int(idT)] = categoryT

        extraction = self.extractFeaturesRaw(textlist,id)
        catlist = utility.initConfusionDict()

        for i in id:
            example = extraction[int(i)]

            result = {'cs':1,'math':1,'physics':1,'stat':1}
            for cat in utility.getCats():
                for index in xrange(len(example)):
                    result[cat] *= (self.pfgivenc[cat][index] if example[index] == 1 else 1 - self.pfgivenc[cat][index])
                result[cat] *= (self.py1[cat]/float(self.points))
                
            predicted = max(result.iteritems(), key= operator.itemgetter(1))[0]
            try:
                actual=category[int(i)]
            except Exception as err:
                exc(err)
                print err
            catlist[actual][predicted]+=1
        return catlist

    def test(self, textlist,id):
        extraction = self.extractFeaturesRaw(textlist,id)
        catlist = []

        for i in xrange(len(extraction)):
            try:
                try:
                    example = extraction[i]
                except Exception as err:
                    print str(id[i])+'#'+textlist[i]
                    exc(err)

                result = {'cs':1,'math':1,'physics':1,'stat':1}
                for cat in utility.getCats():
                    for index in xrange(len(example)):
                        try:
                            result[cat] *= (self.pfgivenc[cat][index] if example[index] == 1 else 1 - self.pfgivenc[cat][index])
                        except Exception as err:
                            print str(id[i])+'$'+textlist[i]
                            exc(err)
                    result[cat] *= self.py1[cat]
                    try:
                        result[cat] *= self.py1[cat]
                    except Exception as err:
                        print str(id[i])+'*'+textlist[i]
                        print result
                        exc(err)

                catlist.append((id[i],textlist[i], max(result.iteritems(), key= operator.itemgetter(1))[0]))
            except Exception as err:
                print str(id[i])+'-'+textlist[i]
                print result
                exc(err)
        return catlist    
def exc(errr):
    print errr
    print type(errr)     # the exception instance
    print errr.args      # arguments stored in .args
    print sys.exc_info()[0]
    SystemExit


# validates using 5-corss validation. K in the function here is the number of words in the bag
# not to be confused with the k in k -cross validation
def validateCall(data, wordlist,k):
    random.shuffle(data)

    l = int(len(data)/float(5))

    catlist={}
    for i in range(0,5):

        testdata = data[i*l:(i+1)*l]

        if i*l-1<0:
            traindata = data[(i+1)*l:]
        else:
            traindata= data[:i*l] +data[(i+1)*l:]

        bayes= NaiveBayes(traindata, wordlist)


        catlist =bayes.validate(testdata)


    precision={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0};
    recall={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0};
    TP ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0};
    TN ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0};
    FP ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0};
    FN ={'cs':0.0,'math':0.0,'physics':0.0,'stat':0.0};

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


def testcall (data ,wordlist , k):
    bayes= NaiveBayes(data, wordlist)


    test_list=[]
    id_list = []

    with open('test_input.csv', 'rb') as test_input:
        reader = csv.reader(test_input, delimiter=',', quotechar='"')
        next(reader,None)
        for line in reader:

                id_list.append(line[0])
                test_list.append(line[1])
    test_input.close()

    # test_list = test_list[:300]
    # id_list = id_list[:300]

    test_result =bayes.test(test_list,id_list)



    with open('test_output_submit_old_'+str(k)+'.csv', 'wt') as endfile:
        endfile.write('id'+','+'category'+'\n')
        for key in test_result :
            endfile.write(key[0]+','+key[2]+'\n')

        endfile.close()


if __name__=='__main__':
    k = sys.argv[1]
    print k
    data=utility.readData()
    wordlist=utility.readBagOfWords(k)

    # uncomment to call on the test data. The test data should be in the same folder as this script to run
    # testcall(data,wordlist,k)

    validateCall(data,wordlist,k)

    
