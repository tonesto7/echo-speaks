#!/bin/bash

# ---------------------------------GLOBAL VARIABLES--------------------------------------
_scriptVer="0.2"
_srvcVer="0.1.0"
_useSudo="false"
_instNode="false"
_port="8091"

_currentUser="$USER"

_userDir="/home/$_currentUser"
working_app_dir="$_userDir/echo-speaks-master"

src_zip_name="master.zip"
app_zip_file="$_userDir/$src_zip_name"

cur_srvc_name="echo-speaks.service"
new_srvc_src_path="$working_app_dir/node_server/$cur_srvc_name"
new_srvc_dest_path="/etc/systemd/system/$cur_srvc_name"

old_srvc_name="echo-speaks.service"
old_srvc_path="/etc/systemd/system/$old_srvc_name"

remote_file="https://github.com/tonesto7/echo-speaks/archive/$src_zip_name" 
# ----------------------------------------------------------------------------------------

showTitle() {
    echo "    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓"
    echo "    ┃                Echo Speaks Utility Script (v$_scriptVer)                      ┃"
    echo "    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛"
}

showHelp() {
    echo "    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓"
    echo "    ┃                Echo Speaks Utility Script (v$_scriptVer)                      ┃"
    echo "    ┃                            Help Page                                  ┃"
    echo "    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛"
    echo ""
    echo "      Available Switch Arguments: "
    echo "    ┌──────────────────────────┬────────────────────────────────────────────┐"
    echo "    │  Default [No Arg]        │ Runs the Full Installation Process         │"
    echo "    │                          │                                            │"
    echo "    │  [-port $_port]            │ Defines custom port - Default: ($_port)      │"
    echo "    │                          │                                            │"
    echo "    │  [-f | -force]           │ Forcefully Update Files/Service            │"
    echo "    │                          │                                            │"
    echo "    │  [-r | -remove | -clean] │ Removes the Service and All Echo Speaks    │"
    echo "    │                          │ files from the System                      │"
    echo "    │                          │                                            │"
    echo "    │  [-u | -update]          │ Skip's Pre-req Install and downloads the   │"
    echo "    │                          │ latest package file and updates the        │"
    echo "    │                          │ existing files and reinstalls the service  │"
    echo "    │                          │                                            │"
    echo "    ├──────────────────────────┴────────────────────────────────────────────┤"
    echo "    │       You can use the -port parameter by itself or after -update      │"
    echo "    │       and -force                                                      │"
    echo "    └───────────────────────────────────────────────────────────────────────┘"
    echo ""
    echo ""
    exit
}

#   Checks to see if sudo should be used by default.
#   It allows an argument of "true"/"false" to use sudo if it's allowed/supported on the machine
check_sudo() {
    _useSudo="false"
    if [ -x "$(command -v sudo)" ]; then
        if [ "$1" = "true" ]; then
            if [ $_currentUser != "root" ]; then
                _useSudo="true"
            fi
        fi
    fi
    #echo "Using Sudo: ($_useSudo)"
}

verifySystemd() {
    if [ ! -x "$(command -v systemctl)" ]; then
        echo "Error: Systemd is not available on this device..."
        exit 1
    fi
}

verifyNode() {
    if [ ! -x "$(command -v node)" ]; then
        echo "Info: NodeJS is not installed... Setting Install to True"
        $_instNode = "true"
    fi
}

checkOwnerOk() {
    dir_owner="$(stat -c '%U' $_userDir)"
    if [ $_currentUser != $dir_owner ]; then
        return 1
    else
        return 0
    fi
}

usedSudoDesc() {
    if $_useSudo; then
        return " | (SUDO)"
    else
        return ""
    fi
}

sudoPreCmd() {
    _cmd=$1
    if $_useSudo; then
        _cmd="sudo $_cmd"; $_cmd
    else
        $_cmd
    fi
}

#   Set current user as owner of apps working directory
set_owner() {
    echo ""
    echo "--------------------------------------------------------------"
    check_sudo "true"
    echo "Making ($_currentUser) Owner of directory [$_userDir]$usedSudoDesc"
    sudoPreCmd "chown -R $_currentUser:$_currentUser $_userDir"
    # verifies owner was set
    if [ ! checkOwnerOk ]; then
        echo "Error: Setting $_currentUser failed for Directory ($_userDir)"
        exit 1
    fi
}

#   This installs all pre-requisite APT Packages required by the service
install_prereqs() {
    echo ""
    echo "--------------------------------------------------------------"
    check_sudo "true"
    echo "Installing/Updating Required APT Packages$usedSudoDesc"
    sudoPreCmd "apt-get update"
    sudoPreCmd "apt-get upgrade -f -y --force-yes"
    sudoPreCmd "apt-get install curl wget git-core unzip openssh-server build-essential -y"
    if $_instNode; then
        if $_useSudo; then
            curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
        else
            curl -sL https://deb.nodesource.com/setup_10.x | bash -
        fi
        sudoPreCmd "apt-get install nodejs -y"
    fi
}

getLatestPackage() {
    download_package
    if [ -f $app_zip_file ];
    then
        remove_old_srvc
        unzip_pkg
        #check_sudo # because set_owner changes the sudo allow to true
        echo "Changing to [$working_app_dir] directory..."
        cd $working_app_dir
        install_node_app
        update_srvc
        remove_package
        exit 0
    else
        echo "Error: Echo Speaks Package Zip File ($app_zip_file) Not Found"
        echo "at ($_userDir)"
        exit 1
    fi
}

#   This Downloads the latest NST Streaming package from the distribution source and extracts it
download_package() {
    if [ checkOwnerOk != "true" ]; then
        set_owner
    fi
    check_sudo
    echo ""
    echo "--------------------------------------------------------------------------"
    echo "Downloading the Latest Echo Speaks Package ($src_zip_name)$usedSudoDesc"
    echo "--------------------------------------------------------------------------"
    sudoPreCmd "wget -N $remote_file -P $_userDir"
}

remove_package() {
    if [ checkOwnerOk != "true" ]; then
        set_owner
    fi
    check_sudo
    echo ""
    echo "--------------------------------------------------------------------------"
    echo "Cleaning Up Downloaded Package ($app_zip_file)$usedSudoDesc"
    echo "--------------------------------------------------------------------------"
    sudoPreCmd "rm -rf $app_zip_file"
}

unzip_pkg() {
    echo ""
    echo "--------------------------------------------------------------"
    check_sudo
    if [ -f $app_zip_file ];
    then
        cd $_userDir
        echo "Unzipping Latest Echo Speaks Package to $working_app_dir$usedSudoDesc"
        sudoPreCmd "unzip -o $app_zip_file"
        set_owner
    else
        echo "Error: Unzip Failed because $app_zip_file can't be located"
        exit 1
    fi
}

install_node_app() {
    echo ""
    echo "--------------------------------------------------------------"
    check_sudo
    if [ -d $working_app_dir/node_server ]; then
        cd $working_app_dir/node_server
        echo "Running Node Service Install$usedSudoDesc"
        sudoPreCmd "npm install --no-optional"
    else
        echo "Error: Node App NPM Install Failed because the directory ($working_app_dir/node_server) wasn't found"
        exit 1
    fi
}

modify_srvc_file_for_user() {
    check_sudo
    #echo "New NST Streaming Service File found. Updating!!!"
    if [ $_currentUser != "pi" ];
    then
        echo "Modifying Service File with Current User Path [$_userDir]$usedSudoDesc"
        sudoPreCmd "sed -ia 's|/home/pi/echo-speaks-master|'$working_app_dir/node_server'|g' $cur_srvc_name"
    fi
}

create_srvc_file_for_user() {
    echo "Creating Service File with Current User Path [$_userDir]$usedSudoDesc"
    echo "[Unit]" >> $new_srvc_src_path
    echo "Description=Echo Speaks Node Server" >> $new_srvc_src_path
    echo "After=network.target remote-fs.target" >> $new_srvc_src_path
    echo "" >> $new_srvc_src_path
    echo "[Service]" >> $new_srvc_src_path
    echo "Type=simple" >> $new_srvc_src_path
    # echo "Environment=NODE_ENV=production" >> $new_srvc_src_path
    echo "Environment=ECHO_SPEAKS_PORT=$_port" >> $new_srvc_src_path
    echo "ExecStart=/usr/bin/node index.js" >> $new_srvc_src_path
    echo "Restart=always" >> $new_srvc_src_path
    echo "RestartSec=10" >> $new_srvc_src_path
    echo "KillMode=process" >> $new_srvc_src_path
    echo "WorkingDirectory=$working_app_dir/node_server" >> $new_srvc_src_path
    echo "" >> $new_srvc_src_path
    echo "[Install]" >> $new_srvc_src_path
    echo "WantedBy=multi-user.target" >> $new_srvc_src_path
}

remove_old_srvc() {
    echo ""
    echo "--------------------------------------------------------------"
    echo "Checking for Existing Echo Speaks Service File"
    if [ -f $old_srvc_path ]; then
        check_sudo "true"
        echo "Result: Found Existing Service File..."
        echo "Disabling and Removing Existing Echo Speaks Service$usedSudoDesc"
        sudoPreCmd "systemctl stop $old_srvc_name"
        sudoPreCmd "systemctl disable $old_srvc_name"
        sudoPreCmd "rm $old_srvc_path"
    else
        echo "Result: Nothing to Remove.  No Existing Service File Found"
    fi
}

update_srvc() {
    echo ""
    echo "--------------------------------------------------------------"
    create_srvc_file_for_user
    if [ ! -f "$new_srvc_src_path" ]; then
        echo "Error: The created service file cannot be found"
        exit 1
    fi
    if [ ! -f "$old_srvc_path" ]; then
        check_sudo "true"
        echo "Copying Updated Echo Speaks Service File to Systemd Folder$usedSudoDesc"
        sudoPreCmd "cp $new_srvc_src_path $new_srvc_dest_path -f"
        if [ -f $new_srvc_dest_path ]; then
            echo "Reloading Systemd Daemon to Reflect Service File Changes$usedSudoDesc"
            sudoPreCmd "systemctl daemon-reload"
            echo "Enabling Echo Speaks Service (Systemd)$usedSudoDesc"
            sudoPreCmd "systemctl enable $cur_srvc_name"
            echo "Starting Echo Speaks Streaming Service (v$_srvcVer)$usedSudoDesc"
            sudoPreCmd "systemctl start $cur_srvc_name"
        else
            echo "Error: Copying Service File to Systemd folder didn't work"
            exit 1
        fi
    else
        echo "Error: Can't Copy New Service because Old File is Still There!!"
        exit 1
    fi
}

pkg_cleanup() {
    check_sudo
    if [ -d $working_app_dir ]; then
        echo ""
        echo "----------------------------------------------------------------------"
        if [ checkOwnerOk != "true" ]; then
            set_owner
        fi
        echo "Removing All Echo Speaks Data and Files$usedSudoDesc"
        sudoPreCmd "rm -rf $working_app_dir"
        if [ -f $_userDir/$src_zip_name ]; then
            sudoPreCmd "rm -rf $_userDir/$src_zip_name"
        fi
    fi
}

setPortVariable() {
    _val=$1
    ECHO_SPEAKS_PORT=$_val
    echo "ECHO_SPEAKS_PORT variable is: $ECHO_SPEAKS_PORT";
}

remove_all() {
    remove_old_srvc
    unset ECHO_SPEAKS_PORT
    pkg_cleanup
}

showPkgDlOk() {
    echo ""
    echo "=============================================================="
    echo "               Install/Update Result: (Success)               "
    echo "          New Data Downloaded and Service Installed           "
    echo "                                                              "
    echo "            View Service Logs Using this Command:             "
    echo "               journalctl -f -u echo-speaks                 "
    echo "=============================================================="
    echo ""
}

showCleanupOk() {
    echo ""
    echo "=============================================================="
    echo "              Cleanup/Removal Result: (Success)               "
    echo "        All Echo Speaks Data, Files, Service Removed"
    echo "=============================================================="
    echo ""
}

# echo "Executing Script $0 $1"
clear
verifySystemd
verifyNode
if [ $# -eq 0 ]; then
    showTitle
    install_prereqs
    getLatestPackage
    
else
    if [ "$1" = "-p" ] || [ "$1" = "-port" ]; then
        _port="$2"
        setPortVariable "$_port"
        showTitle
        install_prereqs
        getLatestPackage

    elif [ "$1" = "-r" ] || [ "$1" = "-remove" ] || [ "$1" = "-clean" ]; then
        showTitle
        remove_all
        showCleanupOk

    elif [ "$1" = "-f" ] || [ "$1" = "-force" ]; then
        if [ "$2" = "-p" ] || [ "$2" = "-port" ]; then
            _port="$3"
        fi
        setPortVariable "$_port"
        showTitle
        remove_all
        getLatestPackage
        showPkgDlOk
        sudoPreCmd "journalctl -f -u echo-speaks.service"

    elif [ "$1" = "-u" ] || [ "$1" = "-update" ]; then
        if [ "$2" = "-p" ] || [ "$2" = "-port" ]; then
            _port="$3"
        fi
        setPortVariable "$_port"
        showTitle
        getLatestPackage
        showPkgDlOk
        sudoPreCmd "journalctl -f -u echo-speaks.service"

    elif [ "$1" = "-help" ] || [ "$1" = "-h" ] || [ "$1" = "-?" ] || [ "$1" != "-u" ] || [ "$1" != "-update" ] || [ "$1" != "-f" ] || [ "$1" != "-force" ] || [ "$1" != "-r" ] || [ "$1" != "-remove" ] || [ "$1" != "-clean" ] || [ "$1" != "-p" ] || [ "$1" != "-port" ]; then
        showHelp
    fi
fi
