package Server;

public class Response {
    private String http_Status;
    private String http_Version;
    private String server;
    private int contentLength;
    private String contentType;
    private String message;

    public Response(String http_Status){
        http_Version = "HTTP/1.1";
        this.http_Status = http_Status;
        server = "mtcg-server";
        contentType = "application/json";
        contentLength = 0;
        message = "";
    }

    public String getHttp_Status() {
        return http_Status;
    }

    public void setHttp_Status(String http_Status) {
        this.http_Status = http_Status;
    }

    public String getHttp_Version() {
        return http_Version;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        contentLength = message.length();
    }

    public void setHttp_Version(String http_Version) {
        this.http_Version = http_Version;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}
