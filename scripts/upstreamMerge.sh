#!/usr/bin/env bash

(
set -e
PS1="$"
basedir="$(cd "$1" && pwd -P)"
workdir="$basedir/work"
gitcmd="git -c commit.gpgsign=false"

updated="0"
function getRef {
    git ls-tree $1 $2  | cut -d' ' -f3 | cut -f1
}
function update {
    cd "$workdir/$1"
    $gitcmd fetch && $gitcmd clean -fd && $gitcmd reset --hard origin/master
    refRemote=$(git rev-parse HEAD)
    cd ../
    $gitcmd add --force $1
    refHEAD=$(getRef HEAD "$workdir/$1")
    echo "$1 $refHEAD - $refRemote"
    if [ "$refHEAD" != "$refRemote" ]; then
        export updated="1"
    fi
}

update Bukkit
update CraftBukkit
update Spigot

if [[ "$2" = "all" || "$2" = "a" ]] ; then
	update BuildData
fi
if [ "$updated" == "1" ]; then
    echo "Rebuilding patches without filtering to improve apply ability"
    cd "$basedir"
    ./gradlew cleanCache || exit 1 # todo: Figure out why this is necessary
    ./gradlew applyPatches -Dpaperweight.debug=true || exit 1
    ./gradlew -Ppaperweight.filter-patches=false rebuildPatches || exit 1
fi
)
