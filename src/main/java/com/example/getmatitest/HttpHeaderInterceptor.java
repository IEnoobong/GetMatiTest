package com.example.getmatitest;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

public class HttpHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final String name;

    private final String value;

    /**
     * Creates a new {@link HttpHeaderInterceptor} instance.
     *
     * @param name  the header name to populate. Cannot be null or empty.
     * @param value the header value to populate. Cannot be null or empty.
     */
    public HttpHeaderInterceptor(String name, String value) {
        Assert.hasLength(name, "Name must not be empty");
        Assert.hasLength(value, "Value must not be empty");
        this.name = name;
        this.value = value;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        request.getHeaders().add(this.name, this.value);
        return execution.execute(request, body);
    }

}
