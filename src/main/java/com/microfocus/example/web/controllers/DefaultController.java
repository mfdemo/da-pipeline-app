/*
        Simple Secure App

        Copyright (C) 2020 Micro Focus or one of its affiliates

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.microfocus.example.web.controllers;

import com.microfocus.example.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Default (root) controllers
 * @author Kevin A. Lee
 */
@Controller
public class DefaultController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @Value("${messages.home:default-value}")
    private String message = "Hello World";

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            User loggedInUser = (User) ((Authentication) principal).getPrincipal();
            model.addAttribute("user", loggedInUser);
        }
        model.addAttribute("message", message);
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, Principal principal) {
        if (principal != null) {
            User loggedInUser = (User) ((Authentication) principal).getPrincipal();
            model.addAttribute("user", loggedInUser);
        }
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model, Principal principal) {
        if (principal != null) {
            User loggedInUser = (User) ((Authentication) principal).getPrincipal();
            String userInfo = WebUtils.toString(loggedInUser);
            model.addAttribute("user", userInfo);
            String message = "Sorry " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);
        }
        return "/error/403-access-denied";
    }

}
