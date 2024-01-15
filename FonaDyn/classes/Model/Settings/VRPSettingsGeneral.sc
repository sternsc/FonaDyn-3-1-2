// Copyright (C) 2016-2024 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
// Copyright (C) 2016-2023 by Sten Ternström & Dennis J. Johansson, KTH Stockholm
// Released under European Union Public License v1.2, at https://eupl.eu
// *** EUPL *** //
VRPSettingsGeneral {
	var <>output_directory; // Output directory for the session

	var <>start; // True if the server should be started
	var <>stop; // True if the server should be stopped
	var <>guiChanged; // True if there are changes pending to the GUI

	var <>layout; // Any of VRPViewMain.layout*
	var <>stackType; // Any of VRPViewMain.stackType*
	var <>enabledDiagnostics;
	var <>saveSettingsOnExit;
	var <>queueInitScript;

	var >eval; 	// placeholder variable for evaluating statements in script files

	var <colorThemeKey;
	var themes;  // Dictionary of Dictionaries each containing a color theme
	classvar themeColorIndeces = #[\backDrop, \backPanel, \backGraph, \backTextField, \panelText, \dullText, \brightText, \curveScope];
	classvar <colorThemeStandard = 0;
	classvar <colorThemeStudio = 1;
	classvar <colorThemeMilitary = 2;

	*new {
		^super.new.init;
	}

	init {
		var theme;

		output_directory = thisProcess.platform.recordingsDir;

		start = false;
		stop = false;
		guiChanged = true;
		enabledDiagnostics = false;
		saveSettingsOnExit = false;
		queueInitScript = false;

		colorThemeKey = VRPSettingsGeneral.colorThemeStandard;
		themes = Dictionary.new;

		// init the csStandard colors ("Grand Piano")
		theme = Dictionary.newFrom( List [
			\backDrop, Color.black,
			\backPanel, Color.gray(0.2),
			\backGraph, Color.black,
			\backTextField, Color.white,
			\panelText, Color.white,
			\dullText, Color.gray(0.5),
			\brightText, Color.white,
			\curveScope, Color.black
		]);
		themes.put(VRPSettingsGeneral.colorThemeStandard, theme);

		// init the csStudio colors ("Nordic Light")
		theme = Dictionary.newFrom( List [
			\backDrop, Color.gray(0.85),
			\backPanel, Color.gray(0.9),
			\backGraph, Color.white,
			\backTextField, Color.white,
			\panelText, Color.gray(0.4),
			\dullText, Color.gray(0.6),
			\brightText, Color.white,
			\curveScope, Color.black
		]);
		themes.put(VRPSettingsGeneral.colorThemeStudio, theme);

		// init the csMilitary colors ("Army Surplus")
		theme = Dictionary.newFrom( List [
			\backDrop, Color.new(0.1, 0.15, 0),
			\backPanel, Color.new(0.1, 0.2, 0.1),
			\backGraph, Color.green(0.2),
			\backTextField, Color.white,
			\panelText, Color.yellow(0.6),
			\dullText, Color.gray(0.6),
			\brightText, Color.white,
			\curveScope, Color.black
		]);
		themes.put(VRPSettingsGeneral.colorThemeMilitary, theme);
	}

	getThemeColor { arg tColor;
		^themes.at(colorThemeKey).at(tColor);
	}

	colorThemeKey_ { arg themeKey;
		colorThemeKey = themeKey;
		guiChanged = true;
	}
}
