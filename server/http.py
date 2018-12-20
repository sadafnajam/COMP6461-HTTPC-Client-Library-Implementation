class http:

    def __init__(self, status, content):
        self.status = status
        self.content = content
        self.header = {}

    def addHeader(self, key, value):
        self.header[key] = value
        
    def getHeader(self):
        head = "\r\n"
        for k, v in self.header.items():
            head += (k+": "+v+"\r\n")
        return head

    def setStatus(self, status):
        self.status = status

    def getStatus(self):
        return self.status

# Error Handling

    def getState(self):
        if self.status == 200:
            return "OK"
        elif self.status == 400:
            return "Bad Request"
        elif self.status == 404:
            return "Not Found"
        else:
            return "Unknown Error"

    def setContent(self, content):
        self.content = content
        self.header["Content-Length"] = str(len(content))

    def getBody(self):
        return self.content

    def headToString(self):
        return "HTTP/1.0 " + str(self.getStatus()) + " " + self.getState() + self.getHeader() + "\r\n"


