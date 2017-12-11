Archaeology README
Authors: Chris Besser, Xin Guo, Long Nguyen, Kevin Trinh

GENERAL SUMMARY
---------------
Archaeological items and relevant information (internal/external color, weight, photo) are stored in a database. This app is able to look up an item using different fields (easting, northing, context number, and sample number), view/edit current information, and/or add missing information. The app is simulantenously connected to a bluetooth scale and a wifi direct camera device to aid in documenting the item in the event these fields are missing. The user can manually input the internal/external color.

USER INTERACTION
----------------
Once connected to the database using a hosting service (heroku), the app should allow for item lookup using OCR on the museum's sample number, manually entering this number, or manually look up. UI features will allow them to view and/or edit an item's entry to either change current fields or add missing ones (using the connected accessories if necessary).

TECHNICAL DETAILS
-----------------
UI (16)
-------
CameraDialog - handles connection to Sony QX1 camera, mini camera app that allows you to use camera functions (take photo, start liveview, stop liveview, zoom in, zoom out)

CameraUIActivity - displays the OCR view and serves as the main view of the app. This class
can call the HistoryActivity and FavoriteActivity by swiping left or right and can call
ManualActivity or use QR to search the database and view the result.

CeramicInputActivity - pulls item information from the database using spinners to select easting, northing, context number, and sample number

DownloadActivity - used to copy and download the db

FavoriteActivity - displays the favorite activity. This class uses HistoryHelper to retrieve items
from the history database and display them.

HistoryActivity - displays the history activity. This class uses HistoryHelper to retrieve items
from the history database and display them.

InitialActivity - first screen, verifies connection to web service base URL, settings button (see SettingsActivity)

ManualActivity - handles manual entry of sample number to search for sample

MyWifiActivity - used to establish/handle connections with the WifiDirect camera

ObjectDetailActivity - allows user to record weight of object (manually or with bluetooth scale), shows color information, and photo manipulation (taking photos / color correction)

PhotoFragment - aids with image selection, manipulation, syncing, etc...

PhotosActivity - allows users to select images from a gallery to color correct or to view

SearchActivity - activity that displays the results based on the inputs to find a museum object

SettingsActivity - change the database URL, select the active camera (either remote or local), and the camera calibration interval if using a remote professional camera

SimpleStreamSurfaceView - handles live streaming functionality

TaggedImageView - allows users to tag images for search purposes


EXTERNAL LIBRARIES
------------------

VOLLEY
------
VolleyWrapper and VolleyStringWrapper - Two wrapper classes that utilize the Volley library, an HTTP library that makes networking tasks in Android applications easier.
These wrappers perform HTTP requests via Volley and use the returned data throughout the application for various uses. 

REQUIRED EXTERNAL LIBRARIES
---------------------------
	postgresql-42.0.0 - JAR file for handling postgres SQL queries
	eu.livotov.labs.android:CAMView:2.0.1@aar - live view from camera
	com.squareup.okhttp3:okhttp:3.7.0 - HTTP/HTTPS client for Android
	com.github.nisrulz:qreader:2.0.1 - QR code reader
	com.squareup.picasso:picasso:2.5.2 - library for easy loading and rendering of image resources into Android views
	com.squareup.retrofit2:retrofit:2.2.0 - library for handling BlueTooth connection to the scale
	com.squareup.retrofit2:converter-gson:2.2.0 - library for parsing JSON responses from the scale into usable data
	com.siclo.ezphotopick:library:1.0.7 - library for picking an image from the local file system