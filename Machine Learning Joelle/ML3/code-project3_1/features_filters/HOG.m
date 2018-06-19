%input=csvread('/Users/dhsingh/Dropbox/ML3/data_and_scripts/train_inputs.csv',1,0);
%%
datadir = dir('/Users/dhsingh/Dropbox/ML3/data_as_images/train_images/*.png');
fileNames = {datadir.name};
img=cell(numel(fileNames),1);
for i=1:1:numel(fileNames)
    img{i} = imread(['/Users/dhsingh/Dropbox/ML3/data_as_images/train_images/',fileNames{i}]);
    
end
%%
n=50000;
M=zeros(n,900);
for i=1:1:n
    %a=reshape(input(i,2:end),48,48);
    out=ifft2(fft2(img{i}));
    features = extractHOGFeatures(out,'cellSize',[7,7]);
    M(i,:) = features;
    
end
%%
csvwrite('/Users/dhsingh/Dropbox/ML3/data_as_images/train_images/HOG_7.csv',M);
display('done');





%%
%%
%Test
datadir = dir('/Users/dhsingh/Dropbox/ML3/data_as_images/test_images/*.png');
fileNames = {datadir.name};
img_test=cell(numel(fileNames),1);
for i=1:1:numel(fileNames)
    img_test{i} = imread(['/Users/dhsingh/Dropbox/ML3/data_as_images/test_images/',fileNames{i}]);
    
end
%%
n_test=20000;
M=zeros(n_test,900);
for i=1:1:n_test
    %a=reshape(input(i,2:end),48,48);
    out=ifft2(fft2(img_test{i}));
    features = extractHOGFeatures(out,'cellSize',[7,7]);
    M(i,:) = features;
    
end
display('done');
%%
csvwrite('/Users/dhsingh/Dropbox/ML3/data_as_images/test_images/HOG_7_test.csv',M);
display('done1');



