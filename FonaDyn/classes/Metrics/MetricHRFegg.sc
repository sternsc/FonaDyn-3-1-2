// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

MetricHRFegg : VRPMetric {
	classvar <metricNumber;		// the desired index of this metric
	classvar <symbol = \HRFegg;
	classvar <busName = \HRFEGG;
	classvar <busRate = \audio;

	*new { arg number=nil, bDifference=false, file=nil;
		^super.new.init(number, bDifference, file);
	}

	init { arg number, bDiff=false, file;
		metricNumber = number ? VRPSettings.iHRFEGG;
		bDifferencing = bDiff;
		if (file.notNil,
			{ this.configFromFile(file) },
			{ this.configFromCode() }
		);
	}

	fnStandardizeMsg {
		// updates these settings when called
		^msgStandardize = ['linlin', minVal, maxVal, 0, 1, \min];
	}

	configFromCode {
		csvName = "HRFegg";	// Column title in map files
		csvPrecision = 1e-05;
		menuText = "EGG harmonic richness factor";	// String for the layers menu
		// For clustering, we use the HRFegg
		msgStandardize = ['linlin', rangeLow, rangeHigh, 0, 1, \min] ;
		colorBarWarpType = \lin;				// Color axis \lin or \exp
		unit = "dB";							// Unit, if applicable, e.g. "dB"

		if (bDifferencing.not, {
			// Color mapping of value
			// map HRF -30 dB ... +5 dB to magenta...red
			minVal = -30.0;
			maxVal = 5;
			rangeLow = minVal;
			rangeHigh = maxVal;
			palette = { | v |
				var cHue;
				cHue = v.linlin(minVal, maxVal, 5/6, 0);
				Color.hsv(cHue, 1, 1)
			};
			colorBarText = "Mean HRF-egg";				// String for the color bar
			trendText = "→ Red: rich harmonics";  	// String for info text
			trendTextColor = Color.red;
		}, {
			// Color mapping of delta-value
			// map HRFegg difference -20...+20 (dB) to red...green
			minVal = -20;
			maxVal = 20.01;
			palette = { | v |
				var cSat = v.abs.linlin(0, maxVal, 0, 1.0);
				Color.hsv((v.sign+1)/6, cSat, 0.93+(0.07*cSat))
			};
			colorBarText = "HRF-egg diff";				// String for the color bar
			trendText = "→ Green: increased";  	// String for info text
			trendTextColor = Color.green;
		});
	}
}

