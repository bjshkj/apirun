package io.apirun.controller;

import com.alibaba.fastjson.JSONObject;
import io.apirun.base.domain.User;
import io.apirun.base.domain.UserGroup;
import io.apirun.base.mapper.UserGroupMapper;
import io.apirun.commons.constants.UserSource;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.user.SessionUser;
import io.apirun.commons.utils.*;
import io.apirun.commons.utils.constants.BaseConstants;
import io.apirun.controller.request.LoginRequest;
import io.apirun.dto.UserDTO;
import io.apirun.i18n.Translator;
import io.apirun.security.GuestAccess;
import io.apirun.service.BaseDisplayService;
import io.apirun.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static io.apirun.commons.utils.constants.BaseConstants.*;

@RestController
@RequestMapping
public class LoginController {

    @Resource
    private UserService userService;
    @Resource
    private BaseDisplayService baseDisplayService;

    @GuestAccess
    @PostMapping(value = "/signin")
    public ResultHolder login(@RequestBody LoginRequest request) {
        SessionUser sessionUser = SessionUtils.getUser();
        if (sessionUser != null) {
            if (!StringUtils.equals(sessionUser.getId(), request.getUsername())) {
                return ResultHolder.error(Translator.get("please_logout_current_user"));
            }
        }
        SecurityUtils.getSubject().getSession().setAttribute("authenticate", UserSource.LOCAL.name());
        return userService.login(request);
    }

    @GetMapping(value = "/currentUser")
    public ResultHolder currentUser() {
        return ResultHolder.success(SecurityUtils.getSubject().getSession().getAttribute("user"));
    }

    @GetMapping(value = "/signout")
    public ResultHolder logout(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        userService.logout();
        SecurityUtils.getSubject().logout();
        return ResultHolder.success("");
    }

    /*Get default language*/
    @GetMapping(value = "/language")
    public String getDefaultLanguage() {
        return userService.getDefaultLanguage();
    }

    @GetMapping("display/file/{imageName}")
    public ResponseEntity<byte[]> image(@PathVariable("imageName") String imageName) throws IOException {
        return baseDisplayService.getImage(imageName);
    }

}
