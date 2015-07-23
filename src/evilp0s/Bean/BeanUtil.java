package evilp0s.Bean;

import evilp0s.Collection.CollectionUtil;
import evilp0s.ValidUtil;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * JavaBean相关的一些操作
 */
public class BeanUtil {

    private static Map<String, BeanStruct> simpleProperties(Object obj) {
        return BeanFactory.BEAN_SIMPLE_PROPERTIES.get(obj.getClass().getName());
    }

    private static Map<String, BeanStruct> simplePropertiesIgnore(Object obj) {
        return BeanFactory.BEAN_SIMPLE_PROPERTIESIGNORE.get(obj.getClass().getName());
    }

    private static Method getReadMethod(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simpleProperties(obj).get(pro);
        return st.getReadMethod();
    }

    private static Method getWriteMethod(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simpleProperties(obj).get(pro);
        return st.getWriteMethod();
    }

    private static Method getReadMethodIgnore(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simplePropertiesIgnore(obj).get(pro);
        return st.getReadMethod();
    }

    private static Method getWriteMethodIgnore(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simplePropertiesIgnore(obj).get(pro);
        return st.getWriteMethod();
    }

    private static Object readMethod(Object bean, Method readMethod) throws InvocationTargetException, IllegalAccessException {
        return readMethod.invoke(bean);
    }

    private static void writeMethod(Object bean, Method writeMethod, Object value) throws InvocationTargetException, IllegalAccessException {
        writeMethod.invoke(bean, value);
    }


    /**
     * 添加Bean到BeanFactory的解析范围中
     *
     * @param obj
     */
    public static void add(Object obj) {
        try {
            BeanFactory.add(obj);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加Bean到BeanFactory的解析范围中
     *
     * @param clazz
     */
    public static void add(Class clazz) {
        try {
            BeanFactory.add(clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断属性是否存在
     *
     * @param bean
     * @param pro
     * @return
     */
    public static boolean hasProperty(Object bean, String pro) {
        add(bean);
        Map map = simpleProperties(bean);
        return map.containsKey(pro);
    }


    /**
     * 判断自己定义的而非继承的属性pro是否存在
     *
     * @param bean
     * @param pro
     * @return
     */
    public static boolean hasDeclaredProperty(Object bean, String pro) {
        add(bean);
        Map map = simpleProperties(bean);
        BeanStruct st = (BeanStruct) map.get(pro);
        if (ValidUtil.isValid(st)) {
            return st.isDeclared();
        }
        return false;
    }

    /**
     * 判断属性是否存在忽略大小写
     *
     * @param bean
     * @param pro
     * @return
     */
    public static boolean hasPropertyIgnoreCase(Object bean, String pro) {
        add(bean);
        Map map = simplePropertiesIgnore(bean);
        return map.containsKey(pro.toLowerCase());
    }


    /**
     * 使用自定义的过滤器
     *
     * @param bean
     * @param pro
     * @param filter
     * @return
     */
    public static boolean hasPropertyFilter(Object bean, String pro, PropertyFilter filter) {
        add(bean);
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            for (String s : set) {
                if (pro.equals(filter.Properties(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取对象的属性
     *
     * @param bean
     * @param pro
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object getProperty(Object bean, String pro) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        return readMethod(bean, getReadMethod(bean, pro));
    }

    /**
     * 获取对象的属性
     *
     * @param bean
     * @param pro
     * @return 如果发生异常返回空
     */
    public static Object getPropertyPeaceful(Object bean, String pro) {
        add(bean);
        Object result = null;
        try {
            result = readMethod(bean, getReadMethod(bean, pro));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取对象自定义的属性
     *
     * @param bean
     * @param pro
     * @return
     */
    public static Object getDeclaredPropertyPeaceful(Object bean, String pro) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        Object result = null;
        if (hasDeclaredProperty(bean, pro)) {
            result = readMethod(bean, getReadMethod(bean, pro));
        }
        return result;
    }

    /**
     * 获取对象自定义的属性
     *
     * @param bean
     * @param pro
     * @return
     */
    public static Object getDeclaredProperty(Object bean, String pro) {
        add(bean);
        Object result = null;
        if (hasDeclaredProperty(bean, pro)) {
            try {
                result = readMethod(bean, getReadMethod(bean, pro));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 获取对象的属性(忽略属性名字大小写)
     *
     * @param bean
     * @param pro
     * @return
     */
    public static Object getPropertyIgnoreCase(Object bean, String pro) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        return readMethod(bean, getReadMethodIgnore(bean, pro));
    }


    public static Object getPropertyIgnoreCasePeaceful(Object bean, String pro) {
        add(bean);
        Object result = null;
        try {
            result = readMethod(bean, getReadMethodIgnore(bean, pro));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 使用自定义的过滤器获取对象的属性获取对象的属性
     *
     * @param bean
     * @param pro
     * @param filter
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object getPropertyFilter(Object bean, String pro, PropertyFilter filter) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        Object result = null;
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            for (String s : set) {
                if (pro.equals(filter.Properties(s))) {
                    result = readMethod(bean, getReadMethod(bean, s));
                }
            }
        }
        return result;
    }

    /**
     * 使用自定义的过滤器获取对象的属性
     *
     * @param bean
     * @param pro
     * @param filter
     * @return
     */
    public static Object getPropertyFilterPeaceful(Object bean, String pro, PropertyFilter filter) {
        add(bean);
        Object result = null;
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            try {
                for (String s : set) {
                    if (pro.equals(filter.Properties(s))) {
                        result = readMethod(bean, getReadMethod(bean, s));
                    }
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 设置对象的属性
     *
     * @param bean
     * @param pro
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setProperty(Object bean, String pro, Object value) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        writeMethod(bean, getWriteMethod(bean, pro), value);
    }

    /**
     * 设置对象的属性
     *
     * @param bean
     * @param pro
     * @param value
     */
    public static void setPropertyPeaceful(Object bean, String pro, Object value) {
        add(bean);
        try {
            writeMethod(bean, getWriteMethod(bean, pro), value);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置对象的自定义属性
     *
     * @param bean
     * @param pro
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setDeclaredProperty(Object bean, String pro, Object value) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        if (hasDeclaredProperty(bean, pro)) {
            writeMethod(bean, getWriteMethod(bean, pro), value);
        }
    }

    /**
     * 设置对象的自定义属性
     *
     * @param bean
     * @param pro
     * @param value
     */
    public static void setDeclaredPropertyPeaceful(Object bean, String pro, Object value) {
        add(bean);
        if (hasDeclaredProperty(bean, pro)) {
            try {
                writeMethod(bean, getWriteMethod(bean, pro), value);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置对象的属性忽略大小写
     *
     * @param bean
     * @param pro
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setPropertyIgnoreCase(Object bean, String pro, Object value) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
    }

    /**
     * 设置对象的属性忽略大小写
     *
     * @param bean
     * @param pro
     * @param value
     */
    public static void setPropertyIgnoreCasePeaceful(Object bean, String pro, Object value) {
        add(bean);
        try {
            writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 使用自定义的filter进行属性设值
     *
     * @param bean
     * @param pro
     * @param value
     * @param filter
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setPropertyFilter(Object bean, String pro, Object value, PropertyFilter filter) throws InvocationTargetException, IllegalAccessException {
        add(bean);
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            for (String s : set) {
                if (pro.equals(filter.Properties(s))) {
                    writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
                }
            }

        }
    }

    /**
     * 使用自定义的filter进行属性设值
     *
     * @param bean
     * @param pro
     * @param value
     * @param filter
     */
    public static void setPropertyFilterPeaceful(Object bean, String pro, Object value, PropertyFilter filter) {
        add(bean);
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            try {
                for (String s : set) {
                    if (pro.equals(filter.Properties(s))) {
                        writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
                    }
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 拷贝对象指定的属性
     *
     * @param srcBean
     * @param destBean
     * @param pros
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void copyProperty(Object srcBean, Object destBean, String[] pros) throws InvocationTargetException, IllegalAccessException {
        add(srcBean);
        add(destBean);
        if (ValidUtil.isValid(pros)) {
            for (String s : pros) {
                Object value = readMethod(srcBean, getReadMethod(srcBean, s));
                writeMethod(destBean, getWriteMethod(destBean, s), value);
            }
        }
    }

    public static void copyPropertyPeaceful(Object srcBean, Object destBean, String[] pros) {
        add(srcBean);
        add(destBean);
        if (ValidUtil.isValid(pros)) {
            try {
                for (String s : pros) {
                    writeMethod(destBean, getWriteMethod(destBean, s), readMethod(srcBean, getReadMethod(srcBean, s)));
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制同名属性
     *
     * @param srcBean 源Bean
     * @param destBean 目标Bean
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void copyProperties(Object srcBean, Object destBean) throws InvocationTargetException, IllegalAccessException {
        add(srcBean);
        add(destBean);
        Map srcMap = simpleProperties(srcBean);
        Map dstMap = simpleProperties(destBean);
        Map intersection = CollectionUtil.intersection(srcMap, dstMap);
        Iterator iter = intersection.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            Object value = readMethod(srcBean, getReadMethod(srcBean, key));
            writeMethod(destBean, getWriteMethod(destBean, key), value);
        }
    }


    public static void copyPropertiesIgnoreCase(Object srcBean,Object destBean) throws InvocationTargetException, IllegalAccessException {
        add(srcBean);
        add(destBean);
        Map srcMap = simplePropertiesIgnore(srcBean);
        Map dstMap = simplePropertiesIgnore(destBean);
        Map intersection = CollectionUtil.intersection(srcMap, dstMap);
        Iterator iter = intersection.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            Object value = readMethod(srcBean, getReadMethodIgnore(srcBean, key));
            writeMethod(destBean, getWriteMethodIgnore(destBean, key), value);
        }
    }



    public static void copyProperties(Object srcBean, Object destBean, PropertyFilter filter) {
        add(srcBean);
        add(destBean);
        Map srcMap = simpleProperties(srcBean);
        Map dstMap = simpleProperties(destBean);
    }
}