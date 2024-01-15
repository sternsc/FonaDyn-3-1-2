function [outMatrix] = FonaDynMatrixFromVRP(vrpArray, threshold)
%% FonaDynMatrixFromVRP takes a vrpArray as loaded by FonaDynLoadVRP.m
% and creates a sparse matrix
% outMatrix becomes a flat array (1:layers,1:fo_values,1:spl_values] corresponding to the voice field.
% FonaDyn version <:VRPMain.mVersion:>

dataCols = size(vrpArray, 2) - 2; % cols 1 and 2 hold the indices
VRPx=ones(dataCols,<:VRPDataVRP.vrpWidth:>,<:VRPDataVRP.vrpHeight:>)*NaN;

for i = 1 : size(vrpArray, 1)
    foIx  = vrpArray(i, 1) - (<:VRPDataVRP.nMinMIDI:> - 1);
    splIx = vrpArray(i, 2) - (<:VRPDataVRP.nMinSPL:> - 1);
    if foIx > <:VRPDataVRP.vrpWidth:> 
        continue
    end
    if splIx < 2
        continue
    end
    if vrpArray(i, 3) >= threshold
        for j = 1:dataCols
            VRPx(j, foIx, splIx) = vrpArray(i, j+2);
        end
        % VRPx(10, foIx, splIx) = 1.0;  % count this participant as one "cycle"
    end
end

outMatrix = VRPx;
end
