1  	Instructions to Run the RMI Version.

1.1 building the RMI version

	set the env SRC512_HOME in the shell so that it has the complete path to the rmi directory
	For example
	export SRC512_HOME=/home/joe/demo01/rmi

	go to rmi/scripts directory and execute
	./compile_all.sh

1.2 starting the servers and clients.

	Edit the 512_env file in the rmi directory to update the hostnames so that they point to the hosts and ports in which each of the server/middleware is supposed to run. ( this is mostly for the client interfaces to know here to connect)

 Now login to each host, and cd to the rmi/scripts folder
 ensure the SRC512_HOME variable is set to point the full path to rmi folder as in the case of build instructions.

 Now start the rmi registry script meant for that host.
 For example ./start_htl_rmiregistry.sh &
 Next start the server script meant for that host.
 For example ./start_htl_server.sh &
 The logs may be redirected to some file, else it will be printed to the screen.

 To start the client, follow the same instructions as server, set the SRC512_HOME directory to point to rmi folder, cd to scripts folder and execute
 ./start_client.sh


=================================================================

2  	Instructions to Run TCP version.

2.1 Building the charon source code.

	cd to the demo01 directory, execute
	javac charon/*.java
	jar cvf charon.jar charon/*.class
	mv charon.jar tcp/tools

2.2 building the TCP version

	set the env SRC512_HOME in the shell so that it has the complete path to the tcp directory
	For example
	export SRC512_HOME=/home/joe/demo01/tcp

	go to tcp/scripts directory and execute
	./compile_all.sh

2.3 starting the servers and clients.

	Edit the 512_env file in the tcp directory to update the hostnames so that they point to the hosts and ports in which each of the server/middleware is supposed to run. ( this is mostly for the client interfaces to know here to connect)

 Now login to each host, and cd to the tcp/scripts folder
 ensure the SRC512_HOME variable is set to point the full path to tcp folder as in the case of build instructions.

 Now start the server script meant for that host.
 For example ./start_htl_server.sh &
 The logs will be generated under tcp/logs directory and will bear the name of the script and timestamp to identify multiple runs.

 To start the client, follow the same instructions as server, set the SRC512_HOME directory to point to tcp folder, cd to scripts folder and execute
 ./start_client.sh


