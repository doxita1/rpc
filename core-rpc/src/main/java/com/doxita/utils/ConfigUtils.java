package com.doxita.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ConfigUtils {
    /**
     * 加载配置文件
     *
     * @param clazz  目标类
     * @param prefix 前缀
     * @param <T>    目标类
     * @return 目标类
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, "");
    }
    
    /**
     * 根据指定的类、前缀和环境加载配置文件。
     * <p>
     * 此方法用于动态加载指定环境的配置文件，并将配置文件中的参数映射到指定的Java类对象中。
     * 这样做可以方便地在不同环境下使用相同的配置类，而不需要修改代码，只需要加载对应的配置文件即可。
     * </p>
     *
     * @param clazz 配置类的Class对象，用于创建并填充配置数据的对象。
     * @param prefix 配置项的前缀，用于在配置文件中定位特定的配置项。
     * @param environment 环境标识，用于生成特定环境的配置文件名称，如"dev"、"test"、"prod"等。
     *                    如果环境为空，则加载默认的"application.properties"文件。
     * @param <T> 配置类的类型。
     
     * @return 返回一个填充了配置数据的配置类对象。
     */
    /**
     * 加载配置文件
     *
     * @param clazz       目标类
     * @param prefix      前缀
     * @param environment 环境
     * @param <T>         目标类
     * @return 目标类
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {
        // 构建配置文件名称，基于环境变量添加特定的前缀，如"application-dev.properties"。
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        // 记录加载的配置文件名。
        log.info("load config file: {}", configFileBuilder);
        // 创建Props对象，用于加载和解析配置文件。
        Props props = new Props(configFileBuilder.toString());
        // 将配置文件中的数据映射到指定的类对象中，并返回该对象。
        // prefix参数用于指定配置项的前缀，这在处理具有层级结构的配置属性时非常有用
        return props.toBean(clazz, prefix);
    }
    
}
