import csv

def readData():
        data = []
        results = []
        with open('train_input.csv', 'rb') as csvfile:
            with open('train_output.csv', 'rb') as csvfile2:
                reader = csv.reader(csvfile, delimiter=',', quotechar='"')
                reader2 = csv.reader(csvfile2, delimiter=',', quotechar='"')
                next(reader, None)
                next(reader2, None)
                for [line,line2] in zip(reader, reader2):
                    if line2[1]=="category":
                        continue
                    data.append(line)
                    results.append(line2)

        return list((thedata[0], thedata[1],topic) for (thedata,[ordinal,topic]) in zip(data, results))

def readTestData():
        data = []
        with open('test_input.csv', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',', quotechar='"')
            next(reader, None) 
            for line in reader:        
                data.append(line)

        results = []
        with open('test_output.csv', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',', quotechar='"')
            next(reader, None) 
            for line in reader:    
                results.append(line)



        return list((thedata[0], thedata[1],topic) for (thedata,[ordinal,topic]) in zip(data, results))

def readFeatures():
     data = []
     valid=[]
     i=0
     with open('features', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',')
            for line in reader:  
                for feature in xrange(len(line)-1):
                    line[feature] = int(line[feature])   
                i=i+1
                if i < 10000:   
                    data.append(line)
                elif i < 20000:
                    valid.append(line)
     return (data,valid)

def readFeatureWords():   
        words = []
        with open('wordlist0', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',')
            next(reader, None) 
            for line in reader:   
                if int(line[1]) < 800:
                    break     
                words.append(line[0])

        return words

def readBagOfWords(k):
    word_bag=set()
    with open('bagofwords_old'+str(k), 'rb') as csvfile:
        reader = csvfile.readlines()
        for line in reader: 
            word_bag.add(line.strip())
    return word_bag

def readFeatureList():
    word_bag=[]
    with open('features', 'rb') as csvfile:
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