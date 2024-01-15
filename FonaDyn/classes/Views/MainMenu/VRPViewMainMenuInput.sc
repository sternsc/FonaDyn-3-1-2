// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
VRPViewMainMenuInput {
	var mView;

	// States
	var mClipping;
	var mClockUpdate;
	var mClockStep;

	// Controls
	var mCheckBoxKeepData;
	var mButtonStart;
	var mButtonPause;
	var mSetFocusPause;

	var mStaticTextInput;
	var mListInputType;

	var mButtonCalibrate;

	var mButtonBrowse;
	var mStaticTextFilePath;

	var mButtonBrowse2;
	var mStaticTextScriptPath;

	var mUserViewClipping;
	var mFontClipping;

	var mButtonAddFilePath;
	var mButtonRemoveFilePath;
	var mListBatchedFilePaths;
	var mStaticTextClock;

	var mScriptLines;
	var mScriptLineIndex;
	var mScriptState;
	var mScriptLastPath;
	var mScriptData; // needed for saving maps and cluster data

	// States
	var mLastPath;
	var mThisIndex; // Index into the batched file paths that is being played
	var mPauseNow;
	var mFileChanged;

	classvar canStart = 0;
	classvar setStart = 1;
	classvar waitingForStart = 2;

	classvar canStop = 3;
	classvar setStop = 4;
	classvar waitingForStop = 5;

	classvar fromRecording = 0;
	classvar fromSingleFile = 1;
	classvar fromMultipleFiles = 2;
	classvar fromScript = 3;
	classvar <initScript;

	classvar iNone = 0;			// Not running from a script
	classvar iLoaded = 1;		// A script is loaded but not read
	classvar iReady = 2;		// Ready to continue in script
	classvar iHeld = 3;			// Waiting for user to press START
	classvar iOKtoRun = 4;		// Waiting for .updateData to press START
	classvar iRunning = 5;		// Waiting for this RUN to complete
	classvar iFileDone = 6;		// This RUN has completed
	classvar iFinished = 7;		// Ran to completion for one whole script
	classvar iAborted = 8;		// An error was detected, script aborted


	*new { | view |
		^super.new.init(view);
	}

	*configureInitScript { arg path;
		if (path.isString and:  { File.exists(path) },
			{ initScript = path;
		});
	}

	init { | view |
		var b = view.bounds;
		var static_font = Font.new(\Arial, 8, usePointSize: true);
		mView = view;
		mLastPath = thisProcess.platform.recordingsDir;
		mScriptLastPath = Platform.userAppSupportDir;
		mSetFocusPause = false;
		mFileChanged = false;
		mScriptLines = [];
		mScriptLineIndex = 0;
		this addDependant: ~gVRPMain;
		this.scriptState_(iNone);
		view onClose: { this.close } ;

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mStaticTextInput = StaticText(mView, Rect(0, 0, 100, 0))
		.string_("Source:")
		.font_(static_font);
		mStaticTextInput
		.fixedWidth_(mStaticTextInput.sizeHint.width)
		.fixedHeight_(35)
		.stringColor_(Color.white);

		mListInputType = ListView(mView, Rect())		// (0, 0, 100, 0)
		.items_([
			"Live signals",
			"From file",
			"Batch multiple files",
			"Run script"
		])
		.font_(static_font);

		mListInputType
		.fixedHeight_(mListInputType.minSizeHint.height * 0.65)
		.fixedWidth_(mListInputType.minSizeHint.width * 1.5)
		.selectionMode_(\single)
		.action_({ | list |
			var is_recording = list.value == fromRecording;
			var is_from_file = list.value == fromSingleFile;
			var is_from_batched_files = list.value == fromMultipleFiles;
			var is_from_script = list.value == fromScript;

			// Make the appropriate controls visible according to the Source mode
			mButtonBrowse.visible_(is_from_file);
			mStaticTextFilePath.visible_(is_from_file);
			mButtonBrowse2.visible_(is_from_script);
			mStaticTextScriptPath.visible_(is_from_script);
			mButtonCalibrate.visible_(is_recording);
			mUserViewClipping.visible_(is_recording);
			mButtonAddFilePath.visible_(is_from_batched_files);
			mButtonRemoveFilePath.visible_(is_from_batched_files);
			mListBatchedFilePaths.visible_(is_from_batched_files);

			this.updateMenu();
		});

		//////////////////////////////////////////////////////////

		mButtonCalibrate = Button(mView, Rect(0, 0, 100, b.height))
		.visible_(false)
		.states_([["Calibrate…"]])
		.action_({ | btn |
			FonaDyn.calibrate();
		});

		//////////////////////////////////////////////////////////

		mButtonBrowse = Button(mView, Rect(0, 0, 100, b.height))
		.visible_(false)
		.states_([["Browse…"]])
		.action_({ | btn |
			mFileChanged = false;
			VRPMain.openPanelPauseGUI({ | path |
					mLastPath = PathName.new(path).pathOnly;

					// A file at path was selected
					mStaticTextFilePath.string_(path);
					mFileChanged = true;

					// Make sure the start button is enabled.
					mButtonStart.enabled_(true);
			}, nil, path: mLastPath);
		})
		.canReceiveDragHandler_({ |v| v.class.prClearCurrentDrag; });

		// Disabled TextField to easily present the path.
		mStaticTextFilePath = TextField(mView, Rect(0, 0, 100, b.height))
		.keyDownAction_({ true })  // Don't let the user enter file names manually
		.visible_(false)
		.background_(Color.white)
		.canReceiveDragHandler_({|v, x, y|
			var str, bOK = false;
			v.enabled_(true);
			str = v.class.currentDrag;
			if (str.class == String, {
				if (str.toLower.endsWith("_voice_egg.wav"), {
					bOK = true;
				} , {
					format("Filename issue: %", PathName(str).fileName).warn;
				})
			} );
			bOK
		})
		.receiveDragHandler_({|v, x, y|
			var str;
			str = v.class.currentDrag;
			mStaticTextFilePath.string_(str);
			mLastPath = PathName.new(str).pathOnly;
			mButtonStart.enabled_(true);
		});

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mButtonBrowse2 = Button(mView, Rect(0, 0, 100, b.height))
		.visible_(false)
		.states_([["Select script…"]])
		.action_({ | btn |
			VRPMain.openPanelPauseGUI(
				{ | path |
					mScriptLastPath = PathName.new(path).pathOnly;

					// A file at path was selected
					mStaticTextScriptPath.string_(path);
					this.loadScript(path);

					// Make sure the start button is enabled.
					mButtonStart.enabled_(true);
			}, path: mScriptLastPath);
		});

		// Enabled TextField to easily present the script path.
		mStaticTextScriptPath = TextField(mView, Rect(0, 0, 100, b.height))
		.enabled_(false)
		.visible_(false)
		.background_(Color.white);

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mClipping = 0;
		mFontClipping = Font.new(\Arial, 24, true, usePointSize: true);

		mUserViewClipping = StaticText(mView, Rect());    // 0, 0, 100, 30
		mUserViewClipping
		.font_(mFontClipping)
		.stringColor_(Color.red)
		.background_(Color.black)
		.string("EGG CLIPPING");
		mUserViewClipping
		.fixedHeight_(mListInputType.bounds.height)
		.fixedWidth_(mListInputType.bounds.width * 3);

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mButtonAddFilePath = Button(mView, Rect())
		.visible_(false)
		.states_([
			["Add File/-s…"]
		]);
		mButtonAddFilePath
		.fixedSize_(mButtonAddFilePath.sizeHint)
		.action_( { | btn |
			VRPMain.openPanelPauseGUI(
				{ | paths |
					mListBatchedFilePaths.items_(
						(mListBatchedFilePaths.items ? [])
						++
						paths
					);

					mButtonStart.enabled_(
						(mListBatchedFilePaths.items ? []).isEmpty.not
					);
					mThisIndex = (mListBatchedFilePaths.items ?? []).size;
				},
				multipleSelection: true,
				path: mLastPath;
			);
		});

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mButtonRemoveFilePath = Button(mView, Rect())
		.visible_(false)
		.states_([
			["Remove File/-s…"]
		]);
		mButtonRemoveFilePath
		.fixedSize_(mButtonRemoveFilePath.sizeHint)
		.action_{ | btn |
			var s = mListBatchedFilePaths.selection;
			var items = mListBatchedFilePaths.items ? [];
			mListBatchedFilePaths
			.selection_([])
			.items_(
				items[ Array.iota(items.size).difference(s) ]
			);

			mButtonStart.enabled_(
				(mListBatchedFilePaths.items ? []).isEmpty.not
			);
		};

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mThisIndex = 0;
		mPauseNow = false;

		mListBatchedFilePaths = ListView(mView, Rect())
		.visible_(false)
		.maxHeight_(120)
		.selectionMode_(\extended);

		mListBatchedFilePaths
		.canReceiveDragHandler_({|v, x, y|
			var dragged, strs, bOK = true;
			v.enabled_(true);
			dragged = v.class.currentDrag;
			strs = (if (dragged.class == String, { Array.with(dragged) } , { dragged }));
			strs do: { |s, i| if (s.class == String, {
				if (s.toLower.endsWith("_voice_egg.wav").not, {
					format("Invalid signal file name: %", PathName(s).fileName).warn;
					bOK = false;
				})
			} , { bOK = false} ) };
			bOK
		})
		.receiveDragHandler_({|v, x, y|
			var dragged, strs;
			dragged = v.class.currentDrag;
			strs = (if (dragged.class == String, { Array.with(dragged) } , { dragged }));
			mListBatchedFilePaths.items_(
				(mListBatchedFilePaths.items ? []) ++ strs
			);
			mLastPath = PathName.new(strs[0]).pathOnly;
			mButtonStart.enabled_(
				(mListBatchedFilePaths.items ? []).isEmpty.not
			);
			mThisIndex = (mListBatchedFilePaths.items ?? []).size;
		});

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mCheckBoxKeepData = CheckBox(mView, Rect(0, 0, 100, b.height), "Keep data")
		.visible_(true)
		.value_(false)
		.font_(static_font);

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mButtonStart = Button(mView, Rect(0, 0, 100, b.height))
		.states_([
			["► START"],     // Can start
			["Starting.. "], // Starting..  (not dispatched)
			["Starting..."], // Starting... (dispatched - waiting for completion)
			["▐▌ STOP"],     // Can stop
			["Stopping.. "], // Stopping..  (not dispatched)
			["Stopping..."]  // Stopping... (dispatched - waiting for completion)
		]);
		mButtonStart
		.fixedWidth_(mButtonStart.sizeHint.width)
		.fixedHeight_(mButtonStart.sizeHint.height * 2)
		.action_{ | btn |
			switch (btn.value,
				setStart, {
					mThisIndex = mListBatchedFilePaths.selection.first ? 0;
					mClockUpdate = 0;
					mClockStep = 1;
					mSetFocusPause = true;
				},

				setStop, {
					mThisIndex = (mListBatchedFilePaths.items ?? []).size;
					mClockStep = 0;
				}
			);

			this.updateMenu();
		};

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		// The Pause button
		mButtonPause = Button(mView, Rect(0, 0, 100, b.height))
		.states_([
			[ "▐▐  Pause" ],
			[ "► Resume" ]
		]);
		mButtonPause
		.fixedWidth_(mButtonPause.sizeHint.width)
		.fixedHeight_(mButtonPause.sizeHint.height * 2)
		.enabled_(false)
		.action_({ |b|
			mPauseNow = true;
			if (b.value == 1, { mClockStep = 0}, { mClockStep = 1} );
		})
		.visible_(true);

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mClockUpdate = 0;
		mStaticTextClock = StaticText(mView, Rect())
		.visible_(true)
		.font_(Font(\Arial, 32, true, false, true))
		.stringColor_(Color.gray)
		.align_(\right);
		mStaticTextClock
		.fixedWidth_(mStaticTextClock.sizeHint.width*5);

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////

		mView.layout = HLayout(
			[mStaticTextInput, stretch: 1],
			[mListInputType, stretch: 1],
			[mButtonCalibrate, stretch: 1],
			[mButtonBrowse, stretch: 1],
			[mStaticTextFilePath, stretch: 20],
			[mButtonBrowse2, stretch: 1],
			[mStaticTextScriptPath, stretch: 20],
			[mUserViewClipping, stretch: 1],
			[mButtonAddFilePath, stretch: 1],
			[mButtonRemoveFilePath, stretch: 1],
			[mListBatchedFilePaths, stretch: 20],
			[nil, stretch: 3],
			[mCheckBoxKeepData, stretch: 0, align:\right],
			[mButtonStart, stretch: 0, align:\right],
			[mButtonPause, stretch: 0, align:\right],
			[40],
			[mStaticTextClock, stretch: 1, align:\right]
		);

		mListInputType.valueAction_(fromSingleFile);   // default mode
		this addDependant: VRPViewMaps.mAdapterUpdate;  // class VRPViewMaps is not init'ed yet...
		this addDependant: ~gVRPMain;

	} /* init */

	updateMenu {
		var not_started = mButtonStart.value == canStart;
		mListInputType.enabled_(not_started);
		mButtonAddFilePath.enabled_(not_started);
		mButtonRemoveFilePath.enabled_(not_started);
		// mListBatchedFilePaths.enabled_(not_started);

		mButtonStart.enabled_(
			(mButtonStart.value == canStart)
			or:
			(mButtonStart.value == canStop);
		);

		if ( mButtonStart.value == canStart, {
			// Enable/Disable the start button if we have/haven't chosen an input file!
			switch (mListInputType.value,
				fromSingleFile, { // From single file
					mButtonStart.enabled_(
						mStaticTextFilePath.string.isEmpty.not
					);
				},

				fromMultipleFiles, { // From multiple batched files
					mButtonStart.enabled_(
						(mListBatchedFilePaths.items ? []).isEmpty.not
					);
				}
			);
		});

		mButtonPause.enabled_(mButtonStart.value == canStop);
		if (mButtonPause.enabled and: mSetFocusPause,
			{ mButtonPause.focus(true); mSetFocusPause = false } );

		if (mClipping > 0, {
			mUserViewClipping.string_("EGG CLIPPING");
			mClipping = mClipping - 1;
		},{
			mUserViewClipping.string_("");
		});

	} /* updateMenu */

	stash { | settings |
		var str = settings.io.filePathInput ? "";
		if (str.notEmpty, {
			mStaticTextFilePath.string_(str)
		});
		mLastPath = settings.general.output_directory;
		mCheckBoxKeepData.value_(settings.io.keepData);
	}

	fetch { | settings |
		var ios, gs, cs, ds;

		if (this.scriptState > iNone, {
			this.advanceScript(settings);
		});

		if (settings.general.queueInitScript, {
			this.loadScript(initScript);
			settings.general.queueInitScript = false;
		});

		if (settings.waitingForStash, {
			this.stash(settings);
		});

		ios = settings.io;
		gs = settings.general;
		cs = settings.cluster;
		ds = settings.csdft;

		if ( mButtonStart.value == setStart, {
			gs.start = true;
			mButtonStart.value = waitingForStart;
		});

		if ( mButtonStart.value == setStop, {
			gs.stop = true;
			mButtonStart.value = waitingForStop;
		});

		ios.inputType = switch ( mListInputType.value,
			fromRecording, VRPSettingsIO.inputTypeRecord,
			fromSingleFile, VRPSettingsIO.inputTypeFile,
			fromMultipleFiles, VRPSettingsIO.inputTypeFile,
			fromScript, VRPSettingsIO.inputTypeScript
		);

		ios.filePathInput = switch ( mListInputType.value,
			fromSingleFile, {
				mStaticTextFilePath.string
			},

			fromMultipleFiles, {
				(mListBatchedFilePaths.items ?? [])[mThisIndex]
			},

			fromScript, { "" }  // don't try to open the script text as a signal
		);

		// Check the bit depth of the chosen input file, if any
		if (ios.filePathInput.notNil, {
			var f = SoundFile.new;
			var fn = PathName(ios.filePathInput).fileName;
			var bSingerMode = (VRPDataVRP.nMaxSPL > 120);
			f.openRead(ios.filePathInput);
			// Is the file word length 16 or 24 bits?
			if (f.sampleFormat == "int16" and: { bSingerMode }, {
				format("% is a 16-bit file: switching to 120 dB max SPL", fn).warn;
				this.changed(this, \splRangeChanged, false);
			});
			f.close;
		});

		ios.keepData = mCheckBoxKeepData.value;
		this.updateMenu();
	}

	updateData { | data |
		var iod = data.io;
		var gd = data.general;
		var gs = data.settings.general;

		// Did we previously attempt to start the server?
		//		if ( (mButtonStart.value == waitingForStart) and: gd.starting.not, {
		if ( (mButtonStart.value == waitingForStart) and: gd.started, {
			if (this.scriptState == iHeld, {
				this.scriptState_(iRunning);
			});
			mClockUpdate = 0;
			mClockStep = 1;
			mButtonStart.value_( if (gd.started, canStop, canStart) );
			if (mListInputType.value > fromRecording, {
				var s = PathName(data.settings.io.filePathInput);
				format("Analyzing %, in %", s.fileName, s.pathOnly).postln;
			});
		});

		// Did we previously attempt to stop the server?
		if ( (mButtonStart.value == waitingForStop) and: gd.stopping.not, {
			switch (this.scriptState,
				iRunning,  { this.scriptState_(iAborted) },
				iFileDone, { this.scriptState_(iReady) }
			);
			mButtonStart.value_( if (gd.started, canStop, canStart) );
		});

		// Have we reached eof?
		if ( iod.eof, {
			if (this.scriptState == iRunning, {
				this.scriptState_(iFileDone);
				mScriptData = data;
			});
			if (mButtonStart.value == canStop, {
				mButtonStart.value_( waitingForStop );
				mThisIndex = mThisIndex + 1;
			});
		});

		// If we can start another one...
		if ( mButtonStart.value == canStart, {
			// ...and we're using batching, and still have items left
			// Then immediately start the next file
			if ( (mListInputType.value == fromMultipleFiles)
			and: (mThisIndex < (mListBatchedFilePaths.items ?? []).size), {
				mButtonStart.value_(setStart);
				mListBatchedFilePaths.selection = mThisIndex.asArray;
			});
			// ...and we're running a script,
			// and are ready to process the next file
			// then continue when server has started again
			if (this.scriptState == iOKtoRun, {
				this.scriptState_(iHeld);
				mButtonStart.value_(setStart);
			});
		});

		if ( iod.clip, {
			mClipping = VRPMain.guiUpdateRate;  	// Flash CLIPPING! for one second
			iod.clip = false;
		});

		if (mButtonStart.value == canStart,
			{
				mButtonPause.value = 0;
				mClockStep = 0;
			}
		);

		if ( mPauseNow,  {
			gd.pause = gd.pause + 1;  // advance the pause state
		});
		mPauseNow = false;

		if ((gd.started and: gd.stopping.not)
		and: mClockUpdate.mod(VRPMain.guiUpdateRate) == 0, {
			var d, str;
			d = mClockUpdate.asFloat / VRPMain.guiUpdateRate;
			str = format("%:%",
				   (d/60).floor.asInteger.asString,
				d.mod(60).floor.asInteger.asString.padLeft(2, "0"));
			mStaticTextClock.string_(str);
		});
		mClockUpdate = mClockUpdate + mClockStep;

		// Update the settings
		if (gs.guiChanged, {
			mView.background_(gs.getThemeColor(\backPanel));
			mUserViewClipping.background_(gs.getThemeColor(\backPanel));
			mStaticTextInput.stringColor_(gs.getThemeColor(\panelText));
			// class CheckBox does not implement .stringColor (!!)
			mCheckBoxKeepData.palette = mCheckBoxKeepData.palette.windowText_(gs.getThemeColor(\panelText));
			mCheckBoxKeepData.palette = mCheckBoxKeepData.palette.window_(gs.getThemeColor(\backPanel));
		});

		this.updateMenu();
	}

	//////////////////////////////////////////////////
	//////////////////////////////////////////////////

	loadScript { arg scriptFile;
		// Open the file, and read all lines into an array of strings
		// Lines beginning in lower case are assignments into the .settings object.
		// This is done by interpreting SC code.
		// Specifying § as the column delimiter means that it will interpret almost anything.
		// This is useful, but can be a security problem.
		var tmpArray = FileReader.read(scriptFile, skipEmptyLines: true, delimiter: $§);
		// FileReader brackets each line in [  ] - strip them off
		mScriptLines = tmpArray.collect({|v,i| v[2..(v.size-3)]});
		mScriptLineIndex = 0;
		mScriptData = nil;
		this.scriptState_(iReady);
		format("SCRIPT loaded: %", scriptFile).postln;
	}

	scriptState_{ | v |
		switch (v,
			iFinished, { v = iNone; "END of script".postln 	},
			iAborted,  { v = iNone; "ABORTED script".postln }
		);
		mScriptState = v;
	}

	scriptState {
		^mScriptState;
	}

	advanceScript { | settings |
		var str, fName, t, x, sMod;
		if ( (this.scriptState == iReady)
			 and: ( mScriptLineIndex < mScriptLines.size )
			 and: ( settings.waitingForStash.not ),
			{
				try {
					// get the next loaded line
					t = mScriptLines[mScriptLineIndex].asString; 	// Begins and ends with brackets (!!??)
					str = t[2..(t.size-3)].stripWhiteSpace;      	// Strip brackets and white space
					format("%  %", mScriptLineIndex+1, str).postln;	// Echo line to user
					x = 0;
					if (str[0].isLower, { x = 1 } );
					if (str.beginsWith("HOLD"), { x = 2 } );
					if (str.beginsWith("RUN"),  { x = 3 } );
					if (str.beginsWith("LOAD"), { x = 4 } );
					if (str.beginsWith("SAVE"), { x = 5 } );
					if (str.beginsWith("EVAL"), { x = 6 } );
					switch (x,
						1, {
							settings.edit(str); // Parse the .settings assignment in str
							settings.waitingForStash_(true);  // asynchronous, has problems
						},

						2, {// HOLD
							// Pause parsing, and transfer the new settings to the GUI
							// Execution begins when user presses START
							// Parsing resumes when the input file has run to completion
							this.scriptState_(iHeld);
							settings.waitingForStash_(true);
							mListInputType.valueAction_(fromSingleFile)
						},

						3, {// RUN
							// Pause parsing and transfer any new settings to the GUI
							// Execution continues directly with the next input file
							// Parsing resumes when the input file has run to completion
							settings.waitingForStash_(true);
							this.scriptState_(iOKtoRun);
						},

						4, {	// LOAD _cEGG.csv, _cPhon.csv, or _VRP.csv
							fName = interpret(str[5..]);
							case
							{ VRPDataCluster.testSuffix(fName) } {
								var c, h, sc, tmpDC;
								sc = settings.cluster;
								tmpDC = VRPSettingsCluster.new(sc);
								#c, h = tmpDC.loadClusterSettings(fName);
								if (c < 2) {
									"on reading file".error;
									this.scriptState_(iAborted);
								} { sc.pleaseStashThis_(tmpDC) };
							}

							{ VRPDataClusterPhon.testSuffix(fName) } {
								var c, m, sc, tmpSCP;
								sc = settings.clusterPhon;
								tmpSCP = VRPSettingsClusterPhon.new(sc);
								#c, m = tmpSCP.loadClusterPhonSettings(fName);
								if (c < 2) {
									"on reading file".error;
									this.scriptState_(iAborted);
								} { sc.requestStash(tmpSCP) };
							}

							{ VRPDataVRP.testSuffix(fName) } {
								var c, sv, tmpDV;
								sv = settings.vrp;
								tmpDV = VRPDataVRP.new(nil);
								c = tmpDV.loadVRPdata(fName);
								if (c < 2) {
									"on reading file".error;
									this.scriptState_(iAborted);
								} { sv.setLoadedData(tmpDV) };
								settings.waitingForStash_(true);
							}; /* end case */
						},

						5, {	// SAVE (here only if EOF was reached)
							fName = interpret(str[5..]);
							if (mScriptData.notNil, {
								var ds = mScriptData.settings;

								// The end of the filename given for SAVE selects which data to save
								case
								{ VRPDataVRP.testSuffixSmooth(fName) }
								{ // fName ends in _S_VRP.csv, so smooth the map first, and then save it
									var tempVRPdata = VRPDataVRP.new(ds);
									tempVRPdata.interpolateSmooth(mScriptData.vrp);
									tempVRPdata.saveVRPdata(fName);
								}

								{ VRPDataVRP.testSuffix(fName) }
								{	// Save the map as a _VRP.csv
									mScriptData.vrp.saveVRPdata(fName)
								}

								{ VRPDataCluster.testSuffix(fName) }
								{	// Save the EGG cluster data
									ds.cluster.pointsInCluster = mScriptData.cluster.pointsInCluster;
									ds.cluster.centroids = mScriptData.cluster.centroids;
									ds.cluster.saveClusterSettings(fName)
								}

								{ VRPDataClusterPhon.testSuffix(fName) }
								{	// Save the phonation cluster data
									ds.clusterPhon.pointsInCluster = mScriptData.clusterPhon.pointsInCluster;
									ds.clusterPhon.centroids = mScriptData.clusterPhon.centroids;
									ds.clusterPhon.saveClusterPhonSettings(fName)
								}
							}; ); // case; if
						}, // switch 5

						6, {	// EVAL
							interpret(str[5..]);
						},

					);
					mScriptLineIndex = mScriptLineIndex + 1;
				} // try
				{  | error |
					error.errorString.postln;
					this.scriptState_(iAborted);
				} // on error
		}); // if

		if (mScriptLineIndex >= mScriptLines.size, {
			this.scriptState_(iFinished);
			this.changed(this, \dialogSettings, settings);  // Should not be needed, but...
		});
		^settings
	} /* .advanceScript */

	close {
		this.release;
	}
}

