// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, 
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
// Copyright (C) 2016-2023 by Sten Ternström & Dennis J. Johansson,
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
// Copyright (C) 2016-2022 by Dennis J. Johansson,
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
DrawableSparseMatrix {
	var mPalette;  // function from VRPMetric
	var <mValues; // Triplets of x, y, value
	var mNewest; // Triplets of x, y, value

	var <mIdx; // Index of the triplet at pos x, y, or nil
	var <mVal; // The actual matrix - not used for drawing
	var <latest; // the latest value added
	var mCols;
	var mRows;
	var mbFullRedrawNeeded;
	var <>mbActive; // True if "this" is being displayed
	var <>refDensity;
	var <thresholdCount;
	var <histogram;		// array of relative occurrences (cells)

	*new { | rows, cols, palette |
		^super.new.init(rows, cols, palette);
	}

	init { | rows, cols, palette |
		var mmCols = cols.neg;
		mCols = cols;
		mRows = rows;
		mPalette = palette;
		mbFullRedrawNeeded = true;
		mbActive = false;
		refDensity = nil;
		thresholdCount = 1;

		mValues = List(cols*rows*0.25);    // avoid re-allocations until the List becomes really big
		mNewest = IdentityDictionary.new(20);
		mIdx = Array.fill(mRows * mCols);
		mVal = Array.fill(mRows * mCols);
		histogram = [];
	}

	////////  Methods that apply to all layers //////////////

	dsm { | index=0 |	// for compatibility with VRPDataClusterMaps
		if (index != 0, "invalid indexed access to a DrawableSparseMatrix".error );
		^this
	}

	thresholdCount_ { arg count;
		if (thresholdCount != count, { histogram = [] });
		thresholdCount = count;
	}

	at { | row, col |
		^mVal[ row * mCols + col ]
	}

	atIndex { | idx |
		^mVal[ idx ]
	}

	rows { ^mRows }
	columns { ^mCols }
	size { ^mValues.size }

	put { | row, col, value |
		var idx = row * mCols + col;
		var pos = mIdx[ idx ];
		if ( pos.isNil,
			{
				mIdx[ idx ] = mValues.size;
				mValues.add( [row, col, value] );
			}, {
				mValues[pos][2] = value;
		});
		mVal[ idx ] = value;
		latest = value;
		mbActive.if { mNewest.put(idx.asSymbol, mValues[mIdx[ idx ]]) } ;
	}

	mark { | row, col |
		var idx = row * mCols + col;
		var pos = mIdx[ idx ];
		if ( pos.isNil,
			{
				mIdx[ idx ] = mValues.size;
				mValues.add( [row, col, 1] );
				latest = 1;
			}
		);
		mVal[ idx ] = 1;
		// mbActive.if { mNewest.put(idx.asSymbol, mValues[mIdx[ idx ]]) } ;
	}

	////////  Methods that apply only to layers that are not in a VRPClusterMap //////////////
	//  Each value is a single number

	increment { | row, col, value |
		var idx = row * mCols + col;
		var pos = mIdx[ idx ];
		var incVal;
		if ( pos.isNil,
			{
				mIdx[ idx ] = mValues.size;
				mValues.add( [row, col, value] );
				incVal = value;
			}, {
				incVal = mValues[pos][2] + value;
				mValues[pos][2] = incVal;
		});
		mVal[ idx ] = incVal;
		latest = incVal;
		mbActive.if { mNewest.put(idx.asSymbol, mValues[mIdx[ idx ]]) } ;
		^incVal
	}

	putMean { | pmArgs, value |
		var mean, row, col, idx, pos;
		row = pmArgs[0];
		col = pmArgs[1];
		idx = row * mCols + col;
		pos = mIdx[ idx ];
		mean = ((mVal[ idx ] ? 0) * pmArgs[2] + value) * pmArgs[3];
		latest = value;
		if ( pos.isNil,
			{
				mIdx[ idx ] = mValues.size;
				mValues.add( [row, col, mean] );
			}, {
				mValues[pos][2] = mean;
		});
		mVal[ idx ] = mean;
		mbActive.if { mNewest.put(idx.asSymbol, mValues[mIdx[ idx ]]) } ;
	}

	////////  Methods that apply only to layers that are in a VRPClusterMap //////////////
	////////  Each value is a two-element vector /////////////////////////////////////////

	// Only when nCluster > 0
	addPercent { | row, col, cycles, totReciprocal |
		var idx = row * mCols + col;
		var pos = mIdx[ idx ];
		var c, incVal;
		if ( pos.isNil,
			{
				mIdx[ idx ] = mValues.size;
				incVal = [ 100.0, cycles ];
				mValues.add( [row, col, incVal] );
			}, {
				c = mValues[pos][2][1] + cycles;
				incVal = [100.0*c*totReciprocal, c];
				mValues[pos][2] = incVal;
		});
		mVal[ idx ] = incVal;
		latest = incVal;
		mbActive.if { mNewest.put(idx.asSymbol, mValues[mIdx[ idx ]]) } ;
		^incVal
	}

	// Only when nCluster > 0
	renumber { arg newOrder;
		var index, value, nc;
		mValues.do ( { | t, pos |
			index = t[0]*mCols + t[1];
			value = mVal[index];
			if (value.class == Array, {
				nc = newOrder[value[0]];
				mVal[index][0] = nc;
				mValues[pos][2] = [nc, value[1]];
			} )
		});
	}

	/////// Drawing the layer's data /////////////

	draw { | userView |
		var b = userView.bounds;
		var r = Rect(-0.5, -0.5, 1, 1);
		var drawList;

		if (mbFullRedrawNeeded, {
			drawList = mValues;
			mbFullRedrawNeeded = false;
		},{
			drawList = mNewest.values;
		});

		Pen.use {
			Pen.scale(b.width / (mCols-1), b.height / (mRows-1));
			Pen.width_(0);
			Pen.smoothing_(false);
			// If thresholding, we need first to check the total of cycles in each cell
			if (thresholdCount>1 and: refDensity.notNil, {
				drawList do: { | triplet |
					var x, y, value;
					#y, x, value = triplet;
					if (refDensity.at(y, x) >= thresholdCount, {
						Pen.translate(x, y);
						Pen.fillColor = mPalette.(value);
						Pen.fillRect(r);
						Pen.translate(x.neg, y.neg);
					})
			}},{
				// If not thresholding, just draw all cells
				drawList do: { | triplet |
					var x, y, value;
					#y, x, value = triplet;
					Pen.translate(x, y);
					Pen.fillColor = mPalette.(value);
					Pen.fillRect(r);
					Pen.translate(x.neg, y.neg);
			}});
		};
		mNewest.clear;
	}

	drawUnderlap { | userView |
		var b = userView.bounds;
		// var drawList;
		var clrWhite = Color.white;

		// drawList = mValues;
		Pen.use {
			Pen.scale(b.width / (mCols-1), b.height / (mRows-1));
			Pen.width_(0.15);
			Pen.smoothing_(true);
			// Draw a diagonal hatch pattern with both colored and white lines for contrast
			mValues do: { | triplet |
				var x, y, value;
				#y, x, value = triplet;
				Pen.translate(x, y);
				Pen.strokeColor = mPalette.(value);
				Pen.line(-0.5@(-0.45), 0.45@0.5);
				Pen.stroke;
				Pen.strokeColor = clrWhite;
				Pen.line(0.5@0.45, (-0.45@(-0.5)));
				Pen.stroke;
				Pen.translate(x.neg, y.neg);
			}
		};
		mNewest.clear;
	}

	setPalette { arg palette;
		mPalette = palette;
	}

	invalidate {
		mbFullRedrawNeeded = true;
	}

	makeHistogram { arg nBins, minBin, maxBin, warpType;
		var binNo, n, pWarp;
		var cFunc;

		n = mValues.size;
		if (mValues[0].isNil, { ^nil });
		if (mValues[0][2].isKindOf(Array),
			{ cFunc = { | v | v[0] + 1 } },
			{ cFunc = { | v | v } }
		);
		nBins = nBins.floor;
		histogram = Array.fill(nBins, 0.0);
		(warpType == \lin).if { pWarp = 'linlin' } { pWarp = 'explin' };
		if (refDensity.isNil, {
			mValues do: { | val, ix |
				binNo = cFunc.(val[2]).perform(pWarp, minBin, maxBin, 0, nBins-1, \minmax).asInteger;
				histogram[binNo] = histogram[binNo] + 1.0;
			}
		}, {
			mValues do: { | val, ix |
				if (refDensity.at(val[0], val[1]) >= thresholdCount, {
					binNo = cFunc.(val[2]).perform(pWarp, minBin, maxBin, 0, nBins-1, \minmax).asInteger;
					histogram[binNo] = histogram[binNo] + 1.0;
				});
		}});
		if (n > 0, { histogram = histogram / n }, { histogram = [] });
	}

	// Mark the cells that are populated in target XOR reference
	mapUnderlap { arg target, reference;
		var r, t, d, diff, row, col;
		target.mValues do: { |v, i|
			row = v[0];
			col = v[1];
			// t = target.at(row, col);
			r = reference.at(row, col);
			if (r.isNil, {
				this.put(row, col, 1);  // +1 means only in target (NOW)
			});
		};
		reference.mValues do: { |v, i|
			row = v[0];
			col = v[1];
			t = target.at(row, col);
			// r = reference.at(row, col);
			if (t.isNil, {
				this.put(row, col, -1);	// -1 means only in reference (BEFORE)
			});
		};
	}

	setDiffs{ arg target, reference, index=nil;
		var r, t, d, diff, row, col;
		if (index.isNil, {
			target.mValues do: { |v, i|
				row = v[0];
				col = v[1];
				t = target.at(row, col);
				r = reference.at(row, col);
				if (r.notNil /* and: { t.notNil } */, {
					diff = t-r;
					this.put(row, col, diff);
				});
			};
		}, {
			target.mValues do: { |v, i|
				row = v[0];
				col = v[1];
				t = target.at(row, col);
				r = reference.at(row, col);
				if (r.notNil /* and: { t.notNil } */, {
					diff = t[index] - r[index];
					this.put(row, col, diff);
				});
			};
		});
	}

	setMins{ arg target, reference;
		var r, t, d, min, row, col;
		target.mValues do: { |v, i|
			row = v[0];
			col = v[1];
			t = target.at(row, col);
			r = reference.at(row, col);
			if (r.notNil /* and: { t.notNil } */, {
				min = min(t, r);
				this.put(row, col, min);
			});
		};
	}

	setRatios{ arg target, reference, index=nil;
		var r, t, d, ratio, row, col;
		if (index.isNil, {
			target.mValues do: { |v, i|
				row = v[0];
				col = v[1];
				t = target.at(row, col);
				r = reference.at(row, col);
				if (r.notNil /* and: { t.notNil } */, {
					ratio = t / r;
					this.put(row, col, ratio);
				});
			};
		}, {
			target.mValues do: { |v, i|
				row = v[0];
				col = v[1];
				t = target.at(row, col);
				r = reference.at(row, col);
				if (r.notNil /* and: { t.notNil } */, {
					ratio = t[index] / r[index];
					this.put(row, col, ratio);
				});
			};
		});
	}

	fnFilter { | slice, kn |
		var aSum = 0.0;
		var kSum = 0.0;
		var bOK = false;
		var src = slice;
		var ret_val = nil;

		src do: { | v, ix |
			aSum = aSum + (kn[ix] * (v ? 0.0));
			kSum = kSum + (kn[ix] * v.notNil.asInteger.asFloat);
			if ((ix == 4) and: v.notNil, { bOK = true });
			// Check that the current cell has >=2 opposing neighbours
			if (bOK.not and: { src[ix].notNil.and(src[8-ix].notNil) }, { bOK = true } );
		};

		if (bOK and: { kSum > 0.0 }, {
			ret_val = aSum / kSum;
		});
		^ret_val
	}

	// Make "this" an interpolated and smoothed copy of "source".
	// "kernel" must be a 3x3 matrix of weights
	// Empty cells with opposing non-empty neighbours are interpolated
	interpolateSmooth { arg source, kernel;
		var kn, offsets;
		kn = kernel.flatten;

		// Most elements of .mVal will be nil,
		// but fnFilter will deal with that.
		source.mVal do: ({ | v, ix |
			var col = ix.mod(mCols);
			var row = (ix/mCols).trunc;
			var slice, smoothedValue;
			slice = [
				source.at(row-1, col-1), source.at(row-1, col), source.at(row-1, col+1),
				source.at(row,   col-1), source.at(row,   col), source.at(row,   col+1),
				source.at(row+1, col-1), source.at(row+1, col), source.at(row+1, col+1)
			];
			smoothedValue = this.fnFilter(slice, kn);
			if (smoothedValue.notNil, {	this.put(row, col, smoothedValue) });
		});

	} /* .interpolateSmooth */

}