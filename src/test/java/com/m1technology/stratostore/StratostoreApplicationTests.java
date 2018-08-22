package com.m1technology.stratostore;

import com.m1technology.stratostore.service.KeyService;
import com.m1technology.stratostore.service.SecretSharingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StratostoreApplicationTests {

	@Autowired
	private KeyService keyService;

	@Autowired
	private SecretSharingService secretSharingService;

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

	}


	@Test
	public void testEncryptAndDecryptDataReturnsExpectedData() {

		String input = "This is my input string with a bunch of special characters, like !@#$%^&*()_+|}{|:<>?'";
		byte[] data = input.getBytes();
		int desiredShares = 11;

		List<byte[]> shares = secretSharingService.share(data, desiredShares, desiredShares);
		byte[] returnedData = secretSharingService.reconstruct(shares);

		assertEquals(input, new String(returnedData, StandardCharsets.UTF_8));
	}
}