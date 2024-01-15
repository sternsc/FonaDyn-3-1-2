function map = cMapEGG(nColors, saturation)
%%  EGG cluster colors for FonaDyn v<:VRPMain.mVersion:>.
%!!   This function maps the default EGG cluster 
%!!   If MetricClusterPhon:fnClusterColor is changed, 
%!!   then this file must also be changed. 
    hues = zeros(nColors, 3);
    map = hues;
    sat = saturation;
for i = 1:nColors
    hues(i,:) = [(i-1)/nColors sat 1]; 
end
    map = hsv2rgb(hues);
end
