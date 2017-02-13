# gamaplugin
This repository contains all feature that connect Gen* api to the Gama platform 

# Information about infrastructure of plugin 

- `/build.gradle` : Define the maven dependencies site and the dependencies needed to merge the p2/maven repository  (__goomph__) and build the p2 site (__bnd-platform__). 

- `genstar.plugin.bundle-all/build.gradle` : Compile sources from plugin, generate gaml primitive, and create an osgi bundle of genstar plugin : `genstar.plugin.bundle-all`.

- `genstar.plugin.platform/build.gradle` : Create the p2 site for the genstar-plugin bundle.

- `target.p2/build.gradle` : Define repository for p2 dependencies

- `target.maven/build.gradle` : Converts maven dependencies into OSGi bundles

# Prerequisites

Install [gradle 3.3](https://docs.gradle.org/current/userguide/installation.html) on your computer, or use directly the gradle eclipse or intelij plugin.

## How to IDE ?

- If you use eclipse, you need to run 'gradle cleanEclipse eclipse' in root folder before importing project to generate good classpath, etc. needed by eclipse.
- If you use intellij IDEA, nothing to do, only import project.

## How to compile with gradle ?

Into root folder run the command `gradle clean build`

## How to produce p2 repository site ?

Go to `/genstar.plugin.platform/` path and run the command `gradle updatesite`

## How to deploy ?

Add your correct credential (contact a team member) into `/genstar.plugin.platform/gradle-local.properties` like this

``` 
genstarPassword=mycorrectcredential
```

- In terminal, or directly in your IDE, run the `gradle deploy` command into the `/genstar.plugin.platform/` folder. 
- The p2 site created in the previous section is updated with ssh to __http://genstar.unthinkingdepths.fr__
 
## Into Gama

Download and run the release or the latest version of Gama simulation software directly on [github](https://github.com/gama-platform/gama/releases) 

- Go to `help > install new software` and add the repository __http://genstar.unthinkingdepths.fr__.
- Install the feature proposed. 
