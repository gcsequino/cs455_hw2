#! /bin/bash

usage_msg="Usage: ./run_client.sh -p <server_port> -t <num_threads> -s <batch-size> -b <batch-time>"
usage() { echo ${usage_msg} 1>&2; exit 1; }
bad_use=false

while getopts ":p:t:s:b:" o; do
    case "${o}" in
        p)
            server_port=${OPTARG}
            ;;
        t)
            num_threads=${OPTARG}
            ;;
        s)
            batch_size=${OPTARG}
            ;;
        b)
            batch_time=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done

if ([ -z "${server_port}" ] || [ -z "${num_threads}" ] || [ -z "${batch_size}" ] || [ -z "${batch_time}" ]) ; then
    usage
fi

#./gradlew build
main_class="scaling.server.Server"
java -cp ./app/build/libs/app.jar ${main_class} "${server_port}" "${num_threads}" "${batch_size}" "${batch_time}"
