package com.haojg.spi.loader;

import com.haojg.spi.annotation.HaojgSpi;
import com.haojg.spi.util.Holder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class HaojgExtensionLoader<T> {

    //extension 的配置路径
    private static final String HAOJG_DIR = "META-INF/HAOJG/";

    private static final Pattern NAME_SEPARATOR=Pattern.compile("\\s*[,]+\\s");

    private static final ConcurrentHashMap<Class<?>, HaojgExtensionLoader<?>> EXTENSION_LOADS = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<?>, Object> EXTENSION_INSTANCES=new ConcurrentHashMap<>();

    private final Class<?> type;

    private String cachedDefaultName;

    private Map<String,IllegalStateException> exceptions = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private final ConcurrentHashMap<String, Holder> cachedInstances = new ConcurrentHashMap<>();
    private static <T> boolean withExtensionLoader(Class<T> type){
        return type.isAnnotationPresent(HaojgSpi.class);
    }
    private HaojgExtensionLoader(Class<T> type){
        this.type=type;
    }

    public static <T> HaojgExtensionLoader<T> getExtensionLoader(Class<T> type){
        HaojgExtensionLoader<T> loader = (HaojgExtensionLoader<T>) EXTENSION_LOADS.get(type);
        if(null == loader){
            EXTENSION_LOADS.putIfAbsent(type, new HaojgExtensionLoader<>(type));
            loader= (HaojgExtensionLoader<T>) EXTENSION_LOADS.get(type);
        }
        return loader;
    }

    public T getExtension(String name){
        Holder<Object> holder = cachedInstances.get(name);
        if(null == holder){
            cachedInstances.putIfAbsent(name, new Holder<Object>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.getT();
        if(null == instance){
            synchronized (holder){
                instance = holder.getT();
                if(null == instance){
                    instance = createExtension(name);
                    holder.setT(instance);
                }
            }
        }
        return (T)instance;
    }

    private T createExtension(String name){
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException(name);
        }
        try {
            T instance = (T)  EXTENSION_INSTANCES.get(clazz);
            if(null == instance){
                EXTENSION_INSTANCES.putIfAbsent(clazz, (T) clazz.newInstance());//反射生成对象
                instance = (T)  EXTENSION_INSTANCES.get(clazz);
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance(name: " + name + ", class: " +
                    type + ")  could not be instantiated: " + t.getMessage(), t);
        }
    }

    private Map<String, Class<?>> getExtensionClasses(){
        Map<String,Class<?>> classes = cachedClasses.getT();
        if(null == classes){
            synchronized (cachedClasses){
                classes = cachedClasses.getT();
                if(null == classes){
                    classes = loadExtensionClasses();
                    cachedClasses.setT(classes);
                }
            }
        }
        return classes;
    }

    public  Map<String,Class<?>> loadExtensionClasses(){
        final HaojgSpi defaultAnnoation = type.getAnnotation(HaojgSpi.class);
        if(null!=defaultAnnoation){
            String value = defaultAnnoation.value();
            if(null!=value && (value=value.trim()).length()>0){
                String names[]=NAME_SEPARATOR.split("value");
                if(names.length>1){
//                    error;
                }
                if(names.length == 1){
                    cachedDefaultName =names[0];
                }
            }
        }
        Map<String, Class<?>> extensionClasses = new HashMap<>();
        loadFile(extensionClasses, HAOJG_DIR);
        return extensionClasses;
    }

    private void loadFile(Map<String, Class<?>> extensionClasses, String dir) {
        String fileName = dir + type.getName(); // 固定文件夹 + 接口名全路径

        // 对本地spi扩展文件操作
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (null != classLoader) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (null != urls) {
                while (urls.hasMoreElements()) {
                    java.net.URL url = urls.nextElement();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(url.openStream(), "UTF-8"));
                        try {
                            String line = null;
                            while ((line = bufferedReader.readLine()) != null) {
                                final int ci = line.indexOf('#');
                                if (ci > 0)
                                    line = line.substring(0, ci);
                                line = line.trim();
                                if (line.length() > 0) {
                                    try {
                                        String name = null;
                                        int i = line.indexOf("=");
                                        if (i > 0) {
                                            name = line.substring(0, i).trim();
                                            line = line.substring(i + 1).trim();
                                        }
                                        if (line.length() > 0) {
                                            Class<?> clazz = Class.forName(line, true, classLoader); // 鼓掌!这里终于得到spi(classs)
                                            if (!type.isAssignableFrom(clazz)) {
                                                throw new IllegalStateException(
                                                        "Error when load extension class(interface: " + type
                                                                + ", class line: " + clazz.getName() + "), class "
                                                                + clazz.getName() + "is not subtype of interface.");
                                            }
                                            extensionClasses.put(name, clazz);// 加入缓存
                                        }
                                    } catch (Throwable t) {
                                        IllegalStateException e = new IllegalStateException(
                                                "Failed to load extension class(interface: " + type + ", class line: "
                                                        + line + ") in " + url + ", cause: " + t.getMessage(),
                                                t);
                                        exceptions.put(line, e);
                                    }
                                }
                            }
                        } finally {
                            bufferedReader.close();
                        }
                    } catch (Exception e) {
                        // ignore...
                    }
                }
            }
        } catch (Exception e) {
            // ignore...
        }

    }


    private static ClassLoader findClassLoader(){
        return HaojgExtensionLoader.class.getClassLoader();
    }

    public T getDefaultExtension(){
        getExtensionClasses();
        if(null == cachedDefaultName || cachedDefaultName.length()==0 || "true".equals(cachedDefaultName)){
            return null;
        }
        return getExtension(cachedDefaultName);
    }

}
