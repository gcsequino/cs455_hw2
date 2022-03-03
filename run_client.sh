#! /bin/bash

usage_msg="Usage: ./run_client.sh -i <server_host> -p <server_port> -r <messaging_rate>"
usage() { echo ${usage_msg} 1>&2; exit 1; }
bad_use=false

while getopts ":h:p:r:" o; do
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
        *)
            usage
            ;;
    esac
done

if [ -z "${server_host}" ] || [ -z "${server_port}" ] || [ -z "${messaging_rate}" ]; then
    usage
fi

./gradlew build
java -cp ./app/build/libs/app.jar scaling.client.Client "${server_host}" "${server_port}" "${messaging_rate}"