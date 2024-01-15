// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

VRPViewMaps {
	var <mView;
	var vrpMaps;
	var vrpViews;
	var mLastIndex, mCurrentIndex;
	var mButtons;
	var modeButtons;
	var mTileStackButton;
	var mbStacked;
	var mLayerRequested;
	var iMainLayout;
	var mvLayout, mbHLayout;
	var prSettings;
	classvar <bTileVertically = false;
	classvar <mAdapterUpdate;

	*new { | view |
		^super.new.init(view);
	}

	*configureTiledMaps { arg vertical;
		if (vertical.notNil, { bTileVertically = vertical } );
	}

	init { | view |
		var btn, btn_state;

		modeButtons =
		[ [["now", Color.gray(0.7), Color.gray(0.4)],["NOW", Color.white, Color.gray(0.7)]],
		  [["twin", Color.gray(0.7), Color.green(0.5)], ["TWIN", Color.white, Color.green(0.75)]],
		  [["before", Color.gray(0.7), Color.magenta(0.5)], ["BEFORE", Color.white, Color.magenta(0.75)]],
		  [["diff", Color.gray(0.7), Color.blue(0.65)], ["DIFF", Color.white, Color.blue(0.85)]],
		  [["smooth", Color.gray(0.7), Color.yellow(0.5)], ["SMOOTH", Color.white, Color.yellow(0.8)]]
		];

		mView = view;
		vrpMaps = [];
		vrpViews = [];
		mButtons = [];
		mbStacked = true;
		mvLayout = StackLayout.new([]);
		iMainLayout = 0;
		mLayerRequested = -1;
		prSettings = nil;
		mAdapterUpdate = { | menu, who, what, newValue |
			this.update(menu, who, what, newValue);
		};

		mTileStackButton = Button(mView, Rect());
		mTileStackButton
		.states_( [["□□□"], ["╒══╗"]] )  // Stack off, Stack on
		.value_(mbStacked.asInteger)
		.action_({ |b| this.setStackMode(b.value.asBoolean) });
		this.addMap(VRPViewVRP.iNormal);  // normal map view
		this.setStackMode(true);
	}

	mapExists { | mode |
		var a = [];
		vrpMaps do: ( { | m, i | if (m.mapMode == mode, { a = a.add(mode) })});
		^a.indexOf(mode).notNil
	}

	toggleLayout {
		bTileVertically = bTileVertically.not;
		this.layout;
	}

	toggleMap { | mode |
		var index = nil;
		vrpMaps do: { | m, i | if (m.mapMode == mode, { index = i }) };
		if (index.isNil,
			{
				this.addMap(mode)
			},{
				this.removeMap(index)
			}
		);
		^index.isNil;
	}

	addMap { | mode |
		var nextView, srcVRPmap, btn;

		btn = Button(mView);
		btn
		.states_(modeButtons[mode])
		.action_({|b| this.setActiveTab( b )})
		.maxWidth_(60);

		srcVRPmap = vrpMaps[0];

		if ([VRPViewVRP.iClone, VRPViewVRP.iSmooth].indexOf(mode).notNil,
			{ // Find the iNormal map, for cloning
				vrpMaps do: { | m, i |
					if (m.mapMode == VRPViewVRP.iNormal,
					{ srcVRPmap = vrpMaps[i] })
				};
			}
		);

		nextView = CompositeView(mView, mView.bounds);

		// The map created first is always iNormal
		if (vrpViews.size == 0, { mode = VRPViewVRP.iNormal });

		if (mode != VRPViewVRP.iReference,
		{
			// Most maps are added last (rightmost)
			vrpViews = vrpViews.add (nextView);
			vrpMaps = vrpMaps.add ( VRPViewVRP( nextView, srcVRPmap ) ) ;
			vrpMaps.last.mapMode_(mode);
			vrpMaps.last.setMapHandler(this);
			this.addDependant(vrpMaps.last);
			mLastIndex = vrpMaps.size - 1;
			mCurrentIndex = mLastIndex;
			mButtons = mButtons.add(btn);
		} , {
			// Reference map is always first (leftmost)
			vrpViews = vrpViews.insert (0, nextView);
			vrpMaps = vrpMaps.insert (0, VRPViewVRP( nextView, srcVRPmap )) ;
			vrpMaps.first.mapMode_(mode);
			vrpMaps.first.setMapHandler(this);
			this.addDependant(vrpMaps.first);
			mLastIndex = vrpMaps.size - 1;
			mCurrentIndex = 0;
			mButtons = mButtons.insert(0, btn)
		}
		);

		if (mode == VRPViewVRP.iDiff,
			{   var tempVRPdata, nC;
				// Make a new but empty VRPViewVRP-diff
				tempVRPdata = VRPDataVRP.new(bDiff: true);

				// Set its cluster counts to those of the BEFORE map
				nC = vrpMaps.first.mVRPdata.layers[\ClustersEGG].cCount;
				tempVRPdata.initClusteredLayers(iType: VRPSettings.iClustersEGG, nClusters: nC, bDiffMap: true);
				nC = vrpMaps.first.mVRPdata.layers[\ClustersPhon].cCount;
				tempVRPdata.initClusteredLayers(iType: VRPSettings.iClustersPhon, nClusters: nC, bDiffMap: true);

				vrpMaps.last.mVRPdata = tempVRPdata;
			    vrpMaps.last.computeDiffs(vrpMaps[0].mVRPdata, vrpMaps[1].mVRPdata);  // We need safeguards for this
			}
		);

		if (mode == VRPViewVRP.iSmooth,
			{
				// Make a new but empty VRPViewVRP
				// The prSettings are needed to initialize the number of clusters
				vrpMaps.last.mVRPdata = VRPDataVRP.new(prSettings);
				vrpMaps.last.interpolateSmooth(srcVRPmap.mVRPdata);
			}
		);

		btn.valueAction_(1);
		this.layout;
	} /* addMap */

	removeMap { | index=nil |
		index = index ?? { vrpViews.size - 1 };
		if (vrpViews.size > 1, {
			this.removeDependant(vrpMaps[index]);
			this.changed(this, \mapWasDeleted, vrpMaps[index]);
			vrpMaps.removeAt(index).close;
			vrpViews.removeAt(index).remove;
			mButtons.removeAt(index).remove;
			vrpMaps do: { |m, i|
				if (m.mapMode == VRPViewVRP.iNormal, {
					this.setActiveTab(mButtons[i])
				});
			};
		});
		this.layout;
	}

	layout {
		var theLayout;

		if (mbStacked,
			{
				mvLayout = StackLayout.new(*vrpViews);
				mvLayout.index = mCurrentIndex;
			}, {
				if (bTileVertically
					and: { [VRPViewMain.layoutGallery, VRPViewMain.layoutGalleryAll].indexOf(iMainLayout).isNil },
					{
						mvLayout = VLayout.new(*vrpViews);
						mTileStackButton.states_([["╞══╡"], ["╒══╗"]]);
					}, {
						mvLayout = HLayout.new(*vrpViews);
						mTileStackButton.states_([["□□□"], ["╒══╗"]]);
					}
				);
			vrpViews do: { |v| v.visible_(true) }; // StackLayout changes .visible
			}
		);

		if (vrpViews.size > 1, {
			mbHLayout = HLayout.new([mTileStackButton, stretch: 0, align: \left]);
			mButtons do: { |b, i| mbHLayout.add(b, stretch: 0, align: \left)};
			mbHLayout.add(nil, stretch: 10);
			theLayout = VLayout.new([mbHLayout, align: \left], mvLayout);
			mTileStackButton.visible_(true);
		}, {
			mTileStackButton.visible_(false);
			theLayout = mvLayout;
		});

		theLayout
		.margins_(0)
		.spacing_(0);

		mView.layout = theLayout;
		mView.refresh;
	}

	setActiveTab { | btn |
		mCurrentIndex = mButtons.indexOf(btn);
		if (mbStacked, { mvLayout.index = mCurrentIndex });
	}

	setStackMode { | bStacked |
		mbStacked = bStacked;
		this.layout;
	}

	stash { | settings |
		mTileStackButton.value_(mbStacked);
		vrpMaps do: { | x |
			x.stash(settings);
		};
		mView.visible_(settings.vrp.isVisible);
	}

	fetch { | settings |
		mbStacked = mTileStackButton.value.asBoolean;
		settings.vrp.isVisible = mView.visible;
		if (iMainLayout != settings.general.layout, 	// User changed the main layout
			{
				iMainLayout = settings.general.layout.abs;
				this.layout;
			}
		);
		vrpMaps do: { | x |
			x.fetch(settings);
		};
		prSettings = settings;
	}

	update { | menu, who, whatHappened, newValue |
		if (whatHappened == \selectLayer, {
			// Don't propagate this change, because it would cause recursion;
			// instead ask the other maps to switch layer.
			if (vrpMaps.indexOf(who) == mCurrentIndex
				and: { who.mapMode != VRPViewVRP.iClone} , {
					mLayerRequested = newValue
			})
		}, {
			// Propagate all other changes to the current maps
			this.changed(who, whatHappened, newValue);
		});
	}

	updateData { | data |
		var dsg = data.settings.general;

		// Emulate radio button behaviour
		mButtons do: ({ |b, i|
			b.value_((i==mCurrentIndex).asInteger);
			b.visible_(vrpViews.size > 1);
		});

		if (mLayerRequested >= 0, {
			vrpMaps do: { | map, i |
				if ((map.mapMode != VRPViewVRP.iClone).and(i != mCurrentIndex),
					{	map.setLayer(mLayerRequested) } );
			};
			mLayerRequested = -1;
		});

		if (dsg.guiChanged, {
			mView.background_(dsg.getThemeColor(\backPanel));
		});

		if (mbStacked or: bTileVertically, { data.vrp.mapsWidth = 1 },
			{
				data.vrp.mapsWidth = vrpMaps.size
			}
		);

		vrpMaps do: { | x, i |
			x.updateData(data);
		};
	}

	close {
		vrpViews do: { | x |
			x.close;
		};
		this.release;
	}
}