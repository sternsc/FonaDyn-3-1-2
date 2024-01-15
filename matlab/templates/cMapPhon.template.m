function map = cMapPhon(nColors, saturation)
%%  PhonType colors for FonaDyn v<:VRPMain.mVersion:>.
%!!   This function maps the default phonation cluster 
%!!   If MetricClusterPhon:fnClusterColor is changed, 
%!!   then this file must also be changed. 
    hues = zeros(nColors, 3);
    map = hues;
    sat = saturation;
for i = 1:nColors
	if i <= nColors/2 
	    cHue = 0.65;
	else
		cHue = 0.1;
	end
	inScaled = i / (nColors+1); 
	cHue = cHue + (0.12 * (0.5 - inScaled));
	cValue = 0.95 - (0.95-0.3)*abs(inScaled-0.5)*2;
	cSat = 1 - 0.7*cValue;
    hues(i,:) = [cHue cSat cValue]; 
end
    map = hsv2rgb(hues);
end
