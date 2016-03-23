package org.wso2.developerstudio.eclipse.artifact.utils;

import java.util.Map;

public class HTTPResponse {
    private String data;
    private int responseCode;
    private String responseMessage;
    private Map<String, String> headers;

    public HTTPResponse(String data, int responseCode) {
        this.data = data;
        this.responseCode = responseCode;
    }

    public HTTPResponse(String data, int responseCode, Map<String, String> headers) {
        this.data = data;
        this.responseCode = responseCode;
        this.headers = headers;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
