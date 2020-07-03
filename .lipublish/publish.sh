#!/usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to you under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

echo "!!This will change the pom.xml files and you will need to revert them. If you have local changes, exit now and stash them!!"

while [ -z "$USER_NAME" ]; do
  echo "Please provide your bintray username"
  read USER_NAME
done
while [ -z "$API_KEY" ]; do
  echo "Please provide your bintray API key"
  read -s API_KEY
  echo
done

echo "If you want a custom version suffix, please specify it. This is useful when generating artifacts for testing. Leave empty if creating a usable release"
read DEV_VERSION

echo "Generating build version"
# we first get calcite's version. We expect that this will be updated if we sync with Apache Calcite
VERSION_PREFIX=$(grep -E "<calciteVersion>(.*)</calciteVersion>" pom.xml | cut -d'>' -f2 | cut -d'<' -f1)
# next, we get the hash of the latest commit that tracks Apache Calcite. We expect that this will be updated if we sync with Apache Calcite
APACHE_CALCITE_LAST_COMMIT_HASH=$(grep -E "<calciteCommitHash>(.*)</calciteCommitHash>" pom.xml | cut -d'>' -f2 | cut -d'<' -f1)
# next, we count the number of commits we have made on top of Apache Calcite since the last sync.
GIT_COMMIT_COUNT=$(git rev-list --count $APACHE_CALCITE_LAST_COMMIT_HASH..HEAD)
# next, we create an internal version. 100 is an arbitrary seed
LI_INTERNAL_VERSION=$(($GIT_COMMIT_COUNT + 100))
# now we can construct a build version
BUILD_VERSION=${VERSION_PREFIX}.${LI_INTERNAL_VERSION}${DEV_VERSION}
echo "Current build version: ${BUILD_VERSION}"
echo "Setting version in mvn (discard any changes to repository once publish is complete)"
mvn versions:set -DnewVersion="$BUILD_VERSION" -q -B
mvn versions:commit -q -B

echo "Publishing to LI bintray"
MVN_DEPLOY_BINTRAY_USER=$USER_NAME MVN_DEPLOY_BINTRAY_KEY=$API_KEY eval 'mvn deploy -s .lipublish/publishSettings.xml -DskipTests -q -DretryFailedDeploymentCount=5 -DaltDeploymentRepository=bintray-linkedin-maven::default::"https://api.bintray.com/maven/linkedin/maven/calcite/;publish=1;override=1"'
