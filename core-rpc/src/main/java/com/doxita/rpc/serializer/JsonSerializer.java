package com.doxita.rpc.serializer;



import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonSerializer implements Serializer{
   public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T res = OBJECT_MAPPER.readValue(bytes, type);
        if(res instanceof RpcRequest){
           return handleRequest((RpcRequest) res,type);
        }else if(res instanceof RpcResponse){
            return handleResponse((RpcResponse) res,type);
        }
        return res;
    }
    
    
    /**
     * Obejcet 原始对象会被擦除, 导致序列化会作为LinkedHashMap, 这里做出特殊处理
     */
    private <T> T handleRequest(RpcRequest request,Class<T> type) throws IOException {
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] args = request.getArgs();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            if(!clazz.isAssignableFrom(args[i].getClass())) {
                byte[] argsBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argsBytes, clazz);
            }
        }
        return type.cast(request);
    }
    
    private <T> T handleResponse(RpcResponse response,Class<T> type) throws IOException {
       byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(response.getData());
       response.setData(OBJECT_MAPPER.readValue(dataBytes, response.getDataType()));
       return type.cast(response);
    }
}
