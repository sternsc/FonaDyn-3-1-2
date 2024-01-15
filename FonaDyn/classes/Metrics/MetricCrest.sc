// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
MetricCrest : VRPMetric {
	classvar <metricNumber; // the desired index of this metric
	classvar <symbol = \Crest;
	classvar <busName = \DelayedCrest;
	classvar <busRate = \audio;

	*new { arg number=nil, bDifference=false, file=nil;
		^super.new.init(number, bDifference, file);
	}

	init { arg number, bDiff=false, file;
		metricNumber = number ? VRPSettings.iCrestFactor;
		bDifferencing = bDiff;
		if (file.notNil,
			{ this.configFromFile(file) },
			{ this.configFromCode(metricNumber) }
		);
	}

	configFromCode { arg metNumber;
		csvName = "Crest";		// Column title in map files
		csvPrecision = 1e-05;

		menuText = "Audio Crest Factor";		// String for the layer menu
		colorBarWarpType = \lin;	 			// Color axis \lin or \exp
		unit = "";								// Unit, if applicable, e.g. "dB"

		if (bDifferencing.not, {
			// Color mapping of value
			// map crest factor 1.414 (+3 dB) ... <4 (+12 dB) to green...red
			minVal = 1.414;
			maxVal = 4.0;
			palette = { | v |
				var cHue = v.linlin(1.414, 4, 0.333, 0);
				Color.hsv(cHue, 1, 1);
			};
			colorBarText = "Mean crest factor";		// String for the color bar
			trendText = "→ Red: peakier signal";  	// String for info text
			trendTextColor = Color.red;
		}, {
			// Color mapping of delta-value
			// map crest factor difference -2 ... <2 to red...green
			minVal = -2;
			maxVal = 2;
			palette = { | v |
				var cSat = v.abs.linlin(0, maxVal, 0, 1.0);
				Color.hsv((v.sign+1)/6, cSat, 0.93+(0.07*cSat))
			};
			colorBarText = "Crest factor diff";		// Ditto, when differencing
			trendText = "→ Green: increase";  	// String for info text
			trendTextColor = Color.green;
		});
	}
}

