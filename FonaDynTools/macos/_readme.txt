The first time you run FonaDyn on a Mac, the computer must be connected to the Internet, or Apple's safety checks will not work. 

These .scx files are built to support both ARM and Intel hardware. 

This build of the MacOS UGens for FonaDyn presumes that SuperCollider is installed in the default location. The UGens GatedDiskOut.scx and PitchDetection.scx will want to link directly to DLL's in that location. 

On FonaDyn.install, the library PitchDetection.scx in this folder replaces the one in sc3-plugins. The version here is linked with the FFTW libraries instead of with the Apple vDSP libraries, which do not give exactly the same result. 
Running FonaDyn.uninstall will reinstate the original PitchDetection.scx. 

