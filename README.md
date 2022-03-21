# cs455_hw2
Homework 2 for CS455 at Colorado State University

# Running the code
if you are in the root of this project then run `scripts/run_all.sh -m run -p <server-port>` to run the project

# Closing your tmux session
run `./scripts/run_all.sh -m clean`

# Checking ssh-machines for script
run `./scripts/run_all.sh -m ssh-check`

# Running the code to show that batch-time is working

run `scripts/run_all.sh -m run -p 5000 -n 10 -r 1 -d 10 -t 2 -s 200`