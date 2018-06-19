import utility
import string

features = utility.readFeatureWords()

datalist = utility.readData()

results = {}

for (num,text,cat) in datalist:
    text = text.translate(None, string.punctuation).lower()
    results[num]=[]
    for word in features:
        if word in text.split(' '):
            results[num].append(1)
        else:
            results[num].append(0)
    results[num].append(cat)

with open('/home/sidious/Desktop/features', 'wt') as endfile:
    for element in results:
        for feature in results[element]:
            endfile.write(str(feature))
            if results[element].index(feature) != len(results[element]) -1:
                endfile.write(",")
        endfile.write("\n")



