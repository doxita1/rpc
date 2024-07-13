package com.doxita;

import com.doxita.rpc.serializer.Serializer;
import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
}
