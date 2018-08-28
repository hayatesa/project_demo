package com.dev.main.kaptcha.controller;

import com.dev.main.common.exception.DataNotFoundException;
import com.dev.main.common.exception.IllegalParameterException;
import com.dev.main.common.util.CommonUtil;
import com.dev.main.common.util.CookieUtils;
import com.dev.main.common.util.ResultMap;
import com.dev.main.kaptcha.service.ICaptchaService;
import com.dev.main.kaptcha.util.CaptchaConstant;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码相关
 */
@Controller
@RequestMapping("/api/captcha")
public class VCodeController {

    @Autowired
    private ICaptchaService captchaService;

    public void setCaptchaService(ICaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @RequestMapping(path="generate",method = RequestMethod.GET)
    public ModelAndView getKaptchaImage(@CookieValue(value = CaptchaConstant.COOKIE_NAME, required = false) String uuidInCookie,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        // 清除原来的Cookie
        if (!StringUtils.isBlank(uuidInCookie)) {
            CookieUtils.clearCookie(request, response, CaptchaConstant.COOKIE_NAME, CaptchaConstant.COOKIE_PATH);
        }
        captchaService.generate(request, response);
        return null;
    }

    @RequestMapping(path = "/verify/{codeInput}", method = RequestMethod.GET)
    @ResponseBody
    public ResultMap verify(@CookieValue(value = CaptchaConstant.COOKIE_NAME, required = false) String uuidInCookie,
                            @PathVariable("codeInput") String codeInput,
                            HttpServletRequest request, HttpServletResponse response) {
        boolean isMatch = captchaService.verify(uuidInCookie, codeInput, request, response);
        if (!isMatch) {
            return ResultMap.error("验证码错误");
        }
        return ResultMap.success();
    }

}
