package com.nix.jingxun.addp.web.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;
import com.nix.jingxun.addp.web.common.config.WebConfig;

/**
 * @author keray
 * @date 2019/05/19 14:15
 */
public class AESUtil {
    private static final AES AES = new AES(WebConfig.aesKey.getBytes());
    /**
     * 加密
     * */
    public static String encryption(String source) {
        if (StrUtil.isBlank(source)) {
            return null;
        }
        return AES.encryptBase64(source);
    }
    /**
     * 解密
     * */
    public static String decrypt(String base64Str) {
        if (StrUtil.isBlank(base64Str)) {
            return null;
        }
        return AES.decryptStr(base64Str);
    }
}
