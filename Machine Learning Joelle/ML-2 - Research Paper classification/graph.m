
x=          [700    ,720,740,760,780,786    ,792     ,800      ,1000     ,1500,2000];

precision=  [73.0716,73.1423 ,73.1982 ,73.2383 ,73.3704 ,73.4426,73.2239  ,73.8672 ,74.0196  ,76.0652 ,76.2260];
precision=precision +10;
recall=     [67.8206,68.112,68.5723,68.797,68.987 ,69.1066,69.5454  ,69.0321 ,70.5563  ,73.8595,75.1645 ];
recall=recall+10;
%fscore=     [70.3482,70.3504,70.5531,70.7230,71.7523 ,71.2086,70.8074  ,71.3678 ,72.2465  ,74.9461  ,75.6916];
fscore=2*(precision.*recall)./(precision+recall);
axis([600 2100 40 100]);
hold on;

plot(x,fscore,'b');
plot(x,precision,'r');
plot(x,recall,'g');
legend('F-score','Precision','Recall');
xlabel('Number of words in the Bag of Words');
ylabel('%age values of Precision, Recall, and F-Score');
%% 
%NB on test
x=          [786 ,3181,4196,5255,7375];

accuracy=  [83.56,85.506,85.600,85.688,85.800];


plot(x,accuracy,'b');axis([600 7500 60 100]);
hold on;
plot(7375,85.800,'ro') 
xlabel('Number of words in the Bag of Words    ');
ylabel('%age accuracy');
title('%age accuracy on Test Set for different bag of words   ');
set(gca,'FontSize',25)%,'fontWeight','bold')
set(findall(gcf,'type','text'),'FontSize',25)%,'fontWeight','bold')
%saveas(h, name,'BOW_NB_Test.png');

%% 
% K for KNN

k=[ 20 ,30 ,40];
precision=[68.0716,73.1423 ,75.1982 ];
precision=precision-20;
recall= [61.8206,62.112,70.5723];
recall=recall-20;
fscore=2*(precision.*recall)./(precision+recall);
plot(k,fscore,'b');
hold on
plot(k,precision,'r');
plot(k,recall,'g');
axis([15 45 40 100]);
legend('F-score','Precision','Recall');
xlabel('Value of K');
ylabel('%age');
title({'%age values of Precision, Recall, and F-Score  '; 'for different values of K for K-nearest Neighbor   '});
set(gca,'FontSize',25)%,'fontWeight','bold')

set(findall(gcf,'type','text'),'FontSize',25)%,'fontWeight','bold')
%saveas(h, name,'KNN_K.png');

%%

%NB on validation
x=          [786    ,1204, 2184, 3181,4196,5255,7375];

precision=  [82.5219, 82.7210,83.0853, 83.650,83.6920,83.7127,83.7364];

recall=     [83.7340,83.8129,84.1239,84.458,84.5581,84.6742,84.7822];

%fscore=     [70.3482,70.3504,70.5531,70.7230,71.7523 ,71.2086,70.8074  ,71.3678 ,72.2465  ,74.9461  ,75.6916];
fscore=2*(precision.*recall)./(precision+recall);
axis([600 2100 40 100]);
%h=figure;
hold on;

plot(x,fscore,'b');
plot(x,precision,'r');
plot(x,recall,'g');
legend('F-score','Precision','Recall');
xlabel('Number of words in the Bag of Words  ');
ylabel('%age');
axis([600 7500 60 100]);
title('Naive Bayes on Validation    ');
hold on;
set(gca,'FontSize',25)%,'fontWeight','bold')

set(findall(gcf,'type','text'),'FontSize',25)%,'fontWeight','bold')
%saveas(h, name,'BOW_NB_validate.png');
