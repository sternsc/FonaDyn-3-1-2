function FonaDynPlotLogfile(pathname, limits, tracks)
%% Makes a nice plot of selected tracks and selected frames (cycles) 
%% from a FonaDyn _Log.aiff file. 
%!! To create a working version of this template m-file that matches your FonaDyn settings:
%!! When you have just made the _Log.aiff file, with FonaDyn still running,
%!! evaluate FonaDyn.makeMatlabCode('FonaDynPlotLogfile', targetDir: somewhere)
%!! where targetDir defaults to <userExtensionDir>\matlab.
%!!
%!! Lines in this template that begin with %!! will be skipped.
%!! Regular Matlab comments will not be skipped. 
%!! Text bracketed in <: :> will be sent to the SuperCollider interpreter,
%!! and the result will be substituted in the m-code text.
%!!
% Plotting of _Log.aiff files from FonaDyn v<:VRPMain.mVersion:>
% Customize to your own needs
% <limits> = [start stop]   times (s) in the Log.aiff file
% <tracks> = [1 2 5 8 9]    numbers of the tracks to plot (1..<:VRPControllerIO.logBaseTracks:>), in any order
% Create a figure before calling this function.
%
% Track no - contents - unit - domain
% 
%(0  time in seconds - not accessible through the arg <tracks>)
% 1  fo - semitones - MIDI - <:VRPDataVRP.nMinMIDI:> .. <:VRPDataVRP.nMaxMIDI:> 
% 2  Signal level - dB re full scale - < 0. If SPL was calibrated correctly, 
%    add <:VRPDataVRP.nMaxSPL:> (dB) to this track and change the plotted axis.
% 3  Clarity - fraction - <threshold>..1.0 (default threshold = 0.96)
% 4  Crest factor - number - 0 .. 5
% 5  Spectrum Balance -40..0 dB
% 6  CPP smoothed 0..30 dB
% 7  EGG cluster # - integer - 0 to <nEGGclusters-1>
% 8  PhonType cluster # integer - 0 to <nPhonClusters-1>
% 9  SampEn (CSE) - number - 0 .. 10 
% 10  Icontact = Qcontact * log10(dEGGmax)
% 11  dEGGmax - peak dEGG normalized to 1 for a sine wave - >= 1
% 12  Qcontact - area of normalized EGG pulse - 0...1

[buffer, samplerate] = audioread(pathname, 'native');

% First find the indeces for start and stop
i=1;
while buffer(i, 1) < limits(1)
    i = i+1;
end    
startframe = i; 

while buffer(i, 1) < limits(2)
    i = i+1;
end    
stopframe  = i;

clear buffer
[data, samplerate] = audioread(pathname, [startframe stopframe], 'native');
[frames, channels] = size(data);
nTracks = size(tracks, 2);			% number of tracks to be plotted

nBaseTracks = <:VRPControllerIO.logBaseTracks:>;
nBaseTrackNames = <:VRPSDIO.getAllLogMetricNames:>;

nharm = (channels - nBaseTracks)/2;
firstharm = nBaseTracks;

% This assumes that the last (max) cluster is represented in the log file excerpt
eggClusterTrack = 0;
for i = 1 : nBaseTracks-1
    if nBaseTrackNames(i) == "cEGG #"
		eggClusterTrack = i;
		nEGGclusters = max(data(:,eggClusterTrack+1)) + 1;   
	end
end
	
% This assumes that the last (max) cluster is represented in the log file excerpt
phonClusterTrack = 0;
for i = 1 : nBaseTracks-1
    if nBaseTrackNames(i) == "cPhon #"
		phonClusterTrack = i;
		nPhonClusters = max(data(:,phonClusterTrack+1)) + 1;   
	end
end
	
minClarity =  <:VRPSettings.metrics[VRPSettings.iClarity].minVal:>;
crestLimits = [ <:VRPSettings.metrics[VRPSettings.iCrestFactor].minVal:>, <:VRPSettings.metrics[VRPSettings.iCrestFactor].maxVal:> ]; 
sbLimits  =  [ <:VRPSettings.metrics[VRPSettings.iSpecBal].minVal:>, <:VRPSettings.metrics[VRPSettings.iSpecBal].maxVal:> ]; 
cppLimits  =  [ <:VRPSettings.metrics[VRPSettings.icppSmoothed].minVal:>, <:VRPSettings.metrics[VRPSettings.icppSmoothed].maxVal:> ]; 
cseLimits  =  [ <:VRPSettings.metrics[VRPSettings.iEntropy].minVal:>, <:VRPSettings.metrics[VRPSettings.iEntropy].maxVal:> ]; 
qDeltaLimits  =  [ <:VRPSettings.metrics[VRPSettings.idEGGmax].minVal:>, <:VRPSettings.metrics[VRPSettings.idEGGmax].maxVal:> ]; 

% <:VRPSettings.metrics[VRPSettings.iClustersEGG].setClusters(0):> set cluster #
cEGGLimits = [ <:VRPSettings.metrics[VRPSettings.iClustersEGG].minVal:>-0.5, <:VRPSettings.metrics[VRPSettings.iClustersEGG].maxVal:>+1.5 ];

% <:VRPSettings.metrics[VRPSettings.iClustersPhon].setClusters(0):> set cluster #
cPhonLimits = [ <:VRPSettings.metrics[VRPSettings.iClustersEGG].minVal:>-0.5, <:VRPSettings.metrics[VRPSettings.iClustersEGG].maxVal:>+1.5 ];

yLimits = [ <:VRPDataVRP.nMinMIDI:> <:VRPDataVRP.nMaxMIDI:> ; ... 
            -80 -20; [minClarity, 1.01]; crestLimits; sbLimits; cppLimits; ...
			cEGGLimits; cPhonLimits; cseLimits; -0.1 1; qDeltaLimits; 0 1]; 

axisLabels = cellstr(nBaseTrackNames);

for j = 1:nTracks
    i = tracks(j);
    ax(j) = subplot(nTracks,1,j);
    if (i==eggClusterTrack) || (i==phonClusterTrack) % special treatment for the cluster# track
        hold on
		if (i==eggClusterTrack) 
			nClusters = nEGGclusters;
			fdmap = cMapEGG(nClusters, 1); 
		end;
		if (i==phonClusterTrack) 
			nClusters = nEGGclusters;
			fdmap = cMapPhon(nClusters, 1); 
		end;	
        for c = 1 : nClusters
           clr = fdmap(c,:); 
           jx = find(data(:,i+1)==(c-1));
           plot (data(jx,1), data(jx,i+1)+1,'.', 'MarkerSize', 8, 'Color', clr);
           axis ij
        end
		colormap(ax(j), fdmap); 
        hold off
    else
        plot (data(:,1),data(:,i+1),'.', 'MarkerSize', 4);
    end
	ylabel(ax(j), axisLabels{i}, 'FontSize', 8);
    ylim(ax(j), yLimits(i,:));
    grid on
    grid minor
end;

xlabel ('time (s)');

linkaxes(ax, 'x');

%% EXAMPLE: Plot the levels and phases for the first 4 harmonics in a separate figure
% figure(2);

% % nharm == # of harmonics + 1
% % The last "harmonic" holds the power level of residual higher harmonics, 
% % and a copy of the phase of the fundamental. 

% for i = firstharm : firstharm+3
    % subplot(2,1,1)
    % plot (data(:,1), data(:,i).*10, '.', 'MarkerSize', 2);
    % %title('First 4 levels');
    % ylabel('Level (dB down)');
    % grid on
    % grid minor
    % hold on
    % subplot(2,1,2)
    % %plot (unwrap(data(:,1), data(:,i+nharm)));
    % plot (data(:,1), data(:,i+nharm), '.', 'MarkerSize', 2);
    % %title('First 4 phases');
    % xlabel('time (s)');
    % ylabel('phase (rad)');
    % ylim([-pi, pi]);
    % grid on
    % grid minor
    % hold on
% end;

% subplot(2,1,1)
% legend('FD1','FD2','FD3','FD4');
% end
