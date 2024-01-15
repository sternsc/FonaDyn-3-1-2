// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

MetricQdelta : VRPMetric {
	classvar <metricNumber;		// the desired index of this metric
	classvar <symbol = \Qdelta;
	classvar <busName = \DEGGmax;
	classvar <busRate = \audio;

	*new { arg number=nil, bDifference=false, file=nil;
		^super.new.init(number, bDifference, file);
	}

	init { arg number, bDiff=false, file;
		metricNumber = number ? VRPSettings.idEGGmax;
		bDifferencing = bDiff;
		if (file.notNil,
			{ this.configFromFile(file) },
			{ this.configFromCode() }
		);
		// For clustering, we use the log of Qdelta
		msgStandardize = ['explin', rangeLow, rangeHigh, 0, 1, \min] ;
	}

	fnStandardizeMsg {
		// updates these settings when called
		^msgStandardize = ['explin', minVal, maxVal, 0, 1, \min];
	}

	configFromCode {
		csvName = "dEGGmax";	// Column title in map files; a.k.a. Qdelta, in publications
		menuText = "QΔ - peak dEGG normalized";	// String for the layers menu
		colorBarWarpType = \exp;				// Color axis \lin or \exp
		unit = "slope";							// Unit, if applicable, e.g. "dB"

		if (bDifferencing.not, {
			// Color mapping of value
			// map log(dEGGmax) 1.0 ... <20 to green...red
			minVal = 1.0;
			maxVal = 20.01;
			rangeLow = minVal;
			rangeHigh = maxVal;
			palette = { | v |
				var cHue = v.explin(minVal, maxVal, 0.333, 0);
				Color.hsv(cHue, 1, 1)
			};
			colorBarText = "Mean QΔ";				// String for the color bar
			trendText = "→ Red: faster contact";  	// String for info text
			trendTextColor = Color.red;
		}, {

			// Color mapping of delta-value; actually a ratio, not diff
			// map lower QΔ ×0.2 to red, larger QΔ ×5 to green
			// map mid=0 to grey
			minVal = 0.2;
			maxVal = 5.01;
			palette = { | v |
				var cHue, cSat;
				cSat = v.biexp(1, minVal, maxVal, 0, 1, 1);
				Color.hsv((v >= 1.0).asInteger/3, cSat, 0.93+(0.07*cSat))
			};
			colorBarText = "QΔ ratio"; 			// Ditto, when dividing
			trendText = "→ Green: increased";  // String for info text
			trendTextColor = Color.green;
			unit = "";
		});
	}
}

