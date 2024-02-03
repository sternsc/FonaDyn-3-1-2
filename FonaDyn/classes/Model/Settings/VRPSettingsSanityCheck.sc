// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
//
// Addition of a sanity check for all the settings.
// May help to contain all the sanityChecks in the same file,
//	 instead of small checks in each individual settings file.
//

+ VRPSettings {
	sanityCheck {
		var ret = true;

		ret = ret and: csdft.sanityCheck(this);

		^ret;
	}
}

+ VRPSettingsCSDFT {
	sanityCheck { | settings |
		var ret = true;
		var tmpHarm =
		[
			settings.cluster.nHarmonics,
			settings.sampen.amplitudeHarmonics,
			settings.sampen.phaseHarmonics
		].maxItem.asInteger;

		if (tmpHarm != nHarmonics, {
			format("Harmonics count mismatch: nHarmonics=%, needed=%", nHarmonics, tmpHarm).error;
			nHarmonics = tmpHarm;
			settings.cluster.nHarmonics = tmpHarm;
			ret = false;
		});

		^true;
	}
}