# gamaplugin

[![Build Status](https://travis-ci.org/ANRGenstar/gamaplugin.svg?branch=master)](https://travis-ci.org/ANRGenstar/gamaplugin)

This repository contains all feature that connect Gen* api to the Gama platform

In order to build the **genstar-plugin** for Gama, the Gradle build system use multiples dependencies from :
- [Genstar](https://github.com/ANRGenstar/genstar) bundles, built with Maven, and stored/updated on [Bintray repository](https://bintray.com/anrgenstar)
- [Gama](https://github.com/gama-platform/) bundles, built with Maven and stored/updated on [Eclipse P2 repository](http://gama-platform.org/updates/)

# Information about structure of folder

- `/build.gradle` : Define the maven dependencies site and the dependencies needed to merge the p2/maven repository  (__goomph__) and build the p2 site (__bnd-platform__). 

- `genstar.plugin.bundle-all/build.gradle` : Compile sources from plugin, generate gaml primitive, and create an osgi bundle of genstar plugin : `genstar.plugin.bundle-all`.

- `genstar.plugin.platform/build.gradle` : Create the p2 site for the genstar-plugin bundle.

- `target.p2/build.gradle` : Define repository for p2 dependencies

- `target.maven/build.gradle` : Converts maven dependencies into OSGi bundles

# Prerequisites

## Terminal (1)

- Follow [Gradle >= 3.x installation](https://docs.gradle.org/current/userguide/installation.html) instructions on your computer

## Gradle & Intellij IDE (2)

- Import project using Intellij.
- Configure Gradle options, use if it's possible the **Gradle wrapper** option during import of the project.

If it not works, install a local Gradle using Terminal like described in step **(1)**
then copy/paste the path given by `which gradle` in your terminal in your Intellij Gradle settings, in the **Local installation directory** box.

## Gradle & Eclipse IDE (3)

- Install [Gradle plugin](https://projects.eclipse.org/projects/tools.buildship) available on Eclipse Marketplace (already installed in Eclipse Oxygen).
- Import the project and config your Gradle, use if it's possible the **Gradle wrapper** option during import of the project.

If it not works, install a local Gradle using Terminal like described in step **(1)**
then copy/paste the path given by `which gradle` in your terminal in your Intellij Gradle settings, in the **Local installation directory** box.

## How to build genstar-plugin-all bundle ?

Using Eclipse/Idea build, or directly by using the terminal with the command `gradle clean build`,
you could find the final jar into `genstar-gama-plugin/genstar.plugin.bundle-all/build/libs`

## How to generate P2 Eclipse Site

Using Eclipse/Idea build, or directly by using the terminal with `genstar.plugin.platform:updateSite` command,
you could find the generated **update site** (or also know as [P2 Repository](http://www.vogella.com/tutorials/EclipseP2Update/article.html#creating-p2-update-sites)
into `genstar.plugin.platform/build/updatesite`

## How to generate P2 Eclipse Site and also deploy (project member only) ?

We use the plugin [gradle-credentials-plugin](https://github.com/etiennestuder/gradle-credentials-plugin) to encrypt the password during deploy on travis.

If you want to deploy with your local gradle you need to follow this procedure :

- Get the `gradle.xxx.encrypted.properties` file in the Genstar vault.
- Unzip it in your `~/.gradle/` folder.
- Get the Genstar sftp password Genstar vault and copy/paste in place of __****__ in this command 'gradle deploy -PcredentialsPassphrase= **** -i'

Actually we work to fix the P2 site __http://genstar.unthinkingdepths.fr__ but you could already
download a dev version of the plugin for gama at this url : __http://genstar.unthinkingdepths.fr/genstar-plugin.zip__

## Install plugin into Gama

### Using P2 repository
Download and run the release or the latest version of Gama simulation software directly on [github](https://github.com/gama-platform/gama/releases) 

- Go to `help > install new software` and add the web repository __http://genstar.unthinkingdepths.fr__.
- Install the feature proposed. 

### Using local updateSite

Download and run the release or the latest version of Gama simulation software directly on [github](https://github.com/gama-platform/gama/releases)

- Go to `help > install new software`, choose local, and add the folder `genstar.plugin.platform/build/updatesite` generated by Gradle.
- Install the feature proposed.

## Thanks to !

- [@nedTwigg](https://github.com/nedtwigg) which release the great [goomph](https://github.com/diffplug/goomph) library
- [@Stempler](https://github.com/stempler) which release the great [bnd-platform](https://github.com/stempler/bnd-platform)