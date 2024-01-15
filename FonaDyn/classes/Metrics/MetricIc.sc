// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

MetricIc : VRPMetric {
	classvar <metricNumber;		// the desired index of this metric
	classvar <symbol = \Ic;
	classvar <busName = \Icontact;
	classvar <busRate = \audio;

	*new { arg number=nil, bDifference=false, file=nil;
		^super.new.init(number, bDifference, file);
	}

	init { arg number, bDiff=false, file;
		metricNumber = number ? VRPSettings.iIcontact;
		bDifferencing = bDiff;
		if (file.notNil,
			{ this.configFromFile(file) },
			{ this.configFromCode() }
		);
	}

	configFromCode {
		csvName = "Icontact";	// Column title in map files
		menuText = "Ic - EGG index of contacting";	// String for the layers menu
		colorBarWarpType = \lin;				// Color axis \lin or \exp
		unit = "";								// Unit, if applicable, e.g. "dB"

		if (bDifferencing.not, {
			// Color mapping of value
			// map large Ic=1.0 to red, small Ic=0 to blue
			minVal = 0.0;
			maxVal = 0.7;
			palette = { | v |
				var cHue = v.linlin(minVal, maxVal, 0.67, 0.0);
				Color.hsv(cHue, 1, 1)
			};
			colorBarText = "Mean Ic";				// String for the color bar
			trendText = "← Blue: no contact";  		// String for info text
			trendTextColor = Color.blue;
		}, {
			// Color mapping of delta-value
			// map lower Ic=-0.4 to red, larger Ic=0.4 to green
			// map mid=0 to grey
			minVal = -0.4;
			maxVal = 0.4;
			palette = { | v |
				var cSat = v.abs.linlin(0, maxVal, 0, 1.0);
				Color.hsv((v.sign+1)/6, cSat, 0.93+(0.07*cSat))
			};
			colorBarText = "Ic diff"; 			// Ditto, when differencing
			trendText = "→ Green: increased";  // String for info text
			trendTextColor = Color.green;
		});
	}
}

