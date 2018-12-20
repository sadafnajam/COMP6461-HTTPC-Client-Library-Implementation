import sys
sys.path.extend(["../"])
sys.path.extend(["."])
from threading import Thread
from client.http import http
#   Concurrent Requests: One client is reading(bar), while another is writing(bar) to the same file:


def test_post_request_multi(file, index):
    body = str(index)
    _reqInfo = http("http://localhost/"+file, 8080)
    _reqInfo.setType('post')
    _reqInfo.setData(body)
    _reqInfo.addHeader("Content-Type", "application/json")
    _reqInfo.addHeader("Content-Length", str(len(body)))
    _reqInfo.buildRequestInfo()
    reply = _reqInfo.send()

def test_get_file(file, self):
    _reqInfo = http("http://localhost/"+file, 8080)
    _reqInfo.setType('get')
    _reqInfo.buildRequestInfo()
    reply = _reqInfo.send()


for i in range(1, 10):
    Thread(target=test_post_request_multi, args=("bar", i)).start()
    Thread(target=test_get_file, args=("foo", i)).start()