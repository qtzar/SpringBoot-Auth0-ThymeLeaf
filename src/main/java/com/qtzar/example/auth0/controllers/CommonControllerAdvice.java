package com.qtzar.example.auth0.controllers;

import com.auth0.SessionUtils;
import com.auth0.json.auth.UserInfo;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


@ControllerAdvice
public class CommonControllerAdvice {

    @ModelAttribute
    public void addUserModel(ModelMap model, Principal principal, HttpServletRequest req) {
        final UserInfo userInfo = (UserInfo) SessionUtils.get(req, "userInfo");
        if (userInfo == null) {
            model.addAttribute("userName", "Anonymous");
            model.addAttribute("userEmail", "");
            model.addAttribute("userPicture", "/img/person-placeholder.jpg");

        } else {
            model.addAttribute("userName", userInfo.getValues().get("name"));
            model.addAttribute("userEmail", userInfo.getValues().get("email"));
            model.addAttribute("userPicture", userInfo.getValues().get("picture"));
        }
    }

}
