// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
MetricSpecBal : VRPMetric {
	classvar <metricNumber;		// the desired index of this metric
	classvar <symbol = \SpecBal;
	classvar <busName = \DelayedSpecBal;
	classvar <busRate = \audio;

	*new { arg number=nil, bDifference=false, file=nil;
		^super.new.init(number, bDifference, file);
	}

	init { arg number, bDiff=false, file;
		metricNumber = number ? VRPSettings.iSpecBal;
		bDifferencing = bDiff;
		if (file.notNil,
			{ this.configFromFile(file) },
			{ this.configFromCode(metricNumber) }
		);
	}

	configFromCode { arg metNumber;
		csvName = "SpecBal";	// Column title in map files
		csvPrecision = 1e-05;
		menuText = "Audio Spectrum Balance";		// String for the layer menu
		colorBarWarpType = \lin;						// Color axis \lin or \exp
		unit = "dB";									// Unit, if applicable, e.g. "dB"

		if (bDifferencing.not, {
			// Color mapping of value
			// map spectrum balance -42... 0 (dB) to green...red
			minVal = -42.0;
			maxVal = 0.0;
			palette = { | v |
				var cHue = v.linlin(minVal, maxVal, 0.333, 0);
				Color.hsv(cHue, 1, 1);
			};
			colorBarText = "Mean Spectrum Balance";			// String for the color bar
			trendText = "→ Red: more high freq";	  		// String for info text
			trendTextColor = Color.red;
		}, {
			// Color mapping of delta-value
			// map specBal difference -20...+20 (dB) to red...green
			minVal = -20;
			maxVal = 20.01;
			palette = { | v |
				var cSat = v.abs.linlin(0, maxVal, 0, 1.0);
				Color.hsv((v.sign+1)/6, cSat, 0.93+(0.07*cSat))	// red...green
				// Color.hsv((8-(v.sign*3))/12, cSat, 0.93+(0.07*cSat))		// towards magenta..cyan
			};
			colorBarText = "Spectrum Balance diff"; 	// Ditto, when differencing
			trendText = "→ Green: more high freq";	  		// String for info text
			trendTextColor = Color.green;
			// trendTextColor = Color.hsv(5/12, 1, 1);
		});
	}
}

