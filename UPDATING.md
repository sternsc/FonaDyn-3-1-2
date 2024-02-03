
On Windows:
Platform.userSupportDir = C:\Users\your-user-name\AppData\Local\SuperCollider

Platform.userExtensionDir =	C:\Users\your-user-name\AppData\Local\SuperCollider\Extensions

Platform.systemExtensionDir: 	C:\ProgramData\SuperCollider\Extensions

Updating FonaDyn to a higher version
====================================

Please do these things in the given order. 

1. Check which custom modifications, if any, that have been made to your FonaDyn installation. These will need to be re-done after updating. A good idea is to note any such modifications as comments in the file userSupportDir/startup.scd, which is not affected by the FonaDyn installation. 

2. Run SuperCollider, and execute the command "FonaDyn.uninstall". 
   This restores certain files to the system defaults, 
   and deletes the folders FonaDyn and FonaDynTools.

3. If SuperCollider itself needs updating, follow the procedure for that (below). 

4. Download the new FonaDyn version from the repository as a ZIP file. 

5. Follow the instructions in the enclosed file README.md


Updating SuperCollider to a higher version
==========================================

1. If the sc3-plugins need updating, follow the procedure for that (below). These updates are published less often than are updates to SuperCollider.

2. In your computer's Control Panel, uninstall your current SuperCollider. 

3. Download the new version of SC from https://supercollider.github.io/downloads. Make sure to get the variant that matches your operating system.

4. Run the downloaded installer. 


Updating the sc3-plugins to a higher version
============================================

1. Delete the folder SC3plugins. It is either in the userExtensionDir or in the systemExtensionDir.

2. Download the latest release from https://supercollider.github.io/sc3-plugins. They come in a ZIP file (or a .tar file, for Linux). 

3. Copy the folder SC3plugins from the archive file into userExtensionDir (for the current user only) or systemExtensionDir (for all users of SuperCollider on your machine). 


 





