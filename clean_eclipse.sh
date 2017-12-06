#!/bin/sh 
find . \( -name .classpath -o -name .project -o -name .settings -not -path "*/eclipse-downloads/*" \) -exec rm -rf {} \;
