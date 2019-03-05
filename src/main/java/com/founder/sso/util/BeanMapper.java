package com.founder.sso.util;

import org.dozer.DozerBeanMapper;

public class BeanMapper {
    private static DozerBeanMapper dozer= new DozerBeanMapper();
    
    public static <T> T map(Object source,Class<T> destinationClass) {
        return dozer.map(source, destinationClass);
    }
    
    public static void copy(Object source,Object destination) {
        dozer.map(source, destination);
        
    }

    public static void setDozer(DozerBeanMapper dozer) {
        BeanMapper.dozer = dozer;
    }

}
