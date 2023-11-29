source build-global.properties
source build-local.properties

export home=`pwd`/..

if [[ ! "x" = "x${ant_home}" ]]; then
  export ANT_HOME="${ant_home}"
  export PATH="${ANT_HOME}/bin:${PATH}"
  fi
if [[ ! "x" = "x${java_home}" ]]; then
  export JAVA_HOME="${java_home}"
  export PATH="${JAVA_HOME}/bin:${PATH}"
  fi

alias grapher='java -jar ../lib/Grapher.exe.jar'

echo "commands: grapher"
