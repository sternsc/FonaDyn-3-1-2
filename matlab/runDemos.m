%% Demo script for FonaDyn Matlab routines
%  SECTION 1: Set up the selection of files.
%  This section is useful to run before any of the others.
%  It sets up variables that are specific to this whole experiment.

maindir = 'C:\Recordings\Test files';
mapFileName = 'Test_S_VRP.csv';
regionFileName = 'Test_region_VRP.csv'; % cropped to HRFegg > -1.5 dB
nHarmonics = 10;
nClusters = 5;

% Names of the columns in the array pArr as loaded from Log files
columnLabels = { 'time (s)', '\it f\rm_o (ST)', 'SPL (dB)', 'Clarity', 'Crest (dB)', 'SpecBal', 'CPP', 'cEGG #', 'cPhon #', 'SampEn', 'Icontact', 'dEGGmax', 'Qcontact', ...
                 'L_1',  'L_2',  'L_3',  'L_4',  'L_5',  'L_6',  'L_7',  'L_8',  'L_9',  'L_10', 'L_1', ...
               'phi_1','phi_2','phi_3','phi_4','phi_5','phi_6','phi_7','phi_8','phi_9','phi_10','2phi_1' };

%% SECTION 2: BASIC EXAMPLES OF PLOTTING METRICS
f = figure ('Units', 'centimeters', 'Position', [1, 1, 20, 24]);
t = tiledlayout(4, 2);
tstr = sprintf ("Using FonaDynPlotVRP.m with '%s'", mapFileName);
title(t, tstr, 'Interpreter', 'none');

mapFilePath = fullfile(maindir, mapFileName);
[names, vrpArray] = FonaDynLoadVRP(mapFilePath); 

% Vanilla plot with no options
nexttile
FonaDynPlotVRP(vrpArray, names, 'Total', f);
title ("'Total', with no options");

% Add a grid, a title and a cycle threshold
nexttile
FonaDynPlotVRP(vrpArray, names, 'Crest', f, 'MinCycles', 8);
title ("'Crest'; MinCycles=8, grid on");
grid on

% Add a grid, a title and a cycle threshold
nexttile
FonaDynPlotVRP(vrpArray, names, 'SpecBal', f, 'PlotHz', 'on');
title ("'SpecBal'; PlotHz on");
grid on

% Zoom in to a smaller range 
nexttile
FonaDynPlotVRP(vrpArray, names, 'CPP', f, 'Range', [40 70 40 100]);
title ("'CPP'; Range [40 70 40 100]");
grid on

% Add a colorbar 
nexttile
FonaDynPlotVRP(vrpArray, names, 'Entropy', f, 'ColorBar', 'on', 'PlotHz', 'on');
title ("'Entropy'; +ColorBar");
grid on

% Mesh, with contours - works best with maps already smoothed in FonaDyn
nexttile
FonaDynPlotVRP(vrpArray, names, 'dEGGmax', f, 'ColorBar', 'on', ...
     'Range', [40 70 40 100], 'PlotHz', 'on', 'Mesh', 'on');
title ("'dEGGmax'; Mesh on");
grid on

% Shift SPL for gain correction 
nexttile
FonaDynPlotVRP(vrpArray, names, 'Icontact', f, 'ColorBar', 'on', 'OffsetSPL', 12.3);
title ("'Icontact'; SPL +12.3 dB");
grid on

% Add a subregion overlay
nexttile
regionPath = fullfile(maindir, regionFileName);
[n, reg] = FonaDynLoadVRP(regionPath); 
FonaDynPlotVRP(vrpArray, names, 'Qcontact', f, 'ColorBar', 'on', ...
     'PlotHz', 'on', 'Region', { reg }, 'Range', [40 70 40 100]);
title ("'Qcontact'; Region on");
grid on

% printpathname = fullfile(maindir, 'runDemos-Plot map layers.pdf'); 
% f.PaperOrientation = 'portrait';
% print('-painters','-dpdf', printpathname);

%% EXAMPLES OF PLOTTING EGG CLUSTERS
f = figure ('Units', 'centimeters', 'Position', [1, 1, 20, 24]);
t = tiledlayout(4, 2);

eggCentroidFileName = 'test_cEGG.csv';
eggCentroidPath = fullfile(maindir, eggCentroidFileName);
[egg, Qci, Qsi, maxDegg, Ic, ampl] = synthEGGfromFile(eggCentroidPath, 100, 3);

tstr = sprintf("Plotting EGG clusters from '%s' and '%s'", mapFileName, eggCentroidFileName);
title(t, tstr, 'Interpreter', 'none');

mapFilePath = fullfile(maindir, mapFileName);
[names, vrpArray] = FonaDynLoadVRP(mapFilePath); 

% Plot the map of all EGG clusters
nexttile
FonaDynPlotVRP(vrpArray, names, 'maxCluster', f, 'MinCycles', 5, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'maxCluster' shows all 5 clusters ");
grid on

% Plot only EGG cluster 1
nexttile
FonaDynPlotVRP(vrpArray, names, 'Cluster 1', f, 'MinCycles', 5);
title ("'Cluster 1'");
grid on

% Plot only EGG cluster 2
nexttile
FonaDynPlotVRP(vrpArray, names, 'Cluster 2', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 2'");
grid on

% Plot only EGG cluster 3
nexttile
FonaDynPlotVRP(vrpArray, names, 'Cluster 3', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 3'");
grid on

% Plot only EGG cluster 4
nexttile(5)
FonaDynPlotVRP(vrpArray, names, 'Cluster 4', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 4'");
grid on

% Plot only EGG cluster 5
nexttile(7)
FonaDynPlotVRP(vrpArray, names, 'Cluster 5', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 5'");
grid on

nexttile(6, [2 1])
ax = gca; 
ax.YDir = 'reverse';
plotEGG(egg, eggCentroidFileName, f); 
grid on

% printpathname = fullfile(maindir, 'runDemos-Plot EGGs.pdf'); 
% f.PaperOrientation = 'portrait';
% print('-painters','-dpdf', printpathname);

%% EXAMPLES OF PLOTTING PHONATION TYPE CLUSTERS
f = figure ('Units', 'centimeters', 'Position', [1, 1, 20, 24]);
t = tiledlayout('flow');

phonCentroidFileName = "test_cPhon.csv";
phonCentroidPath = fullfile(maindir, phonCentroidFileName);
tstr = sprintf("Plotting Phon clusters from '%s' and '%s'", mapFileName, phonCentroidFileName);
title(t, tstr, 'Interpreter', 'none');

mapFilePath = fullfile(maindir, mapFileName);
[names, vrpArray] = FonaDynLoadVRP(mapFilePath); 

% All clusters
nexttile
FonaDynPlotVRP(vrpArray, names, 'maxCPhon', f, 'MinCycles', 5, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'maxCPhon' shows all phon clusters ");
grid on

% Plot only phon cluster 1
nexttile
FonaDynPlotVRP(vrpArray, names, 'cPhon 1', f, 'MinCycles', 5, 'ColorBar', 'on');
title ("'Cluster 1'");
grid on

% % Plot only phon cluster 2
nexttile
FonaDynPlotVRP(vrpArray, names, 'cPhon 2', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 2'");
grid on

% Plot only phon cluster 3
nexttile
FonaDynPlotVRP(vrpArray, names, 'cPhon 3', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 3'");
grid on

% % Plot only phon cluster 4
nexttile
FonaDynPlotVRP(vrpArray, names, 'cPhon 4', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 4'");
grid on

% Plot only phon cluster 5
nexttile
FonaDynPlotVRP(vrpArray, names, 'cPhon 5', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 5'");
grid on

% Plot only phon cluster 6
nexttile
FonaDynPlotVRP(vrpArray, names, 'cPhon 6', f, 'PlotHz', 'on', 'ColorBar', 'on');
title ("'Cluster 6'");
grid on

% printpathname = fullfile(maindir, 'runDemos-Plot phonation types.pdf'); 
% f.PaperOrientation = 'portrait';
% print('-painters','-dpdf', printpathname);

fig2 = figure;
theTable = plotPolarCentroids(phonCentroidPath, fig2); 

% printpathname = fullfile(maindir, 'runDemos-Phonation types radar plot.pdf'); 
% fig2.PaperOrientation = 'portrait';
% print('-painters','-dpdf', printpathname);


%% EXAMPLE OF PLOTTING A LOG FILE

logFileName = 'Test_Log.aiff';
tstr = sprintf("Log file %s", logFileName);
f = figure ('Name',tstr, 'NumberTitle','off', 'Units', 'centimeters', 'Position', [1, 1, 20, 24]);
title(tstr, 'Interpreter', 'none');

logFilePath = fullfile(maindir, logFileName);
tracks = [1 2 4 5 7 8 12];

FonaDynPlotLogfile(logFilePath, [0.1 26.0], [1 2 5 7 8 11 12 10]);

% printpathname = fullfile(maindir, 'runDemos-Plot log file.pdf'); 
% f.PaperOrientation = 'portrait';
% print('-painters','-dpdf', printpathname);


%% A PEEK AT THE FIRST 4 FOURIER DESCRIPTORS OF THE EGG CYCLES
% This code assumes that there are 10 harmonics in the Log file

logFileName = 'Test_Log.aiff';
tstr = sprintf("Fourier Descriptors of the EGG - Log file %s", logFileName);
f = figure ('Name',tstr, 'NumberTitle','off', 'Units', 'centimeters', 'Position', [1, 1, 20, 24]);
startFrame = 1;
stopFrame = 1000;

logFilePath = fullfile(maindir, logFileName);
logData = FonaDynLoadLogfile(logFilePath, 0, 0);
tiledlayout(2,1);

nexttile
for k = 14:17
   plot(logData(startFrame:stopFrame,1), logData(startFrame:stopFrame,k).*10, '.' );
   hold on
end
title(tstr, 'Interpreter', 'none');
xlabel ('time (s)');
ylabel('Harmonic Level (dB)');
legend(columnLabels{14:17}); 

nexttile
for k = 25:28
   plot(logData(startFrame:stopFrame,1), logData(startFrame:stopFrame,k)./10, '.' );
   hold on
end
title(tstr, 'Interpreter', 'none');
xlabel ('time (s)');
ylabel('Harmonic phases (rads)');
legend(columnLabels{25:28}); 


           