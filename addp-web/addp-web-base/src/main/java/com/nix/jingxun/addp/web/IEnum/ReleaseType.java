package com.nix.jingxun.addp.web.IEnum;

/**
 * @author keray
 * @date 2019/05/20 18:03
 * 发布单发阶段发布布状态
 */
public enum ReleaseType {
    // 发布暂停
    stop,
    //wait
    wait,
    // 发布中
    run,
    // 发布失败
    releaseFail,
    // 发布成功
    releaseSuccess
}
