copy C:\Users\User\Desktop\PSD\TP\gpb\bin\messages.proto C:\Users\User\Desktop\protobuf-3.14.0\done
cd C:\Users\User\Desktop\protobuf-3.14.0\done
protoc --java_out="." messages.proto
move .\Protos\Messages.java C:\Users\User\Desktop\PSD\TP\client\src\main\java\Protos\
rmdir .\Protos
PAUSE 