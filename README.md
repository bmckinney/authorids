# Author Identifiers

An API-driven application to search for common researcher and author identifiers.

## Getting started

This project requires Java, Maven and Git. Follow the instructions below to install these tools.

### On a Mac using [Homebrew](http://brew.sh/)

`/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"`

`brew tap caskroom/cask`

`brew tap caskroom/versions`

`brew cask install java` (installs JDK 8)

`brew install maven`

`brew install git`

## Clone the project using git

`git clone https://github.com/xhanin/restx-singlejar.git`

## Build the project using Maven

`mvn package`

## Run the project using Java

`java -jar target/authorids-0.1-SNAPSHOT.jar`

Open the application in your browser: [http://localhost:7777/search.html](http://localhost:7777/search.html)
