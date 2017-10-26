package com.qtzar.example.auth0.controllers.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@SuppressWarnings("unused")
@Controller
@Slf4j
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {
    private static final String PATH = "/error";

    @RequestMapping("/error")
    protected String error(final RedirectAttributes redirectAttributes, final HttpServletRequest req) throws IOException {
        log.error("Controller : /error");

        String referrer = req.getHeader("Referer");
        req.getSession().setAttribute("url_prior_login", referrer);

        log.debug("Referrer Url : " + req.getHeader("Referer"));
        log.debug("Request URI : " + req.getRequestURI());

        Enumeration<String> e = req.getHeaderNames();

        while (e.hasMoreElements()) {
            String param = e.nextElement();
            log.debug("Header : " + param);
        }

        redirectAttributes.addFlashAttribute("error", true);
        return "redirect:/login";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}