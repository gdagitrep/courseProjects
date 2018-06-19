% Regularized Logistic Regression 
clear;
% Input data matrix
% rows are number of examples
% columns are features
test100 = csvread('test_100.csv');

X=test100(:,1:1620);

%  Setup the data matrix appropriately, and add ones for the intercept term
[n, m] = size(X);
% Augmenting ones in the 1st column
X = [ones(n, 1), X];
%output labels
y=test100(:,1621);

% Initialize fitting parameters
initial_weight = zeros(size(X, 2), 1);

% Set regularization parameter lambda to 1
lambda = 10;

% hyperparameter alpha
alpha = 0.01;

% Compute and display initial cost and gradient for regularized logistic
% regression
[weight] = graddescent(initial_weight, X, y, lambda, alpha);

result=X*weight;
%%
% % % % prediction on training data
% % % n = size(X, 1); % Number of training examples
% % % predict = zeros(n, 1);
% % % for i=1:n
% % %     if sigmoid((X(i,:))*weight) >= 0.5
% % %         predict(i) = 1;
% % %     elseif sigmoid((X(i,:))*weight) < 0.5
% % %         predict(i) = 0;
% % %     end
% % % end
% % % (sum(predict == y)/size(X,1))*100% percentage of prediction

% plot(numiter,err_history);% plot how error history changed across number of iterations
save('variables');