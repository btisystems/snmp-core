#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "btisystems/snmp-core" ] && [ "$TRAVIS_JDK_VERSION" == "oraclejdk7" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing site...\n"

  cp -R target/site $HOME/site-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/btisystems/snmp-core gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./site
  cp -Rf $HOME/site-latest ./site
  git add -f .
  git commit -m "Lastest site on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published site to gh-pages.\n"
  
fi
