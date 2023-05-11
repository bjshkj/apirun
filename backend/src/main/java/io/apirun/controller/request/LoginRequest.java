package io.apirun.controller.request;

import io.apirun.commons.utils.CommonBeanFactory;
import io.apirun.commons.utils.RsaKey;
import io.apirun.commons.utils.RsaUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
    private String authenticate;


    public String getUsername() {
        try {
            RsaKey rsaKey = CommonBeanFactory.getBean(RsaKey.class);
            return RsaUtil.privateDecrypt(username, rsaKey.getPrivateKey());
        } catch (Exception e) {
            return username;
        }
    }

    public String getPassword() {
        try {
            RsaKey rsaKey = CommonBeanFactory.getBean(RsaKey.class);
            return RsaUtil.privateDecrypt(password, rsaKey.getPrivateKey());
        } catch (Exception e) {
            return password;
        }
    }
}
