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

echo "Generating build version"
VERSION_PREFIX=$(grep -E "<calciteVersion>(.*)</calciteVersion>" pom.xml | cut -d'>' -f2 | cut -d'<' -f1)
GIT_COMMIT_ID=$(git rev-parse --short HEAD)
BUILD_VERSION=${VERSION_PREFIX}-${GIT_COMMIT_ID}
echo "Current build version: ${BUILD_VERSION}"
echo "Setting version in mvn (discard any changes to repository once publish is complete)"
mvn versions:set -DnewVersion="$BUILD_VERSION" -q -B
mvn versions:commit -q -B

echo "Publishing to LI bintray"
MVN_DEPLOY_BINTRAY_USER=$USER_NAME MVN_DEPLOY_BINTRAY_KEY=$API_KEY eval 'mvn deploy -s .lipublish/publishSettings.xml -DskipTests -q -DretryFailedDeploymentCount=5 -DaltDeploymentRepository=bintray-linkedin-maven::default::"https://api.bintray.com/maven/linkedin/maven/calcite/;publish=1;override=1"'
