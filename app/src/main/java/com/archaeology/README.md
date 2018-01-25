# Code Overview

This folder contains all the Java code for the application, divided into principal components.
Below is a brief overview of what each class does:

models - this directory contains wrapper classes for Heroku response objects
- AfterImageSavedMethodWrapper.java - wrapper interface for image postprocessing
- AsyncHTTPCallbackWrapper.java - wrapper interface for HTTP communications
- AsyncHTTPWrapper.java - wrapper for uploading images to Heroku
- ImageResponseWrapper.java - wrapper interface for downloading images from Heroku
- JSONObjectResponseWrapper.java - wrapper interface for fetching JSON objects from Heroku
- JSONArrayResponseWrapper.java - wrapper interface for fetching JSON arrays from Heroku
- StringObjectResponseWrapper.java - wrapper interface for fetching Strings from Heroku

services - this directory contains classes that communicate with Heroku, PostgreSQL, Bluetooth, or WiFi Direct
- BluetoothService.java - connects to a selected Bluetooth device
- DatabaseUpdater.java - 
- NutriScaleBroadcastReceiver.java - receives and parses Bluetooth messages from the scale
- PicassoWrapper.java - wrapper for Picasso, a library that handles asynchronous image rendering for images downloaded from remote addresses
- Retriever.java - interface for receiving HTTP responses
- SimpleLiveViewSlicer.java - Sony written API for communicating with the remote camera
- UpdateDatabase.java - interface for downloading data from the public archive.
- UpdateDatabaseMuseum.java - downloads public archive data if not present on the phone or the data present is at least 1 week old. Implements UpdateDatabase.
- VolleyStringWrapper.java - wrapper for String data requests to the camera
- VolleyWrapper.java - wrapper for communications with the remote camera
- WiFiDirectBroadcastReceiver.java - service in charge of receiving data from the camera

ui - This directory contains all files that correspond to an element that the user sees. All activities end with "Activity"
- CameraDialog.java - alert window for interacting with the remote camera
- CameraUIActivity.java - the screen used for taking a picture of an object to lookup, scanning QR codes, or transitioning to FavoriteActivity, HistoryActivity, or CeramicInputActivity. Swiping left takes users to HistoryActivity. Swiping right takes users to FavoritesActivity. Tapping the middle floating action button takes users to CeramicInputActivity. Taking a picture takes the user to ManualActivity.
- CeramicInputActivity.java - a page where the user can lookup an item by its primary key(s). Takes users to ObjectDetailActivity.
- DownloadActivity.java - a splash activity that also fetches public archive information. Launches InitialActivity.
- FavoriteActivity.java - a list of favorited artifacts in the public archive. Swiping left takes the user back to CameraUIActivity. Tapping an entry takes the user to SearchActivity and renders that page.
- HistoryActivity.java - a list of previously searched archives from the public archive. Swiping right takes the user back to CameraUIActivity.
- InitialActivity.java - a screen where the user enters the URL for the Heroku database their data is hosted on. Launches CameraUIActivity when the connection succeeds.
- ManualActivity.java - the screen where users can enter a public archive code or correct the OCRed code. Takes the user to SearchActivity.
- MyWiFiActivity.java - a UI "remote" listing all of the functions available to the remote camera
- ObjectDetailActivity.java - an activity that displays all of the fetched data on an object selected in CeramicInputActivity. Contains buttons that send the user to PhotosActivity and MyWiFiActivity.
- PhotoFragment.java - a container view that Picasso loads images asynchronously into
- PhotosActivity.java - a screen where users can apply color correction to an image stored on their phones
- SearchActivity.java - a container view where public archive results are loaded into
- SettingsActivity.java - where users can configure the Bluetooth connection, database URL, toggle the remote camera, and set the camera remote address.
- SimpleStreamSurfaceView.java - a container view that Sony's API loads the camera's live feed into
- TaggedImageView.java - an image wrapper that contains an id tag

util - this directory contains miscellaneous helper libraries for other files
- CheatSheet.java - this file contains commonly used helper methods. See the javadocs for their functions
- HistoryHelper.java - this file does most of the heavy lifting for fetching the user's search history from PostgreSQL
- ImageFileProvider.java - this empty file is important! Newer Android devices will not allow apps to provision external memory without a file provider. While file providers allow for some additional functionality, none of it is necessary for the app. Hence the file provider is empty.
- LocalRetriever.java - this file searches the public archive file downloaded in DownloadActivity for the URL corresponding to the artifact ID entered in ManualActivity.
- MagnifyingGlass.java - this file contains logic for magnifying the image being color corrected at the point where the user is holding their finger. This helps them select a white pixel if the color chart is small.
- StateStatic.java - this file contains static variables shared between the rest of the files.
- Syncable.java - an interface representing a syncable object
- Taggable.java - an interface for extending an image to have an id tag.