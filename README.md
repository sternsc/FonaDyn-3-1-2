/*  FonaDyn installation README.MD
    ===============================

3 February 2024

The following procedure performs a basic install, using SuperCollider's default audio device. We recommend that you first open the file "FonaDyn Handbook v3-1-2.pdf" and read the chapter "Software Setup". If your computer is centrally managed, show that chapter to your IT support staff. Follow this file only if you are impatient, savvy and have admin rights on your computer.

------------------------------
Apple MacOS only 
------------------------------

When you run FonaDyn for the first time, the computer must be connected to the Internet, or Apple's safety checks will not work. The FonaDyn plugins for Mac will run on both ARM and Intel CPUs. 

------------------------------

Once FonaDyn is up and running, you may want to tweak various settings.
These, too, are described in the FonaDyn Handbook.

If you have an earlier version of either FonaDyn or SuperCollider installed, 
please first follow the steps in the text file "UPDATING.md" in the order given there.

Install SuperCollider on your computer. 
With this release of FonaDyn, you must use SC version 3.13.0 or later. 
The download is at http://supercollider.github.io/download.

With version 3.13.0 of SC, you must use this release of FonaDyn, or a later one. 

The 32-bit SuperCollider for Windows will run on both 64-bit and 32-bit Windows.
The 64-bit SuperCollider for Windows runs only on 64-bit Windows.

Download also the corresponding version of the "sc3-plugins"
and follow the instructions in its README file.
If you are updating SuperCollider to v3.13.x then you should also install the updated plugins. Remove the old ones first.  

To install FonaDyn into your SuperCollider system, follow these steps:

1) Unpack the folders FonaDyn and FonaDynTools from this ZIP file
into folders with the same names.

2) Run SuperCollider (scide.exe) and wait for the "Post window"
to display this line:

	*** Welcome to SuperCollider 3.13.0. *** For help press Ctrl-D.

3) Choose File > New . This opens a new text file "Untitled" for editing.

4) In the window tab "Untitled", type

	Platform.userExtensionDir;    // if only you will be using FonaDyn on this computer
	
or

	Platform.systemExtensionDir;  // if you want all users on the computer to use FonaDyn

("Dir" is short for "directory" which is synonymous with "folder")

5) With the cursor still on that line, execute the line by pressing Shift+Enter (Cmd-Enter on the Mac).
Or, in the popup menu "Language", choose "Evaluate file".

SuperCollider will now print the full pathname
of the appropriate Extensions folder in its "Post window".

6) Copy the folders FonaDyn and FonaDynTools from the ZIP file to that folder. 

7) Create a startup file for SuperCollider: Choose File > Open user support directory. 
If it already contains a "startup.scd" file that you were using earlier, you can keep that file. 
Otherwise just copy the file "startup.scd.example.txt" from the ZIP file to "startup.scd" in this directory.

8) Exit SuperCollider and restart it.

9) In a new text window, evaluate the line

	FonaDyn.install;

Note any messages that appear in the "Post window".

If you see the message "FonaDyn was installed successfully.", then celebrate, briefly. 

10) Copy the folder "Test files" from the ZIP archive into the directory displayed by FonaDyn as "Output Directory". 

11) If you will be using Matlab to post-process the results from FonaDyn, copy the folder "matlab" to your "user support directory" (which is the parent directory of the Extensions directory). Also, read the file _ReadMe.txt in the "matlab" folder. 

12) At the bottom of the SuperCollider window, there is a row of numbers that are white. To start FonaDyn, evaluate the line

	FonaDyn.run;

This will open the FonaDyn main window. Wait until the numbers first turn yellow (server is starting) and then green (server is running). For the first session, it may take quite a while before the numbers turn green (due to anti-virus scanning). 

To test, select "Source: From file" and browse to the file "test_Voice_EGG.wav" in the folder "Test files". Then press "> START". 

----------------

To uninstall FonaDyn (no questions asked!), evaluate the line

	FonaDyn.uninstall;

WARNING: this deletes everything in the folders FonaDyn and FonaDynTools,
including any changes you may have made to the source code.

*/

