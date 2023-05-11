package io.apirun.ldap.controller;

import io.apirun.base.domain.User;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.constants.ParamConstants;
import io.apirun.commons.constants.UserSource;
import io.apirun.commons.exception.MSException;
import io.apirun.controller.ResultHolder;
import io.apirun.controller.request.LoginRequest;
import io.apirun.i18n.Translator;
import io.apirun.ldap.service.LdapService;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.SystemParameterService;
import io.apirun.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ldap")
public class LdapController {

    @Resource
    private UserService userService;
    @Resource
    private LdapService ldapService;
    @Resource
    private SystemParameterService systemParameterService;

    @PostMapping(value = "/signin")
    @MsAuditLog(module = "system_parameter_setting", type = OperLogConstants.LOGIN, title = "LDAP")
    public ResultHolder login(@RequestBody LoginRequest request) {

        String isOpen = systemParameterService.getValue(ParamConstants.LDAP.OPEN.getValue());
        if (StringUtils.isBlank(isOpen) || StringUtils.equals(Boolean.FALSE.toString(), isOpen)) {
            MSException.throwException(Translator.get("ldap_authentication_not_enabled"));
        }

        DirContextOperations dirContext = ldapService.authenticate(request);
        String email = ldapService.getMappingAttr("email", dirContext);
        String userId = ldapService.getMappingAttr("username", dirContext);

        SecurityUtils.getSubject().getSession().setAttribute("authenticate", UserSource.LDAP.name());
        SecurityUtils.getSubject().getSession().setAttribute("email", email);


        if (StringUtils.isBlank(email)) {
            MSException.throwException(Translator.get("login_fail_email_null"));
        }

        // userId 或 email 有一个相同即为存在本地用户
        User u = userService.selectUser(userId, email);
        String name = ldapService.getMappingAttr("name", dirContext);
        String phone = ldapService.getNotRequiredMappingAttr("phone", dirContext);
        if (u == null) {

            // 新建用户 获取LDAP映射属性
//            String name = ldapService.getMappingAttr("name", dirContext);
//            String phone = ldapService.getNotRequiredMappingAttr("phone", dirContext);

            User user = new User();
            user.setId(userId);
            user.setName(name);
            user.setEmail(email);

            if (StringUtils.isNotBlank(phone)) {
                user.setPhone(phone);
            }

            user.setSource(UserSource.LDAP.name());
            userService.addLdapUser(user);
        } else {
            // 更新
            u.setName(name);
            u.setPhone(phone);
            u.setEmail(email);
            userService.updateUser(u);
        }

        // 执行 LocalRealm 中 LDAP 登录逻辑
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(userId);
        return userService.login(loginRequest);
    }

    @PostMapping("/test/connect")
    public void testConnect() {
        ldapService.testConnect();
    }

    @PostMapping("/test/login")
    @MsAuditLog(module = "system_parameter_setting", type = OperLogConstants.LOGIN, title = "LDAP")
    public void testLogin(@RequestBody LoginRequest request) {
        ldapService.authenticate(request);
    }

    @GetMapping("/open")
    public boolean isOpen() {
        return ldapService.isOpen();
    }

}
