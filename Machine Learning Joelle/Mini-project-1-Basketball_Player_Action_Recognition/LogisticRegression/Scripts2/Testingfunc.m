%input 
X=test100(:,1:1620);

%  Setup the data matrix appropriately, and add ones for the intercept term
[n, m] = size(X);
% Augmenting ones in the 1st column
X = [ones(n, 1), X];

%output labels
y1=test100(:,1621);

y=categorical(y1);

B = mnrfit(X,ans);

% prediction on training data
n = size(X, 1); % Number of training examples
predict = zeros(n, 1);
for i=1:n
    if sigmoid((X(i,:))*B) >= 0.5
        predict(i) = 1;
    elseif sigmoid((X(i,:))*B) < 0.5
        predict(i) = 0;
    end
end
(sum(predict == y)/size(X,1))*100% percentage of prediction