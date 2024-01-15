function [colors, minVal, maxVal] = FonaDynColors(strMetric, nClusters)
%%function [colors, minVal, maxVal] = FonaDynColors(strMetric, nClusters)
% This function creates a color array to match the colors in FonaDyn v<:VRPMain.mVersion:>
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
	colors = <:VRPSettings.metrics[VRPSettings.iDensity].cMap(shades: 24, bLog: true):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.iDensity].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.iDensity].maxVal:> ;

    case 'clarity',
	colors = <:VRPSettings.metrics[VRPSettings.iClarity].cMap(20):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.iClarity].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.iClarity].maxVal:> ;

    case 'crest',
	colors = <:VRPSettings.metrics[VRPSettings.iCrestFactor].cMap(30):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.iCrestFactor].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.iCrestFactor].maxVal:> ;
       
    case 'specbal',
	colors = <:VRPSettings.metrics[VRPSettings.iSpecBal].cMap(30):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.iSpecBal].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.iSpecBal].maxVal:> ;
        
    case {'cpp', 'cpps' }  % CPP or CPP(smoothed)
	colors = <:VRPSettings.metrics[VRPSettings.icppSmoothed].cMap(30):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.icppSmoothed].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.icppSmoothed].maxVal:> ;
	
    case { 'entropy', 'sampen', 'cse' } % SampEn, CSE
	colors = <:VRPSettings.metrics[VRPSettings.iEntropy].cMap(24):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.iEntropy].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.iEntropy].maxVal:> ;

    case { 'deggmax', 'qdelta' }  % same as Qdelta, normalized peak dEGG
	colors = <:VRPSettings.metrics[VRPSettings.idEGGmax].cMap(30, bLog: true):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.idEGGmax].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.idEGGmax].maxVal:> ;
        
    case 'qcontact',  % Qci 
	colors = <:VRPSettings.metrics[VRPSettings.iQcontact].cMap(30):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.iQcontact].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.iQcontact].maxVal:> ;
        
%    case 'qspeed' THE SPEED QUOTIENT Qsi IS NOT IMPLEMENTED IN PUBLIC RELEASES
                
    case 'icontact',
	colors = <:VRPSettings.metrics[VRPSettings.iIcontact].cMap(30):> ;
	minVal = <:VRPSettings.metrics[VRPSettings.iIcontact].minVal:> ;
	maxVal = <:VRPSettings.metrics[VRPSettings.iIcontact].maxVal:> ;

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
