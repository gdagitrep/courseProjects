import utility
import operator
import string
import csv
class NaiveBayes:
    def __init__(self,data1, wordlist):
        self.data =data1
        self.features = wordlist
        self.py1={}
        self.train()

    def extractFeaturesRaw(self, examples):
        extraction={}
        
        for text in examples:
            num = examples.index(text)
            extraction[num] = []
            text = text.translate(None, string.punctuation).lower()
            for word in self.features:
                if word in text.split(' '):
                    extraction[num].append(1)
                else:
                    extraction[num].append(0)
        return extraction

    def extractFeaturesByClass(self, examples):
        extraction=utility.initCatDict()
        
        for (num, text, cat) in examples:
            num = int(num)
            extraction[cat][num] = []
            text =text.translate(None, string.punctuation).lower()
            for word in self.features:
                if word in text.split(' '):
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
        extraction = self.extractFeaturesRaw(list(text for (text,cat) in testdata))
        catlist = utility.initConfusionDict()

        for i in xrange(len(extraction)):
            example = extraction[i]

            result = {'cs':1,'math':1,'physics':1,'stat':1}
            for cat in utility.getCats():
                for index in xrange(len(example)):
                    result[cat] *= (self.pfgivenc[cat][index] if example[index] == 1 else 1 - self.pfgivenc[cat][index])
                result[cat] *= (self.py1[cat]/float(self.points))
                
            predicted = max(result.iteritems(), key= operator.itemgetter(1))[0]
            actual=testdata[i][1]
            catlist[actual][predicted] = catlist[actual][predicted]+1


        return catlist    

    def test(self, textlist):
        extraction = self.extractFeaturesRaw(textlist)
        catlist = []

        for i in xrange(len(extraction)):
            example = extraction[i]
            result = {'cs':1,'math':1,'physics':1,'stat':1}
            for cat in utility.getCats():
                for index in xrange(len(example)):
                    result[cat] *= (self.pfgivenc[cat][index] if example[index] == 1 else 1 - self.pfgivenc[cat][index])
                result[cat] *= self.py1[cat]
            catlist.append((textlist[i], max(result.iteritems(), key= operator.itemgetter(1))[0]))
            
        return catlist    
            
if __name__=='__main__':
    data=utility.readData()
    len(data)
    wordlist=utility.readBagOfWords()

    bayes= NaiveBayes(data, wordlist)
    test_list=[]
    with open('train_input.csv', 'rb') as test_input:
        reader = csv.reader(test_input, delimiter=',', quotechar='"')
        next(reader,None)
        for line in reader:
            test_list.append(line[1])
    test_input.close()
    ##catlist =bayes.validate()
    test_result=bayes.test(test_list[0:2])
    print test_result
    ##for i in catlist:
    ##    print catlist[i]
    