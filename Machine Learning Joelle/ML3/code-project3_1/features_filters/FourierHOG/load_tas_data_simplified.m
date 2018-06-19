function [ins, imgset] = load_tas_data_simplified(class_name, data_dir, params, ...
    image_indices)

% Directories
imagedir   = [data_dir, '/Images'];
cand_dir   = [data_dir, '/Candidates'];
region_dir = [data_dir, '/Regions'];
gt_dir     = [data_dir, '/Groundtruth'];

% Constants
scoretype = 'margin';

% Files
D = dir([imagedir, '/*jpg']);
if(~exist('image_indices')), image_indices=1:length(D); end;

M = length(image_indices);
for m = 1:M
    
    id = image_indices(m);
    
    fprintf('Loading ', m);
    % Image
    image_filename = sprintf('%s/%s', imagedir, D(id).name);
    [d ins.name{m} e] = fileparts(image_filename);
    ins.image_filename{m} = image_filename;
    fprintf('\tIMAGE: %s\n', image_filename);
    I = imread(image_filename);
    ins.image_size{m} = size(I);
    
    % GROUNDTRUTH
    gt_filename = sprintf('%s/%s/%s.gt', gt_dir, class_name, ins.name{m});
    ins.gt{m} = load(gt_filename);
end
return
end

