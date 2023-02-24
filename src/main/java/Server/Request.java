package Server;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String http_Method;
    private String http_Version;
    private String url_Content;
    private Map<String, String> http_header;
    private String http_Body;

    public Request(){
        http_header = new HashMap<>();
    }

    public  void add_http_headers(String key, String value){
        http_header.put(key, value);
    }

    public int getContentLength(){
        if(http_header != null && http_header.containsKey("content-length:")){
            return Integer.parseInt(http_header.get("content-length:"));
        }
        return 0;
    }

    public String getHttp_Method() {
        return http_Method;
    }

    public void setHttp_Method(String http_Method) {
        this.http_Method = http_Method;
    }

    public String getUrl_Content() {
        return url_Content;
    }

    public void setUrl_Content(String url_Content) {
        this.url_Content = url_Content;
    }

    public Map<String, String> getHttp_header() {
        return http_header;
    }

    public void setHttp_header(Map<String, String> http_header) {
        this.http_header = http_header;
    }

    public String getHttp_Body() {
        return http_Body;
    }

    public void setHttp_Body(String http_Body) {
        this.http_Body = http_Body;
    }

    public String getHttp_Version() {
        return http_Version;
    }

    public void setHttp_Version(String http_Version) {
        this.http_Version = http_Version;
    }


}
