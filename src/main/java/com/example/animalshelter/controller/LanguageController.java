package com.example.animalshelter.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;

@Controller
public class LanguageController {

    @GetMapping("/changeLang")
    public String setLanguage(@RequestParam("lang") String lang,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        request.getSession().setAttribute("LANGUAGE", lang);

        // Получение предыдущего URL
        String referer = request.getHeader("Referer");

        // Возвращение пользователя на предыдущий URL после смены языка
        return "redirect:" + (referer != null ? referer : "/home");
    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        if (request.getSession().getAttribute("LANGUAGE") == null) {
            request.getSession().setAttribute("LANGUAGE", "ru");
        }
        return "redirect:/home";
    }
}