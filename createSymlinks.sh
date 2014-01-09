#!/bin/sh

WEBAPP_ROOT=casino-web/src/main/webapp/
RIA_ROOT=../../../../casino-ria/target

echo "Removing existing symlinks"
find $WEBAPP_ROOT -type l | xargs rm -rfv

echo "Creating new symlinks"

# content store files
ln -sv $RIA_ROOT/casinoria.swf $WEBAPP_ROOT/casinoria.swf

echo "Symlink creation complete!"
