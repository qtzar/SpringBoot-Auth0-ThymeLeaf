package com.qtzar.example.auth0.controllers.security;

import com.auth0.AuthenticationController;
import com.qtzar.example.auth0.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unused")
@Controller
@Slf4j
public class LoginController {

    @Autowired
    private AuthenticationController controller;

    @Autowired
    private AppConfig appConfig;


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    protected String login(final HttpServletRequest req) {
        log.debug("Controller : /login");

        String redirectUri = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/callback";
        String authorizeUrl = controller.buildAuthorizeUrl(req, redirectUri)
                .withScope("openid profile email")
                .withAudience(String.format("https://%s/userinfo", appConfig.getDomain()))
                .build();
        return "redirect:" + authorizeUrl;
    }

    @RequestMapping(value = "/login", params = "error")
    public String loginError() {
        return "error";
    }

}