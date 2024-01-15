function outArray = skimVRP(inArray, colNames, percentCycles, bManualPick)

colTotal = find(ismember(colNames, 'Total')); 
counts = inArray(:,colTotal);
totalCycles = sum(counts);
includeCells = totalCycles * percentCycles * 0.01;
acc = 0;
ix = [];
[B, I] = sort(counts, 'descend');
for k = 1 : length(B) 
    if (acc > includeCells)
        break;
    end
    acc = acc + B(k);
    ix(k) = I(k);       
end

if bManualPick > 0
    % Build a matrix with a logical non-zero pixel for each row
    allPixels = zeros(150,100);
    for j = 1 : k-1
       y = inArray(ix(j),2);  
       x = inArray(ix(j),1);  
       allPixels(y,x) = 1;
    end

    figure('Units', 'centimeters', 'Position', [10, 10, 15, 20]);
    % Find the largest 4-connected region
    seedX = inArray(ix(1),1);
    seedY = inArray(ix(1),2);
    [somePixels, idx] = bwselect(allPixels, 4);

    % Pick those cells from inArray
    m = 1;
    jx = [];
    for j = 1 : k-1
       y = inArray(ix(j),2);  
       x = inArray(ix(j),1);  
       if somePixels(y,x) == 1
           jx(m) = ix(j);
           m = m + 1;
       end
    end
    ix = jx;
end

outArray = inArray(ix,:);
end