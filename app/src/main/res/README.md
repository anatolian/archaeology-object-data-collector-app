# Overview

This document provides a brief overview of the resources folder. This folder contains:

- Animations
- Images
- Styles
- Colors
- Strings
- Layouts
- Menus

## Changing the App Icon

To change the app icon, replace ic_launcher.png under all mipmap folders.

## Changing other icons used within the App

Any other icons and images that are used within the app will appear under the drawable-v24 folder. Replace all occurences of an icon or image to change it within the app.

## Animations

The configurations for the animations between the favorites, history, and camera activities can be found in the anim directory.

## Style

The themes used within the app are present in styles.xml in the values folder.

## Colors

The color palette of the overall application, and other specific colors within the screens are defined in colors.xml under the values folder.

## Strings and Translations

All text appearing withing the app can be found in values/strings.xml.

To change a string, it would have to be changed in all strings.xml for every language.

## Layouts

The XML layouts for all the screens are contained here.

Splash screen
  - activity_download.xml and activity_initial.xml correspond to the initial screen
  
Public archive lookup
  - activity_camera_ui.xml - the main camera screen the user sees after connecting to Heroku in activity_initial.xml
  - activity_favorite.xml - the user's favorites screen reached by swiping right from the camera ui
  - activity_history.xml - the user's lookup history reached by swiping left from the camera ui
  - activity_manual.xml - the page where the user can manually enter an artifact number or correct the OCRed number from the preview
  - activity_search.xml - a container activity for rendering archive webpages
  - list_item.xml - container view for an item in a list. Used to render the history and favorites screens.
  - photo_fragment.xml - container view to hold artifact photo fetched from the webpage.

App Settings
  - activity_settings.xml - settings screen where the user can change the database URL, connect to a scale, and change the camera address

Data lookup and modification
  - activity_ceramic_input.xml - screen for user to select primary keys of the artifact they want to find. Currently hardcoded for primary keys easting, northing, context, sample. Hopefully will be dynamically generated after the user enters their primary keys.
  - activity_object_detail.xml - screen that displays object information fetched from Heroku
  - activity_photos.xml - screen that allows users to color correct photos
  - approve_photo_dialog.xml - dialog box asking user to confirm that the picture they took looks fine
  - record_weight_dialog.xml - dialog box for reading object weights from the scale or inputting them manually
  - remote_camera_layout.xml - container view for the remote camera live view
  - activity_my_wi_fi.xml - interface for interacting with the remote camera
  
## XML directory

This directory contains a required xml file for the FileProvider used by the app to provision external storage. Do not modify this file.