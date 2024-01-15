function mSymbol = FonaDynPlotVRP(vrpArray, colNames, colName, fig, varargin)
%% function FonaDynPlotVRP(vrpArray, colNames, colName, fig, ...)
% <vrpArray> is an array of numbers and <colNames> is a cell array of column names, 
% both previously returned by FonaDynLoadVRP.m. 
% <colName> is the name of the column (metric) to be plotted (case sensitive).
% <fig> is the number of the current figure or subplot.
% Optional arguments: 
% 'MinCycles', integer       - set a minimum cycles-per-cell threshold
% 'Range', [foMin, foMax, Lmin, Lmax]   - specify plot range, in MIDI and dB
% 'OffsetSPL', value         - offset the dB SPL (for post-hoc calibration)
% 'ColorBar', 'on' or 'off'  - show a horizontal color bar at the top left
% 'PlotHz', 'on' or 'off'    - plot frequency axis in Hz rather than in MIDI
% 'Mesh', 'on' or 'off'      - plot an interpolated mesh rather than cells
% 'Region', <overArray>      - plot rectangles over all cells in <overArray>
%                            - <overArray is in the same format as vrpArray>
% 'SDscale', <number>		 - scale factor for plotting standard deviations,
% FonaDyn version: 3.1.1                             
  
minCycles = 1;
xmax = 96; 	% can be <=96
xmin = 30;		% can be >=30
ymax = 120; 		% can be <=120
ymin = 40;		% can be >=40
offsetSPL = 0;  % useful for SPL re-calibration
bColorBar = 0;
bMesh = 0;
SDscale = 0.0;
plotHz = 0;
cbLabel = '';
tickLabels = {}; 
tickLength = 0.12;
ticks = [];
bOverlay = 0;
bFlip = 1;
bSpecial = 0;
nCluster = 0;
  
args = nargin-4;
  
% Handle any optional arguments
for i = 1 : 2 : args
    switch varargin{i}
        case 'MinCycles'
            minCycles = varargin{i+1};
        case 'Range'
            range = varargin{i+1};
            xmin = range(1);
            xmax = range(2);
            ymin = range(3);
            ymax = range(4);
        case 'OffsetSPL'
            offsetSPL = varargin{i+1};
        case 'ColorBar'
            if strcmpi('on', varargin{i+1})
                bColorBar = 1;
            end
        case 'PlotHz'
            if strcmpi('on', varargin{i+1})
                plotHz = 1;
            end
        case 'Mesh'
            if strcmpi('on', varargin{i+1})
                bMesh = 1;
            end
        case 'SDscale'
            SDscale = varargin{i+1};
        case 'Region'
            overArray = varargin{i+1};
            bOverlay = size(overArray, 2);
%             if size(overArray, 1) > 0
%                bOverlay = 1;
%             end
        case 'Special'
            bSpecial = varargin{i+1};
        otherwise
            warning (['Unrecognized option: ' varargin{i}]);
    end
end
  
colPlot = find(ismember(colNames, colName)); 
if size(colPlot,2) < 1
    warning(['Unrecognized column name: ' colName]);
    return
end
  
[ colMaxClusterEGG, ixEGG]   = find(ismember(colNames, 'maxCluster')); 
[ colMaxClusterPhon, ixPhon] = find(ismember(colNames, 'maxCPhon')); 
  
if isempty(colMaxClusterPhon)
	colMaxClusterPhon = 0;
else
	colMaxClusterPhon = ixPhon;
end
if isempty(colMaxClusterEGG)	
	colMaxClusterEGG = 0;
else	
	colMaxClusterEGG = ixEGG;
end
  
colTotal = find(ismember(colNames, 'Total')); 
nClustersPhon = size (vrpArray, 2) - colMaxClusterPhon;
nClustersEGG  = size (vrpArray, 2) - nClustersPhon - colMaxClusterEGG - 1;
  
if colMaxClusterPhon == 0                % for dealing with earlier _VRP versions
	nClustersEGG = nClustersEGG - 1; 
end
  
switch lower (colName)
    case 'maxcluster'
	nClusters = nClustersEGG;
	colMaxCluster = colMaxClusterEGG;
  
    case 'maxcphon'
	nClusters = nClustersPhon;
	colMaxCluster = colMaxClusterPhon;
    
    otherwise 
    nClusters = 5;   % dummy default value
end	
  
if strfind(colName, 'Cluster') == 1
	nClusters = nClustersEGG;
	colMaxCluster = colMaxClusterEGG;
end
  
if strfind(colName, 'cPhon') == 1
	nClusters = nClustersPhon;
	colMaxCluster = colMaxClusterPhon;
end
  
if SDscale <= 0.0
    % Set up a colormap to give similar colors as in FonaDyn
    [colors, cmin, cmax] = FonaDynColors(colName, nClusters);
else
    cmin = 0.0;
    cmax = SDscale;
    intensity = (0.95:-0.05:0.2)';
    fullW = ones(size(intensity,1),1)*0.95;
    % Make magenta, light to dark
    colors = [fullW intensity fullW]; 
end
  
allPixels = ones(150,100)*NaN;
  
indices = find((vrpArray(:, colTotal) >= minCycles ) & (vrpArray(:,1) >= xmin));
for i=1:length(indices)
    y = vrpArray(indices(i), 2) + round(offsetSPL);
    x = vrpArray(indices(i), 1);
    z = vrpArray(indices(i), colPlot);
    if colPlot == colTotal
        allPixels(y, x) = max(z, 5);
    else
        allPixels(y, x) = z;
    end
end
ym = vrpArray(indices, 2) + round(offsetSPL);
xm = vrpArray(indices, 1);
zm = vrpArray(indices, colPlot);
        
switch lower(colName)
    case 'total'
        allPixels = log10(allPixels);
        cbLabel = 'log_{10}(cycles)';
        tickLabels = {'0', '1', '2', '3', '4'};
        ticks = [0, 1, 2, 3, 4];
        cmin = log10(cmin);
        cmax = log10(cmax);
        conLevels = ticks;
    case 'clarity'
        cbLabel = 'Clarity (latest)';
    case 'crest' % scale it to dB (1..4) -> (0..12)
        if SDscale > 0.0
            cbLabel = 'Crest Cell-SDs';
            conLevels = [1:0.1:SDscale];
        else
            cbLabel = 'Crest factor';
            conLevels = [1:1:5];
        end    
    case 'specbal' % scale it to dB (-42..-6) 
        if SDscale > 0.0
            cbLabel = 'SB Cell-SDs';
            conLevels = [1:0.1:SDscale];
        else
            tickLabels = {'-40', '-30', '-20', '-10'};
            ticks = [-40, -30, -20, -10];
            cbLabel = 'SB (dB)';
            conLevels = [-40:5:10];
            bFlip = 0; % SB values are negative, so a mesh appears on top
        end    
    case 'cpp' % scale it to dB (0..+30) 
        if SDscale > 0.0
            cbLabel = 'CPP Cell-SDs';
            conLevels = [1:0.1:SDscale];
        else
            tickLabels = {'0', '', '10', '', '20', '', '30'};
            ticks = [0:5:cmax];
            cbLabel = 'CPP (dB)';
            conLevels = [0:5:cmax];
        end    
    case 'cpps' % scale it to dB (0..+15) 
        if SDscale > 0.0
            cbLabel = 'CPPs Cell-SDs';
            conLevels = [1:0.1:SDscale];
        else
            tickLabels = {'0', '5', '10', '15'};
            ticks = [0, 5, 10, 15];
            cmax = 15;
            cbLabel = 'CPPs (dB)';
            conLevels = [0:5:cmax];
        end    
    case { 'entropy', 'sampen', 'cse' }
            tickLabels = {'0', '2', '4', '6', '8', '10'};
            ticks = [0:2:cmax];
			cbLabel = 'CSE'; %'Max SampEn';
			conLevels = [0:2:cmax];
    case { 'deggmax', 'qdelta' }
        if SDscale > 0.0
            cbLabel = '\itQ\rm_{\Delta} Cell-SDs';
            conLevels = [1:1:SDscale];
        else
            negIx = find(allPixels <= 0);
            allPixels(negIx) = 0.01;   % patch out rare negative values 
            allPixels = log10(allPixels); 
            
            negIx = find(zm <= 0);
            zm(negIx) = 0.01;   % patch out rare negative values 
            zm = log10(zm);  
            
            tickLabels = {'1', '2', '4', '7', '10', '20'};
            ticks = [0, 0.301, 0.602, 0.8451, 1, log10(20)];
            conLevels = ticks;
            cbLabel = '\itQ\rm_{\Delta}';
            cmin = log10(cmin);
            cmax = log10(cmax);
        end   
        if bSpecial > 0
            cbLabel = [cbLabel ' FD'];
        end
    case 'qcontact'
        if SDscale > 0.0
            cbLabel = 'SD \itQ\rm_{ci}';
        else
            ticks = [cmin:0.1:cmax];
            tickLabels = { '0.1', '0.2', '0.3', '0.4', '0.5', '0.6' };
            cbLabel = '\itQ\rm_{ci}';
            conLevels = [cmin:0.1:cmax];
         end
        if bSpecial > 0
            cbLabel = [cbLabel ' FD'];
        end
    case 'qspeed' % WAS USED FOR AN ARTICLE BUT IS NOT IN versions >2.2
        if SDscale > 0.0
            cbLabel = 'SD \itQ\rm_{si}';
            conLevels = [1:1:SDscale];
        else
            allPixels = log10(allPixels); 
            zm = log10(zm);  
            tickLabels = {'1', '2', '3', '4', '5'};
            ticks = [0, 0.301, 0.4771, 0.602, 0.7];
            cbLabel = '\itQ\rm_{si} Cell-Means';
            conLevels = ticks;
        end
        if bSpecial > 0
            cbLabel = [cbLabel ' FD'];
        end
    case 'icontact'
        if SDscale > 0.0
            cbLabel = 'SD \itI\rm_c';
        else
            ticks = [cmin:0.1:cmax];
            conLevels = [cmin:0.1:cmax];
            tickLabels  = {'0', '0.1', '0.2', '0.3', '0.4', '0.5', '0.6', '0.7' };
            cbLabel = '\itI\rm_{c}';
         end
        if bSpecial > 0
            cbLabel = [cbLabel ' FD'];
        end
    case 'hrfegg'
        if SDscale > 0.0
            cbLabel = 'SD HRF-EGG (dB)';
        else
            ticks = [-30:5:5];
            conLevels = [-30:5:5];
            tickLabels  = {'-30', '-25', '-20', '-15', '-10', '-5', '0', '5' };
            cbLabel = 'HRFegg (dB)';
        end
    case { 'maxcluster', 'maxcphon' }
    % Plot overlay of all clusters (max cycles on top)
        allPixels = ones(150,100)*NaN;
        for c = 1 : nClusters
            subIx = find((vrpArray(:, colMaxCluster)==c) & vrpArray(:,colTotal)>=minCycles);
            rel = vrpArray(subIx, colMaxCluster+c) ./ vrpArray(subIx, colTotal);
            for i=1:length(subIx)
                y = vrpArray(subIx(i),2) + round(offsetSPL);
                x = vrpArray(subIx(i),1);
                z = 0.999 * rel(i);
                allPixels(y, x) = c - 1 + z;
            end
            ticks(c) = c-0.5;
            tickLabels{c} = num2str(c);
        end
        tickLength = 0.0;
        cbLabel = 'Cluster #';
        nCluster = 0;
    otherwise     % one of the clusters
        t = lower (colName);
        t = t(1:5);
        switch t
            case 'clust'
                colMaxCluster = colMaxClusterEGG;
            case 'cphon'
                colMaxCluster = colMaxClusterPhon;
        end           
        nCluster = colPlot - colMaxCluster; % This assumes that the columns appear in ascending order
        if nCluster >= 1
            allPixels = ones(150,100)*NaN;
            subIx = find((vrpArray(:, colMaxCluster+nCluster) > 0) & vrpArray(:,colTotal)>=minCycles);
            percent = vrpArray(subIx, colMaxCluster+nCluster) ./ vrpArray(subIx, colTotal);
            for i=1:length(subIx)
                y = vrpArray(subIx(i),2) + round(offsetSPL);
                x = vrpArray(subIx(i),1);
                allPixels(y, x) = percent(i);
            end
            step = (cmax-cmin)/5.0;
            ticks = [cmin : step : cmax];
            tickLabels = {'0%', '20%', '40%', '60%', '80%', '100%'};
            cbLabel = 'Of total';
        end
end
  
mSymbol = cbLabel;
  
% Plot the cells astride the grid, not next to it
X = (1:100)-0.5;
Y = (1:150)-0.5;
figure(fig);
axH = gca;
grid on
  
if (bMesh == 0) || (nCluster >= 0)
    handle = pcolor(X,Y,allPixels);
    set(handle, 'EdgeColor', 'none');
    view(2);
    axis xy
elseif (bMesh == 1)
    %[ym,xm,vm] = find(allPixels*1.0); 
    FDATATEST=scatteredInterpolant(xm, ym, zm, 'natural', 'none');
    dspl=1.0;  % use 1 for contours/quivers
    df0=1.0;
    [xq,yq]=meshgrid(xmin:df0:xmax, ymin:dspl:ymax);
    vq=FDATATEST(xq,yq);
    m = mesh(xq, yq, vq); %, vq);
    m.LineStyle = 'none';
    m.FaceColor = 'interp';
    hold on
    con = contour(axH, xq, yq, vq, conLevels, 'LineColor', [0.3, 0.3, 0.3]);
%     [px,py] = gradient(vq,100,100);
%     quiver(xq, yq, px, py, 2.0);
  
if bFlip > 0
    axis ij
    view(0, -90);
else
    view(0, 90);
end
    hold off
end
  
if (bOverlay > 0)
   gridColors = [0.3 0.9];
   for b = 1 : bOverlay
		myGrid = overArray{b};
        gc = gridColors(mod(b, bOverlay)+1);
        for i = 1 : size(myGrid, 1)
            y = myGrid(i, 2) + round(offsetSPL) - 0.5;
            x = myGrid(i, 1) - 0.5;
            rectangle('Position', [x y 1 1], 'FaceColor', 'none', 'EdgeColor', [gc 0.3 0.3]);
        end
   end
end
  
colormap(axH, colors);
caxis(axH, [cmin cmax]);
xlim(axH, [xmin xmax]);
ylim(axH, [ymin ymax]);
  
if bColorBar == 1
    cb = colorbar(axH);
    cb.Location = 'eastoutside';
    cb.TickLength = tickLength; 
    cb.AxisLocation = 'out';
    if size(ticks) > 0
        cb.Ticks = ticks;
        cb.TickLabels = tickLabels;
    end
    cb.Label.String = cbLabel; 
    cb.Label.Position = [0, (cmax-cmin)*0.03+cmax];
    cb.Label.Rotation = 0;
    cb.Label.VerticalAlignment = 'bottom';
    cb.Label.HorizontalAlignment = 'left';
end
  
if plotHz == 1
    fMin = 220*2^((xmin-57)/12);
    fMax = 220*2^((xmax-57)/12); 
    ticks = [];
    tickLabels = {};
    ix = 1;
  
    for j = 1 : 1 : 20
        i = (2^(j-2))*110;
        if (i >= fMin) & (i <= fMax)
            st = 57+12*log(i/220)/log(2);
            ticks(ix) = st;
            tickLabels(ix) = {num2str(i)};
            ix = ix + 1;
        end
    end
    axH.XTick = ticks;
    axH.XTickLabel = tickLabels;
end
  
  
end
