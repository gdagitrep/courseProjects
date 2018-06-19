function [weight] = graddescent(weight, X, y, lambda, alpha)
% this script calculates gradient descent 
% also keeps a history of error for all iterations

n = length(y); % length of output labels
%grad = zeros(size(weight)); % initialising a vector to calculate gradient descent
% err_history = []; % initialsing an empty matrix to keep track of error history
i=0;

while true

    %Logistic Function
    logisticfunc = 1/(1+exp(-(X*weight))); % dimension of 1 X 958
    logisticfunc = logisticfunc'; % need to transpose for subtraction from y

    % gradient descent
    gradient = (1/n)*(X'*(logisticfunc - y)); 

    gradient(2:end) = gradient(2:end) + ((lambda/n) * weight(2:end));% regularized gradient descent
   
    gradient = alpha * gradient;
    
    new_weight = weight - gradient;% updating the weights
    
    if  i>=1000000
        break;
    end

    weight = new_weight;
    
    i=i+1;
%     [err] = costfuncreg(weight, X, y, lambda);% determine error for new weights
%     
%     err_history(i) = err;% save error everytime
end
display(i)
end