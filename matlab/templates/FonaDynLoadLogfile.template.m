function [logPlus] = FonaDynLoadLogfile(logFile, xFourier, delta)
%% This fn loads a Log file and translates time-domain metrics (but not physio).
%  xFourier = 1 means to replace the "raw" EGG metrics with values from Fourier resynthesis
%  xFourier = 0 means to use the raw EGG metrics
%  delta = 1 means convert levels and phases to delta-levels and delta-phases re fundamental
%		The fundamental's level and phase are left in-place
%  delta = 0 means keep the absolute values from the log file
%  The SPL track is set to actual dB. Converted EGG metrics are 
%  derived from resynthesizing the EGG waveforms with amplitude preserved.
%  This FonaDynLoadLogfile.m is for FonaDyn v<:VRPMain.mVersion:>. 
    [data, samplerate] = audioread(logFile, 'native');
    [frames, channels] = size(data);

    % TRACKS IN _Log.aiff files in this version ==============
    % cTime = data(:, 1);
    % cF0 = data(:, 2);
    % cLevel = data(:, 3);
    % cClarity = data(:, 4);
    % cCrest = data(:, 5);
    % cSpecBal = data(:, 6);
	% cCPPsmoothed = data(:, 7);
    % cClusterEGG = data(:, 8);
    % cClusterPhon = data(:, 9);
    % cSampEn = data(:, 10);
    % Icontact = data(:, 11);
    % Qdelta   = data(:, 12);
    % Qcontact = data(:, 13);

	nBaseTracks = round(<:VRPControllerIO.logBaseTracks:>);
	nBaseTrackNames = <:VRPSDIO.getAllLogMetricNames:>;

	nharm = round((channels - nBaseTracks)/2);
	h1 = round(nBaseTracks+1);

    % Offset dB track to actual dB
    data(:,3) = data(:,3) + <:VRPDataVRP.nMaxSPL:>;
	
	% This assumes that the last (max) cluster is represented in the log file excerpt
	eggClusterTrack = 0;
	for i = 4 : nBaseTracks-1
		if nBaseTrackNames(i) == "cEGG #"
			eggClusterTrack = i+1;
			nEGGclusters = max(data(:,eggClusterTrack+1)) + 1;   
		end
	end
	
	% This assumes that the last (max) cluster is represented in the log file excerpt
	phonClusterTrack = 0;
	for i = 4 : nBaseTracks-1
		if nBaseTrackNames(i) == "cPhon #"
			phonClusterTrack = i+1;
			nPhonClusters = max(data(:,phonClusterTrack+1)) + 1;   
		end
	end
	
    % Offset cluster # to 1-based
    data(:,eggClusterTrack) = data(:,eggClusterTrack) + 1;
    data(:,phonClusterTrack) = data(:,phonClusterTrack) + 1;
    
    if delta > 0
        for i = 1:nharm-1
            data(:,i+h1) = (data(:,i+h1) - data(:,h1)); % compute delta-levels 
            phidiff = data(:,i+h1+nharm) - data(:,h1+nharm);
            folddiff = atan2(sin(phidiff),cos(phidiff));  % constrain to [-pi,pi]
            data(:,i+h1+nharm) = folddiff;
        end;
        data(:,h1) = 0.0;
        data(:,h1+nharm) = data(:,h1+nharm+nharm-1) * 0.5; 
    end
    
    % insert a new column 12 for Qspeed (Qsi)
    % dataExt = data(:,1:11);
    % qsCol = single(zeros(size(dataExt,1),1));
    % dataExt = [dataExt qsCol];
    % dataExt = [dataExt data(:,12:end)];
    % data = dataExt;
    % clear dataExt;
 
	if xFourier == 1
    % Replace time-domain metrics with those from the FD data
		for j = 1: size(data,1)
			[~, ts] = synthEGGfromArrays(data(j,h1:h1+nharm-2), data(j,h1+nharm:h1+nharm+nharm-2),nharm-1,100,1.05); 
% 	   		timeTemp(j,1) = ts.p2pAmpl;
			data(j,nBaseTracks-2) = ts.iC;
			data(j,nBaseTracks-1) = ts.maxDegg;
			data(j,nBaseTracks) = ts.qC;
		end
	end
    logPlus = single(data);
end
