import sys
sys.path.extend(["../"])
sys.path.extend(["."])
from threading import Thread
from client.http import http
#   Concurrent Requests: Multi clients are writing to the same file(bar):

def test_post_request_multi(file, index):
    body = str(index)
    _reqInfo = http("http://localhost/"+file, 8080)
    _reqInfo.setType('post')
    _reqInfo.setData(body)
    _reqInfo.addHeader("Content-Type", "application/json")
    _reqInfo.addHeader("Content-Length", str(len(body)))
    _reqInfo.buildRequestInfo()
    reply = _reqInfo.send()


for i in range(0, 5):
    Thread(target=test_post_request_multi, args=("bar", i)).start()