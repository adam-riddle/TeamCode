Welcome to the Steel Serpents' First Tech Challenge TeamCode for the 2016-2017 competition. 

## Installation
Open Git Shell

Use the cd command in Git Shell to go to the folder where you want the project.

Download the lastest code from FIRST
`git clone https://github.com/ftctechnh/ftc_app.git`

Enter the newly downloaded directory with `cd ftc_app`.

Download our latest FTCVision code.
```
git submodule init
git submodule add https://github.com/SteelSerpents/FTCVision ftc-vision
```

Open `FtcRobotController/src/main/AndroidManifest.xml` in a text editor like notepad++ (https://notepad-plus-plus.org/download/v7.1.html) and add the following line one line above the </application> tag and save the file.

`<uses-permission android:name="android.permission.CAMERA" android:required="true" />`

Open `ftc_app/settings.gradle` in a texteditor.

At the bottom of the `settings.gradle` file add the following lines and save the file
```
include ':opencv-java'
include ':ftc-visionlib'
include ':ftc-cameratest'
project(':opencv-java').projectDir = new File('ftc-vision/opencv-java')
project(':ftc-visionlib').projectDir = new File('ftc-vision/ftc-visionlib')
project(':ftc-cameratest').projectDir = new File('ftc-vision/ftc-cameratest')
```

And finally download this team code by using the cd command to get to the teamcode folder and then cloning this project.
```
cd TeamCode\src\main\java\org\firstinspires\ftc\teamcode
git clone https://github.com/SteelSerpents/TeamCode.git
```

You can open this project in Android Studio by starting Android Studio and opening the ftc_app folder you downloaded.

Android Studio may require some updates before you can build the project. In the bottom pannel you will be given a link that will allow Android studio to update its self, you will just need to click "Next" on the wizard. After Android Studio updates you can test to see if the project builds by going to the build menu and selecting "clean project".

If your project still won't build go to the Tools menu and under "Android" select "Sync Project with Gradle Files" and compleate any updates that are needed.

## Updating code

Since our code is now broken up into 3 parts, `ftc_app`, `ftc-vision`, and `TeamCode` we can now update these three independently of each other. 
### ftc_app
To update ftc_app simply navigate to the `ftc_app` folder and type

```
git stash
git pull
git stash pop
```
This will `stash` our local changes, download the latest changes from ftc, and `pop` our local changes back onto the code from ftc.
### ftc-vision
To update the vision code navigate to the ftc-vision folder and type

`git pull`

### TeamCode
To update our team code (which you will probably do most frequently) navigate to our team code directory. From the ftc_app directory type 

```
cd .\TeamCode\src\main\java\org\firstinspires\ftc\teamcode\
git pull
```

You may encounter an error about local changes. It's saying that you've made changes to your code on your computer that git doesn't want to overwrite. If you don't care about these changes and just want to download the latest team code just type

```
git stash
git pull
```

This will `stash` all changes on your machine. If you want those changes back you can type `git stash pop` to apply those changes onto the code after you've updated it.



