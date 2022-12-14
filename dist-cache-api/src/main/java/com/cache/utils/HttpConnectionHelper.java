package com.cache.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

/** Helper for HTTP connection - this is helping call HTTP endpoints with given method and body with headers */
public class HttpConnectionHelper {

    /** base URL of HTTP(s) connection */
    private String baseUrl;
    /** default timeout in milliseconds to be applied for HTTP connection
     * default is 10seconds
     * */
    private int defaultTimeout = 10000;

    public HttpConnectionHelper(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public HttpConnectionHelper(String baseUrl, int timeout) {
        this.baseUrl = baseUrl;
        this.defaultTimeout = timeout;
    }
    public HttpResponseContent callHttp(String appendUrl, String method, Optional<String> body, Map<String, String> headers, int timeout) {
        long startTime = System.currentTimeMillis();
        try {
            java.net.URL url = new java.net.URL(baseUrl + appendUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            headers.entrySet().stream().forEach(x -> {
                con.setRequestProperty(x.getKey(), x.getValue());
            });
            con.setRequestMethod(method);
            con.setConnectTimeout(timeout);
            con.setDoInput(true);
            if ((method.equals(METHOD_POST) || method.equals(METHOD_PUT)) && body.isPresent()) {
                con.setDoOutput(true);
                java.io.BufferedWriter w = new java.io.BufferedWriter(new java.io.OutputStreamWriter(con.getOutputStream()));
                w.write(body.orElse(""));
                w.flush();
                w.close();
            } else {
                con.setDoOutput(false);
            }
            String error = tryReadContent(con.getErrorStream());
            int code = -1;
            try {
                code = con.getResponseCode();
            } catch (Exception ex) {
            }
            String contentType = con.getContentType();

            Object outObject = null;
            long outLength = -1;
            try {
                outLength = con.getContentLengthLong();
                outObject = con.getContent();
            } catch (Exception ex) {
            }
            con.connect();
            return new HttpResponseContent(false, code, outObject, outLength, contentType, error, System.currentTimeMillis()-startTime);
        } catch (IOException ex) {
            return new HttpResponseContent(true, -1, "", -1, "", ex.getMessage(), System.currentTimeMillis()-startTime);
        }
    }
    private String tryReadContent(java.io.InputStream inpStr) {
        try {
            StringBuilder responseText = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(inpStr));
            var line = responseReader.readLine();
            while (line != null) {
                responseText.append(line);
                line = responseReader.readLine();
                if (line != null) {
                    responseText.append("\r\n");
                }
            }
            return responseText.toString();
        } catch (Exception ex) {
            return "";

        }
    }

    /** GET method without body and JSON default header */
    public HttpResponseContent callHttpGet(String appendUrl) {
        return callHttp(appendUrl, METHOD_GET, Optional.empty(), applicationJsonHeaders, defaultTimeout);
    }
    public HttpResponseContent callHttpGet(String appendUrl, Map<String, String> headers) {
        return callHttp(appendUrl, METHOD_GET, Optional.empty(), headers, defaultTimeout);
    }
    public HttpResponseContent callHttpGet(String appendUrl, String body) {
        return callHttp(appendUrl, METHOD_GET, Optional.of(body), applicationJsonHeaders, defaultTimeout);
    }
    public HttpResponseContent callHttpPost(String appendUrl) {
        return callHttp(appendUrl, METHOD_POST, Optional.empty(), applicationJsonHeaders, defaultTimeout);
    }
    public HttpResponseContent callHttpPost(String appendUrl, String body) {
        return callHttp(appendUrl, METHOD_POST, Optional.of(body), applicationJsonHeaders, defaultTimeout);
    }
    public HttpResponseContent callHttpPut(String appendUrl, String body) {
        return callHttp(appendUrl, METHOD_PUT, Optional.of(body), applicationJsonHeaders, defaultTimeout);
    }
    public HttpResponseContent callHttpDelete(String appendUrl) {
        return callHttp(appendUrl, METHOD_DELETE, Optional.empty(), applicationJsonHeaders, defaultTimeout);
    }

    public static final Map<String, String> emptyHeaders = Map.of();
    public static final Map<String, String> applicationJsonHeaders = Map.of("Content-Type", "application/json");

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";


}
