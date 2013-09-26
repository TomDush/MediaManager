#!/bin/bash

SRC_PATH="D:\Dev\git\MediaManager"
ZIP="${SRC_PATH}\mediamanager-daemon\target\MediaManager_0.1-SNAPSHOT.zip"

CURRENT_PATH=`pwd`

echo "Compile version..."
cd "$SRC_PATH"
mvn install -Dmaven.test.skip=true > "$CURRENT_PATH\log\compile.log"

echo "Install new version ..."
cd $CURRENT_PATH
rm -r bin
mkdir -p bin

cd bin
unzip "$ZIP" > /dev/null

echo "Done."

