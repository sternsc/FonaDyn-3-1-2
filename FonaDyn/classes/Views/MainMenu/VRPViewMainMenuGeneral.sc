// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

VRPViewMainMenuGeneral {
	var mView;

	// Controls
	var mStaticTextShowAs;
	var mListShowAs;
	var mListShow;

	var mStaticTextOutputDirectory;
	var mButtonBrowseOutputDirectory;
	var mStaticTextOutputDirectoryPath;

	// Holders for the non-modal Settings dialog
	var mButtonSettingsDialog;
	var mbSettingsLoaded;
	var <newSettings, <oldSettings, >bSettingsChanged;
	var bResetLayout;

	*new { | view |
		^super.new.init(view);
	}

	init { | view |
		var b = view.bounds;
		var static_font = Font.new(\Arial, 8, usePointSize: true);
		mView = view;
		bSettingsChanged = false;
		mbSettingsLoaded = false;
		bResetLayout = false;
		oldSettings = ~gVRPMain.mContext.model.settings;
		this addDependant: ~gVRPMain;
		this addDependant: VRPViewMaps.mAdapterUpdate;


		////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////

		mStaticTextShowAs = StaticText(mView, Rect())
		.string_("  Show:")
		.font_(static_font);
		mStaticTextShowAs
		.fixedWidth_(mStaticTextShowAs.sizeHint.width)
		.fixedHeight_(35)
		.stringColor_(Color.white);

		mListShowAs = ListView(mView, Rect())
		.items_([
			"Tiled",
			"Gallery",
			"One graph",
			"All tiled",
			"All gallery"
		]);
		mListShowAs
		.fixedHeight_(mListShowAs.minSizeHint.height/1.6)
		.fixedWidth_(mListShowAs.minSizeHint.width)
		.selectionMode_(\single)
		.value_(0)   			// Start in "Tiled" mode
		.font_(static_font)
		.enterKeyAction_({
			bResetLayout = true;
		});

		mListShow = ListView(mView, Rect())
		.visible_(true)
		.font_(static_font)
		.items_([
			"Voice Field",
			"EGG clusters",
			"Phon clusters",
			"Time Plots",
			"Moving EGG",
			"Signal"
		]);
		mListShow
		.fixedHeight_(mListShow.minSizeHint.height/1.2)
		.fixedWidth_(mListShow.minSizeHint.width*1.2)
		.selectionMode_(\single);

		////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////

		mStaticTextOutputDirectory = StaticText(mView, Rect(0, 0, 100, 0))
		.string_("Output directory:")
		.font_(static_font);
		mStaticTextOutputDirectory
		.fixedWidth_(mStaticTextOutputDirectory.sizeHint.width)
		.fixedHeight_(35)
		.stringColor_(Color.white);

		mButtonBrowseOutputDirectory = Button(mView, Rect(0, 0, 100, 0))
		.resize_(4)
		.states_([["Browse…"]])
		.action_{ |b|
			FileDialog(
				{ | path |
					mStaticTextOutputDirectoryPath.string = path.first;
				},
				nil, 2, 0, // Select a single existing directory
				path: mStaticTextOutputDirectoryPath.string;
			);
		};

		mStaticTextOutputDirectoryPath = TextField(mView, Rect(0, 0, 100, b.height))
		.enabled_(false)
		.visible_(true)
		.string_( thisProcess.platform.recordingsDir )
		.background_(Color.white);

		////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////

		mButtonSettingsDialog = Button(mView, Rect(0, 0, 100, 0))
		.resize_(4)
		.states_([["Settings…"]])
		.action_({
			newSettings = oldSettings.deepCopy;
			VRPSettingsDialog.new(this)
		});

		////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////

		mView.layout = HLayout(
			[mStaticTextShowAs, stretch: 1],
			[mListShowAs, stretch: 1],
			[mListShow, stretch: 1],
			[mStaticTextOutputDirectory, stretch: 1],
			[mButtonBrowseOutputDirectory, stretch: 1],
			[mStaticTextOutputDirectoryPath, stretch: 8],
			[nil, stretch: 2],
			[mButtonSettingsDialog, stretch: 1]
		);
	}

	stash { | settings |
		var gs = settings.general;
		mbSettingsLoaded = true;
		mStaticTextOutputDirectoryPath.string_(gs.output_directory.tr($\\, $/));
	}

	fetch { | settings |
		var gs = settings.general;

		if (settings.waitingForStash, {
			this.stash(settings);
		});

		oldSettings = settings.deepCopy;

		gs.layout = switch( (mListShowAs.value ? 0),
			0, VRPViewMain.layoutGrid,
			1, VRPViewMain.layoutGallery,
			2, VRPViewMain.layoutStack,
			3, VRPViewMain.layoutGridAll,
			4, VRPViewMain.layoutGalleryAll
		);
		if (bResetLayout, {
			gs.layout = gs.layout.neg;
			bResetLayout = false;
		});

		gs.stackType = switch( mListShow.value,
			0, VRPViewMain.stackTypeVRP,
			1, VRPViewMain.stackTypeClusterEGG,
			2, VRPViewMain.stackTypeClusterPhon,
			3, VRPViewMain.stackTypeSampEn,
			4, VRPViewMain.stackTypeMovingEGG,
			5, VRPViewMain.stackTypeSignal
		);

		gs.output_directory = mStaticTextOutputDirectoryPath.string;

		if (bSettingsChanged, {
			// Keep data UNLESS .clarityThreshold has changed
// 			bTempKeepData = settings.io.keepData;
// 			settings.io.keepData = true; // (settings.vrp.clarityThreshold == newSettings.vrp.clarityThreshold);

			settings.csdft.method = newSettings.csdft.method;
			settings.vrp.clarityThreshold = newSettings.vrp.clarityThreshold;
			settings.vrp.wantsContextSave = newSettings.vrp.wantsContextSave;
			settings.io.enabledEGGlisten = newSettings.io.enabledEGGlisten;
			settings.io.enabledWriteLog = newSettings.io.enabledWriteLog;
			settings.io.writeLogFrameRate = newSettings.io.writeLogFrameRate;
			settings.io.keepInputName = newSettings.io.keepInputName;
			settings.io.enabledWriteGates = newSettings.io.enabledWriteGates;
			settings.cluster.suppressGibbs = newSettings.cluster.suppressGibbs;
			settings.io.arrayRecordInputs = newSettings.io.arrayRecordInputs;
			settings.io.enabledRecordExtraChannels = newSettings.io.enabledRecordExtraChannels;
			settings.io.arrayRecordExtraInputs = newSettings.io.arrayRecordExtraInputs;
			settings.io.rateExtraInputs = newSettings.io.rateExtraInputs;
			settings.general.enabledDiagnostics = newSettings.general.enabledDiagnostics;
			settings.general.colorThemeKey = newSettings.general.colorThemeKey;
			settings.general.saveSettingsOnExit = newSettings.general.saveSettingsOnExit;
			// settings.vrp.bSingerMode = newSettings.vrp.bSingerMode;
			// this.changed(this, \splRangeChanged, settings.vrp.bSingerMode);

			settings.general.guiChanged_(true);
			settings.waitingForStash_(true);
			bSettingsChanged = false;
			mbSettingsLoaded = false;
		});
	}

	updateData { | data |
		var dsg = data.settings.general;

		if (bSettingsChanged.not and: (mbSettingsLoaded.not), { oldSettings = data.settings });
		if (data.general.starting, { mButtonSettingsDialog.enabled = false; }); // Disable when starting
		if (data.general.stopping, { mButtonSettingsDialog.enabled = true;  }); // Enable when stopping

		if (dsg.guiChanged, {
			mView.background_(dsg.getThemeColor(\backPanel));
			[mStaticTextShowAs, mStaticTextOutputDirectory].do ({ arg c;
				c.stringColor_(dsg.getThemeColor(\panelText))}
			);
		});
		mListShow.visible_(mListShowAs.value == (VRPViewMain.layoutStack-1));  // only for "one graph"
	}

	close {
		this.release;
	}
}