# cs455_hw2
Homework 2 for CS455 at Colorado State University

# Running the code
if you are in the root of this project then run `scripts/run_all.sh -m run -p <server-port>` to run the project

# Running the code with custom options
```
USAGE: ./run_all.sh
 REQUIRED-
 	 Mode: -m <clean|run|ssh-check>
 	 Port: -p <server_port>
 OPTIONAL-
 	 Number of clients: -n <num_nodes> (default = 100)
 	 Client messaging rate: -r <messaging_rate> (default = 2)
 	 Server thread count: -d <thread_pool_size> (default = 10)
 	 Server batch time: -t <batch_time> (default = 20)
 	 Server batch size: -s <batch_size> (default = 20)
```

# Closing your tmux session
run `./scripts/run_all.sh -m clean`

# Checking ssh-machines for script
run `./scripts/run_all.sh -m ssh-check`

# Running the code to show that batch-time is working

run `scripts/run_all.sh -m run -p 5000 -n 10 -r 1 -d 10 -t 2 -s 200`