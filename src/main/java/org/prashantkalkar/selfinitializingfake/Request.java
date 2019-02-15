package org.prashantkalkar.selfinitializingfake;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public final class Request {
    private final String method;
    private final String requestURI;

    public Request(final HttpServletRequest request) {
        this.method = request.getMethod();
        this.requestURI = request.getRequestURI();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(method, request.method) &&
                Objects.equals(requestURI, request.requestURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, requestURI);
    }
}
