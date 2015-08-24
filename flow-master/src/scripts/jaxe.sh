#!/bin/sh

dossiercourant=`pwd`

# ajout du chemin complet vers les fichiers en parametre
parametres=
for arg in "$@" ; do
  if expr "$arg" : '/.*' > /dev/null; then
    parametres="$parametres \"$arg\""
  else
    parametres="$parametres \"$dossiercourant/$arg\""
  fi
done

# resolution des liens - $0 peut etre un lien symbolique
if [ -z "$JAXE_HOME" -o ! -d "$JAXE_HOME" ] ; then
  PRG="$0"
  progname=`basename "$0"`

  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
    else
    PRG=`dirname "$PRG"`"/$link"
    fi
  done

  JAXE_HOME=`dirname "$PRG"`

  # chemin absolu
  JAXE_HOME=`cd "$JAXE_HOME" && pwd`
fi

# recherche de la commande java
if [ -n "$JAVA_HOME"  ] ; then
  if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
    # IBM's JDK on AIX uses strange locations for the executables
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
else
  JAVACMD=`which java 2> /dev/null `
  if [ -z "$JAVACMD" ] ; then
      JAVACMD=java
  fi
fi

# Jaxe doit etre lance dans son repertoire
cd $JAXE_HOME

execution="exec \"$JAVACMD\" -Xmx256m -jar lib/Jaxe.jar $parametres"
eval $execution
