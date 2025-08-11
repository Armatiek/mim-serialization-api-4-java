package nl.geostandaarden.mim.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {
  
  public static String getId(Object obj) {
    String id = null;
    try {
      Method method = obj.getClass().getMethod("getId");
      id = (String) method.invoke(obj);
    } catch (NoSuchMethodException | InvocationTargetException e) { 
      return null;
    } catch (IllegalAccessException e) { 
      throw new RuntimeException(e);
    }
    return id; 
  }
  
}