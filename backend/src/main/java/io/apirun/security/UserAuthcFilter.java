package io.apirun.security;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

public class UserAuthcFilter extends UserFilter {


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        WebApplicationContext ctx = RequestContextUtils.findWebApplicationContext(httpServletRequest);
        RequestMappingHandlerMapping mapping = ctx.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        HandlerExecutionChain handler = null;
        try {
            handler = mapping.getHandler(httpServletRequest);
            Annotation[] declaredAnnotations = ((HandlerMethod) handler.getHandler()).
                    getMethod().getDeclaredAnnotations();
            for(Annotation annotation:declaredAnnotations){
                /**
                 *如果含有@GuestAccess注解，则认为是不需要验证是否登录，
                 *直接放行即可
                 **/
                if(GuestAccess.class.equals(annotation.annotationType())){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.isAccessAllowed(request, response, mappedValue);
    }
}
