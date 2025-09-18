# chatty-catty
AI chat tool for SWFE 503, Fall 2025

![Alt text](doc/resources/media/chatty-catty-logo.jpg)

## Introduction

This is an application for serving AI responses to questions related to the University of Arizona's SWFE degrees. 

## Requirements

1. [Git Client](http://git-scm.com)
2. [Java Development Kit](https://www.oracle.com/java/technologies/downloads/)
3. Development environment (choose one):
   1. [Visual Studio Code](https://code.visualstudio.com/download)
   2. [JetBrains IntelliJ](https://www.jetbrains.com/idea/download)
   3. [Eclipse](https://www.eclipse.org/downloads/)
4. TODO...
   1. Dependency installers like [Maven](https://maven.apache.org/download.cgi?) (Java) or [npm](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) (Node) 

## Development instructions

To get the code and application, you'll need [Git](https://git-scm.com).

Run the following at the command line:
```bash
# Clone this repository
$ gh repo clone agile-wildcats-082025503/chatty-catty

# Go into the repository
cd chatty-catty

# Install dependencies
mvn clean install

# Run the app
mvn spring-boot:run
```
## Testing It Out
As of this writing, the app only tells jokes. Open a browser to the following url to see what it has to say:
http://localhost:8080/ai/chat/string

## Git Commands
Switch to a branch before making any changes
```
git checkout existing-branch
git checkout -b new-branch-name
```
Pull the latest code to an existing branch
```
git checkout main
git pull origin main
git checkout your-branch
git merge main
```
Check in changes:
```
git add .
git commit -m "Description of changes"
git push origin your-branch
```
After checking in changes, it will display a URL to open to get the code changes reviewed and approved.
