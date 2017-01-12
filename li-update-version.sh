#!/usr/bin/env bash

base_version=1.21.0

patch_count=`git log --oneline li-${base_version}-base..HEAD | wc -l `

echo "Found ${patch_count} patches applied to base"

li_version="${base_version}.${patch_count}"

echo "New version: ${li_version}"

echo "Updating version info..."

# perl -p -i -e "s/\\Q${base_version}\\E/${li_version}/g" `find . -name "pom.xml"`
# The following command does it as per how maven wants us to do a version change

mvn versions:set -DnewVersion=${li_version} -DgenerateBackupPoms=false

# Note: this is for building the artifact-spec in the hive multiproduct
echo "Creating build.properties file with new version info..."
cat > build.properties <<EOF
version=${li_version}
EOF

echo "Done!"
