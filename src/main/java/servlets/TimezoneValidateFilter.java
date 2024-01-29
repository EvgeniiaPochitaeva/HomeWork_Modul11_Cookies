package servlets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        var existsParameter = req.getParameter("timezone");

        if(existsParameter == null) {
            chain.doFilter(req, res);
        }

        String timezoneParameter = req.getParameter("timezone").replace(" ", "+");

        if (timezoneParameter != null && !timezoneParameter.isEmpty()) {
            if (!isValidTimezone(timezoneParameter)) {
                res.setContentType("text/html; charset=utf-8");
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Invalid timezone");
                return;
            }
        }

        chain.doFilter(req, res);
    }
    private boolean isValidTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
