#!/usr/bin/python

import csv
import re

with open('datasets/train_input.csv', 'rb') as inputcsv:
	with open('datasets/train_output.csv','rb') as outputcsv:
	
		data = csv.reader(inputcsv, delimiter=',')
		cate = csv.reader(outputcsv, delimiter=',')
		dict = dict()
		boo =  True
		for d , c in zip(data , cate):
			if boo :
				boo = False 
				continue
			
			text = d[1].split()
			for eachword in text :
				eachword = re.sub('[^A-Za-z]+','',eachword)
				eachword = eachword + ','+ c[1]
				temp = dict.get(eachword)
				if temp :
					value = dict.get(eachword)
					value = value + 1
					dict[eachword] = value
				else:
					dict[eachword] = 1
		
		f = open('datasets/dict.csv' , 'w')
		for key in dict.keys():
			f.write (key + ',' + str(dict.get(key)))
			f.write ( '\n')
		f.close	
		#	l = c[1]+'/'+c[0] 	
		#	f = open(l , 'w')
		#	print 'id %s cat %s id %s' % (c[0] , c [1] , d[1] )
		#	print '\n'
		#	f.write(d[1])
		#	f.close()

	 
