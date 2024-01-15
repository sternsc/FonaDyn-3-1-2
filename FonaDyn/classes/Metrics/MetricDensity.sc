// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
MetricDensity : VRPMetric {
	classvar <metricNumber; 		// the desired index of this metric
	classvar <symbol = \Density;
	// classvar <busName;			// This metric is not relevant for clustering
	// classvar <busRate;

	*new { arg number=nil, bDifference=false, file=nil;
		^super.new.init(number, bDifference, file);
	}

	init { arg number, bDiff=false, file;
		metricNumber = number ? VRPSettings.iDensity;
		bDifferencing = bDiff;
		if (file.notNil,
			{ this.configFromFile(file) },
			{ this.configFromCode(metricNumber) }
		);
	}

	fnStandardizeMsg {
		// updates these settings when called
		^msgStandardize = ['explin', minVal, maxVal, 0, 1, \min];
	}

	configFromCode { arg metNumber;
		csvName = "Total";								// Column title in map files
		csvPrecision = 0.01;							// Allow fractions, for smoothed maps
		menuText = "Density - number of cycles";		// String for the layer menu
		colorBarText = "Density";						// String for the color bar
		colorBarWarpType = \exp;	 					// Color axis \lin or \exp
		unit = "c";										// Unit, if applicable, e.g. "dB"
		trendText = "Darker → more cycles";  			// String for info text
		trendTextColor = Color.grey;

		// Color mapping of value
		// map 1..<10000 to light...darker grey
		minVal = 1;
		maxVal = 10000;
		palette = { | v |
			var cSat = v.explin(minVal, maxVal, 0.95, 0.25);
			Color.grey(cSat, 1);
		};

		if (bDifferencing, {
			colorBarText = "Least Density";
		});
	}
} /* MetricDensity */

