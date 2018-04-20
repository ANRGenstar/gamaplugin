# Temporary install procedure in GAMA

This install procedure has been tested the 19th of April 2018. It has many drawbacks (copy-paste of jars, incompatible with the continuous built, use of very heavy jars for genstar, with duplicated libraries, Eclipse-dependent...) but has the main advantage to integrate very well in a classical IDE for GAMA. So it is only temporary!

To use and develop the Genstar Plugin in GAMA, you need:
- GAMA in its development version
- the genstar library
- the genstar GAMA plugin


# 1. Install Eclipse and GAMA source code
cf. as presented here: https://github.com/gama-platform/gama/wiki/InstallingGitVersion

# 2. Get the source code of the genstar library and genstar plugin.

Clone the following GitHub repository Github: 
 * the genstar library:  https://github.com/ANRGenstar/genstar.git
 * [Optional] the genstar templates (i.e. examples): https://github.com/ANRGenstar/template.git
 * the gama plugin: https://github.com/ANRGenstar/gamaplugin.git

With Eclipse: 
* Open the Git perspective
* In the View Git Repositories, Click on the icon "Clone a Git Repository and add the clone to this view"

In the View Git Repositories, you should have 4 repositories: gama, gamaplugin, genstar and template.

# 3. Import the genstar library as Eclipse projects.
 * File > Import ...
 * [Window: Import] Select: Git / Projects from Git (Click on Next button)
 * [Window: Import Project from Git] Select: Existing local repository (Next)
 * [Window: Import Projects from Git] Select: genstar (Next)
 * [Window: Import Projects from Git] "Import existing Eclipse projects" should be checked and "Working Tree" selected (Next)
 * [Window: Import Projects from Git] Check "Search for nested projects", select the 5 projects (genstar-core, genstar-gospl, genstar-spin, genstar-spll and parent) (Finish)


# 4. Import the genstar plugin as Eclipse projects.
 * File > Import ...
 * [Window: Import] Select: Git / Projects from Git (Click on Next button)
 * [Window: Import Project from Git] Select: Existing local repository (Next)
 * [Window: Import Projects from Git] Select: gamaplugin (Next)
 * [Window: Import Projects from Git] "Import existing Eclipse projects" should be checked and "Working Tree" selected (Next)
 * [Window: Import Projects from Git] Check "Search for nested projects", select the last project (genstar.plugin.bundle-all, the last one, the deepest one, among the 2 available) (Finish)

# 5. Import the template as Eclipse projects. (similarly)

# 6. Associate genstar library with the GAMA plugin.
The GAMA Plugin comes with all the necessary jar libraries and in particular the genstar library.
  

If you want to modify the genstar library, after a modifications you need to build again the genstar libraries:
  * right-click on the modified plugin > Run As > Maven install
  * in the genstar project, in the target folder, copy the genstar*.jar and paste it in the plugin lib_genstar folder.

In the case where a ClassNotFoundException appears in GAMA, when running a model using Genstar operators, it could be due to a missing .jar in the gamaplugin plugin. You should thus need to add the missing library in the lib folder (you also have to add it in the plugin.xml, in the classpath pane).

A sure case to avoid any missing library, you can follow the following procedure, to prouce the genstar library with all the needed libraries:
  * Right-click on the `parent` plugin > Run As >  Maven install
  * in the 4 genstar projects (genstar-core, -gospl, -spll, -spin), in the target folder, take the genstar-*-jar-with-dependencies.jar and paste them in the plugin `lib_genstar` folder.
  * add these 4 libraries to the classpath of the gamaplugin plugin and to the classpath in the plugin.xml.

# 7. Ask GAMA to call the plugin at start
  * In ummisco.gama.feature.core.extensions plugin, feature.xml, add the genstar plugin to the Included Plug-ins




# 
# 

Below are the old install instructions. 

# Plugin Genstar for GAMA platform simulation.

[![Build Status](https://travis-ci.org/ANRGenstar/gamaplugin.svg?branch=master)](https://travis-ci.org/ANRGenstar/gamaplugin)

This repository contains all features that connect [GenStar](http://www.agence-nationale-recherche.fr/Projet-ANR-13-MONU-0006) project and [Api](https://github.com/ANRGenstar/) to the [Gama simulation platform](http://gama-platform.org/)

In order to build the **genstar-plugin** for Gama, the Gradle build system use multiples dependencies from :
- [Genstar](https://github.com/ANRGenstar/genstar) bundles, built with Maven, and stored/updated on [Bintray repository](https://bintray.com/anrgenstar)
- [Gama](https://github.com/gama-platform/) bundles, built with Maven and stored/updated on [Eclipse P2 repository](http://gama-platform.org/updates/)

# A - Information about structure of folder

- `/build.gradle` : Define the maven dependencies site and the dependencies needed to merge the p2/maven repository  (__goomph__) and build the p2 site (__bnd-platform__). 

- `genstar.plugin.bundle-all/build.gradle` : Compile sources from plugin, generate gaml primitive, and create an osgi bundle of genstar plugin : `genstar.plugin.bundle-all`.

- `genstar.plugin.platform/build.gradle` : Create the p2 site for the genstar-plugin bundle.

- `target.p2/build.gradle` : Define repository for p2 dependencies

- `target.maven/build.gradle` : Converts maven dependencies into OSGi bundles

# B - Prerequisites

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

You could also use the `gradle eclipse` command in root folder to generate *.classpath*, *.project* files for import into Eclipse.

# C - Build and/or deploy genstar-gama bundles

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
- Get the Genstar sftp password Genstar vault and copy/paste in place of __****__ in this command `gradle deploy -PcredentialsPassphrase= **** -i`

Actually we work to fix the P2 site __http://genstar.unthinkingdepths.fr__ but you could already
download a dev version of the plugin for gama at this url : __http://genstar.unthinkingdepths.fr/genstar-plugin.zip__

# D - Install genstar-gama-plugin plugin

## Install plugin into Gama

Download and run the release or the latest version of Gama simulation software directly on [github](https://github.com/gama-platform/gama/releases)

### Using P2 repository

- Go to `help > install new software` and add the web repository __http://genstar.unthinkingdepths.fr__.
- Install the feature proposed. 

### Using local updateSite

- Go to `help > install new software`, choose local, and add the folder `genstar.plugin.platform/build/updatesite` generated by Gradle.
- Install the feature proposed.

# E - Debug ?!

Some notes for genstar plugin future developper :)

## OSGI debug

It's very difficult to debug OSGI bundle when it failed during install or loading. You could debug OSGI directly into Gama using this command : `.\Gama -consolelog -console localhost:5467 -debug`

After that, connect to OSGI bus using `telnet localhost 5437` and then help yourself with [OSGI command](http://www.vogella.com/tutorials/OSGi/article.html)

## Manifest explanation

Here the specific command in [bnd](https://github.com/bndtools/bnd) section of `build.gradle` into `genstar.gama.bundle-all` folder :

- **Conditional-Package** at `!org.w3c.*, !org.xml.*, !javax.*, !org.jfree.*, !org.graphstream.*, !msi.*, *`: Static linking, see the [documentation](http://njbartlett.name/2014/05/26/static-linking.html) We remove all class given by some dependencies from the jar, especially some `msi.*` class which already exists in `msi.gama.core`.
- **Import-Package** at `!*` : All dependencies are normally included.
- **Require-Bundle** : We need `msi.gama.core` bundle, this bundle is given by Gama OSGI bus at runtime.

# F - Thanks to !!

- [@nedTwigg](https://github.com/nedtwigg) which release the great [goomph](https://github.com/diffplug/goomph) library
- [@Stempler](https://github.com/stempler) which release the great [bnd-platform](https://github.com/stempler/bnd-platform) library