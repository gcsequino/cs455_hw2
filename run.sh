#! /bin/bash

usage_msg="Usage: ./run_client.sh -h <server_host> -p <server_port> -r <messaging_rate>"
usage() { echo ${usage_msg} 1>&2; exit 1; }
bad_use=false

while getopts ":h:p:r:t:" o; do
    case "${o}" in
        h)
            server_host=${OPTARG}
            ;;
        p)
            server_port=${OPTARG}
            ;;
        r)
            messaging_rate=${OPTARG}
            ;;
        t)
            type=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done

if [ -z "${server_host}" ] || [ -z "${server_port}" ] || [ -z "${messaging_rate}" ] || [ -z "${type}" ]; then
    usage
fi

./gradlew build
if [ "${type}" = "client" ]; then
    main_class="scaling.client.Client"
elif [ "${type}" = "server" ]; then
    # TODO - CHANGE ME TO ACTUAL SERVER
    main_class="scaling.client.TestServer"
else
    echo "invalid main class type given: ${type}"
fi
java -cp ./app/build/libs/app.jar ${main_class} "${server_host}" "${server_port}" "${messaging_rate}"