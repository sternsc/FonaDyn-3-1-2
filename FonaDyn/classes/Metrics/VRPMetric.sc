// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
// Copyright (C) 2016-2023 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
// VRPMetric is an abstract base class for all FonaDyn metrics
// - do not instantiate it directly.
// Each metric has a subclass defined in "MetricXxxx.sc"
// The var members and some classvar members are set by the subclasses.

VRPMetric {
	// Subclass classvars:
	// classvar <metricNumber = nil; 			// indicates desired position in array of metrics
	// classvar <symbol = \AbstractMetric;		// Symbol to identify this metric, e.g. \Qci
	// classvar <busName; 						// Symbol for the bus carrying the unscaled value
	// classvar <busRate;						// \audio or \control
	var <>csvName;			// String name for columns in map files
	var <>csvPrecision=1e-05;     // Parameter for .round(aFloat) when saving to .csv files
	var <>defName;			// Pathname to an optional text file that can initialize this metric
	var <>minVal, <>maxVal;	// Limits for the color mappings
	var <>rangeLow, <>rangeHigh; 	// Limits for standardization for clustering
	var <palette;			// Palette function
	var <msgStandardize;	// defines the mapping function for clustering of this metric
	var <>menuText;			// String for the layer menu
	var <>trendText;
	var <>trendTextColor;   // Colour for the trend text
	var <>colorBarText;		// String for the color bar
	var <>colorBarWarpType; // Color axis \lin or \exp
	var <>unit;				// Metric unit if applicable, e.g. "dB"
	var bDifferencing;				// true if a metric difference should be displayed
	var colorPre, colorPost;

	classvar <fnPrePostPalette;
	classvar colorPre, colorPost;

	*new { arg file=nil;
		^super.new.init(file);
	}

	*initClass {
		// Special coloring for DIFF maps underlap regions
		colorPost = Color.gray(0.6);
		colorPre = Color.hsv(0.83, 0.4, 0.8);
		fnPrePostPalette = { arg v;
			var color = switch (v)
			{ -1 } { colorPre }
			{  1 } { colorPost }
			{ Color.black };
			color
		};
	}

	init { arg file;
		trendTextColor = Color.black; 		// default
		trendText = "→ Green: increased";   // dummy setting
		msgStandardize = msgStandardize ? ['linlin', rangeLow, rangeHigh, 0, 1, nil] ;
	}

	setDifferencing { | bDiff |
		bDifferencing = bDiff;
		this.configFromCode(nil);
	}

	fnStandardizeMsg {
		// Updates these settings when called
		// If not initialized, or not overridden,
		// use GUI limits as default values
		rangeLow  = rangeLow  ? minVal;
		rangeHigh = rangeHigh ? maxVal;
		^msgStandardize = ['linlin', rangeLow, rangeHigh, 0, 1];
	}

	getPaletteFunc {
		^palette;
	}

	setClusters { | nClusters, nCluster |
		// Dummy no-op, overridden by VRPClusterMaps
	}

	// Apply content and color to View tf
	setTrendText { | tf |
		// These can be constants or functions, so .value them
		tf.string_(trendText.value);
		tf.stringColor_(trendTextColor.value)
	}

	///////// For Matlab: ///////////////////////////////////////

	cMap { arg shades=20, bLog=false;
		var color, val;
		var rgbArray = [], step;
		var rgbArrayString = String.new;
		if (bLog == true)
		{
			step = (maxVal/minVal)**((shades+1).reciprocal);
			rgbArray = shades collect: { | n |
				val = minVal*(step**n);
				color = this.palette.(val).asArray[0..2].round(0.001);
				// a bracketed RGB triplet, but remove the commas:
				color.asCompileString.tr($, , $ )
			}
		} {
			step = (maxVal-minVal)/shades;
			rgbArray = shades collect: { | n |
				val = (minVal + (n*step));
				color = this.palette.(val).asArray[0..2].round(0.001);
				// a bracketed RGB triplet, but remove the commas:
				color.asCompileString.tr($, , $ )
			};
		};

		rgbArray do: { | triplet |
			rgbArrayString = rgbArrayString ++ "\t" ++ triplet ++ ";\n" ;
		};

		^("[" ++ rgbArrayString ++ "]");
	}

	cLims {
		^[minVal, maxVal].asCompileString;
	}

	//////////////////////////////////////////////////////////

	// UNDOCUMENTED FEATURE - PROBABLY NOT NECESSARY
	configFromFile { arg defFile;
		var tmpArray;
		var lines;
		tmpArray = FileReader.read(defFile, skipEmptyLines: true, delimiter: $§);
		// FileReader brackets each line in [  ] - strip them off
		lines = tmpArray.collect({ arg s, i; var str = s.asString; str[2..(str.size-3)] });
		~meVRPMetric = this;
		lines do: { | str, i | interpret("~meVRPMetric."++str) };
		format("Metric % loaded: %", csvName, defFile).postln;
		defName = defFile;
	}
}


