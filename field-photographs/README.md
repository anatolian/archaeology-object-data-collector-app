**_Disclaimer:_** This application was coded by an inexpensive freelance company.  It was written quickly and for a very specific purpose, and it has fulfilled this purpose during multiple archaeological field seasons.  However, the quality of the code leaves much to be desired and the application could be more robust.  For those who wish to contribute to archaeological research, pull requests that help to improve the program would definitely be welcomed.  Since each archaeological project's data structure tends to be unique, the purpose of publishing this code was less to provide software for ready download and use, and more as an example of potential workflows and perhaps as a code base that could be adapted, with much effort, to other projects.

## field-photographs

These Android apps were designed to ease photograph file management on archaeological projects.  The goal is to rename and place photographs on the filesystem in a structured manner, as the photographs are captured in the field.  Photograph file names are set based upon associated archaeological metadata, so that data viewing applications can easily connect the data with the image file.

**The usual simple workflow:**

1. The app requests the proper structured metadata about what is going to be photographed.  The user may either enter the data manually, or select data from an archaeological project's central relational datastore.
2. A photograph is taken with the Android smartphone, tablet, or camera.
3. Upon acceptance of the photograph or group of photographs, the photograph files are renamed and placed into an appropriate location on the filesystem, based on the selected metadata.  The final location may either be on the local storage of the Android device, or uploaded over the network to a central server via a webservice.

These apps were initially developed for the data structures of Boston University's Gygaia Projects (www.gygaia.org). The simplicity of the workflow should allow adaptation to other situations.

## subprojects

* excavation/app
* excavation/webservice

This app was designed for archaeological excavation and incorporates three different program flows.  The first takes individual photographs of excavated archaeological contexts, the second collects a batch of photographs for 3d photogrammetry processing, and the third enables multiple photographs of archaeological samples, such as ceramics or lithics.  The app depends on the php webservice both to provide the archaeological metadata for users to select, as well as to upload the resultant photograph files.  Network connectivity is assumed at all times.

* survey/app

This app was designed for archaeological field surface survey and has one program flow.  A user locally enters the survey unit identification, and then can take photographs either of the unit being surveyed, or of the bag of samples collected from the unit.  Photograph files are stored locally on the Android device, enabling offline usage.


