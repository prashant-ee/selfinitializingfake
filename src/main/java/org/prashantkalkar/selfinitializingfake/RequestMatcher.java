package org.prashantkalkar.selfinitializingfake;

import javax.servlet.http.HttpServletRequest;

public class RequestMatcher {

    public static boolean isRequestMatch(HttpServletRequest req1, HttpServletRequest req2) {
        if(!req1.getMethod().equals(req2.getMethod()))
            return false;

        if(!req1.getRequestURI().equals(req2.getRequestURI()))
            return false;

        return true;
    }
}
