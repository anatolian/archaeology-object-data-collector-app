# Archaeological Artifact Recording - Android App

This Android app is designed to facilitate recording weights and images of archaeological artifacts stored in a Heroku database. The user can lookup an artifact by primary key(s)
and retrieve all stored information about the object. The app can communicate over Bluetooth with a Bluetooth enabled scale, measure the weight of their artifact, and store/update
the weight. Users can also take pictures with their hardware camera or WiFi Direct enabled camera and upload them to the database. Users can also color correct their images through
white-balancing.

For artifacts stored on public webpages (e.g. The University of Pennsylvania Digital Archives https://www.penn.museum/collections/), users can fetch and display these pages in app by
scanning a QR Code or entering an artifact key. Users outside of the Penn network will currently need to change the root URL (endResult[2] in https://github.com/anatolian/archaeology-object-data-collector-app/blob/master/app/src/main/java/com/archaeology/util/LocalRetriever.java) to that of their own pages. Hopefully, this step will be refactored so
users can enter this URL upon initial launch rather than modifying the code.

The corresponding PHP web services code can be found in the [archaeology-object-data-collector-service] repository. This code is designed to work with Heroku, but might be extended
to other data hosting platforms in the future.

In order to better enable reuse of this code base for other projects and data structures, the intent of this documentation is to briefly overview the flow of the application. Readme files within certain important subfolders also help provide details on the files in those folders.

## Project File and Folder Structure
This section provides a brief overview of the structure and location of the files in this Android app, as well as links to further information about each.

- The build settings file for this app details the target Android SDK version, the current app version name and number, dependencies, and other information.  This file is found here: [/app/build.gradle](https://github.com/anatolian/archaeology-object-data-collector-app/blob/master/app/build.gradle)

- The Java source code can be found here [/app/src/main/java/com/archaeology/](https://github.com/anatolian/archaeology-object-data-collector-app/tree/master/app/src/main/java/com/archaeology). That folder also contains an additional readme file for further information.

- Datasets for OCR of labels in images can be found here [/app/src/main/assets/](https://github.com/anatolian/archaeology-object-data-collector-app/tree/master/app/src/main/assets)

- Resources are the graphical elements of the app and include screen layouts, display text, colors, images, etc.  These can be found here: [/app/src/main/res/](https://github.com/anatolian/archaeology-object-data-collector-app/tree/master/app/src/main/res) That folder also contains an additional readme file specific to the Resources side of the app, for further information.

  - All text in app is stored in strings.xml. Adding other languages (currently only supports English) is achieved by adding a values subfolder and strings.xml specific to the
  language in [/app/src/main/res/](https://github.com/anatolian/archaeology-object-data-collector-app/tree/master/app/src/main/res).
  
  - Screen layouts are .xml files that determine how other resources appear on each screen
    
- An Android manifest file contains information about which workflows and Java classes, known as Actvities, need to be packaged into the application, and the overall permissions that the application requires from the device. The manifest file can be found here: [/app/src/main/AndroidManifest.xml](https://github.com/anatolian/archaeology-object-data-collector-app/blob/master/app/src/main/AndroidManifest.xml).

- Assets pertaining to lookup history and favorite artifacts can be found here [/archres/](https://github.com/anatolian/archaeology-object-data-collector-app/tree/master/archres).
These features communicate with a local PostgreSQL database and need to be stored separately from the rest of the assets.

# LICENSE

The use of this project is governed by the license found [here](https://github.com/anatolian/archaeology-object-data-collector-app/blob/master/LICENSE)