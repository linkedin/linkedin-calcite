#!/usr/bin/env bash

li_version=$1

echo "Updating version info..."

# The following command does it as per how maven wants us to do a version change
mvn versions:set -DnewVersion=${li_version} -DgenerateBackupPoms=false

# Note: this is for building the artifact-spec in the calcite multiproduct
echo "Creating build.properties file with new version info..."
cat > build.properties <<EOF
version=${li_version}
EOF

echo "Done!"
