**********************************************************
** Media Manager					**
**********************************************************

I - Object
----------------------------------------------------------

MediaManager's objectives are provide interfaces (web, android, ...) to
control other media player.

It facilitaties watching TV Series or Manga (remember last episode seen of
each series), switching between music and video, ...


II - Architecture
----------------------------------------------------------
Media Manager intended to be easily extending by plug-in.

It use CDI (Java EE - JSR 299) to manage plug-in and dispatch events. Only
principal fonctionnalities are hard coded in core.


III - How To ...
----------------------------------------------------------

	a) Build Media Manager
Core project and plug-ins are using Maven. To compil project :
$ mvn clean install

	b) Configure Media Manager
TODO

	c) Launch Media Manager
TODO


---------------------------------

Je ne me souviens jamais du numéro de l'épisode de chaque série que je regarde, ou des derniers films acquis que je n'ai pas encore vu. (ni même des albums fraichements téléchargés à écouter / playlistes préparées)
Le MediaManager se veut être un média-center, sans les players embarqués. Il aide à organiser les différentes bibliothèques (musiques, films, séries, mangas annimés, ...), et utilise les players préférés (via des plugins) pour les lire.