# How to run:

Build message protocols, Java .class files and Erlang .beam files:

```
make build
```




**First terminal:**

Spawn broker process and districts' processes, all running in background.
Districts' loggers print to terminal during run time.

```
make run-broker
make run-districts
```


**Second terminal:**

Spawn directory process, running in background.

```make run-directory```


**Third terminal:**

Starts frontend server, which connects to all district servers.
Logger prints to terminal during run time.

```
erl -pa frontend/ebin\
server:start(12345,12346).
```


**Fourth terminal:**

Starts client process, connecting to frontend and district servers.

```
make run-client PORT=<PRIVATE_SOCKET_PORT>
```
