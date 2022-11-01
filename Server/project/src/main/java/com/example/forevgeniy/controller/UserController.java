package com.example.forevgeniy.controller;


import com.example.forevgeniy.dao.entities.StatsResponse;
import com.example.forevgeniy.dao.entities.User;
import com.example.forevgeniy.dao.entities.UserProfileLogin;
import com.example.forevgeniy.dao.entities.UserProfileRegister;
import com.example.forevgeniy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        passwordEncoder = new BCryptPasswordEncoder();
    }



    @PostMapping("/checkLogin")
    public boolean login(HttpServletResponse response, @RequestBody UserProfileLogin userProfileLogin) {
        User expectedUser = userService.getUserByEmail(userProfileLogin.email);
        if(expectedUser!=null && passwordEncoder.matches(userProfileLogin.password,expectedUser.getPassword())) {
            Cookie cookie = new Cookie("user", expectedUser.getRole());//создаем объект Cookie,
            cookie.setPath("/");//устанавливаем путь
            cookie.setMaxAge(86400);
            response.addCookie(cookie);
            response.setContentType("text/plain");
            return true; //main
        }
        return false;
    }

    @GetMapping("/exit")
    public String login(HttpServletResponse response) {
        Cookie cookie = new Cookie("user", null);//создаем объект Cookie,
        cookie.setPath("/");//устанавливаем путь
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
        response.setContentType("text/plain");
        return "redirect:/index";
    }

    @GetMapping("/exittt")
    public StatsResponse login() {
        return new StatsResponse();
    }

    @PostMapping("/checkRegister")
    public boolean register(HttpServletResponse response, @RequestBody UserProfileRegister userProfileRegister){
        User expectedUser = userService.getUserByEmail(userProfileRegister.email);
        if(expectedUser!=null) return false;
        User user = new User(userProfileRegister.firstName,userProfileRegister.surname,userProfileRegister.email, passwordEncoder.encode(userProfileRegister.password), "user");
        userService.addUser(user);
        Cookie cookie = new Cookie("user", "user");//создаем объект Cookie,
        cookie.setPath("/");//устанавливаем путь
        cookie.setMaxAge(86400);//устанавливаем время жизни
        response.addCookie(cookie);
        response.setContentType("text/plain");
        return true;
    }

    @GetMapping("/index")
    public ModelAndView mainPage(Model model) {
        return new ModelAndView("index");
    }

    @GetMapping("/map")
    public String mapPage(HttpServletRequest request) {
        Cookie userCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("user")).collect(Collectors.toList()).get(0);
        if(userCookie.getValue().equals("user") || userCookie.getValue().equals("admin")) {
            return "Перешли на мапу";
        }
        return "Permission denied!";
    }

    @GetMapping("/stats")
    public String statsPage(HttpServletRequest request) {
        Cookie userCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("user")).collect(Collectors.toList()).get(0);
        if(userCookie.getValue().equals("admin")) {
            return "Перешли на стату";
        }
        return "Permission denied!";
    }

}
