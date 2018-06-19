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


def readFeatureWords():   
        words = []
        with open('/home/sidious/Desktop/wordlist0', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',')
            next(reader, None) 
            for line in reader:   
                if int(line[1]) < 800:
                    break     
                words.append(line[0])

        return words

def readBagOfWords():
    word_bag=set()
    with open('bagofwords736', 'rb') as csvfile:
        reader = csvfile.readlines()
        for line in reader: 
            word_bag.add(line.strip())
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
