import sklearn.ensemble
import utility

forest = sklearn.ensemble.RandomForestClassifier(
    n_estimators=10, #number of trees
    criterion="gini" #or entropy
)

(data,valid) = utility.readFeatures()

y= (item[-1] for item in data)
x = (item[:-1] for item in data)

forest.fit(list(x),list(y))

results = zip(forest.predict(list(item[:-1] for item in valid)), (item[-1] for item in valid))

print sum(1 for item in results if item[0]==item[1])
