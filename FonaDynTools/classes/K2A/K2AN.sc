// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, 
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
K2AN : UGen {
	*ar {
		| in = 0 |
		^this.multiNew('audio', in);
	}
}