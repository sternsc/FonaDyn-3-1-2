function [outVRParray] = FonaDynVRPFromMatrix(inMatrix)
% inMatrix is a flat array (1:layers,1:fo_values,1:spl_values] corresponding to the voice field.
% outVRParray is an array of one VRP cell per row
% FonaDyn version <:VRPMain.mVersion:>

layers = size(inMatrix,1); % number of layers
cols = size(inMatrix,2);
rows = size(inMatrix,3); 

totals = zeros(rows, cols);
for y = 1 : rows
    for x = 1 : cols
        totals(y, x) = inMatrix(1,x,y);
    end
end
        
[row, col] = find(totals>0);     % make a list of all occupied cells

outArray = zeros(size(row, 1), layers+2);

for i = 1 : size(row, 1)
    foIx  = col(i);
    splIx = row(i);
    outArray(i,1) = foIx+<:VRPDataVRP.nMinMIDI:>-1;
    outArray(i,2) = splIx+<:VRPDataVRP.nMinSPL:>-1;
    for j = 1 : layers
        outArray(i, j+2) = inMatrix(j, foIx, splIx);
    end
end
outVRParray = outArray;
end
