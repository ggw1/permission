package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

@Slf4j
public class JsonMapper {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    public static <T> String obj2String(T src){
        if(src==null){
            return null;
        }
        try{
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        }catch (Exception ex){
            log.warn("parse object to String exception,error:{}",ex);
            return null;
        }
    }

    public static <T> T string2obj(String src, TypeReference<T> tTypeReference){
        if(src==null ||tTypeReference==null){
            return null;
        }
        try{
            return (T) (tTypeReference.getType().equals(String.class) ? src :objectMapper.readValue(src,tTypeReference));
        }catch (Exception ex){
            log.warn("parse String to object exception,String:{},TypeReference<T>:{},error:{}",src,tTypeReference.getType(),ex);
            return null;
        }
    }
}
