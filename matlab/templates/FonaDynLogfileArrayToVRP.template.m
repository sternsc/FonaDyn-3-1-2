function [names, dataArray, vrpArray] = FonaDynLogfileArrayToVRP(data)
% "data" is an array returned by FonaDynLoadLogfile.
% This column order is expected (FonaDyn v<:VRPMain.mVersion:>):
% [time, freq, amp, clarity, crest, specbal, cpps, numEGGcluster, numPhonCluster, sampen, iContact, dEGGmax, qContact] ++ amps ++ phases
% dataArray becomes a sparse 3D matrix (1:layers,1:fo_values,1:spl_values] corresponding to the voice field.
% dataArray and names can be passed to FonaDynPlotVRP or FonaDynPlotVRPratios/-diffs
% vrpArray is filled as for a _VRP.csv format file with one cell per row.
% nBaseTrackNames is filled with the column names for that file.

names = {'MIDI', 'dB', 'Total', 'Clarity', 'Crest', 'SpecBal', 'CPPs', 'Entropy', 'Icontact', 'dEGGmax', 'Qcontact', 'maxCluster'};
paramCols = size(names,2) - 2;

%nBaseTracks = <:VRPControllerIO.logBaseTracks:>;
nBaseTrackNames = <:VRPSDIO.getAllLogMetricNames:>;

nClustersEGG = max(data(:,8));   % This assumes that the highest EGG cluster number is present in data
for c = 1 : nClustersEGG
    names{paramCols+2+c} = char( ['Cluster ' num2str(c)]);
end

names{paramCols+3+nClustersEGG} = 'maxCPhon'; 
nClustersPhon = max(data(:,9)); 	% This assumes that the highest phon cluster number is present in data
for c = 1 : nClustersPhon
    names{paramCols+3+nClustersEGG+c} = char( ['cPhon ' num2str(c)]);
end

VRPx=zeros(paramCols+nClustersEGG+nClustersPhon+1, <:VRPDataVRP.vrpWidth:>,<:VRPDataVRP.vrpHeight:>);

for i = 1 : size(data, 1)
    foIx = max(1, round(data(i, 2)) - <:VRPDataVRP.nMinMIDI:> + 1);
    if foIx > <:VRPDataVRP.vrpWidth:> 
        continue
    end
    splIx  = max(1, round(data(i, 3)) - <:VRPDataVRP.nMinSPL:> + 1);
    if splIx > <:VRPDataVRP.vrpHeight:> 
        continue
    end
    VRPx(1, foIx, splIx) = VRPx(1, foIx, splIx)  +  1 ;   % accumulate total cycles
    VRPx(2, foIx, splIx) = data(i, 4);   % latest clarity
    VRPx(3, foIx, splIx) = VRPx(3, foIx, splIx) + data(i, 5);  % accumulate crest factor
    VRPx(4, foIx, splIx) = VRPx(4, foIx, splIx) + data(i, 6); % accumulate spectrum balance
    VRPx(5, foIx, splIx) = VRPx(5, foIx, splIx) + data(i, 7); % accumulate CPP smoothed
    VRPx(6, foIx, splIx) = VRPx(6, foIx, splIx) + data(i, 10); % accumulate entropy
    VRPx(7, foIx, splIx) = VRPx(7, foIx, splIx) + data(i, 11); % accumulate iContact
    VRPx(8, foIx, splIx) = VRPx(8, foIx, splIx) + data(i, 12); % accumulate dEGGmax/Qdelta
    VRPx(9, foIx, splIx) = VRPx(9, foIx, splIx) + data(i, 13); % accumulate qContact
    clusterNoEGG = data(i, 8);   % get the cluster number of this cycle
    VRPx(paramCols+clusterNoEGG, foIx, splIx) = VRPx(paramCols+clusterNoEGG, foIx, splIx) + 1; % accumulate cycles per cluster
    clusterNoPhon = data(i, 9);   % get the cluster number of this cycle
	ix = paramCols+nClustersEGG+1+clusterNoPhon;
    VRPx(ix, foIx, splIx) = VRPx(ix, foIx, splIx) + 1; % accumulate cycles per cluster
end

vArr = [];
row = 1;
for foIx = 1 : <:VRPDataVRP.vrpWidth:>
    for splIx = 1 : <:VRPDataVRP.vrpHeight:>
        totCycles = VRPx(1, foIx, splIx);
        if totCycles > 0
            VRPx(3, foIx, splIx) = VRPx(3, foIx, splIx) / totCycles;
            VRPx(4, foIx, splIx) = VRPx(4, foIx, splIx) / totCycles;
            VRPx(5, foIx, splIx) = VRPx(5, foIx, splIx) / totCycles;
            VRPx(6, foIx, splIx) = VRPx(6, foIx, splIx) / totCycles;
            VRPx(7, foIx, splIx) = VRPx(7, foIx, splIx) / totCycles;
            VRPx(8, foIx, splIx) = VRPx(8, foIx, splIx) / totCycles;
            VRPx(9, foIx, splIx) = VRPx(9, foIx, splIx) / totCycles;
			
            [~, maxIx] = max(VRPx(paramCols+1:paramCols+nClustersEGG, foIx, splIx));
            VRPx(paramCols, foIx, splIx) = maxIx;     % fill in maxCluster

            [~, maxIx] = max(VRPx(paramCols+nClustersEGG+2:paramCols+nClustersEGG+1+nClustersPhon, foIx, splIx));
            VRPx(paramCols+nClustersEGG+1, foIx, splIx) = maxIx;     % fill in maxCPhon

            vArr(row, :) = [foIx-1+<:VRPDataVRP.nMinMIDI:>; splIx-1+<:VRPDataVRP.nMinSPL:>; VRPx(1:paramCols+nClustersEGG+1+nClustersPhon, foIx, splIx)]';
            row = row+1;
        end
    end
end
dataArray = VRPx;
vrpArray = vArr;
end
