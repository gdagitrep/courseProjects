HOMEIMAGES='/Users/dhsingh/Downloads/train/Images';
HOMEANNOTATIONS='/Users/dhsingh/Downloads/train/Annotations';
D=LMdatabase(HOMEANNOTATIONS);
%%
[Dcar,j]=LMquery(D,'object.name','building','exact');

%%
Dwindow=Dcar;

num = 1;
for i = 1:1:length(Dwindow);
    [annotation,img] = LMread(Dwindow, i, HOMEIMAGES);
for k = 1:1:length(Dwindow(i).annotation.object)
    
    imgCrop = LMobjectcrop(img,annotation,k );
    %ext(num).P= imgCrop;
     %mask = LMobjectmask(annotation, size(img), 'window');
     
    
    str = strcat(int2str(i),int2str(k),'.jpg');
    filename = fullfile('/Users/dhsingh/Dropbox/ML4/extracted_images/building/', str);
    imwrite(imgCrop,filename);
    num = num +1;
end
end

%%
[x,y] = LMobjectpolygon(Dcar(1).annotation, 10);
figure
plot(x{1}, y{1}, 'r')
axis('ij')
%%
% LMobjectnames(D);
[names,counts]=LMobjectnames(D);
