#!/bin/bash

# variables
BASE_DIR=`pwd`
WAR=$BASE_DIR/$1
JETTY=$2

echo "BASE directory: $BASE_DIR"
echo "WAR location: $WAR"
echo "JETTY location: $JETTY"

# validate the paths

if [ ! -f $WAR ]
then
    echo "Web app could not be found..."
    exit
fi

if [ ! -f $JETTY/bin/jetty.sh ]
then
    echo "Jetty could not be found..."
    exit
fi

# 1st we need to stop jetty
echo "Stopping Jetty..."
$JETTY/bin/jetty.sh stop

echo "Removing old webapp and logs"
# 2nd we need to clear out old code from jetty
rm -rf $JETTY/webapps/root
rm -rf $JETTY/logs/*

# 3rd we need to move our webapp over to Jetty's webapps directory

echo "Re-creating webapp directory"
# recreate root webapp
mkdir $JETTY/webapps/root

echo "Pushd into webapps directory"
# go into that directory
pushd $JETTY/webapps/root

echo "Extracting new code into webapp directory"
# extract war into directory
jar xf $WAR

# back to where we were
popd

echo "Starting Jetty..."
# restarting jetty
$JETTY/bin/jetty.sh start > /dev/null 2>&1

# default exit call and we can call it a day
exit 0
