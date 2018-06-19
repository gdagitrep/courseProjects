%input=csvread('/Users/dhsingh/Dropbox/ML3/data_and_scripts/train_inputs.csv',1,0);
%%
datadir = dir('/Users/dhsingh/OneDrive/ML3/data_as_images/train_images/*.png');
fileNames = {datadir.name};
img=cell(numel(fileNames),1);
for i=1:1:numel(fileNames)
    img{i} = imread(['/Users/dhsingh/OneDrive/ML3/data_as_images/train_images/',fileNames{i}]); 
end
%%
n=50000;
% M=zeros(n,2304);
for i=1:1:n
    %a=reshape(input(i,2:end),48,48);
    out=ifft2(fft2(img{i}));
    %features = extractHOGFeatures(out,'cellSize',[7,7]);
    imwrite(out,['/Users/dhsingh/OneDrive/ML3/data_as_images/train_images_filtered/',fileNames{i}])
    %M(i,:) = reshape(out,1,2304);
end
%csvwrite('/Users/dhsingh/OneDrive/ML3/FT_train.csv',M);
display('done');

%%
%%
%Test
datadir = dir('/Users/dhsingh/OneDrive/ML3/data_as_images/test_images/*.png');
fileNames = {datadir.name};
img_test=cell(numel(fileNames),1);
for i=1:1:numel(fileNames)
    img_test{i} = imread(['/Users/dhsingh/OneDrive/ML3/data_as_images/test_images/',fileNames{i}]);
    
end
display('done');
%%
n_test=20000;
M=zeros(n_test,2304);
for i=1:1:n_test
    %a=reshape(input(i,2:end),48,48);
    out=ifft2(fft2(img_test{i}));
    imwrite(out,['/Users/dhsingh/OneDrive/ML3/data_as_images/test_images_filtered/',fileNames{i}])
    %M(i,:) = reshape(out,1,2304);
end
%csvwrite('/Users/dhsingh/OneDrive/ML3/FT_test.csv',M);
display('done1');

%%
%imagesMNIST= loadMNISTImages('train-images-idx3-ubyte');
%MNISTlabel= loadMNISTLabels('train-labels-idx1-ubyte');

