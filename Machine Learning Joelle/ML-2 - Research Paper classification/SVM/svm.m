clear
trainf=fopen('../Features_train copy.csv','rt');
fmt=[repmat('%f ',1,737),'%s'];
out=textscan(trainf, fmt,'delimiter',',');
fclose(trainf);
train=out(1,12:737);
traint=cell2mat(train);
cat=out(1,738);
%% 

catt=cat{1,1};

test=csvread('../Features_test.csv');
testt=test(:,2:end);
%Testing_all=csvread('../Features_test.csv');
SVMStruct = svmtrain(traint,catt);
output=svmclassify(SVMStruct,testt);
