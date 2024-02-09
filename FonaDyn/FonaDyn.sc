// Copyright (C) 2016-2023 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //

/* This installation utility assumes that files have been unpacked from the ZIP as follows:

	...\Extensions\FonaDyn: including FonaDyn.sc and GridLinesExp.sc.txt
	...\Extensions\FonaDynTools
	...\Extensions\FonaDynTools\win32
	...\Extensions\FonaDynTools\win64
	...\Extensions\FonaDynTools\macos, including a recompiled PitchDetection.scx
	...\Extensions\FonaDynTools\linux

Three of the four subfolders will be deleted during the installation.
*/

FonaDyn {

	*run { arg bReRun = false;
		var setPathStr;
		var resDir = Platform.resourceDir;
		var currentPath = "PATH".getenv;
		if (currentPath.notNil and: { currentPath.find(resDir, true).isNil }, {
			setPathStr = resDir ++ ";" ++ currentPath;
			"PATH".setenv(setPathStr);
		});
		// This can be needed because the user might not
		// be the one who installed FonaDyn for all users
		File.mkdir(thisProcess.platform.userAppSupportDir +/+ "tmp");
		VRPMain.new(bReRun);
	}

	*rerun {
		FonaDyn.run(true);
	}

	*calibrate { arg voiceMicInput, refMicInput;
		FDcal.new(
			voiceMicInput ? VRPControllerIO.audioInputBusIndexVoice,
			  refMicInput ? VRPControllerIO.audioInputBusIndexEGG
		)
	}

	*refreshMfiles { arg whereTo=nil, bEcho=false;
		if (~gVRPMain.notNil, {
			var m = MfSC.new(targetPath: whereTo, echo: bEcho);
			m.updateAllMfiles
		}, {
			"FonaDyn must be running to refresh the m-files".error;
		});
	}

	// Only the arguments actually given will be acted upon,
	// so multiple calls to .config can be made in the startup file.
	*config { arg inputVoice, inputEGG, sampleFormat, singerMode, fixedAspectRatio, tileMapsVertically, enableMapPlay, runScript, mRefresh;
		VRPControllerIO.configureInputs(inputVoice, inputEGG);
		VRPControllerIO.configureSampleFormat(sampleFormat);
		VRPDataVRP.configureSPLrange(singerMode);
		VRPDataVRP.configureAspectRatio(fixedAspectRatio);
		VRPViewMaps.configureTiledMaps(tileMapsVertically);
		VRPDataPlayer.configureMapPlayer(enableMapPlay);
		VRPViewMainMenuInput.configureInitScript(runScript);
	}

	*setPaths {
		var fdAllUsers, fdExtPath, plugsAllUsers, plugsPath;

		// Find out if user has copied FonaDyn "per-user", or "system-wide"
		fdExtPath = PathName(VRPMain.class.filenameSymbol.asString.standardizePath).parentPath; // classes
		fdExtPath = PathName(fdExtPath).parentPath; // FonaDyn
		fdExtPath = PathName(fdExtPath).parentPath.asString.withoutTrailingSlash; // Extensions
		fdAllUsers = (fdExtPath == thisProcess.platform.systemExtensionDir);

		if (fdAllUsers,
			{ ~fdExtensions = thisProcess.platform.systemExtensionDir; },
			{ ~fdExtensions = thisProcess.platform.userExtensionDir; }
		);
		("Found FonaDyn in" + ~fdExtensions).postln;

		~fdProgram = ~fdExtensions +/+ "FonaDyn";
		~fdProgramTools = ~fdExtensions +/+ "FonaDynTools";

		// Find out if user has installed SC3Plugins "per-user", or "system-wide"
		plugsPath = PathName(Tartini.class.filenameSymbol.asString.standardizePath).parentPath; // classes
		plugsPath = PathName(plugsPath).parentPath; // PitchDetection
		plugsPath = PathName(plugsPath).parentPath; // SC3plugins
		plugsPath = PathName(plugsPath).parentPath.asString.withoutTrailingSlash; // Extensions
		plugsAllUsers = (plugsPath == thisProcess.platform.systemExtensionDir);

		if (plugsAllUsers,
			{ ~plugsExtensions = thisProcess.platform.systemExtensionDir; },
			{ ~plugsExtensions = thisProcess.platform.userExtensionDir; }
		);
		("Found SC3-plugins in" + ~plugsExtensions).postln;
	}

	*removeFolder { arg folder;
		var rmCmd;

		rmCmd = Platform.case(
			\windows, { "rmdir /s /q" },
			\osx,     { "rm -R" },
			\linux,   { "rm -R" }
		);

		rmCmd = rmCmd +  "\"" ++ folder ++ "\"";
		rmCmd.postln;
		rmCmd.unixCmd;
	}

	*install {
		var success = true;
		var gridLinesOverrideName;
		var dirName, fName;
		var cmdCompileStr;

		// Check that the SC3 plugins are installed
		// and post instructions if they are not.
		Platform.when(#[\Tartini], {
			FonaDyn.setPaths;
			if (Main.versionAtMost(3,12),
				{
					postln ("This SuperCollider is at version" + Main.version + ".");
					postln ("Please update to SuperCollider 3.13.0 or higher before continuing.");
					success = false;
			});

			gridLinesOverrideName = "GridLinesExp.sc.txt";
			if (Main.versionAtLeast(3,13),
				{
				gridLinesOverrideName = "GridLinesExp313.sc.txt";
				}
			);

			// Move the log-grid option to the proper location.
			// The .txt file is ignored, it can stay.
			// fName = PathName(~fdExtensions +/+ "SystemOverwrites" +/+ "GridLinesExp.sc");
			// This might become redundant in a future SC release
			if (success, {
				dirName = ~fdExtensions +/+ "SystemOverwrites" ;
				dirName.mkdir;
				fName = dirName +/+ "GridLinesExp.sc";
				if (File.exists(fName),
					{ (fName + "exists - ok,").postln },
					{ File.copy(~fdProgram +/+ gridLinesOverrideName, fName)}
				);
				File.mkdir(Platform.userAppSupportDir +/+ "tmp");

				success = Platform.case(
					\windows, { cmdCompileStr = "(Ctrl+Shift-L)"; FonaDyn.install_win },
					\osx,     { cmdCompileStr = "(Cmd+Shift-L)";  FonaDyn.install_osx },
					\linux,   { cmdCompileStr = "(Ctrl+Shift-L)"; FonaDyn.install_linux }
				);
			});

			if (success,
				{
					thisProcess.recompile;
					postln ("FonaDyn was installed successfully.");
					// format ("Recompile the class library to complete the installation %.", cmdCompileStr).postln ;
				},
				{ error ("There was a problem with the installation.") }
			);
		},{
			FonaDyn.promptInstallSC3plugins;
		});
	} /* .install */

	*install_win {
		var retval = false;

		if (Platform.architecture == 'x86_64', {
			FonaDyn.removeFolder(~fdProgramTools +/+ "win32");
			postln ("Installing Win64 plugins");
		}, {
			FonaDyn.removeFolder(~fdProgramTools +/+ "win64");
			postln ("Installing Win32 plugins");
		});
		FonaDyn.removeFolder(~fdProgramTools +/+ "macos");
		FonaDyn.removeFolder(~fdProgramTools +/+ "linux");
		^retval = true
	}

	*install_osx {
		var retval = false;
		// Rename the original PitchDetection.scx so that ours becomes the active one
		var scxName = "PitchDetection/PitchDetection.scx";
		var destPath;
		var cmdLine;

		destPath = ~plugsExtensions +/+ "SC3plugins" +/+ scxName;
		if (File.exists(destPath), {
			cmdLine = "mv" + destPath.quote + (destPath ++ ".original").quote;
			cmdLine.postln;
			cmdLine.unixCmd;
			postln (scxName + "overridden.");
		},{
			("Did not find "+ scxName).postln;
		});

		FonaDyn.removeFolder(~fdProgramTools +/+ "win32");
		FonaDyn.removeFolder(~fdProgramTools +/+ "win64");
		FonaDyn.removeFolder(~fdProgramTools +/+ "linux");
		^true
	}

	*uninstall_osx {
		var retval = false;
		// Restore the name of the original PitchDetection.scx
		var scxName = "PitchDetection/PitchDetection.scx";
		var srcPath, destPath;
		var cmdLine;
		destPath = ~plugsExtensions +/+ "SC3plugins" +/+ scxName;
		srcPath = destPath ++ ".original";
		if (File.exists(srcPath), {
			cmdLine = "mv" + srcPath.quote + destPath.quote;
			cmdLine.postln;
			cmdLine.unixCmd;
			postln (scxName + "restored.");
		},{
			("Did not find "+ scxName).postln;
		});
		^true
	}

	*install_linux {
		FonaDyn.removeFolder(~fdProgramTools +/+ "win32");
		FonaDyn.removeFolder(~fdProgramTools +/+ "win64");
		FonaDyn.removeFolder(~fdProgramTools +/+ "macos");
		^true
	}

	*uninstall {
		var dirName;
		var fName;

		Server.killAll;  // otherwise the plugin binaries won't be deleted
		FonaDyn.setPaths;
		dirName = ~fdExtensions +/+ "SystemOverwrites" ;
		fName = dirName +/+ "GridLinesExp.sc";
		warn("This removes all FonaDyn code, including any changes you have made.");
		if (File.exists(fName),	{
			File.delete(fName);
			(fName + "removed.").postln;
		});
		Platform.case(
			\osx,     { FonaDyn.uninstall_osx }
		);

		FonaDyn.removeFolder(~fdProgram);
		FonaDyn.removeFolder(~fdProgramTools);
		FonaDyn.removeFolder(Platform.userAppSupportDir +/+ "tmp");
	}

	*promptInstallSC3plugins {
		postln ("The \"SC3 plugins\" are not yet installed.");
		postln ("Download the version for your system,");
		postln ("and follow the instructions in its file README.txt.");
		postln ("Then re-run this installation.");
	}

} /* class FonaDyn */


