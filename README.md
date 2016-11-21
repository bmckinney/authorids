# Author Identifiers
===============

An API-driven application to search for common researcher and author identifiers.

## Getting Started

### On a Mac Using [Homebrew](http://brew.sh/)

`/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"`

`brew tap caskroom/cask`

`brew tap caskroom/versions`

`brew cask install java` (installs JDK 8)

`brew install maven`

`brew install git`

## Build

`mvn package`

## Run

`java -jar target/authorids-0.1-SNAPSHOT.jar`

Then go to:

[http://localhost:7777/search.html](http://localhost:7777/search.html)
