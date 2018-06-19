import csv
import operator

def getothercat(cat):
    if cat == 'cs':
        return ['math','stat','physics']
    elif cat == 'math':
        return ['cs','stat','physics']
    elif cat == 'stat':
        return ['cs','math','physics']
    else:
        return ['cs','math','stat']

def compare(x,y):
    catlist = x[1]
    first = max(x[1], key=lambda a: a[1])
    second = max(y[1], key=lambda b: b[1])
    if first==second: return 0
    return 1 if first > second else -1 

data = {'cs':{}, 'math':{},'stat':{},'physics':{}}

for cat in ['cs','math','stat','physics']:
    with open('/home/sidious/Desktop/wordlist'+cat, 'rb') as csvfile:
        reader = csv.reader(csvfile, delimiter=',', quotechar='"') 
        for (word, count,percent) in reader:     
            data[cat][word] = float(percent)

diff = {'cs':{}, 'math':{},'stat':{},'physics':{}}


for cat in data:
    for word in data[cat]:
        if word not in diff[cat]:
            diff[cat][word] =[]
        #diff[cat][word].append((cat,data[cat][word]))
        for othercat in getothercat(cat):

            if word in data[othercat]:
                diff[cat][word].append((abs(data[cat][word] - data[othercat][word]), othercat))
            else:
                diff[cat][word].append((data[cat][word], othercat))

for cat in diff:
    with open('/home/sidious/Desktop/difflist'+cat, 'wt') as endfile:
        for word in sorted(diff[cat].items(), cmp=lambda x,y: compare(x,y),reverse=True):
            endfile.write(word[0]+" " + str(max(word[1]))+"\n")

       
