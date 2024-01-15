function [colors, minVal, maxVal] = FonaDynColors(strMetric, nClusters)
%%function [colors, minVal, maxVal] = FonaDynColors(strMetric, nClusters)
% This function creates a color array to match the colors in FonaDyn v3.1.1
%  strMetric can be any of the names of columns > 2 in the _VRP.csv file
%  as saved from FonaDyn (not case sensitive). Currently these can be:
%  'Total' | 'Clarity' | 'Crest' | 'SpecBal' | 'CPP' | 'Entropy' | 'dEGGmax' | 'Qcontact' |
%  'Icontact' |'maxCluster' | 'Cluster <N>' | 'maxCphon' | 'cPhon <N>'       
%  where <N> is a cluster number. 'CPP' might be 'CPPs' depending on the compilation. 
%
%  FonaDynColors returns an RGB color array for the Matlab "colormap" function,
%  as well as the minimum and maximum values for the Matlab "caxis" function.
%  The integer argument nClusters is used only with the last four strMetric options,
%  and is required but ignored in all other cases.
  
%  These color map arrays have been computed by FonaDyn's VRPMetric base class, 
%  calling on each metric's palette. 
  
switch lower(strMetric)
    case 'total',
	colors = [	[ 0.95  0.95  0.95 ];
	[ 0.922  0.922  0.922 ];
	[ 0.894  0.894  0.894 ];
	[ 0.866  0.866  0.866 ];
	[ 0.838  0.838  0.838 ];
	[ 0.81  0.81  0.81 ];
	[ 0.782  0.782  0.782 ];
	[ 0.754  0.754  0.754 ];
	[ 0.726  0.726  0.726 ];
	[ 0.698  0.698  0.698 ];
	[ 0.67  0.67  0.67 ];
	[ 0.642  0.642  0.642 ];
	[ 0.614  0.614  0.614 ];
	[ 0.586  0.586  0.586 ];
	[ 0.558  0.558  0.558 ];
	[ 0.53  0.53  0.53 ];
	[ 0.502  0.502  0.502 ];
	[ 0.474  0.474  0.474 ];
	[ 0.446  0.446  0.446 ];
	[ 0.418  0.418  0.418 ];
	[ 0.39  0.39  0.39 ];
	[ 0.362  0.362  0.362 ];
	[ 0.334  0.334  0.334 ];
	[ 0.306  0.306  0.306 ];
] ;
	minVal = 1 ;
	maxVal = 10000 ;
  
    case 'clarity',
	colors = [	[ 0.5  0.5  0.5 ];
	[ 0.0  0.525  0.0 ];
	[ 0.0  0.55  0.0 ];
	[ 0.0  0.575  0.0 ];
	[ 0.0  0.6  0.0 ];
	[ 0.0  0.625  0.0 ];
	[ 0.0  0.65  0.0 ];
	[ 0.0  0.675  0.0 ];
	[ 0.0  0.7  0.0 ];
	[ 0.0  0.725  0.0 ];
	[ 0.0  0.75  0.0 ];
	[ 0.0  0.775  0.0 ];
	[ 0.0  0.8  0.0 ];
	[ 0.0  0.825  0.0 ];
	[ 0.0  0.85  0.0 ];
	[ 0.0  0.875  0.0 ];
	[ 0.0  0.9  0.0 ];
	[ 0.0  0.925  0.0 ];
	[ 0.0  0.95  0.0 ];
	[ 0.0  0.975  0.0 ];
] ;
	minVal = 0.96 ;
	maxVal = 1.0 ;
  
    case 'crest',
	colors = [	[ 0.002  1.0  0.0 ];
	[ 0.069  1.0  0.0 ];
	[ 0.135  1.0  0.0 ];
	[ 0.202  1.0  0.0 ];
	[ 0.268  1.0  0.0 ];
	[ 0.335  1.0  0.0 ];
	[ 0.402  1.0  0.0 ];
	[ 0.468  1.0  0.0 ];
	[ 0.535  1.0  0.0 ];
	[ 0.601  1.0  0.0 ];
	[ 0.668  1.0  0.0 ];
	[ 0.735  1.0  0.0 ];
	[ 0.801  1.0  0.0 ];
	[ 0.868  1.0  0.0 ];
	[ 0.934  1.0  0.0 ];
	[ 1.0  0.999  0.0 ];
	[ 1.0  0.932  0.0 ];
	[ 1.0  0.866  0.0 ];
	[ 1.0  0.799  0.0 ];
	[ 1.0  0.733  0.0 ];
	[ 1.0  0.666  0.0 ];
	[ 1.0  0.599  0.0 ];
	[ 1.0  0.533  0.0 ];
	[ 1.0  0.466  0.0 ];
	[ 1.0  0.4  0.0 ];
	[ 1.0  0.333  0.0 ];
	[ 1.0  0.266  0.0 ];
	[ 1.0  0.2  0.0 ];
	[ 1.0  0.133  0.0 ];
	[ 1.0  0.067  0.0 ];
] ;
	minVal = 1.414 ;
	maxVal = 4.0 ;
       
    case 'specbal',
	colors = [	[ 0.002  1.0  0.0 ];
	[ 0.069  1.0  0.0 ];
	[ 0.135  1.0  0.0 ];
	[ 0.202  1.0  0.0 ];
	[ 0.268  1.0  0.0 ];
	[ 0.335  1.0  0.0 ];
	[ 0.402  1.0  0.0 ];
	[ 0.468  1.0  0.0 ];
	[ 0.535  1.0  0.0 ];
	[ 0.601  1.0  0.0 ];
	[ 0.668  1.0  0.0 ];
	[ 0.735  1.0  0.0 ];
	[ 0.801  1.0  0.0 ];
	[ 0.868  1.0  0.0 ];
	[ 0.934  1.0  0.0 ];
	[ 1.0  0.999  0.0 ];
	[ 1.0  0.932  0.0 ];
	[ 1.0  0.866  0.0 ];
	[ 1.0  0.799  0.0 ];
	[ 1.0  0.733  0.0 ];
	[ 1.0  0.666  0.0 ];
	[ 1.0  0.599  0.0 ];
	[ 1.0  0.533  0.0 ];
	[ 1.0  0.466  0.0 ];
	[ 1.0  0.4  0.0 ];
	[ 1.0  0.333  0.0 ];
	[ 1.0  0.266  0.0 ];
	[ 1.0  0.2  0.0 ];
	[ 1.0  0.133  0.0 ];
	[ 1.0  0.067  0.0 ];
] ;
	minVal = -42.0 ;
	maxVal = 0.0 ;
        
    case {'cpp', 'cpps' }  % CPP or CPP(smoothed)
	colors = [	[ 0.0  0.004  1.0 ];
	[ 0.0  0.137  1.0 ];
	[ 0.0  0.27  1.0 ];
	[ 0.0  0.404  1.0 ];
	[ 0.0  0.537  1.0 ];
	[ 0.0  0.67  1.0 ];
	[ 0.0  0.803  1.0 ];
	[ 0.0  0.936  1.0 ];
	[ 0.0  1.0  0.93 ];
	[ 0.0  1.0  0.797 ];
	[ 0.0  1.0  0.664 ];
	[ 0.0  1.0  0.531 ];
	[ 0.0  1.0  0.398 ];
	[ 0.0  1.0  0.264 ];
	[ 0.0  1.0  0.131 ];
	[ 0.002  1.0  0.0 ];
	[ 0.135  1.0  0.0 ];
	[ 0.268  1.0  0.0 ];
	[ 0.402  1.0  0.0 ];
	[ 0.535  1.0  0.0 ];
	[ 0.668  1.0  0.0 ];
	[ 0.801  1.0  0.0 ];
	[ 0.934  1.0  0.0 ];
	[ 1.0  0.932  0.0 ];
	[ 1.0  0.799  0.0 ];
	[ 1.0  0.666  0.0 ];
	[ 1.0  0.533  0.0 ];
	[ 1.0  0.4  0.0 ];
	[ 1.0  0.266  0.0 ];
	[ 1.0  0.133  0.0 ];
] ;
	minVal = 0.0 ;
	maxVal = 30.0 ;
	
    case { 'entropy', 'sampen', 'cse' } % SampEn, CSE
	colors = [	[ 0.902  1.0  0.9 ];
	[ 0.952  0.887  0.887 ];
	[ 0.94  0.857  0.857 ];
	[ 0.927  0.828  0.828 ];
	[ 0.915  0.798  0.798 ];
	[ 0.902  0.769  0.769 ];
	[ 0.89  0.739  0.739 ];
	[ 0.877  0.709  0.709 ];
	[ 0.865  0.68  0.68 ];
	[ 0.852  0.65  0.65 ];
	[ 0.84  0.621  0.621 ];
	[ 0.827  0.591  0.591 ];
	[ 0.815  0.561  0.561 ];
	[ 0.802  0.532  0.532 ];
	[ 0.79  0.502  0.502 ];
	[ 0.777  0.473  0.473 ];
	[ 0.765  0.443  0.443 ];
	[ 0.752  0.414  0.414 ];
	[ 0.74  0.384  0.384 ];
	[ 0.727  0.354  0.354 ];
	[ 0.715  0.325  0.325 ];
	[ 0.702  0.295  0.295 ];
	[ 0.69  0.266  0.266 ];
	[ 0.677  0.236  0.236 ];
] ;
	minVal = 0.0 ;
	maxVal = 10.0 ;
  
    case { 'deggmax', 'qdelta' }  % same as Qdelta, normalized peak dEGG
	colors = [	[ 0.002  1.0  0.0 ];
	[ 0.066  1.0  0.0 ];
	[ 0.131  1.0  0.0 ];
	[ 0.195  1.0  0.0 ];
	[ 0.26  1.0  0.0 ];
	[ 0.324  1.0  0.0 ];
	[ 0.389  1.0  0.0 ];
	[ 0.453  1.0  0.0 ];
	[ 0.518  1.0  0.0 ];
	[ 0.582  1.0  0.0 ];
	[ 0.647  1.0  0.0 ];
	[ 0.711  1.0  0.0 ];
	[ 0.775  1.0  0.0 ];
	[ 0.84  1.0  0.0 ];
	[ 0.904  1.0  0.0 ];
	[ 0.969  1.0  0.0 ];
	[ 1.0  0.967  0.0 ];
	[ 1.0  0.902  0.0 ];
	[ 1.0  0.838  0.0 ];
	[ 1.0  0.773  0.0 ];
	[ 1.0  0.709  0.0 ];
	[ 1.0  0.645  0.0 ];
	[ 1.0  0.58  0.0 ];
	[ 1.0  0.516  0.0 ];
	[ 1.0  0.451  0.0 ];
	[ 1.0  0.387  0.0 ];
	[ 1.0  0.322  0.0 ];
	[ 1.0  0.258  0.0 ];
	[ 1.0  0.193  0.0 ];
	[ 1.0  0.129  0.0 ];
] ;
	minVal = 1.0 ;
	maxVal = 20.01 ;
        
    case 'qcontact',  % Qci 
	colors = [	[ 0.98  0.0  1.0 ];
	[ 0.814  0.0  1.0 ];
	[ 0.648  0.0  1.0 ];
	[ 0.482  0.0  1.0 ];
	[ 0.316  0.0  1.0 ];
	[ 0.15  0.0  1.0 ];
	[ 0.0  0.016  1.0 ];
	[ 0.0  0.182  1.0 ];
	[ 0.0  0.348  1.0 ];
	[ 0.0  0.514  1.0 ];
	[ 0.0  0.68  1.0 ];
	[ 0.0  0.846  1.0 ];
	[ 0.0  1.0  0.988 ];
	[ 0.0  1.0  0.822 ];
	[ 0.0  1.0  0.656 ];
	[ 0.0  1.0  0.49 ];
	[ 0.0  1.0  0.324 ];
	[ 0.0  1.0  0.158 ];
	[ 0.008  1.0  0.0 ];
	[ 0.174  1.0  0.0 ];
	[ 0.34  1.0  0.0 ];
	[ 0.506  1.0  0.0 ];
	[ 0.672  1.0  0.0 ];
	[ 0.838  1.0  0.0 ];
	[ 1.0  0.996  0.0 ];
	[ 1.0  0.83  0.0 ];
	[ 1.0  0.664  0.0 ];
	[ 1.0  0.498  0.0 ];
	[ 1.0  0.332  0.0 ];
	[ 1.0  0.166  0.0 ];
] ;
	minVal = 0.1 ;
	maxVal = 0.6 ;
        
%    case 'qspeed' THE SPEED QUOTIENT Qsi IS NOT IMPLEMENTED IN PUBLIC RELEASES
                
    case 'icontact',
	colors = [	[ 0.02  0.0  1.0 ];
	[ 0.0  0.114  1.0 ];
	[ 0.0  0.248  1.0 ];
	[ 0.0  0.382  1.0 ];
	[ 0.0  0.516  1.0 ];
	[ 0.0  0.65  1.0 ];
	[ 0.0  0.784  1.0 ];
	[ 0.0  0.918  1.0 ];
	[ 0.0  1.0  0.948 ];
	[ 0.0  1.0  0.814 ];
	[ 0.0  1.0  0.68 ];
	[ 0.0  1.0  0.546 ];
	[ 0.0  1.0  0.412 ];
	[ 0.0  1.0  0.278 ];
	[ 0.0  1.0  0.144 ];
	[ 0.0  1.0  0.01 ];
	[ 0.124  1.0  0.0 ];
	[ 0.258  1.0  0.0 ];
	[ 0.392  1.0  0.0 ];
	[ 0.526  1.0  0.0 ];
	[ 0.66  1.0  0.0 ];
	[ 0.794  1.0  0.0 ];
	[ 0.928  1.0  0.0 ];
	[ 1.0  0.938  0.0 ];
	[ 1.0  0.804  0.0 ];
	[ 1.0  0.67  0.0 ];
	[ 1.0  0.536  0.0 ];
	[ 1.0  0.402  0.0 ];
	[ 1.0  0.268  0.0 ];
	[ 1.0  0.134  0.0 ];
] ;
	minVal = 0.0 ;
	maxVal = 0.7 ;
  
	% For the cluster colors, we would have to pre-build colormaps 
	% for all possible cluster counts; that would be too cumbersome. 
	% Instead, the functions cMapEGG and cMapPhon have to be updated manually.
    % Build a stack of (nClusters x 10) graded cluster colors:
    case { 'maxcluster', 'maxcphon' }
        colors=zeros(10*nClusters, 3);
		switch lower(strMetric) 
            case 'maxcluster'
			cmap = cMapEGG(nClusters, 0.7);
            for c=1:nClusters
                for i=1:10
                    hmap = rgb2hsv(cmap(c,:));
                    hmap(2) = hmap(2)*(i/10.0);
                    colors((c-1)*10+i,:) = hsv2rgb(hmap);
                end
            end      
            
            case 'maxcphon'
			cmap = cMapPhon(nClusters, 1);
            for c=1:nClusters
                for i=1:10
                    hmap = rgb2hsv(cmap(c,:));
                     hmap(2) = hmap(2)*((5+i/2)/10);
                     hmap(3) = hmap(3)+((1-hmap(3))*(10-i)/10);
                    colors((c-1)*10+i,:) = hsv2rgb(hmap);
                end
            end       
		end
        minVal = 0;
        maxVal = nClusters;
        
    otherwise
        nCluster = sscanf(lower(strMetric), 'cluster %u');
        if ~isempty(nCluster)
            cmap = cMapEGG(nClusters, 0.7);
            hsv = rgb2hsv(cmap(nCluster, :));
            sats = (0.1 : 0.031 : 0.7)';
            hsvs = [ones(size(sats))*hsv(1) sats ones(size(sats))];
            colors = hsv2rgb(hsvs);
        else
            nCluster = sscanf(lower(strMetric), 'cphon %u');
            if ~isempty(nCluster)
                cmap = cMapPhon(nClusters, 1);
                hsv = rgb2hsv(cmap(nCluster, :));
                sats = zeros(20, 1);
                vals = sats;
                hues = sats;
                for i = 1 : 20
                     sats(i) = hsv(2)*i/20; %((10+0.5*i)/20);
                     vals(i) = hsv(3) + ((1-hsv(3))*(20-i)/20);
                     hues(i) = hsv(1);
                end
                hsvs = [hues'; sats'; vals']';
                colors = hsv2rgb(hsvs);
            end
		end
		if isnumeric(nCluster)
            minVal = 0;
            maxVal = 1;
		end 
	end
end
