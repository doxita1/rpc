package com.doxita.rpc.spi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.map.MapUtil;
import com.doxita.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spi加载器
 */

@Slf4j
public class SpiLoader {
    
    /**
     * 存储已经加载的类 接口->(key->实现类)
     */
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    
    /**
     * 存储已经实例化的对象
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    
    /**
     * 系统spi路径
     */
    private static final String RPC_SYSTEM_SPI_PATH = "META-INF/rpc/system/";
    
    /**
     * 用户spi路径
     */
    private static final String RPC_USER_SPI_PATH = "META-INF/rpc/custom/";
    
    /**
     * 扫描路径
     */
    private static final String[] SCAN_PATH = new String[]{RPC_SYSTEM_SPI_PATH, RPC_USER_SPI_PATH};
    
    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> CLASS_LIST = Arrays.asList(Serializer.class);
    
    /**
     * 加载某个类的实现类
     *
     * @param loadClass 要加载的类
     * @return
     */
    
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("load class 为{} 的SPI", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new ConcurrentHashMap<>();
        for (String path : SCAN_PATH) {
            List<URL> resources = ResourceUtil.getResources(path + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        // 读取到的内容
                        String[] split = line.split("=");
                        // 获取key
                        String key = split[0];
                        // 获取实现类
                        String className = split[1];
                        // 获取实现类
                        Class<?> aClass = Class.forName(className);
                        // 添加到map中
                        keyClassMap.put(key, aClass);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    log.error("SPI resources load error: ", e);
                }
            }
        }
        if (!keyClassMap.isEmpty()) {
            loaderMap.put(loadClass.getName(), keyClassMap);
            return keyClassMap;
        } else {
            return MapUtil.empty();
        }
    }
    
    /**
     * 加载所有spi
     */
    public static void loadAll(){
        log.info("load all SPI");
        for (Class<?> aClass : CLASS_LIST){
            load(aClass);
        }
    }
    
    /**
     * 获取实例
     * @param key
     * @param loadClass
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> loadClass,String key) {
        String className = loadClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(className);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SPI 没有加载{}",className));
        }
        if(!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SPI 没有找到key为{}的实现类",key));
        }
        Class<?> aClass = keyClassMap.get(key);
        String keyName = aClass.getName();
        if(!instanceCache.containsKey(aClass.getName())){
            try {
                instanceCache.put(keyName, aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("SPI getInstance error: ", e);
                throw new RuntimeException(e);
            }
        }
        return (T) instanceCache.get(keyName);
    }
}
