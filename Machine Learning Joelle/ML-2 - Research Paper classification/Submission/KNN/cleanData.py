import csv
import string

#def make_feature_file():
bagofwords_set=set();
with open('bagofwords_2000','rb') as bagofwords:
    for word in bagofwords:
        bagofwords_set.add(word.strip())
bagofwords.close()

with open('cleaned_training_data.csv', 'rb') as inputcsv:
    with open('Features_train.csv', 'w') as outfile:
        data = csv.reader(inputcsv, delimiter=',')
        
        training_FF= "id"
        for bog in bagofwords_set:
            training_FF= training_FF+","+bog
        outfile.write(training_FF+"\n")
        
        for line in data:
            line_stripped= line[1].strip().split(' ')
            training_FF=line[0]
            
            for bog in bagofwords_set:
                if bog in line_stripped:
                    out="1"
                else:
                    out="0"    
                training_FF= training_FF+","+out
            training_FF=training_FF+","+line[2]
            outfile.write(training_FF+"\n")
    outfile.close()
inputcsv.close()
        

with open('cleaned_test_data.csv', 'rb') as test_input:
    with open('Features_test.csv', 'w') as outfile:
	    data = csv.reader(test_input, delimiter=',')
	    test_FF='id'
	    for bog in bagofwords_set:
	        test_FF = test_FF +","  + bog
	    outfile.write(test_FF+"\n")    
            for line in data:
            	line_stripped = line[1].split(' ')
            	test_FF = line[0]
            	for bog in bagofwords_set:
                	if bog in line_stripped:
                  	  	out="1"
                	else:
                    		out="0"
                	test_FF= test_FF+","+out
         	outfile.write(test_FF+"\n")
    outfile.close()
test_input.close()


