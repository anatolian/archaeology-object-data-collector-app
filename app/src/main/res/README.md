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

App Settings
  - activity_settings.xml - settings screen where the user can change the database URL, connect to a scale, and change the camera address

Data lookup and modification
  - activity_ceramic_input.xml - screen for user to select primary keys of the artifact they want to find based on UTM schema (Hemisphere, Zone, Easting, Northing, Find).
  - activity_utm_object_detail.xml - screen that displays object information fetched from Heroku on UTM schema finds
  - activity_archon_object_detail.xml - screen for photographing objects under the Archon.Find schema
  - approve_photo_dialog.xml - dialog box asking user to confirm that the picture they took looks fine
  - record_weight_dialog.xml - dialog box for reading object weights from the scale or inputting them manually
  - activity_remote_camera.xml - interface for interacting with the remote camera
  - activity_camera_ui.xml - the main camera screen the user sees after connecting to Heroku in activity_initial.xml
  - list_item.xml - container view for an item in a list.
  - photo_fragment.xml - container view to hold artifact photo fetched from the webpage.
  - spinner_item.xml - entry in a spinner
  
## XML directory

This directory contains a required xml file for the FileProvider used by the app to provision external storage. Do not modify this file.
