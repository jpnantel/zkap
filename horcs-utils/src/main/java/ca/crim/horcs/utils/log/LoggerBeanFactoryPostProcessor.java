package ca.crim.horcs.utils.log;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Classe pour traiter les annotations loggable pour l'injection d'un logger
 * slf4j.
 * 
 * @author nanteljp
 * 
 */
public class LoggerBeanFactoryPostProcessor implements BeanPostProcessor {

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Loggable.class)) {
            try {
                Field field = bean.getClass().getDeclaredField("logger");
                Logger logger = LoggerFactory.getLogger(bean.getClass());
                if (field != null && field.getType().isInstance(logger)) {
                    field.setAccessible(true);
                    field.set(bean, logger);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        return bean;
    }
}
