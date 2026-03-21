package com.xfs.base.util;

import com.xfs.base.exception.ServiceException;
import com.xfs.base.response.StatusCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 轻量登录态工具：
 * 1. 登录时生成签名token
 * 2. 业务入口从 Authorization 解析当前用户ID
 */
public final class AuthTokenUtil {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SECRET = "xfs-vehicle-auth-secret";
    private static final long EXPIRE_MILLIS = 7L * 24 * 60 * 60 * 1000;

    private AuthTokenUtil() {}

    public static String generateToken(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
        long expireAt = System.currentTimeMillis() + EXPIRE_MILLIS;
        String payload = userId + ":" + expireAt;
        String signature = sign(payload);
        String raw = payload + "." + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Long getCurrentUserId() {
        HttpServletRequest request = currentRequest();
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.trim().isEmpty()) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
        String token = authorization.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        return parseToken(token);
    }

    public static Long parseToken(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] sections = decoded.split("\\.");
            if (sections.length != 2) {
                throw new ServiceException(StatusCode.UNAUTHORIZED);
            }

            String payload = sections[0];
            String signature = sections[1];
            if (!sign(payload).equals(signature)) {
                throw new ServiceException(StatusCode.UNAUTHORIZED);
            }

            String[] payloadSections = payload.split(":");
            if (payloadSections.length != 2) {
                throw new ServiceException(StatusCode.UNAUTHORIZED);
            }

            Long userId = Long.valueOf(payloadSections[0]);
            long expireAt = Long.parseLong(payloadSections[1]);
            if (System.currentTimeMillis() > expireAt) {
                throw new ServiceException(StatusCode.UNAUTHORIZED);
            }
            return userId;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || attributes.getRequest() == null) {
            throw new ServiceException(StatusCode.UNAUTHORIZED);
        }
        return attributes.getRequest();
    }

    private static String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] sign = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sign);
        } catch (Exception ex) {
            throw new ServiceException(StatusCode.OPERATION_FAILED);
        }
    }
}

