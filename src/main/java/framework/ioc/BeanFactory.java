package framework.ioc;

import framework.annotation.Autowired;
import framework.annotation.Path;
import framework.mvc.Handler;
import framework.mvc.HandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanFactory {
    private static final Map<String, Object> BEAN_MAP = new HashMap<>();

    public BeanFactory() {
        System.out.println("New BeanFactory，开始扫描包、创建Bean并注入容器！同时处理Controller");



        System.out.println("完成 BeanFactory 创建，完成Bean创建、注入及Controller处理！");
    }

    /**
     * 获取单例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getSingletonBean(Class<T> clazz) {
        //1.判断容器中是否有需要的Bean
        if (BEAN_MAP.containsKey(clazz.toString())){
            return (T)BEAN_MAP.get(clazz.toString());
        }

        //2.容器中无需要的Bean，递归创建
        //2.1.判断是不是Controller，如果是，创建Handler
        if (clazz.isAnnotationPresent(Path.class)){
            String classPath = clazz.getAnnotation(Path.class).value();
            Method[] methods = clazz.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.isAnnotationPresent(Path.class)){
                    String methodPath = method.getAnnotation(Path.class).value();
                    String path=classPath+methodPath;
                    Handler handler = new Handler(path, clazz, method);
                    HandlerMapping.addHandler(handler);
                }
            }
        }
        //2.2.创建Bean
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                //判断是否被Autowried注解
                if (field.isAnnotationPresent(Autowired.class)){
                    field.set(t,getSingletonBean(field.getType()));
                }
            }
            BEAN_MAP.put(clazz.toString(),t);
            return t;
        }catch (Exception e){
            System.out.println(e.toString());
            return null;
        }
    }
}
