if isempty(currentFigures), currentFigures = []; end;
close(setdiff(findall(0, 'type', 'figure'), currentFigures))
clear mex
delete *.mexmaci64
[~,~,~] = rmdir('/Users/dhsingh/OneDrive/ML3/coderdemo_edge_detection/codegen','s');
clear /Users/dhsingh/OneDrive/ML3/coderdemo_edge_detection/sobel.m
delete /Users/dhsingh/OneDrive/ML3/coderdemo_edge_detection/sobel.m
delete /Users/dhsingh/OneDrive/ML3/coderdemo_edge_detection/hello.jpg
clear
load old_workspace
delete old_workspace.mat
delete /Users/dhsingh/OneDrive/ML3/coderdemo_edge_detection/cleanup.m
cd /Users/dhsingh/OneDrive/ML3
rmdir('/Users/dhsingh/OneDrive/ML3/coderdemo_edge_detection','s');
