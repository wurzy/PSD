DIRECTORY_PATH=stock-directory
DIRECTORY_VERSION=1.0-SNAPSHOT
DIRECTORY_MVN_OPTIONS=-f $(DIRECTORY_PATH)/pom.xml

build: stock-directory client frontend exchange

stock-directory:
	mvn $(DIRECTORY_MVN_OPTIONS) compile
	mvn $(DIRECTORY_MVN_OPTIONS) package

client:
	protoc --java_out=. protos/protocol.proto
	javac -cp dependencies/jar/protobuf-java-3.4.1.jar:dependencies/jar/jeromq-0.4.3.jar:dependencies/jar/java-json.jar client/*.java

frontend:
	dependencies/gpb/bin/protoc-erl -I. -maps -o frontend/ protos/protocol.proto
	erlc -I dependencies/gpb/include -o frontend/ frontend/protocol.erl
	erlc -I dependencies/erlzmq/include -o frontend/ frontend/erlzmq.erl
	erlc -I dependencies/erlzmq/include -o frontend/ frontend/erlzmq_nif.erl
	erlc -o frontend/ frontend/exchange*.erl frontend/mochijson.erl frontend/frontend.erl frontend/loginManager.erl frontend/userSession.erl

exchange:
	protoc --java_out=. protos/protocol2.proto
	javac -cp dependencies/jar/protobuf-java-3.4.1.jar:dependencies/jar/jeromq-0.4.3.jar:dependencies/jar/gson-2.6.2.jar exchange/*.java

pub-broker:
	javac -cp dependencies/jar/jeromq-0.4.3.jar exchange/PubSubBroker.java

push-broker:
	javac -cp dependencies/jar/jeromq-0.4.3.jar exchange/Broker.java


# 7000 -> frontend (mudar consoante cliente) | 4442 XPUB
runcli:
	java -cp .:dependencies/jar/protobuf-java-3.4.1.jar:dependencies/jar/jeromq-0.4.3.jar:. client.Client localhost 7000 4442

# 3331 -> push para broker | 5000 pull do ator (mudar consoante exchange) | XSUB 4441
run-exchange:
	java -cp .:dependencies/jar/protobuf-java-3.4.1.jar:dependencies/jar/jeromq-0.4.3.jar:dependencies/jar/gson-2.6.2.jar exchange.Exchange 3331 5000 4441 1

run-pub-broker:
	java -cp .:dependencies/jar/jeromq-0.4.3.jar exchange.PubSubBroker 4441 4442

push-pull-broker:
	java -cp .:dependencies/jar/jeromq-0.4.3.jar exchange.Broker 3331 3332 


run-stock-directody:
	java -jar stock-directory/target/stockdirectory-$(DIRECTORY_VERSION).jar server stock-directory/conf.yml

.PHONY: stock-directory exchange frontend client 

clean:
	-@rm client/*.class
	-@rm frontend/*.beam
	-@rm frontend/protocol.erl
	-@rm client/Protocol.java
	-@rm exchange/Protocol.java
	-@mvn $(DIRECTORY_MVN_OPTIONS) clean
	-@rm exchange/*.class
