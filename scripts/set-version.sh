#!/usr/bin/env bash

###
# This script sets version of Lider Console  and updates references as necessary.
#
# Please conform to semantic versioning 2.0 when setting the version:
# http://semver.org/
#
###

# Set up bash to handle errors more aggressively - a "strict mode" of sorts
set -e # give an error if any command finishes with a non-zero exit code
set -u # give an error if we reference unset variables
set -o pipefail # for a pipeline, if any of the commands fail with a non-zero exit code, fail the entire pipeline with that exit code

pushd $(dirname $0) > /dev/null
PRJ_ROOT_PATH=$(dirname $(pwd -P))
popd > /dev/null
echo "Project path: $PRJ_ROOT_PATH"

if [ $# -eq 0 ]; then
    echo "No arguments supplied. Please supply new version."
    exit 1
fi
NEW_VERSION=$1

#
# Install dependencies if necessary & create directory
#
echo "Checking dependencies..."
apt-get install -y --force-yes maven
echo "Checked dependencies."

echo "Setting version to $NEW_VERSION..."
cd "$PRJ_ROOT_PATH"
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$NEW_VERSION
echo "Version changed successfully."
