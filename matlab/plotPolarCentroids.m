function theTable = plotPolarCentroids(cPath, fig)
% Loads a set of FonaDyn phonation type centroids 
% (with a pathname ending in _cPhon.csv) into theTable. 
% If fig is nonzero then a polar plot is made in figure(fig). 
% FonaDyn version 3.1.1
  
opts = detectImportOptions(cPath);
opts.VariableNamesLine = 1; 
opts.DataLines = [2, inf];
  
T = readtable(cPath, opts);
  
varNames = T.Properties.VariableNames;
% Skip the first and last 'varNames' 
varNames = varNames(2 : size(varNames,2)-1);
nMetrics = length(varNames);
nClusters = size(T,1)-2;
pColors = cMapPhon(nClusters, 1);
  
data = zeros(nClusters,nMetrics+1);
angles = deg2rad([0 : 360/nMetrics : 361]);
spokes = [1:nMetrics, 1];
metricNames = T{3:2+nClusters, 'Var9'};
  
if fig ~= 0
    figure(fig);
    pax = polaraxes;
    for i = 3 : size(T,1)
        rowData = T{i, vartype('numeric')};
        data(i-2,:) = [rowData(2:nMetrics+1), rowData(2)];
        polarplot(pax, angles, data(i-2, :), 'Color', pColors(i-2,:), 'LineWidth', 2 );
        hold on
    end
    pax.ThetaDir = 'clockwise';
    pax.ThetaTick = rad2deg(angles(1:nMetrics));
    pax.ThetaTickLabel = varNames;
    pax.ThetaZeroLocation = 'top';
    legend(metricNames, 'Location', 'northeastoutside');
end
  
theTable = T;
end
  
