--------------------------------------------------------------------------------
using curl:

useful options:
--dump-header file
--dump-header -
-v


GET:
curl [URL]

PUT, POST, HEAD:
curl -X [METHOD] [URL]

send file as content in PUT, POST (default PUT):
curl -T file [URL]
curl -X PUT -T file [URL]
curl -X POST -T file [URL]
curl -T - [URL]
curl -X PUT -T - [URL]
curl -X POST -T - [URL]

send form data in POST, PUT (default POST):
curl -d "k1=v1&k2=v2&..." [URL]
curl -X [METHOD] -d "k1=v1&k2=v2&..." [URL]

--------------------------------------------------------------------------------
