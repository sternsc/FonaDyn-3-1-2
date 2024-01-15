// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
MetricQci : VRPMetric {
	classvar <metricNumber;		// the desired index of this metric
	classvar <symbol = \Qci;
	classvar <busName = \Qcontact;
	classvar <busRate = \audio;

	*new { arg number=nil, bDifference=false, file=nil;
		^super.new.init(number, bDifference, file);
	}

	init { arg number, bDiff=false, file;
		metricNumber = number ? VRPSettings.iQcontact;
		bDifferencing = bDiff;
		if (file.notNil,
			{ this.configFromFile(file) },
			{ this.configFromCode(metricNumber) }
		);
	}

	configFromCode { arg metNumber;
		symbol = \Qci;			// Symbol to identify this metric, e.g. \Qci
		csvName = "Qcontact";	// Column title in map files
		menuText = "Qci - EGG contact quotient";	// String for the layer menu
		colorBarWarpType = \lin;	 				// Color axis \lin or \exp
		unit = "";									// Unit, if applicable, e.g. "dB"

		if (bDifferencing.not, {
			// Color mapping of value
			// large Qc=0.6 to red, small Qc=0.1 to purple
			minVal = 0.1;
			maxVal = 0.6;
			palette = { | v |
				var cHue = v.linlin(minVal, maxVal, 0.83, 0.0);
				Color.hsv(cHue, 1, 1)
			};
			colorBarText = "Mean Qci";							// String for the color bar
			trendText = "→ Red: longer contact";  				// String for info text
			trendTextColor = Color.red;
		}, {

			// Color mapping of delta-value
			// Diff/ratio: lower Qci=-0.2 to red, larger Qci=0.2 to green; mid=0 to grey
			minVal = -0.2;
			maxVal = 0.2;
			palette = { | v |
				var cSat = v.abs.linlin(0, maxVal, 0, 1.0);
				Color.hsv((v.sign+1)/6, cSat, 0.93+(0.07*cSat))
			};
			colorBarText = "Qci diff"; 						// Ditto, when differencing
			trendText = "→ Green: increased";  // String for info text
			trendTextColor = Color.green;
		});
	}
}

