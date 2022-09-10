#!/bin/bash

if [ -z "$1" ] || [ -z "$2" ] 
then
  echo "Usage: $0 <RELEASE_VERSION> <DEVELOPMENT_VERSION>"
  exit 1
fi

RELEASE_VERSION="$1"
DEVELOPMENT_VERSION="$2"

export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-11.0.16.101-hotspot"

mvn release:clean release:prepare release:perform -B -DreleaseVersion="$RELEASE_VERSION" -DdevelopmentVersion="$DEVELOPMENT_VERSION"