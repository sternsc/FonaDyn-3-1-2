function [names, dataArray, vrpArray] = FonaDynLogfileArrayStatsToVRP(data, statmode)
% Log file columns from FonaDyn v<:VRPMain.mVersion:> are loaded; this order is expected:
% [time, freq, amp, clarity, crest, specbal, cpps, numEGGcluster, numPhonCluster, sampen, iContact, dEGGmax, qContact] ++ amps ++ phases
% statmode selects the statistic in vrpArray as 
% 1: mean, 2: stdev, 3: median, 4: spread (between 1-sigma quantiles)
% dataArray becomes a sparse array (1:layers,1:fo_values,1:spl_values] corresponding to the voice field.
% dataArray and names can be passed to FonaDynPlotVRP or FonaDynPlotVRPratios/-diffs
% vrpArray is filled as for a _VRP.csv format file with one cell per row.
% names is filled with the column names for that file.

names = {'MIDI', 'dB', 'Total', 'Clarity', 'Crest', 'SpecBal', 'CPP', 'Entropy', 'Icontact', 'dEGGmax', 'Qcontact', 'maxCluster'};
paramCols = size(names,2) - 2;

nBaseTracks = <:VRPControllerIO.logBaseTracks:> - 1;  % The time track is skipped
nBaseTrackNames = <:VRPSDIO.getAllLogMetricNames:>;

nClustersEGG = max(data(:,8));   % This assumes that the highest EGG cluster number is present in data
for c = 1 : nClustersEGG
    names{paramCols+2+c} = char( ['Cluster ' num2str(c)]);
end

names{paramCols+3+nClustersEGG} = 'maxCPhon'; 
nClustersPhon = max(data(:,9)); 	% This assumes that the highest phon cluster number is present in data
for c = 1 : nClustersPhon
    names{paramCols+3+c} = char( ['cPhon ' num2str(c)]);
end

VRPx=zeros(paramCols+nClustersEGG+nClustersPhon+1,<:VRPDataVRP.vrpWidth:>,<:VRPDataVRP.vrpHeight:>);

for i = 1 : size(data, 1)
    foIx = max(1, round(data(i, 2)) - <:VRPDataVRP.nMinMIDI:> + 1);
    if foIx > <:VRPDataVRP.vrpWidth:> 
        continue
    end
    splIx  = max(1, round(data(i, 3)) - <:VRPDataVRP.nMinSPL:> + 1);
    if splIx > <:VRPDataVRP.vrpHeight:> 
        continue
    end
    clusterNoEGG = data(i, 8);   % get the cluster number of this cycle
    VRPx(paramCols+clusterNoEGG, foIx, splIx) = VRPx(paramCols+clusterNoEGG, foIx, splIx) + 1; % accumulate cycles per cluster
    clusterNoPhon = data(i, 9);   % get the cluster number of this cycle
    VRPx(paramCols+1+clusterNoPhon, foIx, splIx) = VRPx(paramCols+1+clusterNoPhon, foIx, splIx) + 1; % accumulate cycles per cluster
end

% For all cells in the voice field
vArr = [];
row = 1;
for foIx = <:VRPDataVRP.nMinMIDI:> : <:VRPDataVRP.nMaxMIDI:>
    for splIx = <:VRPDataVRP.nMinSPL:> : <:VRPDataVRP.nMaxSPL:>
        cellIx = [];
        k = 1;
        % for all cycle frames in the log file
        for j = 1 : size(data,1)
            if round(data(j,2))==foIx && round(data(j,3))==splIx
                cellIx(k) = j;
                k = k + 1;
            end
        end
        cellData = data(cellIx,:); 
        totCycles = k-1; % size(cellIx,1);  % Total cycles
        if totCycles > 0
            fo  = foIx-<:VRPDataVRP.nMinMIDI:>+1;
            spl = splIx-<:VRPDataVRP.nMinSPL:>+1;
            VRPx(1, fo, spl) = totCycles;
            VRPx(2, fo, spl) = cellData(end, 4);
            layer = 3;
            for m = [5 6 7 10 11 12 13]
                switch (statmode)
                    case 1
                        VRPx(layer, fo, spl) = mean(cellData(:, m));
                    case 2
                        VRPx(layer, fo, spl) = std(cellData(:, m));
                    case 3
                        VRPx(layer, fo, spl) = median(cellData(:, m));
                    case 4
                        % Compute normal-dist 1-sigma quantiles instead of stdev
                        q = quantile(cellData(:, m),[0.1590, 0.8410]);
                        spread = q(2) - q(1);
                        VRPx(layer, fo, spl) = abs(spread); 
                end
                layer = layer + 1 ;
            end
            [maxVal maxIx] = max(VRPx(paramCols+1:paramCols+nClustersEGG, fo, spl));
            VRPx(paramCols, fo, spl) = maxIx;     % fill in maxCluster

            [maxVal maxIx] = max(VRPx(paramCols+nClustersEGG+2:paramCols+nClustersEGG+2+nClustersPhon, fo, spl));
            VRPx(paramCols+nClustersEGG+1, fo, spl) = maxIx;     % fill in maxCPhon

            vArr(row, :) = [fo; spl; VRPx(1:paramCols+nClustersEGG+nClustersPhon, fo, spl)]';
            row = row+1;
        end
    end
end


dataArray = VRPx;
vrpArray = vArr;
end