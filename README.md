# Media Manager

Have you ever wondered what recently acquired movies watching? Or what's the next unseen episode of TV shows you're watching? Have you ever wanted to stop music when you quit home? And replay when you came back ?

MediaManager is plugin-architectured. Basically, it help you to manage your mediatech, with meta-data on each movie, show, ... Modules are plugged to core to provide controls on  your system :

 - Scanning movie folders (recursive)
 - Find extra movie data from TheMovieDatabase (poster, summary, ...)
 - Provide web based UI to navigate in the mediatech and start playing movie (with MPlayer)
 - Support Amarok: stop music before playing movie and control playing from UI
 - Resume playing movie, view count, ...

MediaManager is __not__ a media center. Its objectives is to be integrated in system context, not to provide alternative to players.

------------------------------------------
# Getting start

## How to install

Download binaries and extract zip content.

Add to PATH file _medima.sh_:
```bash
#!/bin/bash

MEDIMA_HOME="/opt/medima-current"
MEDIMA_BIN=$MEDIMA_HOME/medima.jar

/usr/bin/java -jar $MEDIMA_BIN --config /home/<user>/.medima/medima.properties $*
```

Properties could be:

```properties
## MEDIMA CONFIG

generic.mediamanager.root /media/medima-files
persistence.mongodb.databaseName mediamanager

daemon.remotecontrol.port 5899

webui.port 8090
```

## How to use it?

Start it with command:
```
medima.sh --start &
```

You can close your console after that.

To scan or refresh repertory, use command:
```
medima.sh --scan movie <repertory to scan>
```

Other information:
```
medima.sh --update
```

# Development and Architecture

## Medima V0.1

This is a first shoot release. Application is not completed.

Functionalities:
 - Scanning movie folders (recursive)
 - Find extra movie data from TheMovieDatabase (poster, summary, ...)
 - Provide web based UI to navigate in the mediatech and start playing movie (with MPlayer)
 - Support Amarok: stop music before playing movie and control playing from UI
 - Resume playing movie, view count, ...

## Roadmap

Indicative development roadmap is :

1. __version 0.2__ : _WEB-UI_
	- Full control on implemented functionalities: manage Root Directories, configure application, enrichment manual selection,
	show trailers, ...
	- Mobile version: responsive design
	- Update bootstrap and angular versions

1. __version 0.3__ : _TV Shows_
	- manage TV show and mangas : restart on next unseen episode
	- TO-GET and TO-WATCH lists : movies/shows to watch on theaters, or buy on DVD.

1. __version ?__ : 
	- provide mobile application for android

1. ... This is not the end !

## Plugins

I call it _plugins_, but there are _modules_ : we can't hot load one, and classpath have to change.

## How To ...

* __Contribute__	
	Don't hesitate to contact me ;) Push requests are welcome.

* __Build Media Manager__	
	Core project and plug-ins are using _Maven_. To compil project :
  
	> mvn clean install

You'll need _node.js_ installed with _bower_ and _grunt_.

* __Configure Media Manager__	
	TODO

* __Launch Media Manager__	
	TODO



