#!/usr/bin/env bash

###
# This script clones and builds Lider Ahenk core & plugin projects
# then refreshes e4 p2 repository
#
# Therefore it needs a stable Internet connection and a running HTTP server.
#
###

# Set up bash to handle errors more aggressively - a "strict mode" of sorts
set -e # give an error if any command finishes with a non-zero exit code
set -u # give an error if we reference unset variables
set -o pipefail # for a pipeline, if any of the commands fail with a non-zero exit code, fail the entire pipeline with that exit code

#
# Common variables
#
# You need to generate 'personal access token' with 'repo' scope.
USERNAME=YOUR_USER_NAME
PERSONAL_ACCESS_TOKEN=YOUR_TOKEN
NAMESPACE="Agem-Bilisim"
BASE_PATH="https://api.github.com/"
PROJECT_SELECTION="select( .name | contains(\"lider-ahenk-\") )"
PROJECT_PROJECTION="{ \"path\": .full_name, \"git\": .ssh_url }"
LIDER_CONSOLE_CLONE_URL="https://github.com/Agem-Bilisim/lider-console.git"
CLONE_PATH=/tmp/lider-console-repo/
FILENAME=$CLONE_PATH"repos.json"
REPO_NAME="lider-console-repo"

#
# Remove on error
#
trap "{ rm -rf $CLONE_PATH; }" EXIT

#
# Install dependencies if necessary & create directory
#
echo "Checking dependencies..."
apt-get install -y --force-yes maven jq curl sed xmlstarlet rsync
echo "Checked dependencies."
mkdir -p "$CLONE_PATH"

#
# Clone (or update) lider-console
#
GIT_URL=$(echo "$LIDER_CONSOLE_CLONE_URL" | sed -E "s/https:\/\//https:\/\/$PERSONAL_ACCESS_TOKEN@/g")
if [ ! -d "$CLONE_PATH"lider-console ]; then
	echo "Cloning lider-console ( $GIT_URL )"
	cd "$CLONE_PATH" && git clone "$GIT_URL" --quiet
else
	echo "Updating lider-console"
	cd "$CLONE_PATH"lider && git pull --quiet
fi

#
# Generate third-party dependencies
#
echo "Generating third-party dependencies..."
cd "$CLONE_PATH"lider-console/lider-console-dependencies
mvn clean p2:site
echo "Generated third-party dependencies."

#
# Start jetty server for Tycho to use generated dependencies
#
# Kill old process if exists
#OLD_J_PID=$(pgrep -f jetty | head -n1)
#if [ -n "${OLD_J_PID+x}" ]; then
#	kill $OLD_J_PID
#fi
echo "Starting server for Tycho..."
mvn jetty:run &
J_PID=$!
echo "Started server."

#
# Build lider-console
#
echo "Building lider-console project..."
cd "$CLONE_PATH"lider-console
# Build except update-site module! It requires other plugins to be built...
mvn -pl !lider-console-update-site clean install -DskipTests
echo "lider-console project built successfully."

#
# Kill jetty server process
#
echo "Shutting down server..."
kill $J_PID
echo "Server shut down."

#
# Find lider ahenk plugin projects
#
echo "Trying to find Lider Ahenk projects... Make sure you have a stable Internet connection."
curl -su "$USERNAME":"$PERSONAL_ACCESS_TOKEN" "$BASE_PATH"orgs/"$NAMESPACE"/repos \
	| jq --compact-output '.[] | select( .name | contains("lider-ahenk-") ) | select( .name | contains("-plugin") ) | { "path": .full_name, "git": .clone_url }' > "$FILENAME"
echo "Found Lider Ahenk projects."

while read repo; do
	REPO_PATH=$(echo "$repo" | jq -r ".path")
	REPO_FULL_PATH=$CLONE_PATH$REPO_PATH
	GIT_URL=$(echo "$repo" | jq -r ".git" | sed -E "s/https:\/\//https:\/\/$PERSONAL_ACCESS_TOKEN@/g")

	if [ ! -d "$REPO_FULL_PATH" ]; then
		echo "Cloning $REPO_PATH ( $GIT_URL )"
		(cd "$CLONE_PATH" && git clone "$GIT_URL" --quiet) &
	else
		echo "Updating $REPO_PATH"
		(cd "$REPO_FULL_PATH" && git pull --quiet) &
	fi

done < "$FILENAME"

#
# Wait for update & clone operations
#
echo "Waiting for update & clone operations..."
wait

#
# Build projects
#
echo "Building Lider Ahenk projects...This may take a while."
find "$CLONE_PATH" -maxdepth 1 -type d -name 'lider-ahenk-*' -exec sh -c '
		for i do
			BASE_MODULE_NAME=$(echo "$i" | sed -E "s/.*lider-ahenk-([a-zA-Z-]*)-plugin/\1/g")
			CLONE_PATH=$(echo "$i" | sed -E "s/(.*)lider-ahenk-.*/\1/g")
			if [ "$BASE_MODULE_NAME" != "remote-access" ]; then
				continue
			fi
			LIDER_CONSOLE_MODULES=lider-console-"$BASE_MODULE_NAME",lider-console-"$BASE_MODULE_NAME"-feature
			J_PID=-1
			if [ -d "$i"/lider-console-"$BASE_MODULE_NAME"-dependencies ]; then
				echo "Project has third-party dependencies ( $BASE_MODULE_NAME ). Generating deps & starting server..."
				cd "$i"/lider-console-"$BASE_MODULE_NAME"-dependencies && mvn clean p2:site
				mvn jetty:run &
				J_PID=$!
			fi
			cd "$i" && mvn -pl .,$LIDER_CONSOLE_MODULES clean install -DskipTests
			if [ -d "$i"/lider-console-"$BASE_MODULE_NAME"-dependencies ]; then
				echo "Shutting down server..."
				kill $J_PID
				echo "Copying dependency metadata to lider-console..."
				DEPENDENCIES=$(xmlstarlet sel -t -m "//project/build/plugins/plugin/executions/execution/configuration/artifacts/artifact" -v "." -n "$i"/lider-console-"$BASE_MODULE_NAME"-dependencies/pom.xml)
				echo $DEPENDENCIES
				cd "$CLONE_PATH"lider-console/lider-console-dependencies
				for DEPENDENCY in $DEPENDENCIES ; do
					echo $DEPENDENCY
					xmlstarlet ed -s /project/build/plugins/plugin/executions/execution/configuration/artifacts -t elem -n artifact -v "" pom.xml > tmpxml
					xmlstarlet ed -s "/project/build/plugins/plugin/executions/execution/configuration/artifacts/artifact[last()]" -t elem -n id -v "$DEPENDENCY" tmpxml > pom.xml
					rm tmpxml
				done
				rsync -arz "$i"/lider-console-"$BASE_MODULE_NAME"-dependencies/repo/ "$CLONE_PATH"lider-console/lider-console-dependencies/repo
			fi
		done' sh {} +
echo "Lider Ahenk projects are built successfully."

#
# Re-generate third-party dependencies (since they may have changed)
#
echo "Generating third-party dependencies..."
cd "$CLONE_PATH"lider-console/lider-console-dependencies
mvn clean -U p2:site
echo "Generated third-party dependencies."

#
# Start Jetty server again 
# so that Tycho can search for deps in local m2 repo when it couldn't find them in the server
#
echo "Starting server for Tycho..."
mvn jetty:run &
J_PID=$!
echo "Started server."

#
# Generate p2 repository now that we have all the plugins and lider-console
#
echo "Generating p2 repository..."
cd "$CLONE_PATH"lider-console
mvn -pl .,lider-console-update-site clean install -DskipTests
echo "Generated p2 repository."

#
# Kill jetty server process
#
echo "Shutting down server..."
kill $J_PID
echo "Server shut down."

# Copy resulting files
echo "Copying generated p2 repository to $CLONE_PATH..."
cp -rf "$CLONE_PATH"lider-console/lider-console-update-site/target/*.zip "$CLONE_PATH"
echo "Copied generated p2 repository."

echo "Done. Generated/updated lider-console p2 repository."
