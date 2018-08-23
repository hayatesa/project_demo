package com.dev.main.common.controller;

import com.dev.main.common.util.CommonUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * 验证码相关
 */
@Controller
@RequestMapping("vcode")
public class VCodeController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "captcha", method = RequestMethod.GET)
    public ModelAndView getKaptchaImage(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control",
                "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText = captchaProducer.createText();

        String uuid = CommonUtil.createUUID();
        try {
            redisTemplate.opsForValue().set(uuid, capText, 60 * 5, TimeUnit.SECONDS);
            Cookie cookie = new Cookie("captchaCode", uuid);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(uuid + ": " + capText);
        System.out.println("Value In Redis： "+redisTemplate.opsForValue().get(uuid));
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

    //@RequestMapping("verify")

    public void setCaptchaProducer(DefaultKaptcha captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
