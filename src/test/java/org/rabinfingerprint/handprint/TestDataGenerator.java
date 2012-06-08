package org.rabinfingerprint.handprint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class TestDataGenerator {
	public static int TEST_RESOURCE_BYTES = 4 * (1 << 20); // 4 MB

	public static List<InputStream> getSimilarRandomBytes(int n) throws IOException {
		Random rand = new Random(42);
		List<InputStream> list = Lists.newArrayList();
		byte[] bytes = new byte[TEST_RESOURCE_BYTES];
		rand.nextBytes(bytes);
		while (n-- > 0) {
			byte[] ibytes = new byte[TEST_RESOURCE_BYTES];
			System.arraycopy(bytes, 0, ibytes, 0, bytes.length);
			
			// change 100 random bytes
			for(int i = 0; i < 100; i++){
				ibytes[(int)(rand.nextDouble() * TEST_RESOURCE_BYTES)] = (byte)rand.nextInt();
			}
			
			list.add(new ByteArrayInputStream(ibytes));
		}
		return list;
	}
	
	public static List<InputStream> getDifferentRandomBytes(int n) throws IOException {
		List<InputStream> list = Lists.newArrayList();
		Random rand = new Random(54);
		while (n-- > 0) {
			byte[] bytes = new byte[TEST_RESOURCE_BYTES];
			rand.nextBytes(bytes);
			list.add(new ByteArrayInputStream(bytes));
		}
		return list;
	}
}
