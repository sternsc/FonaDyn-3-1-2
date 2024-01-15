// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

VRPControllerCSDFT {
	var mTarget;
	var mData;

	// Synths
//	var mSynthDiplo;
	var mSynthQciDEGG;
	var mSynthHRFEGG;
	var mSynthDFTFilter;
	var mSynthNDFTs;
	var mSynthCycleSeparation;

	*new { | target, data |
		^super.new.init(target, data);
	}

	// Init given the target and output data structure
	// This function is called once only!
	init { | target, data |
		mTarget = target;
		mData = data; // General data
	}

	// Tell the busManager what buses it requires.
	// This function is called before prepare.
	requires { | busManager |
		var d = mData;
		var cd = d.csdft;
		var s = d.settings;
		var cs = s.csdft;

		busManager
		.requireAudio(\ConditionedEGG)
		.requireAudio(\GateCycle)
		.requireAudio(\GateDelayedCycle)
		.requireAudio(\GateDFT)
		.requireAudio(\GateFilteredDFT)
		.requireAudio(\DEGGmax)
		.requireAudio(\Qcontact)
		.requireAudio(\Icontact)
		.requireAudio(\HRFEGG)
		.requireAudio(\AmplitudeFirst, cs.nHarmonics+1) /**/
		.requireAudio(\PhaseFirst, cs.nHarmonics+1) 	/**/
		.requireControl(\Clarity)
	}

	// Prepare to start - init the SynthDefs and allocate Buffers.
	// This function is always called before start.
	prepare { | libname, server, busManager, clock |
		var d = mData;
		var cd = d.csdft;
		var s = d.settings;
		var cs = s.csdft;

		VRPSDCSDFT.compile(libname, cs.nHarmonics, cs.tau, cs.minFrequency, cs.minSamples, s.vrp.clarityThreshold);
	}

	// Start - Create the synths, and initiate fetching of data at the regular
	// interval given by the clock parameter.
	// This function is always called between prepare and stop.
	start { | server, busManager, clock |
		var bm = busManager;
		var d = mData;
		var s = d.settings;
		var cs = s.csdft;

		// Select the appropriate method
		var csfn =
		switch (cs.method,
			VRPSettingsCSDFT.methodPhasePortrait, \phasePortrait,
			VRPSettingsCSDFT.methodPeakFollower, \peakFollower
		);

		mSynthCycleSeparation = Synth(*
			VRPSDCSDFT.perform(csfn,
			bm.audio(\ConditionedEGG),
			bm.audio(\GateCycle),
			mTarget,
			\addToTail)
		);

		mSynthNDFTs = Synth(*VRPSDCSDFT.nDFTs(
			bm.audio(\ConditionedEGG),
			bm.audio(\GateCycle),
			bm.audio(\GateDFT),
			bm.audio(\GateDelayedCycle),
			bm.audio(\AmplitudeFirst),
			bm.audio(\PhaseFirst),
			mTarget,
			\addToTail)
		);

		mSynthDFTFilter = Synth(*VRPSDCSDFT.dftFilters(
			bm.audio(\GateDFT),
			bm.audio(\GateCycle),
			bm.audio(\GateDelayedCycle),
			bm.control(\Clarity),
			bm.audio(\GateFilteredDFT),
			mTarget,
			\addToTail)
		);

		mSynthQciDEGG = Synth(*VRPSDCSDFT.qciDEGG(
			bm.audio(\ConditionedEGG),
			bm.audio(\GateCycle),
			bm.audio(\GateDelayedCycle),
			bm.audio(\DEGGmax),
			bm.audio(\Qcontact),
			bm.audio(\Icontact),
			mTarget,
			\addToTail)
		);

		mSynthHRFEGG = Synth(*VRPSDCSDFT.hrfEGG(
			bm.audio(\GateDFT),
			bm.audio(\AmplitudeFirst),
			bm.audio(\HRFEGG),
			mTarget,
			\addToTail)
		);

/*		mSynthDiplo = Synth(*VRPSDCSDFT.diplo(
				bm.audio(\ConditionedEGG),
				bm.audio(\GateCycle),
				bm.audio(\AmplitudeFirst),
				bm.audio(\Diplo),
				mTarget,
				\addToTail)
		); */
	} /* start{} */

	// Free the synths and buffers after finishing fetching data.
	// The synths are guaranteed to be paused at this point - so buffers should be stable.
	stop {
		// Free synths
		mSynthHRFEGG.free;
		mSynthQciDEGG.free;
		mSynthDFTFilter.free;
		mSynthNDFTs.free;
		mSynthCycleSeparation.free;

		// And buffers...
	}

	sync { nil } // Nothing to do, but avoid an empty function (issue #5890)
}