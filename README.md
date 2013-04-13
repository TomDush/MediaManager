# Media Manager

Have you ever wondered what recently acquired movies watching? Or what's the next unseen episode of TV shows you're watching? Have you ever wanted to stop music when you quit home? And replay when you came back ?

MediaManager is plugin-architectured. Basically, it help you to manage your mediatech, with meta-data on each film, series, ... Modules are plugged to core to provide controls on  your system :

* get more meta-data on yours medias
* play media with your favorite player and control it
* automate recurrent actions like stop playing music before see film, start playing when coming back home, ...

MediaManager is __not__ a media center. Its objectives is to be integrated in system context, not to provide alternative to players.

------------------------------------------

# Development and Architecture

## Roadmap

Indicative development roadmap is :

1. __version 0.1__ : application core. 
	- Scan film and series and persist its to mediatech.
	- use module to find out media's meta-data

	This version will have embedded modules :
	
	- to get meta-data
	- UI module to provide web interface

1. __version 0.2__ : connection to players
	- manage TV show and mangas : restart on next unseen episode
	- control _MPlayer_ to play films  and series
	- control _Amarok_ (music player)

1. __version ?__ : 
	- provide mobile application for android
	- TO-WATCH list : films/shows to watch on theaters, or buy on DVD.

1. ... This is not the end !

## Plugins

I call it _plugins_, but there are _modules_ : we can't hot load one, and classpath have to change.

## How To ...

* __Contribute__	
	Don't hesitate to contact me ;) Push requests are welcome.

* __Build Media Manager__	
	Core project and plug-ins are using _Maven_. To compil project :
  
	> mvn clean install

* __Configure Media Manager__	
	TODO

* __Launch Media Manager__	
	TODO



