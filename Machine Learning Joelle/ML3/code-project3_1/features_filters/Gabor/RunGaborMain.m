% clc
 %clear all
 %image_matrix=csvread('train_inputs.csv', 1,1);
 
 


gabor_array=gaborFilterBank(10,15,49,49);

[rows,columns]=size(image_matrix);
energy=zeros(25000,150);

for i=1:(rows/2)
    col=1;
    for j=1:48
        for k=1:48
            single_image(j,k)=image_matrix(i,col);
            col=col+1;
        end
    end
    
    [grows, gcolumns]=size(gabor_array);
    Ecol=1;
    
    for l=1:grows
        for m=1:gcolumns
            gabor_filter=cell2mat(gabor_array(l,m));
            img_conv= conv2(single_image, gabor_filter, 'same');
            F=fft2(img_conv);
            realF=log(real(F));
            magImage=abs(F).^2;
            energy(i,Ecol)=sum(magImage(:));
            Ecol=Ecol+1;
        end
    end 
    i
end
