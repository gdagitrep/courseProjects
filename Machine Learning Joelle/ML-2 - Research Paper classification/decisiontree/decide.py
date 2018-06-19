import tree
import utility
import math
import operator

class DecisionTree:

    def __init__(self,data1):
        self.data=data1
        self.tree = tree.Tree()

    def validateAccuracy(self, testdata,validtree):
        correct=0
        wrong=0
        print "length of testdata is " + str(len(testdata))
        for line in testdata:
            prediction=self.classify(line, validtree)
            if line[-1] == prediction:
                correct = correct+1
            else:
                wrong = wrong+1
        return correct/float(correct+wrong)


    def classify(self,item,tree):
        feature = tree.getData()
        if feature ==None:
            return  max(tree.getProbDict().iteritems(), key= operator.itemgetter(1))[0]
        if item[feature] == 0:
            return self.classify(item,tree.getTree()[1])
        else:
            return self.classify(item,tree.getTree()[2])

    def train(self, validdata):
        self.tree = self.createTree(self.data,[])
        print self.tree
        self.prune(validdata)

    def createTree(self, data, exclusions):
        featureindex = self.findBestSplit(self.calculateEntropy(data),data, exclusions)
        if featureindex == -1:
            next= tree.Tree()
            next.setProbDict(self.createProbDict(data))
            return next
        print "next feature index is " + str(featureindex)
        treebranch = tree.Tree()
        treebranch.setData(featureindex)
        treebranch.setProbDict(self.createProbDict(data))
        exclusions.append(featureindex)
        (rightbranch, leftbranch) = self.splitData(featureindex,data)

        if len(rightbranch) > 1 and len(set(item[-1] for item in rightbranch)) > 1:
            treebranch.setRightChild(self.createTree(rightbranch,exclusions))
        else:
            leaf = tree.Tree()
            leaf.setProbDict(self.createProbDict(rightbranch))
            treebranch.setRightChild(leaf)
        if len(leftbranch) > 1 and len(set(item[-1] for item in leftbranch)) > 1:
            treebranch.setLeftChild(self.createTree(leftbranch, exclusions))
        else:
            leaf = tree.Tree()
            leaf.setProbDict(self.createProbDict(leftbranch))
            treebranch.setLeftChild(leaf)
        return treebranch

    def prune(self, validdata):
        originalAccuracy=self.validateAccuracy(validdata,self.tree)
        while True:
            for node in self.tree.nodeList():
                if node.isLeaf():
                    prunedtree = self.tree.prune(node.getId())
                    newAccuracy = self.validateAccuracy(validdata,prunedtree)
                    if math.abs(originalAccuracy-newAccuracy) < threshold:
                        self.tree = prunedtree
                    else:
                        return
                    
                

    def createLeafDict(self, data):
        #return dictionary with prob of each category
        results = {}
        for item in data:
            cat = item[len(data[0])-1]
            if cat not in results:
                results[cat] = 0
            results[cat] = results[cat]+1
        for cat in results:
            total = results[cat]
            results[cat] = total/float(sum(results.values()))
        return results

    def calculateEntropy(self, data):
        results = utility.initCatDict()
        for cat in results:
            results[cat] = sum(1 for item in data if item[-1]==cat)
        total = sum(results.values())
        for cat in results:
            results[cat] = results[cat]/float(total)
        entropy = sum(item*math.log(item, 2) for item in results.values() if item !=0)
        entropy = -entropy
        return entropy

    def findBestSplit(self, entropyBefore,data,exclusions):
        maxInfoGain = 0
        featureindex = -1
        for i in xrange(len(data[0])-1):
            if i in exclusions:
                continue
           
            temp = self.calculateInfoGain(i, entropyBefore,data)
            print "info gain for feature " + str(i) + " is " + str(temp)
            if temp > maxInfoGain:
                print "new best info gain " + str(temp) + " at feature " + str(i)
                maxInfoGain = temp
                featureindex=i
        if maxInfoGain == 0:
            return -1   
        return featureindex

    def calculateInfoGain(self,i, prevEntropy,data):
        (right, left) = self.splitData(i,data)
        #calculate p(branch)
        pbranch = len(right)/float(len(right)+len(left))
        
        probsbycatright = utility.initCatDict()
        probsbycatleft = utility.initCatDict()

        if len(right) == 0 or len(left) == 0:
            print "hit zero on one branch"
            return 0
        for cat in utility.getCats():
            probsbycatright[cat] = sum(1 for item in right if item[-1] == cat)/float(len(data))
            probsbycatleft[cat] = sum(1 for item in left if item[-1] == cat)/float(len(data))

        firstterm= -pbranch*sum(val*math.log(val,2) for val in probsbycatright.values() if val > 0 )
        secondterm = -(1-pbranch)*sum(val*math.log(val,2) for val in probsbycatleft.values() if val >0)

        return prevEntropy-(firstterm+secondterm)

    def createProbDict(self, datalist):
        result = utility.initCatCounter()        
        for item in datalist:
            result[item[-1]] = result[item[-1]]+1
        total = sum(result.values())
        for cat in result:
            result[cat] = result[cat]/float(total)
        return result

    def splitData(self,index,thedata):
        right = []
        left = []
        for item in thedata:
            if item[index] == 0:
                right.append(item)
            else:
                left.append(item)

        return (right, left)

if __name__=='__main__':

    (data,valid)=utility.readFeatures()
    print "valid length is " + str(len(valid))
    decide= DecisionTree(data)
    decide.train(valid)
    print decide.tree
    print decide.testClassify([0,1,1,1,'0'], decide.tree)
