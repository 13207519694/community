package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtils;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) { //???????????????????????????SpringMVC??????user???????????????Model???
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "????????????,??????????????????????????????????????????????????????,???????????????!");
            model.addAttribute("target", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/register";
        }
    }

    // http://localhost:8080/community/activation/101/code  @PathVariable??????????????????
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "????????????,???????????????????????????????????????!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "????????????,???????????????????????????!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "????????????,??????????????????????????????!");
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // ??????????????????session
//        session.setAttribute("kaptcha", text);
        // ??????????????????
        String kaptchaOwner = CommunityUtils.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // ??????????????????Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("?????????????????????" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, /*HttpSession session,*/ HttpServletResponse response,
                        @CookieValue(value = "kaptchaOwner",required = false) String kaptchaOwner) { //?????????????????????????????????SpringMVC?????????user???????????????Model???,?????????request??????
        // ???????????????
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        // ???redis????????????
        if(StringUtils.isNoneBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "??????????????????!");
            return "site/login";
        }

        // ????????????,??????
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login"; //??????????????????get??????
    }

    // ???????????? ??????
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage(){
        return "site/forget";
    }

    // ???????????? ????????????
    @RequestMapping(path = "/forget-kaptcha/{username}/{email}", method = RequestMethod.GET)
    public String getForgetKaptcha(@PathVariable("username") String username, @PathVariable("email") String email, Model model){
        model.addAttribute("username", username);
        model.addAttribute("email", email);

        Map<String, Object> map = userService.findForgetKaptcha(username, email);
        if (map == null || map.isEmpty()) {
            model.addAttribute("kaptchamsg", "???????????????????????????????????????????????????????????????2??????, ????????????????????????!");
            return "site/forget";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/forget";
        }
    }

    // ???????????? ???????????? userService.updatePassword(int userId, String password)
    @RequestMapping(path = "/forget", method = RequestMethod.POST)
    public String updatePassword(String username, String email, String code, String password, Model model){ // model ??????????????????????????????
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        if(StringUtils.isBlank(email)){
            model.addAttribute("emailmsg", "?????????????????????");
            return "site/forget";
        }
        if(StringUtils.isBlank(username)){
            model.addAttribute("usernamemsg", "????????????????????????");
            return "site/forget";
        }

        // ??????redis???????????????
        if(StringUtils.isBlank(code)){
            model.addAttribute("kaptchamsg", "????????????????????????");
            return "site/forget";
        }
        String kaptcha = null;
        String redisKey = RedisKeyUtil.getForgetKey(email);
        kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("kaptchamsg", "??????????????????!");
            return "site/forget";
        }

        // ????????????
        if(StringUtils.isBlank(password)){
            model.addAttribute("passwordmsg", "?????????????????????!");
            return "site/forget";
        }
        User user = userService.findUserByName(username);
        user.setPassword(CommunityUtils.md5(password + user.getSalt()));
        userService.updatePassword(user.getId(), user.getPassword());

        // ???????????????

        return "site/login";
    }

}
