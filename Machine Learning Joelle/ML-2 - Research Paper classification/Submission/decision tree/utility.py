import csv

def readData():   
        data = []
        valid=[]
        with open('/home/sidious/Desktop/train_input.csv', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',', quotechar='"')
            next(reader, None) 
            i=0
            for line in reader:        
                i=i+1
                if i < 80000:
                    data.append(line)
                else:
                    valid.append(line)


        results = []
        answers=[]
        with open('/home/sidious/Desktop/train_output.csv', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',', quotechar='"')
            next(reader, None) 
            i=0
            for line in reader:    
                i=i+1
                if i < 80000:
                    results.append(line)
                else:
                    answers.append(line)



        return (list((thedata[0], thedata[1],topic) for (thedata,[ordinal,topic]) in zip(data, results)),list((thedata[0], thedata[1],topic) for (thedata,[ordinal,topic]) in zip(valid, answers)))

def readTestData():   
        data = []
        with open('/home/sidious/Desktop/test_input.csv', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',', quotechar='"')
            next(reader, None) 
            for line in reader:        
                data.append(line)

        results = []
        with open('/home/sidious/Desktop/test_output.csv', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',', quotechar='"')
            next(reader, None) 
            for line in reader:    
                results.append(line)



        return list((thedata[0], thedata[1],topic) for (thedata,[ordinal,topic]) in zip(data, results))

def readFeatures():
     data = []
     valid=[]
     i=0
     with open('/home/sidious/Desktop/featuresmultinomial', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',')
            for line in reader:  
                for feature in xrange(len(line)-1):
                    line[feature] = int(line[feature])   
                i=i+1
                if i < 80000:   
                    data.append(line)
                else:
                    valid.append(line)
     return (data,valid)

def readFeatureWords():   
        words = []
        with open('/home/sidious/ml/bag', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',')
            next(reader, None) 
            for line in reader:   
                #if int(line[1]) < 800:
                #    break     
                words.append(line[0])

        return words

def readBagOfWords():
    word_bag=set()
    with open('/home/sidious/ml/bagofwordsig', 'rb') as csvfile:
        reader = csvfile.readlines()
        for line in reader: 
            print line
            word_bag.add(line.strip())
    return word_bag

def readFeatureList():
    word_bag=[]
    with open('/home/sidious/Desktop/features', 'rb') as csvfile:
        reader = csvfile.readlines()
        for line in reader: 
            word_bag.append(line)
    return word_bag

def getCats():
        return ['cs','math','stat','physics']

def initCatDict():
    return {'cs':{},'math':{},'physics':{},'stat':{}}

def initCatCounter():
    return {'cs':0,'math':0,'physics':0,'stat':0} 

def initConfusionDict():
    result = initCatDict()
    for cat in result:
        result[cat] = initCatCounter()
    return result
