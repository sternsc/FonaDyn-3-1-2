// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, 
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
// This class creates an object that remaps colors
// to one of several alternative schemes

ReColorBlind {
	classvar myType;  // one of \normal, \typeIBM, \typeWong, \typeTol

	var typeIBM = #[
		[255,  76,   0],
		[254,  97,   0],
		[220,  38, 127],
		[120,  94, 240],
		[100, 143, 255]
	];

	var typeWong = #[
		[0, 158, 115],
		[0,	114, 178],
		[86, 180, 233],
		[204, 121, 167],
		[240, 228, 66],
		[230, 159, 0],
		[213, 94, 0],
		[30, 30, 30]
	];

	var typeTol = #[
		[51, 34, 136],
		[17, 119, 51],
		[68, 170, 153],
		[136, 204, 238],
		[221, 204, 119],
		[204, 102, 119],
		[170, 68, 153],
		[136, 34, 85]
	];

	*new { arg type;
		^super.new.init(type);
	}

	init { arg type;
		myType = type;
	}

	// The Object::blend method will linearly interpolate in the tables
	reColorPaletteFunc {
	}
}