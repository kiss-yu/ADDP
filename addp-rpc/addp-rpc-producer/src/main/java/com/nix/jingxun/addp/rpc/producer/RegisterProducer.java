package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author keray
 * @date 2018/12/08 19:55
 */
@Component
public class RegisterProducer implements BeanPostProcessor {

    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName);
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces != null && interfaces.length == 1) {
            Annotation[] annotations = interfaces[0].getAnnotations();
            if (annotations != null && annotations.length > 0) {
                for (Annotation annotation:annotations) {
                    if (annotation instanceof RPCInterfaceAnnotation) {
                        System.out.println(annotation);
                        String appName = ((RPCInterfaceAnnotation)annotation).appName();
                        String group = ((RPCInterfaceAnnotation)annotation).group();
                        String version = ((RPCInterfaceAnnotation)annotation).version();
                        RPCProducer.registerProducer(bean,appName,group,version);
                    }
                }
            }
        }
        return bean;
    }
}