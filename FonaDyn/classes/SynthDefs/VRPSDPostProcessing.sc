// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
VRPSDPostProcessing {
	classvar nameDelay = \sdDelay;

	*compile { | libname, maxdelayed |
		maxdelayed = maxdelayed.asInteger;

		SynthDef(nameDelay,
			{ | aiBusGateIn, // When data should be stored for being delayed
				aiBusGateOut, // When the data should be read and written to the output buses
				ciBusClarity,
				ciBusFrequency,
				ciBusAmplitude,
				ciBusCrest,
				ciBusSpecBal,
				aoBusDelayedClarity,
				aoBusDelayedFrequency,
				aoBusDelayedAmplitude,
				aoBusDelayedCrest,
				aoBusDelayedSpecBal |

				// NOTE: We need audio rate output even if we have control rate input:
				// Let gatein be open twice over two consecutive control cycles, once
				// in the first and once more in the second. It is then possible that
				// the output gate is shifted so that both of these are written in the same
				// control cycle. With a control rate output, these would overwrite eachother,
				// which means they may have different results in the input, while they appear
				// to have the same output in the output.
				var gatein = In.ar(aiBusGateIn);
				var gateout = In.ar(aiBusGateOut);
				var clarity = In.kr(ciBusClarity);
				var freq = In.kr(ciBusFrequency);
				var amp = In.kr(ciBusAmplitude);
				var crest = In.kr(ciBusCrest);
				var specBal = In.kr(ciBusSpecBal);

				[
					[aoBusDelayedClarity, clarity],
					[aoBusDelayedFrequency, freq],
					[aoBusDelayedAmplitude, amp],
					[aoBusDelayedCrest, crest],
					[aoBusDelayedSpecBal, specBal]
				]
				do: { | pair |
					var bus, in;
					#bus, in = pair;
					// Use Gate.ar to hold the values on the output buses,
					// in case we want isochronous output for VRPSDIO.writeLog
					Out.ar(bus, Gate.ar(VariadicDelay.ar(in, gatein, gateout, maxdelayed).asArray, gateout));
				};
			}
		).add(libname);
	}

	*delay { |
		aiBusGateIn,  // When data should be stored for being delayed
		aiBusGateOut, // When the data should be read and written to the output buses
		ciBusClarity,
		ciBusFrequency,
		ciBusAmplitude,
		ciBusCrest,
		ciBusSpecBal,
		aoBusDelayedClarity,
		aoBusDelayedFrequency,
		aoBusDelayedAmplitude,
		aoBusDelayedCrest,
		aoBusDelayedSpecBal
		...args |

		^Array.with(nameDelay,
			[
				\aiBusGateIn, aiBusGateIn,
				\aiBusGateOut, aiBusGateOut,
				\ciBusClarity, ciBusClarity,
				\ciBusFrequency, ciBusFrequency,
				\ciBusAmplitude, ciBusAmplitude,
				\ciBusCrest, ciBusCrest,
				\ciBusSpecBal, ciBusSpecBal,
				\aoBusDelayedClarity, aoBusDelayedClarity,
				\aoBusDelayedFrequency, aoBusDelayedFrequency,
				\aoBusDelayedAmplitude, aoBusDelayedAmplitude,
				\aoBusDelayedCrest, aoBusDelayedCrest,
				\aoBusDelayedSpecBal, aoBusDelayedSpecBal
			],
			*args
		);
	}
}