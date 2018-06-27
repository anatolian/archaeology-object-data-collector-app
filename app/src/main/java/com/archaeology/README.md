# Code Overview

This folder contains all the Java code for the application, divided into principal components.
Below is a brief overview of what each class does:

models - this directory contains wrapper classes for Heroku response objects
  - ImageResponseWrapper.java: Wrapper interface for downloading images from the camera
  - JSONObjectResponseWrapper.java: Wrapper interface for fetching JSON objects from the camera
  - StringObjectResponseWrapper.java: Wrapper interface for fetching Strings from Heroku

services - this directory contains classes that communicate with Heroku, PostgreSQL, Bluetooth, or WiFi Direct
  - BluetoothService.java: Connects to the Bluetooth scale
  - NutriScaleBroadcastReceiver.java: Receives and parses Bluetooth messages from the scale
  - PicassoWrapper.java: Wrapper for Picasso, a library that handles asynchronous image rendering
  - SimpleLiveViewSlicer.java: Sony written API for communicating with the remote camera
  - VolleyStringWrapper.java: Wrapper for String GET requests to the web service
  - VolleyWrapper.java: Wrapper for communications with the remote camera and web service
  - WiFiDirectBroadcastReceiver.java: Service in charge of receiving data from the camera

ui - This directory contains all files that correspond to an element that the user sees. All activities end with "Activity"
  - CameraUIActivity.java: The screen used for taking a picture of an object to lookup, scanning QR codes, or transitioning to CeramicInputActivity
  - CeramicInputActivity.java: A page where the user can lookup a UTM item by its primary key(s). Takes users to ObjectDetailActivity.
  - InitialActivity.java: A screen where the user enters the URL for the Heroku database their data is hosted on and selects either UTM or Archon.Find for their schema. Launches CameraUIActivity when the connection succeeds.
  - MagnifyingGlass.java: This file contains logic for magnifying the image being color corrected at the point where the user is holding their finger. This helps them select a white pixel if the color chart is small.
  - ObjectDetailActivity.java: Takes the user to a RemoteSonyCameraActivity implementation based on the camera selected in SettingsActivity. Schema specific implementations are selected based on the schema selected in InitialActivity.
  - ArchonObjectDetailActivity.java: Activity for photographing objects under the Archon.Find schema
  - UTMObjectDetailActivity.java: An activity that displays all of the fetched data on an object selected in CeramicInputActivity. Allows users to record object weights with the digital scale.
  - PhotoFragment.java - a container view that Picasso loads images asynchronously into
  - RemoteSonyCameraActivityFactory.java: Produces a specific implementation of RemoteSonyCameraActivity depending on the selected camera in SettingsActivity.
  - RemoteSonyCameraActivity.java: A UI for operating a Sony API enabled camera. Extended for specific implementations for different cameras.
  - RemoteSonyAlpha7Activity.java: An implementation of RemoteSonyCameraActivity for the Sony Alpha 7.
  - RemoteSonyQX1Activity.java: An implementation of RemoteSonyCameraActivity for the Sony QX1.
  - SettingsActivity.java - where users can configure the Bluetooth connection, select a remote camera, toggle color correction, and set the camera remote address.
  - SimpleStreamSurfaceView.java - a container view that Sony's API loads the camera's live feed into

util - this directory contains miscellaneous helper libraries for other files
  - CheatSheet.java: This file contains commonly used helper methods. See the javadocs for their functions
  - ImageFileProvider.java: A file provider required for Nougat+ compatibility. Do not modify this file.
  - StateStatic.java: This file contains static variables shared between the rest of the files.
