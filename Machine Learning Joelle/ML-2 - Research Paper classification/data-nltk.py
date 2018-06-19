import nltk;
import csv
import re
list_of_INT=['NN','NNS','NNP','NNPS','VB','VBD','VBG','VBN']
output=[]
output2=[]
dic=dict()
with open('datasets/train_input.csv', 'rb') as inputcsv:
	#with open('datasets/train_output.csv','rb') as outputcsv:
		data = csv.reader(inputcsv, delimiter=',')
	
		boo =  True
		i=0;
		for d in data:
			
			if boo :
				boo = False 
				continue
			d[1]=re.sub('[^a-zA-Z,;!? ]','',d[1])
			d[1]=re.sub("[,.;!?]",' ',d[1])
			d[1]=d[1].lower()
			tokens=nltk.word_tokenize(d[1])
			tagged = nltk.pos_tag(tokens)
			for words in tagged:
				if words[1] in list_of_INT and words[0] not in dic:
					dic[words[0]]=words[1]
#					output.append(words[0])
#					output2.append(words[1])
		f = open('datasets/important_words.csv' , 'w')
		for key in dic.keys():
			f.write (key+','+dic.get(key))
			f.write ('\n')
		f.close()
		print "over"