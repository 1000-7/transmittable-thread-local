#!/bin/bash
# SDKMAN! with Travis
# https://objectcomputing.com/news/2019/01/07/sdkman-travis
set -eEuo pipefail

[ -z "${__source_guard_37F50E39_A075_4E05_A3A0_8939EF62D836:+dummy}" ] || return 0
export __source_guard_37F50E39_A075_4E05_A3A0_8939EF62D836=true

# shellcheck source=common.sh
source "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")/common.sh"

__loadSdkman() {
    local this_time_install_sdk_man=false
    # install sdkman
    if [ ! -f "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
        [ -d "$HOME/.sdkman" ] && rm -rf "$HOME/.sdkman"

        curl -s get.sdkman.io | bash || die "fail to install sdkman"
        echo sdkman_auto_answer=true >>"$HOME/.sdkman/etc/config"

        this_time_install_sdk_man=true
    fi

    set +u
    # shellcheck disable=SC1090
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    "$this_time_install_sdk_man" && logAndRun sdk ls java
    set -u
}
__loadSdkman

jdks_install_by_sdkman=(
    7.0.282-zulu
    8.0.275-amzn

    11.0.11-zulu

    16.0.1-open
    17.ea.21-open
)
java_home_var_names=()

__setJdkHomeVarsAndInstallJdk() {
    blueEcho "prepared jdks:"

    JDK6_HOME="${JDK6_HOME:-/usr/lib/jvm/java-6-openjdk-amd64}"
    java_home_var_names=(JDK6_HOME)
    printf '%s :\n\t%s\n' "JDK6_HOME" "${JDK6_HOME}"

    local jdkNameOfSdkman
    for jdkNameOfSdkman in "${jdks_install_by_sdkman[@]}"; do
        local jdkVersion
        jdkVersion=$(echo "$jdkNameOfSdkman" | awk -F'[.]' '{print $1}')

        # jdkHomeVarName like JDK7_HOME, JDK11_HOME
        local jdkHomeVarName="JDK${jdkVersion}_HOME"

        if [ ! -d "${!jdkHomeVarName:-}" ]; then
            local jdkHomePath="$SDKMAN_CANDIDATES_DIR/java/$jdkNameOfSdkman"

            # set JDK7_HOME ~ JDK1x_HOME to global var java_home_var_names
            eval "$jdkHomeVarName='${jdkHomePath}'"

            # install jdk by sdkman
            [ ! -d "$jdkHomePath" ] && {
                set +u
                logAndRun sdk install java "$jdkNameOfSdkman" || redEcho "fail to install jdk $jdkNameOfSdkman by sdkman"
                set -u
            }
        fi

        java_home_var_names=("${java_home_var_names[@]}" "$jdkHomeVarName")
        printf '%s :\n\t%s\n\tspecified is %s\n' "$jdkHomeVarName" "${!jdkHomeVarName}" "$jdkNameOfSdkman"
    done

    echo
    blueEcho "ls $SDKMAN_CANDIDATES_DIR/java/ :"
    ls -la "$SDKMAN_CANDIDATES_DIR/java/"
}
__setJdkHomeVarsAndInstallJdk

switch_to_jdk() {
    [ $# == 1 ] || die "${FUNCNAME[0]} need 1 argument! But provided: $*"

    local javaHomeVarName="JDK${1}_HOME"
    export JAVA_HOME="${!javaHomeVarName}"

    [ -n "$JAVA_HOME" ] || die "jdk $1 env not found: $javaHomeVarName"
    [ -e "$JAVA_HOME" ] || die "jdk $1 not existed: $JAVA_HOME"
    [ -d "$JAVA_HOME" ] || die "jdk $1 is not directory: $JAVA_HOME"
}
