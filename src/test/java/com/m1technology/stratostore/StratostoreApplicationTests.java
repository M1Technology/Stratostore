package com.m1technology.stratostore;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.m1technology.stratostore.service.EndecService;
import com.m1technology.stratostore.service.KeyService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StratostoreApplicationTests {

	@Autowired
    private KeyService keyService;
	
	@Autowired
	private EndecService endecService;
	 
	
	@Test
	public void contextLoads() {
	}

	@Test 
	public void testKeyServiceReturnsProperLengthKey() {
		
		final byte[] key1 = keyService.getKey(100);
		assertEquals(key1.length, 100);
		
		final byte[] key2 = keyService.getKey(2000);
		assertEquals(key2.length, 2000);
		
		final byte[] key3 = keyService.getKey(30000);
		assertEquals(key3.length, 30000);
		
		final byte[] key4 = keyService.getKey(400000);
		assertEquals(key4.length, 400000);
		
		final byte[] key5 = keyService.getKey(50000000);
		assertEquals(key5.length, 50000000);
		
		final byte[] key6 = keyService.getKey(600000000);
		assertEquals(key6.length, 600000000);
		
		final byte[] keyMax = keyService.getKey(Integer.MAX_VALUE-2);
		assertEquals(keyMax.length, Integer.MAX_VALUE-2);
		
	}
	
	@Test 
	public void testEncodeAndDecodeDataReturnsExpectedData() {
	
		String input = "This is my input string with a bunch of special characters, like !@#$%^&*()_+|}{|:<>?'";
		byte[] data = input.getBytes();
		
		int desiredFragments = 3;
		
		byte[][] fragments = endecService.encode2(data, desiredFragments);
		
		byte[] returnedData = endecService.decode2(fragments);
		
		assertEquals(input, new String(returnedData, StandardCharsets.UTF_8));
		
	}
	
}
