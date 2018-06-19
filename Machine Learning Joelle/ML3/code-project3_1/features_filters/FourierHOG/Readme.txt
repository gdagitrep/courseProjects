Matlab code for computing the Fourier HOG descriptor
introduced in:     

Rotation-Invariant HOG Descriptors using Fourier Analysis in Polar and
Spherical Coordinates
K. Liu, H. Skibbe, T. Schmidt, T. Blein, K. Palme, T. Brox, O. Ronneberger
International Journal of Computer Vision


successfully tested on Matlab R2012b and Ubuntu 10.04 X86_64


requires:
  --------
    	matlab
	liblinear	(http://www.csie.ntu.edu.tw/~cjlin/liblinear/) 
	TAS package and image data (http://ai.stanford.edu/~gaheitz/Research/TAS/) 
			(http://ai.stanford.edu/~gaheitz/Research/TAS/tas.v0.tgz)
	export_fig 	(http://www.mathworks.com/matlabcentral/fileexchange/23629-exportfig)


licence:
  -------
    BSD


We offer the following functions:
    demoFourierHOG	demo the descriptor
    main		reproduce the experiment Fourier HOG_2 + linear SVM on the aerial car data
    FourierHOG		the descriptor function



If you find this code package useful please cite our paper.


Acknowledgements

Some of the code pieces are borrowed from other sources. 

    The showboxes.m, nms.m, initrand.m, clipboxes.m files have been borrowed/modified from: Discriminatively trained deformable part models (Version 4)

  
