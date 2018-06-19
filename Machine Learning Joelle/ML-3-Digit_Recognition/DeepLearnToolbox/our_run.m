datadir = dir('/Users/dhsingh/Onedrive/ML3/data_as_images/train_images_filtered/*.png');
fileNames = {datadir.name};
n=numel(fileNames);
train_x=zeros(48,48,n);
for i=1:1:n
    train_x(:,:,i) = double(imread(['/Users/dhsingh/Onedrive/ML3/data_as_images/train_images_filtered/',fileNames{i}]))/255.0;
    
end
%%
train_labels=csvread('/Users/dhsingh/Onedrive/ML3/data_and_scripts/train_outputs.csv',1,1);
train_y=zeros(10,n);
for i=1:n
    train_y(train_labels(i)+1,i)=1;
end
clear train_labels;
%%
datadir = dir('/Users/dhsingh/Onedrive/ML3/data_as_images/test_images_filtered/*.png');
fileNames = {datadir.name};
n=numel(fileNames);
test_x=zeros(48,48,n);
for i=1:1:n
    test_x(:,:,i) = double(imread(['/Users/dhsingh/Onedrive/ML3/data_as_images/test_images_filtered/',fileNames{i}]))/255.0;
    
end
clear fileNames i n datadir;
%%
clear cnn opts net h;
rand('state',0)

cnn.layers = {
    struct('type', 'i') %input layer
    struct('type', 'c', 'outputmaps', 6, 'kernelsize', 5) %convolution layer
    struct('type', 's', 'scale', 2) %sub sampling layer
    struct('type', 'c', 'outputmaps', 12, 'kernelsize', 5) %convolution layer
    struct('type', 's', 'scale', 2) %subsampling layer
};


opts.alpha = 1;
opts.batchsize = 50;
opts.numepochs = 10;

cnn = cnnsetup(cnn, train_x, train_y);
display('cnn setup done');
cnn = cnntrain(cnn, train_x, train_y, opts);
display('training done');
%%
net=cnn;
net = cnnff(net, test_x);
[~, h] = max(net.o);
display('testing done');

%%
datadir = dir('/Users/dhsingh/Onedrive/ML3/data_as_images/train_images/*.png');
fileNames = {datadir.name};
img=cell(numel(fileNames),1);
n=numel(fileNames);
train_labels=csvread('/Users/dhsingh/Onedrive/ML3/data_and_scripts/train_outputs.csv',1,1);
x=[];
dir='/media/shrey/d/Masters/courses/Applied Machine Learning/Project3/data_and_scripts/img/train_images/';
for i=1:1:10
    a=strcat(dir, fileNames{i}, ' ' ,train_labels(i))
    x =[x; a];
end
