Field Photographs README
Author: Christopher Besser
The Field Photographs app takes field and bag photos and stores them to a common directory.

Main screen: MainActivity.java
	MainActivity prompts the user to select the year from a menu and enter their survey unit and field photo number into
text boxes. They can then choose to take a field photo or a bag photo by pressing a respective button. Both buttons launch
TakePhotographActivity. The value of the field photo number is incremented every time a field photo is taken. An icon appears
on the main screen when a bag photo is taken. The user can also launch the SettingsActivity by selecting Settings from the
ActionOverflow.

Other Activities: TakePhotographActivity.java
	TakePhotographActivity opens the Android camera. The user takes a picture, then is taken back to the main screen as a
Toast appears telling them where the picture was saved.

SettingsActivity.java
	SettingsActivity allows the user to toggle thumbnail creation and change the root directory where images are taken.
Changing the path resets the picture count.

Used APIs/External Libraries:
	None.