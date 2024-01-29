package servlets;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        super.init();
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("D:/2_JAVA/HomeWork_Modul11_Cookies/src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=utf-8");

        Context context = new Context(
                req.getLocale(),
                Map.of("time", parseTime(req, resp))
        );

        engine.process("index", context, resp.getWriter());
        resp.getWriter().close();
    }

    private String parseTime(HttpServletRequest request, HttpServletResponse response) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime;

        if (request.getParameterMap().containsKey("timezone")) {
            String utc = request.getParameter("timezone").replace(" ", "+");
            zonedDateTime = ZonedDateTime.now(ZoneId.of(utc));
            updateLastTimeZoneCookie(response, utc);
        } else {
            zonedDateTime = getLastTimeZoneFromCookie(request);
        }

        return dateTimeFormatter.format(zonedDateTime) + " " + String.format("UTC%+d", zonedDateTime.getOffset().getTotalSeconds() / 3600);
    }

    private void updateLastTimeZoneCookie(HttpServletResponse response, String utc) {
        Cookie lastTimeZone = new Cookie("lastTimeZone", utc);
        response.addCookie(lastTimeZone);
        lastTimeZone.setMaxAge(60 * 60);
    }

    private ZonedDateTime getLastTimeZoneFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimeZone")) {
                    return ZonedDateTime.now(ZoneId.of(cookie.getValue()));
                }
            }
        }

        return ZonedDateTime.now();
    }
}

