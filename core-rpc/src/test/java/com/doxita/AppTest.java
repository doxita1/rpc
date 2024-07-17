package com.doxita;

import cn.hutool.core.util.IdUtil;
import com.doxita.constant.RpcConstant;
import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.protocol.*;
import com.doxita.rpc.serializer.Serializer;
import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import io.vertx.core.buffer.Buffer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.doxita.rpc.protocol.ProtocolConstant.PROTOCOL_MAGIC;
import static com.doxita.rpc.protocol.ProtocolConstant.PROTOCOL_VERSION;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    
    public void testApp2()
    {
        assertTrue( true );
    }
    
    public void testApp3()
    {
        Class<?> clazz = Serializer.class;
        System.out.println(clazz.getName());
    }
    
    public void testApp4() throws ExecutionException, InterruptedException {
        Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();
        KV kvClient = client.getKVClient();
        
        ByteSequence key = ByteSequence.from("test", Charsets.UTF_8);
        ByteSequence value = ByteSequence.from("test", Charsets.UTF_8);
        
        kvClient.put(key, value);
        CompletableFuture<GetResponse> getResponseCompletableFuture = kvClient.get(key);
        GetResponse getResponse = getResponseCompletableFuture.get();
        System.out.println(getResponse);
    }
    
    public void testEncoderAndDecoder() throws Exception {
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(PROTOCOL_MAGIC);
        header.setVersion(PROTOCOL_VERSION);
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getCode());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getCode());
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("myService");
        rpcRequest.setMethodName("myMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"hello","world"});
        protocolMessage.setBody(rpcRequest);
        protocolMessage.setHeader(header);
        
        Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
        
        ProtocolMessage<?> decode = ProtocolMessageDecoder.decode(encode);
        System.out.println(decode);
        
        
    }
}
