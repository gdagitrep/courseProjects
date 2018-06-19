function y = normalize(x)
    minn=min(x);
    manx=max(x);
    y= 100*(x-minn)/(manx-minn);
end