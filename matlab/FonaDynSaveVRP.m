function FonaDynSaveVRP(filename, names, vrpArray)
% Write a .CSV file with a header row of column names
% and the remaining rows as data in the FonaDyn format _VRP.csv
% The filename should end in '_VRP.csv'.
% FonaDyn version 3.1.1
  
    fileID = fopen(filename,'w');
    fprintf(fileID, "%s", names{1});
    fprintf(fileID, ";%s", names{2:end});
    fprintf(fileID, "\r\n");
    fclose(fileID);
    dlmwrite(filename, vrpArray, '-append', 'precision', 8, 'delimiter', ';', 'newline', 'pc');
end
  
