package tech.zg.research.lucene.util;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;
import tech.zg.research.lucene.annotation.LongPointMark;
import tech.zg.research.lucene.annotation.StoreFieldMark;
import tech.zg.research.lucene.annotation.StringFieldMark;
import tech.zg.research.lucene.annotation.TextFieldMark;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文档创建工具
 *
 * @version V1.0.0
 * @author: 张弓
 */
public class DocumentCreator<T> {

    private static final Map<Class, Class> DOC_TYPE_MAP = new HashMap<>();
    private static final Map<Class, Class> BASE_TYPE_MAP = new HashMap<>();

    static {
        DOC_TYPE_MAP.put(LongPointMark.class, LongPoint.class);
        DOC_TYPE_MAP.put(StoreFieldMark.class, StoredField.class);
        DOC_TYPE_MAP.put(StringFieldMark.class, StringField.class);
        DOC_TYPE_MAP.put(TextFieldMark.class, TextField.class);

        BASE_TYPE_MAP.put(Long.class, Long.TYPE);
        BASE_TYPE_MAP.put(Double.class, Double.TYPE);
        BASE_TYPE_MAP.put(Float.class, Float.TYPE);
        BASE_TYPE_MAP.put(Boolean.class, Boolean.TYPE);
        BASE_TYPE_MAP.put(Void.class, Void.TYPE);
        BASE_TYPE_MAP.put(Short.class, Short.TYPE);
    }

    public Document createDocument(T object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Document document = new Document();

        Class clazz = object.getClass();
        Field[] fieldArray = clazz.getDeclaredFields();
        for (Field field : fieldArray) {
            field.setAccessible(Boolean.TRUE);
            // 获取字段名
            String fieldName = field.getName();
            // 获取字段值
            Object fieldValue = field.get(object);
            // 遍历字段注解
            Annotation[] annotations = field.getAnnotations();
            if (annotations != null && annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    org.apache.lucene.document.Field.Store store = null;
                    if (annotation instanceof StringFieldMark) {
                        StringFieldMark stringFieldMark = field.getAnnotation(StringFieldMark.class);
                        store = stringFieldMark.store() == 1 ? org.apache.lucene.document.Field.Store.YES : org.apache.lucene.document.Field.Store.NO;
                    } else if (annotation instanceof TextFieldMark) {
                        TextFieldMark stringFieldMark = field.getAnnotation(TextFieldMark.class);
                        store = stringFieldMark.store() == 1 ? org.apache.lucene.document.Field.Store.YES : org.apache.lucene.document.Field.Store.NO;
                    }
                    Class docFieldClazz = DOC_TYPE_MAP.get(annotation.annotationType());
                    Constructor constructor = null;
                    Class type = BASE_TYPE_MAP.get(field.getType()) == null ? field.getType() : BASE_TYPE_MAP.get(field.getType());
                    if (store != null) {
                        constructor = docFieldClazz.getConstructor(String.class, type, org.apache.lucene.document.Field.Store.class);
                        document.add((IndexableField) constructor.newInstance(fieldName, fieldValue, store));
                    } else {
                        constructor = docFieldClazz.getConstructor(String.class, type);
                        document.add((IndexableField) constructor.newInstance(fieldName, fieldValue));
                    }
                }
            }
        }

        return document;
    }
}
