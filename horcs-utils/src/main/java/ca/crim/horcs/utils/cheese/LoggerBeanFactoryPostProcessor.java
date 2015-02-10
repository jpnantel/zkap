package ca.crim.horcs.utils.cheese;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Classe pour augmenter le niveau de cheese d'une classe!
 * 
 * @author nanteljp
 * 
 */
public class LoggerBeanFactoryPostProcessor implements BeanPostProcessor {

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Cheesable.class)) {
            try {
                Field field = bean.getClass().getDeclaredField("cheeseLevel");
                Integer cheeseValue = Integer.MAX_VALUE;
                if (field != null && field.getType().isInstance(cheeseValue)) {
                    field.setAccessible(true);
                    field.set(bean, cheeseValue);
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
