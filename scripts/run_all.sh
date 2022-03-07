#! /bin/bash

USAGE_MSG="USAGE: ./run_all.sh -m <clean|run> -n <num_nodes> -p <server_port> -r <messaging_rate>"
usage() { echo ${USAGE_MSG} 1>&2; exit 1; }

while getopts ":m:n:p:r:" o; do
    case "${o}" in
        m)
            MODE=${OPTARG}
            ;;
        n)
            NUM_CLIENTS=${OPTARG}
            ;;
        p)
            SERVER_PORT=${OPTARG}
            ;;
        r)
            MESSAGING_RATE=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done

if [ -z "${MODE}" ]; then
    usage
elif [ "${MODE}" == "run" ]; then
    if ([ -z "${NUM_CLIENTS}" ] || [ -z ${SERVER_PORT} ] || [ -z "${MESSAGING_RATE}" ]); then
        usage
    fi
fi

COMMAND=$MODE

session_name="cs455-hw2"
KILL_SESSION_CMD="tmux kill-session -t $session_name"

if [[ $COMMAND = "run" ]]; then
    ./gradlew build

    # check if tmux session already exists
    tmux has-session -t $session_name 2>/dev/null
    if [ $? == 0 ]; then
        # Set up your session
        echo "killing old tmux session"
        $KILL_SESSION_CMD
    fi
    echo "creating new tmux session named ${session_name}"
    tmux new -d -s $session_name

    PROJ_DIR=$(pwd)
    echo "PROJ DIR: ${PROJ_DIR}"
    BASE_CMD="cd ${PROJ_DIR} && sh ./scripts/run.sh"

    readarray -t machines < ./scripts/machines.txt
    SERVER=${machines[0]}
    CLIENTS=${machines[@]:1:NUM_CLIENTS}

    echo -e "$SERVER\n$CLIENTS" > scripts/current_machines.txt


    sleep 1s #pls remove this

    # start server
    window=0
    tmux rename-window -t $session_name:$window 'server'
    SERVER_CMD="${BASE_CMD} -t server -p ${SERVER_PORT}"
    tmux send-keys -t $session:$window "ssh $SERVER" C-m
    tmux send-keys -t $session:$window "${SERVER_CMD}" C-m
    echo "Running Server on: ${SERVER}"

    CLIENT_CMD="${BASE_CMD} -h ${SERVER} -p ${SERVER_PORT} -r ${MESSAGING_RATE} -t client"
    let "window+=1"
    for machine in ${CLIENTS[@]}; do
        echo "starting client on ${machine}..."
        if [ $window = 1 ]; then
            tmux split-window -h
            SELECTED_PANE="$session:0.1"
        else
            SELECTED_PANE="$session_name:$window"
            tmux new-window -t "${SELECTED_PANE}" -n "client-${machine}"
        fi
            tmux send-keys -t "${SELECTED_PANE}" "ssh ${machine}" C-m
            tmux send-keys -t "${SELECTED_PANE}" "clear;${CLIENT_CMD}" C-m
        let "window+=1" 
    done
    tmux a
    exit 0
elif [[ $COMMAND = "clean" ]]; then
    echo "killing tmux session $session_name"
    rm scripts/current_machines.txt
    $KILL_SESSION_CMD
fi