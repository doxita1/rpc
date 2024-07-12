package com.doxita.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ConfigUtils {
    /**
     *  加载配置文件
     * @param clazz 目标类
     * @param prefix 前缀
     * @return 目标类
     * @param <T> 目标类
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix){
        return loadConfig(clazz, prefix,"");
    }
    
    /**
     *  加载配置文件
     * @param clazz 目标类
     * @param prefix 前缀
     * @param environment 环境
     * @return 目标类
     * @param <T> 目标类
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)){
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        log.info("load config file: {}", configFileBuilder);
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz, prefix);
    }
}
