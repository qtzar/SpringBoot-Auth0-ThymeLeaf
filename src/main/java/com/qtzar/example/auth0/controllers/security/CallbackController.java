package com.qtzar.example.auth0.controllers.security;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.SessionUtils;
import com.auth0.Tokens;
import com.auth0.client.auth.AuthAPI;
import com.auth0.json.auth.UserInfo;
import com.auth0.jwt.JWT;
import com.auth0.net.Request;
import com.qtzar.example.auth0.config.AppConfig;
import com.qtzar.example.auth0.config.TokenAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("unused")
@Controller
@Slf4j
public class CallbackController {

    private final String redirectOnFail;

    @Autowired
    private AppConfig config;
    @Autowired
    private AuthenticationController controller;

    public CallbackController() {
        this.redirectOnFail = "/login";
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    protected void getCallback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        log.debug("Controller : /callback - GET");
        handle(req, res);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    protected void postCallback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        log.debug("Controller : /callback - POST");
        handle(req, res);
    }

    private void handle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Tokens tokens = controller.handle(req);
            TokenAuthentication tokenAuth = new TokenAuthentication(JWT.decode(tokens.getIdToken()));
            SecurityContextHolder.getContext().setAuthentication(tokenAuth);

            AuthAPI auth = new AuthAPI(config.getDomain(), config.getClientId(), config.getClientSecret());
            Request<UserInfo> request = auth.userInfo(tokens.getAccessToken());
            UserInfo userInfo = request.execute();
            SessionUtils.set(req, "userInfo", userInfo);

            log.debug("UserInfo.getValues : " + userInfo.getValues().toString());

            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(req, res);
            if (savedRequest != null) {
                res.sendRedirect(savedRequest.getRedirectUrl());
            } else {
                res.sendRedirect("/");
            }

        } catch (AuthenticationException | IdentityVerificationException e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();
            res.sendRedirect(redirectOnFail);
        }
    }

}