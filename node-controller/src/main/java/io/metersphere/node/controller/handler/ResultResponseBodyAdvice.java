package io.metersphere.node.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.node.util.LogUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一处理返回结果集
 */
@RestControllerAdvice(value = {"io.metersphere.node"})
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType) || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 处理空值
        if (o == null && StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return null;
        }

        if (!(o instanceof ResultHolder)) {
            if (o instanceof String) {
                ResultHolder success = ResultHolder.success(o);
                try {
                    return objectMapper.writeValueAsString(success);
                } catch (JsonProcessingException e) {
                    LogUtil.error(e);
                    return o;
                }
            }
            return ResultHolder.success(o);
        }
        return o;
    }

}
