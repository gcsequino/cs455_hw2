#! /bin/bash

USAGE_MSG="USAGE: ./run_all.sh -m <clean|run|ssh-check> -n <num_nodes> -p <server_port> -r <messaging_rate>"
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
    BASE_CLIENT_CMD="cd ${PROJ_DIR} && sh ./scripts/run_client.sh"
    BASE_SERVER_CMD="cd ${PROJ_DIR} && sh ./scripts/run_server.sh"

    readarray -t machines < ./scripts/machines.txt
    SERVER=${machines[0]}
    CLIENTS=${machines[@]:1:NUM_CLIENTS}
    CLIENTS=(${CLIENTS[*]})

    echo -e "$SERVER\n$CLIENTS" > scripts/current_machines.txt


    # start server
    window=0
    tmux rename-window -t $session_name:$window 'server-${SERVER}'

    THREAD_POOL_SIZE=10
    BATCH_TIME=20
    BATCH_SIZE=20
    SERVER_CMD="${BASE_SERVER_CMD} -p ${SERVER_PORT} -t ${THREAD_POOL_SIZE} -s ${BATCH_SIZE} -b ${BATCH_TIME}"

    tmux send-keys -t $session:$window "ssh $SERVER" C-m
    tmux send-keys -t $session:$window "${SERVER_CMD}" C-m
    echo "Running Server on: ${SERVER}"

    CLIENT_CMD="${BASE_CLIENT_CMD} -h ${SERVER} -p ${SERVER_PORT} -r ${MESSAGING_RATE}"

    CLIENTS_LEN=${#CLIENTS[@]}
    echo "num clients: $CLIENTS_LEN"
    let "window+=1"
    for(( i=1; i<=$NUM_CLIENTS; ++i)); do

        machine=${CLIENTS[$((i%CLIENTS_LEN))]}
        echo "starting client $i on ${machine}..."
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
    tmux select-window -t "$session_name:0"
    tmux a
    exit 0
elif [[ $COMMAND = "clean" ]]; then
    echo "killing tmux session $session_name"
    rm scripts/current_machines.txt
    $KILL_SESSION_CMD
elif [[ $COMMAND = "ssh-check" ]]; then
    readarray -t machines < ./scripts/machines.txt
    for computer in ${machines[@]}; do
        #echo Trying "$computer" 
        timeout 5 ssh -o PasswordAuthentication=no "$computer" /bin/true
        if [ $? != 0 ];then
            echo "$computer is not ssh-able"
        fi
    done
fi