// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

/*
// FonaDyn (C) Sten Ternström, Dennis Johansson 2016-2024
// KTH Royal Institute of Technology, Stockholm, Sweden.
// For full details of using this software,
// please see the FonaDyn Handbook, and the class help files.
// The main entry point to the online help files is that of the class FonaDyn.
*/

+ VRPMain {
	*setMinorVersion {
		mVersion = "3.1.2c";  // Edit here when adding commits
	}
}

// REVISION NOTES for KTH-Github commits
// v3.1.2c		= Cloned maps did not get the same freq-grid [fixed]
//				= Improved handling of harmonics settings and file bit-depth check
// v3.1.2b		= Output directory was not being set from scripts [fixed]
// v3.1.2a		= Logging was disabled on Settings... -> OK [fixed]
// 				= Backslashes in filenames in context scripts were causing problems [fixed]
//				= Source: list sometimes appears disabled [NOT fixed] (touch the scroll bar)
// v3.1.1a		= phoncluster palette was not updated on reload from archive [fixed]
//				= phon multislider background did not follow the color scheme [fixed]
// v3.1.1		= Updated license texts to 2024
//				= Delayed the audio to sync better with the visuals - only on file input
//				= The moving EGG is now blanked when the audio is below the clarity threshold
//				= New cluster-file names were not shown on Save Clusters [fixed]
//				= SB calculation can (very rarely) give NaN results, now Sanitized
//				= EGG cluster map would be cleared if nClusters <> 5 [fixed]
// v3.1.0e		= implemented drag-n-drop of .csv's onto map and clusters graphs - easier.
// v3.1.0d		= Discovered issue #4924 and removed all (?) empty functions (SC bug).
//					Alas, the interpreter can still crash on a compile after FonaDyn is closed [arrgh]
// v3.1.0c		= signal playback and some other synths were not being freed [fixed]
// v3.1.0b 		= added marking of selected signal onto map region with /// hatch pattern
//				= changed underlap regions from solid fill to hatch
//				= signal window: added feedback on loading-for-listen
// v3.1.0a		= io.enabledWriteLog was "quoted" in context scripts, crashing the script [fixed]
// 				= added separate normalization of voice and EGG when listening (VRPViewPlayer.sc)
// 				= submitted to SoftwareX and on my profile page
// v3.1.0(final) = running of script on start-up was not working [fixed]
//				 = removed obsolete occurrences of VRPMain.panelColor
//				 = added 'keep' arg to .resetData
//				 = published on profile page
// v3.1.0 beta11 = re-instated LOAD of older _clusters.csv files
//				 = color scheme was not always restored on .rerun [fixed]
// v3.1.0 beta10 = SHIFT key would play back on Mac [fixed]
//				= added more .nil checks in VRPViewClusterPhon.initCCPanel
//				= .rerun did not load archived settings [fixed]
// v3.1.0 beta9 = still more work on syncing of fetch/stash/reset, now seems ok
// v3.1.0 beta8 = SC version check upped to 3.13.0; removed some stale comments
// v3.1.0 beta7 = LOAD _VRP.csv gave a blank map [fixed]
//			= selected cluster could change when layer was changed [fixed]
//			= VRP file name in BEFORE would disappear on LOAD VRP [fixed]
// 			= clarityThreshold could change on LOAD VRP [fixed]
// v3.1.0 beta6 = cycle-detection symbol was not updated [fixed]
//				= more work on syncing of fetch/stash
// v3.1.0 beta5 = something in initCCpanel caused SCLang to crash [seems fixed]
//				= reworked all .stash to occur before the respective .fetch, under mutex
// v3.1.0 beta4 = re-disabled cell thresholding for \Clarity layer
// v3.1.0 beta3 = Sometimes crash when rapidly changing the number of phon clusters [fixed]
// 			= Checking for file access did not apply the right filename-suffix [fixed]
// v3.1.0 beta2 = Now checks for file access before dialog saves of .csv files
//			= phoncluster control panel was still not initialized on .rerun [fixed]
// v3.1.0 beta = shared with Hejduk for testing
// v3.0.9b	= Moved the file name onto the map
//			= Tweaked the laying out in several ways
//			= Various small bugs fixed
// v3.0.9a  = Added optional VLayout of maps in Tiled displays
// v3.0.9	= Added display of the name of the current map file
//			= Hide some panel texts on resize to make more room if needed
//			= Cycle threshold now works also on the layers Density and Clarity
// v3.0.8x	= More debugging of cluster initializations
// v3.0.8w	= Cluster settings were not re-initialized from the archive [fixed]
// v3.0.8v  = Revised advanceScript to "breathe" after each line [this fixed some odd behaviours]
// 			= Settings restore on .rerun had stopped working [fixed]
// 			= Noise threshold changes are disabled if vrp.bWantsContextSave is true for a run
// v3.0.8u  = Added optional saving of a context script when a map is finished
// v3.0.8t  = Listening tempfile was not deleted on exit [fixed]
// 			= Ongoing playback did not stop if FonaDyn was closed [fixed]
// v3.0.8s  = Cycle counts were rounded when loading maps [fixed]
//			= Class "VRPClusterMaps" renamed to "VPRDataClusterMap"
// v3.0.8r  = Corrected drawing of cluster-averaged cycles when not normalized
// v3.0.8p  = Removed tracking and clustering of higher-partials residual, because it picks up
//					system noise (centroid files will change a little)
//			= Added instead tracking of the level of the fundamental
//					(scaled down by 0.001 so as to not affect the EGG centroids)
//			= Fundamental absolute level & phase are displayed as a diamond in the EGG centroids plot
//			= Alternative resynthesis of wave-shapes, with relative amplitudes (Normalize: Off)
// v3.0.8o  = changed to delta-% in per-cluster diff maps, made infoText visible on diff
// v3.0.8n  = updates to the VRPMetric help doc
// v3.0.8m  = some grid-plotting bugs when running in SC v3.12.x [fixed]
// v3.0.8l  = more post-restructuring bug fixes
//			= added a toned histogram fill
// v3.0.8j  = Incorrect map-grid resizing when fixedAspectRatio==true or toggling Hz/MIDI [fixed]
//			= Some unnecessary redrawing of maps when nothing has changed [fixed]
// v3.0.8g  = small cleanup fixes including units on colorbar
// v3.0.8f	= All new stuff working, cluster layers display % of total; except units on colorbar
// v3.0.8e  = work in progress on displaying cluster cells as % (not finished)
// v3.0.8d	= Restart bug on 2nd invocation [fixed]
//			= Wrong cluster colors for palettes on initing a diff map [fixed]
// v3.0.8   = Major reworking of all layer/metric structures - much cleaner code
// v3.0.7a-b  = work in progress on difference maps for clusters
// v3.0.7   = Added a new metric/layer for testing: the HRF-egg
//				(but logging is NYI, it can be derived, so don't disturb the _Log.aiff format)
//			= the EGG centroids 'H' now indicates the per-cluster HRF instead (but it can be > 0)
// 			= Added const VRPSettings.iLastMetric
//			= It is now possible to change the order of non-clustered metrics (in VRPSettings.sc)
// v3.0.6g  = reduced VRP grid drawing
// v3.0.6f  = Cluster diff maps now do something: show new color of changed cells
//			= In multi-map mode, switching layers to/from cluster maps did not propagate [fixed].
//			= Reworked redrawing in VRPViewVRP. Refresh of the back matrix is now more reliable.
// v3.0.6e	= Logfile frame rate option 200 Hz changed to 300 Hz (divides into 44100).
// 			= All EGG shapes are now resynthed upon loading a *_cEGG.csv file
// 			= EGG channel was not rescaled when reading 32-bit float signal files [fixed]
//			= Improved the handling of a manual selection in the signal window
// v3.0.6d  = implemented pausing of the GUI updates whenever a file dialog is open.
//               Hopefully, this will eliminate messages that the "scheduler queue is full"
//			= instantiating FonaDyn more than once is now blocked
//			= tiny tweaks to VRPControllerIO and others
// 			= reduced the CPP dither amplitude in VRPSDVRP.sc from 1/24000 to 1/1000000.
//				This increased CPP in strong voice by about 1 dB, and made repeats almost identical.
//			= Columns in _VRP.csv files may now appear in any order, even cluster data columns.
// v3.0.6c  = Writing a Log file is now allowed when Learning is on, but a warning is issued.
// v3.0.6b  = added plotting of "underlap" regions when a difference map is active.
//				(This is not provided in the Matlab routines.)
//				Color of BEFORE changed to magenta, to avoid confusion in a CSE map.
// v3.0.6	= Posted on profile page
// v3.0.5i  = VRPViewPlayer: versions conflict with .labelAppendString [fixed]
// v3.0.5h  = "Show:" list now has Tiled and Gallery that remember which graphs are hidden
//			= Pressing the Enter key when Show: has the focus will unhide all graphs
//			= On listening from maps: cross-fading of segments was broken [fixed]
// v3.0.5f  = Added double-click in Signal window to deselect and stop playback
//			= [Analysis Log] could not be turned off if disabled [fixed]
// 			= Added auto-recompile at the end of FonaDyn.install
// v3.0.5e	= Added metric distributions in the color bar (VRPViewVRP, DrawableSparseMatrix)
// v3.0.5d	= Added gridlines for time in the signal window (VRPViewPlayer)
//			= Grid frames were not drawn bottom and right [fixed for SC 3.13]
//          = Displayed name of log file was not updated for listening [fixed]
// v3.0.5c  = updated grids on FDcal's spectrum to SC 3.13
// 			= disabled noise threshold changes on recording
// v3.0.5b  = asSize error on startup in SC 3.12 [fixed]
// 			= cluster picking not always working [fixed]
// v3.0.5a  = trailing empty cells in .csv files were not ignored [fixed]
// v3.0.5   = grid not plotted on colorbar, backward to 3.12 [fixed]
//			= plot error on loading _cEGG.csv with more harmonics [fixed]
//			= noise-thresholding bus was not initialized on Start [fixed]
// v3.0.4f  = sizing problem backward to 3.12 [fixed]
// v3.0.4e  = Reworked the EGG conditioning with adjustable spectral thresholding - much better
//			= Listening is to raw input during record, but to conditioned signal on playback
//			= On analysis from file, audio is delayed to sync with thresholded EGG
// v3.0.4d  = Signal window: the space bar now stops a listen, even inside a selection
//				Individual segments in the signal window can be heard by clicking on them
//				An arbitrary section of the signal can be marked and then played by clicking on it
// v3.0.4c  = fixed bugs in initializing cluster settings from a script (and LOAD must be last)
//			= in scripts: EVAL <expr> now evaluates ANY single-line SC expression.
//				(general.eval=<expr> still works, too).
// v3.0.4b  = SB is delayed 34 ms to sync better; improves rejection of unvoiced fricatives
// v3.0.4   = The signal window is given a range slider when it is the only graph
// 			= settings.io.keepInputName now defaults to true
//			= copyright banner in .sc files updated to 2023
//			= posted on profile page
// v3.0.3	= Scripts can save the NOW map smoothed: SAVE <*>_S_VRP.csv
//			= Limit the initial window size to be smaller than the screen
// v3.0.2   = Updated all handling of DrawGrid to accommodate the changes in SC v3.13.0,
// 				falling back to the old behaviour if SC is v3.12 or earlier.
//			= With script files,
//				general.eval=<expr> now evaluates ANY single-line SC expression. Know what you are doing.
//				SAVE and LOAD now accept string expressions, and not just literal strings.
//				Evaluation errors are caught, and terminate the script, with a short explanation.
//				Posted line numbers start at 1, not 0
//				Graph panel visibility can be controlled with .isVisible
//					for vrp, sampen, scope, cluster and clusterPhon
//			= replaced +Dialog.open(save)PanelFork with VPRMain.open(save)PanelDefer
//				- seems to prevent some "scheduler queue is full" error messages, but not all
//			= Graph panel visibilities are restored on FonaDyn.rerun
// v3.0.1   = set default name to _cEGG.csv instead of _clusters.csv
//  		= VRPDataPlayer returns Point() instead of nil
// v3.0.0c  = VRPDataPlayer returns 0@0 instead of nil
// v3.0.0b  = added file existence test to VRPDataVRP.loadVRPdata
// v3.0.0   = last trims, and then release on Sten's profile page
// v2.6.9	= added FonaDyn.refreshMfiles, reworked many of the *.template.m files
// v2.6.8   = fixed small bugs in PhonPalette, centroid script save, signal play, layout
// v2.6.6c  = fixed some bugs in loading cPhon settings from a script
// v2.6.6   = implementing auto-updating of the Matlab m-files, in progress (class MfSC)
// v2.6.5   = fixed several mistakes in .csv-file-loading
//			= phon-cluster files (*_cPhon.csv) can be saved and reloaded.
//				Editing the .csv file is as yet the only way to specify the set of metrics
//				to be clustered, and what their standardization ranges are to be
// 			= when Listen is enabled, phon-centroids can be map-picked by number using the digit keys
//				which also plays like spacebar does. Requires "Learning: On".
// v2.6.4	= introduced a context popup menu to complement the Alt-key shortcuts
//			= settings for .rerun are now stored in the SuperCollider global archive,
//				in the file .../<userAppSupportDir>/archive.sctxar
//			= moved the Listen button to ViewMainMenuOutput, avoids duplication; more logical
//			= added suffix testing for map file names
// 			= trailing white space in script files is now tolerated
// 			= changed the set of metrics in the Plots panel
// v2.6.3   = added clustering controls to VRPViewClusterPhon (click on the radar plot to show)
//			= playing is now possible also from smoothed maps
// v2.6.2   = added suffix testing for cluster file names
// 			= map listening can display and play also the EGG signal, if chosen in Settings...
//			= Signal window can be shown as one graph, much bigger
// 			= Load cluster files from scripts (not yet Save); stop script if file doesn't exist
//			= new selection bug in map-listening fixed
// v2.6.1   = Save/Load of -VRP.csv and -cluster.csv files with phon-types
//			= displays cluster-file names in the bar graph if appropriate
// v2.6.0   = Huge makeover of cluster handling, implemented phonation type clusters.
//              More stringent structuring of settings vs. data
//              Still much to do, especially Save/Load of maps in some new format
// v2.5.2c  = Incorrect Install-failed-message [fixed]
//          = from 2.4.13: saveVRPdata would fail on empty layers [fixed]
// v2.5.2b	= stack-button & layer-info-text visibilities corrected
// v2.5.2	= VRPViewMaps.layout to use StackLayout
// 			= layouting of multiple maps is better now, but it still resizes the main window...
// v2.5.1	= Tested unsmoothed CPP - much better... - in MetricCPP.sc.hidden
//				Only one of CPPs or CPP can be active. Either "CPPs" or "CPP" column is saved/loaded
//				to/from _VRP.csv files.
//			= revised 2.4.12: Alt-S operates only on the Now map, not on the leftmost map
// v2.5.0   = "Singer mode" enforces 24-bit file output; 16-bit input files disable "singer mode".
// v2.4.13  = The precision in _VRP.csv files is now reduced from absurd 14 decimals to fewer;
// 				the default is 5, but it can be specified for each metric. Saves a little space and time.
// 			= Reworked .update for VRPViewVRP
// v2.4.12  = new feature: Alt-S creates from the first (leftmost) map
//              an interpolated and Smoothed map (all layers), which can be saved as *_S_VRP.csv
//				and reloaded. See all .interpolateSmooth methods (3 different classes)
// v2.4.11d = the Settings dialog box no longer clears the current map on OK,
//				unless the clarity threshold has changed
//			= CSV files: both semi-colon-delimited and comma-delimited can be loaded
// v2.4.11c = clarity threshold text was not updated [fixed]
// v2.4.11b = "float" input audio is henceforth assumed to represent 1 Pa peak-to-peak as 1.0
// v2.4.11  = re-ordered cluster data was not reliably saved [fixed]
//			= cursor color on single-cluster maps was not changing [fixed]
// v2.4.10  = Color bar of new Diff map was not drawn correctly [fixed]
//			= Diff map is saved as *_D_VRP.csv
//          = FonaDyn.install prompts correctly for recompile on Mac
//			= space bar did not invoke listen on Mac [fixed]
// v2.4.9   = CSE color bar was showing units "dB" [fixed]
//			= Clarity color bar was not updated on threshold changes [fixed]
//			= per-cluster average cycle shapes were not cleared on Unload [fixed]
// 			= added .bHzGrid to settings.vrp and to scripting
//          = ScopeViewer ("Plots") now draws its grid on a background view,
//			    & only if GUI changes (offloads a little CPU to GPU)
// 			= voice map tick label font size is now set consistently
// v2.4.8   = drop-down menu was showing Qci instead of SB [fixed]
//			= colorbar was not repainted properly [fixed]
//			= started on extensive, invisible restructuring:
//				clustering matrices brought into one class VRPClusterMaps
// v2.4.7	= fixed a bug that was causing errors on re-ordering clusters
//			= started weeding out remnants of the SampEn arrows plot
// v2.4.6   = eliminated VRPColorMap.sc; all palette funcs are now in each VRPMetric
//              and loaded to VRPData.metrics[] on startup
//			= fixed a big memory leak in map-playing
//			= tweaked crossfading to 70 ms
// 			= SoundFileView selections are updated when search rectangle is resized
// 			= search rectangle prevented from growing beyond 64 found segments
//				(a limitation of SoundFileView)
//			= "Listen" button shows # of segment being played
// v2.4.5	= Added "Listen" button for showing the current listening options
//			= Ctrl-mousewheel resizes the selection rect for listening
//			= Space bar also stops an ongoing playback
//			= Started on restructuring FonaDyn for modular metric "plug-ins"
//			= Reworded to EGG clusters, in anticipation of Phonation clusters
// v2.4.4   = reworked FonaDyn.config(...) for multiple invocations,
//				added runScript: "path" and enableMapPlayer: true/false
//			= space bar plays the selections
//			= animated playback cursor
//			= sticky map cursor shows the extent of the played cycles/cells
// 			= updated license texts copyright to 2022
// v2.4.3   = intermediate commit of new map player feature (many additions)
//			= Alt-L ("listen") toggles the visibility of the player window
// 			= added a "Calibrate..." button to start FDcal()
// 			= FDcal now allows only one instance of itself
// v2.4.2   = Save Image: press 'F' to save to a file; several open images can be saved
//				(ESC to close one, 'C' to close all)
//			= added FonaDyn.config(fixedAspectRatio: true/false) for the startup file
//			= added an alternative main window layout "All Gallery", better for map comparison
//			= DrawableSparseMatrix: very many Color objects were being created, but not used.
//			     Palette funcs are now invoked only on .draw, not on each cycle.
//			= ScopeViewer: colors are created only as needed
// v2.4.1   = insistent delete when patching _Extra.wav file sample rate
//			= always create <userAppSupportDir>.tmp folder on start
//          = FDCal v0.3 with corrected dB tick labels
// v2.4.0   = Updated the docs and released
//          = general.saveSettingsOnExit not saved, by design
// v2.3.2   = Added support for >2 channels in _Voice_EGG.wav files
//          = removed the 2nd-channel calibration tone (obsolete)
//          = added instead an option to play EGG on 2nd output
//			= fixed a few things so that scripts can set up a state without starting
// v2.3.1   = added the FonaDyn.config method, for the startup file
//          = improved FDcal with better fonts and ABCD PN shortcut keys
// 			= added support for 140 dB max SPL = "singer mode" <=> 24-bit audio
// v2.3.0   = added Auto Reset, which resets the cluster data
//				after 125 ms above the clarity threshold
//			= the new sdCPPsmoothed SynthDef would crash the server on true-zero signals.
//				Don't know in which UGen. Adding a tiny dither fixed it.
// v2.2.2   = added the metric CPP-smoothed in a new layer, with new UGen PeakProminence
//			= reverted to the old layout strategy in VPRViewMain, better
// v2.2.1   = introduces the SPL calibration tool: FonaDyn.calibrate
//			= script can now SAVE also _clusters.csv files
// 			= script parsing was broken - fixed
//			= clusters color palette was not properly set on Load and Script - fixed
//			= voice map grid font color now follows the voice map type
//			= (metric cell averaging moved from VRPControllerScope into DrawableSparseMatrix)
// v2.2.0   = (Mac)FFTW is now statically linked with modified PitchDetection.scx, released
//          = _Extra.wav files now receive the correct sampling rate
// v2.1.9	= updated copyright banners to 2021 in all .sc files
// v2.1.8   = reworked layer linking in VRPViewMaps.sc
//          = implemented drag-n-drop of "*_Voice_EGG.wav" files onto the batch files list
//          = the input file name is now posted on START
//			= improved the graphs layout control
//			= SPL calculation is now cycle-synchronous,
//				which practically eliminates droop due to calculations
// v2.1.7   = remaining rare bug in H level computation fixed (AverageOut, residual=Sanitize)
//				- clustering will change slightly
//			= brown-color threshold for CSE (entropy) raised to 0.1, was > 0.0
//			    - to match the Matlab colormaps better
//          = moved the method VRPDataCluster.palette to the class VRPViewCluster (many changes)
// v2.1.6	= improved the SPL detection, should give smaller variance (VRPSDVRP.sc)
// v2.1.5   = linked the layer switching of multiple maps;
//				the active map selects layers in all except TWIN.
// v2.1.4   = implemented multiple map views; toggle with Alt-T(TWIN), Alt-B(BEFORE), Alt-D(DIFF)
//			= several voice maps can be tiled, or stacked for easier A/B visual comparison
// 			= adjusted the default on-screen size of the main window
//          = fixed new bug under "Load map" button
// v2.1.2   = implemented drag-n-drop of "*_Voice_EGG.wav" files onto the input file name field.
//			= implemented drag-n-drop of "*_clusters.csv" files onto "Load Clusters" button.
//			= implemented drag-n-drop of "*_VRP.csv" files onto "Load Map" buttons.
//          = bug in SynthDef \sdSampEn: phases for SampEn calcs were not .abs as intended (fixed)
// v2.1.1	= bug fix: FonaDyn.install now creates "tmp" folder in userAppSupportDir
// v2.1.0	= updated copyright banners to 2020 in all .sc files
// v2.0.8	= Clarified the layers and colour mapping in the voice map view
//			= Restructured the handling of settings.vrp.clarityThreshold for script control
//			= Modified GridLinesExp.sc to deal with both SC 3.11 and prior versions
// v2.0.7   = Scripting is now usable
// v2.0.6   = new scripting mechanism parses most settings, loads clusters, loads/saves maps,
//            makes optional log files, starts/stops with every input signal file.
//          = invocation of FileDialogs is now in the VRPView* classes
//				while the actual loading/saving of data is in the VRPData* classes,
//				in anticipation of a scripting mechanism.
//          = VRPViewCluster.drawResynthEGG:
//			  -	implemented σ-approximation to minimize Gibbs' phenomenon - but this also reduces QΔ!
//			  -	optimized a bit
// v2.0.5   = added yet another metric layer, for the audio spectrum balance SB,
//		      "SpecBal" in VRP files, replaced Crest with SB in the graph plotter,
//			  and updated the online doc for the _Log.aiff format
//			= changed Max SampEn to Mean SampEn, and set default to 4 instead of 2 FD's
//          = worked around a documented Windows bug in PathName.tmp (BufferRequester.sc)
// v2.0.4c  = introduced the method VRPMain.screenScale to help resizing
//            and applied it in some grid and layout computations
// v2.0.4   = Gating bug in computation of 'H' level found and fixed in VRPSDCSDFT.sc;
//		         it mattered only rarely.
// v2.0.3	= implemented rescaling of fonts for hi-res displays
// 			= added +Dialog.savePanelFork, .loadPanelFork in VRPMain.sc, not sure if they help
//			= added more logging of file saves and loads
// v2.0.2	= implemented saving of all settings (FonaDyn.rerun, Settings... dialog box)
//			= resubmitted to SoftwareX
// v2.0.1	= bug in Log files: wrong order of channels 7,8,9  - corrected
//			= rewrote & moved double-peak-picking to Dolansky.ar, used in 3 places
//			= added nil-check in BusListenerScopeHandler.dispatch, prevents rare hanging
// v2.0.0   = updated copyright banners to 2019 in all .sc files
//			= finally found and fixed the "Reset Count" bug, in KMeansRTv2.scx.
//			= implemented variable plot duration 1..10 s: drag the mouse sideways
//			= submitted to SoftwareX
// v1.7.2   = introduced Ic as a new map-layer and log channel (replacing diplo)
//			= removed the Diplophonia computation and plot from 1.6.5 (not reliable)
//			= completed the rebuild of plugins for MacOS, including PitchDetection.scx
//			= SampEn controls are now hidden unless "Sample Entropy" is checked
//			= The wall clock is now redrawn every second, not just during phonation
//			= Renamed the colour schemes, for fun
// v1.7.1   = fixed a problem with the Reset Counts buttonAction
//          = changed the peak-follower's tau from .99 to .95 => improved cycle picking at high fo
//			= enclosed some Dialog calls in a .forkIfNeeded;
//				hopefully this will prevent "scheduler queue is full"
//			= small points now show the exact centroid coordinates (single centroid view only)
// v1.7.0	= Rewrote the crest factor calculation as a pseudo-UGen (VRPSDVRP.sc),
//			  since the built-in Crest UGen is both clunky and wrong.
//			= Restored the EGG CLIPPING warning that had stopped working at some point
// v1.6.7   = found a problem in AverageOut: Integrator.ar does not work properly,
//			  so the H levels have been wrong. Rewrote it with Phasor.ar
//			= LoadVRP now reloads SampEn fields properly, with pale green zero cells.
// v1.6.6   = changed the line plots to horizontal flecks, one per cycle, which is clearer.
//			= in VRPSDCSDFT.sc:SynthDef(\sdNDFT), prevented residual from becoming too small
//          = fixed a bug that was causing dEGGmax not to be Logged
// v1.6.5   = added a Diplophonia plot (included in _Log.aiff files, but not in the VRP).
//			  It shows the relative level of the halfth partial to the first partial (+4 Bels),
//			  median-filtered over 5 cycles.
//		 	  If fo actually drops an octave, it won't count as diplophonia.
//			= Reliable updating of VRP background finally found and implemented.
// v1.6.4b  = small layout tweaks
// v1.6.4   = added optional plotting of multiple curves in what was the SampEn graph
//			  This included adding a .colors method to ScopeViewer
// v1.6.3b  = tiny bug in VRPViewMainMenuInput.sc fixed
// v1.6.3   = added .sync to the Pause action, improved stability on Pause.
//			= file batching was skipping a file if interrupted
//			= file batch now starts from the selected file (default: the first)
//			= added mLastPath to the file list browser for batches
// v1.6.2   = Press Alt + { c | m | v | p } to toggle the visibility of any graph
//          = A resolution bug in UGen TimeStamp.scx made FonaDyn stick and misbehave
//			  after 380.4 seconds; fixed. Now seems to run OK with long files.
//			= Merged the dEGGmax and Qci computations into one SynthDef 'sdQciDEGG' .
// v1.6.1   = replaced dEGGnt from the FDs with dEGGmax (1..20) from EGGcond
// v1.6.0   = changed the call bm.control(\GateReset).set(1)
//			to bm.control(\GateReset).setSynchronous(1). It seems to be more stable now.
// v1.5.9 = Fixed the color scale bar updating and its dEGGnt grid
//		  = recordingsDir, as specified in the file "startup.scd" or browsed to during a session,
//			is now the initial default folder for all Save/Open file operations.
// v1.5.8 = Added to the VRP display a cycle count threshold which can be changed at any time.
//			- this threshold is for display only, it is not stored.
//		  = added left-clicking on the VRP colour bar to switch layers
// v1.5.7 = Added the qContact metric: display, logging and _VRP.csv save/load (many changes)
//        = keyboard focus now moves to Pause after Start
// v1.5.6 = Removed the SampEn Arrows display, which was never used for anything.
//		    - will keep the code, but commented out, for a few more releases.
//		  = Added the dEGGnorm@trig metric: display, logging and _VRP.csv save/load (many changes)
//		  = Added a color scale bar in the VRP
//		  = Open File now initializes to the last directory on Loads and Saves (_clusters.csv, ...)
//		  = Internal: at SC 3.10.0, had to undo Float.asString, with overrides in GridLinesExp.sc
//		  = Internal: Changed nHarmonics to actually represent the # of harmonics, not nHarm+1
//		  	One extra harmonic was DFT'd, to waste - fixed.
//		  	Not sure how csdft.nHarmonics is initialized; it is used in VRPSDIO.sc.
//		  = Internal: moved the average Crest display computation
//			to the filtered handler (VRPControllerScope.sc)
// v1.5.5 Bug fix: clear averaged cycles when renumbering clusters
//        Fixed PATH bug in FonaDyn.run
//		  Tweaked the "Studio" colour scheme a bit
// v1.5.4 UGen AverageOutput.ar found to be broken;
//        worked around it with a pseudo-UGen AverageOut.ar (in file VRPSDCSDFT.sc)
// v1.5.3 Turned off smoothing in graphs, which became more consistent.
//        Reworked the redraw mechanism, still not bulletproof.
// v1.5.2 Added the Load VRP button for inspecting saved VRPs,
//		  and for recording more into an existing VRP (clusterings must match!).
//        Fixed a bug in Save _VRP.csv: it no longer appends a delimiter char to the last item,
//            old csv files are still parsed OK.
// v1.5.1 Added the "Keep data" option for concatenated runs,
//		     thus making "Batch multiple files" meaningful.
//		  Method .update was overriding Object.update; renamed it to .updateData (in all Views)
//		  Corrected the coding for dependants
//        On loadClusterData, set "Pre-learned" and a new palette
//		  Reordered the fetch/update sequence of color schemes
//        Found PATH bug in FonaDyn.run; needs fixing
// v1.5.0 License info added to all VRP*.sc files; release to SoftwareX
// v1.4.4 in FonaDyn.sc, added a check of PATH and prepending <resourceDir>; to it,
//        if necessary - not sure if this is Windows-specific or not
// v1.4.3 simplified the file format _clusters.csv to a rectangular array
// v1.4.2 added a Pause button, and moved the Start button to its left
//        Pause will also mute any audio feedback.
// v1.4.1 optimized "fetch"; tweaked the color schemes a little; optimized VRP drawing
// 		  so as not to have to redraw everything on every update.
// v1.4.0 added interface for reordering the clusters manually: On cluster columns,
//          ctrl-click left to swap with cluster to the left,
//          ctrl-click right to swap with with cluster to the right.
//          Wraps around at first and last cluster.
// v1.3.6.3 added Settings... GUI for recording of extra channels
// v1.3.6.2 added recording of extra channels;
//		with a hardcoded framerate 100 Hz, enable and input array are in VRPSettingsIO.sc.
// 		Also ganged the left cluster slider to the VRP display but not vice versa
// v1.3.6 added "colour schemes"; more trimming of the GUI
// v1.3.5 Took out the weighting of phase (cos, sin), much better!
// v1.3.4 GUI: repaired the tab order, enabled/disabled buttons on stop/start,
//        tweaked text field sizes, and the SampEn grid
// v1.3.3 Disk-in now records conditioned signals and colors output buttons
// v1.301 fixed the Cluster bug in isochronous log files
// v1.30 added Hz log scale to VRP (toggle with right-click)
//       (the source code: GridLinesExp.sc must be included in .\Extensions\SystemOverwrites)
//       Also realigned the VRP grid to the cell boundaries

