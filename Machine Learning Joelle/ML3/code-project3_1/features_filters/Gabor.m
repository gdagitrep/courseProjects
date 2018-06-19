%%
datadir = dir('/Users/dhsingh/OneDrive/ML3/data_as_images/train_images/*.png');
fileNames = {datadir.name};
img=cell(numel(fileNames),1);
for i=1:1:numel(fileNames)
    img{i} = imread(['/Users/dhsingh/OneDrive/ML3/data_as_images/train_images/',fileNames{i}]);
    
end
%%
n=50000;
M=zeros(n,2304);
close all;
for i=10:1:11
    gaborArray = gaborFilterBank(5,8,39,39);  % Generates the Gabor filter bank
    featureVector = gaborFeatures(img{i},gaborArray,6,6);
    ft= fft2(img{i});
    prod= ft.*featureVector;
    iift=iift2(prod);
%     figure('Name',['original' i]);
%     imshow(img{i});
%     figure(i);
%     out=CoherenceFilter(img{i}, struct('Scheme','R','eigenmode',4));
%     imshow(out);
end


