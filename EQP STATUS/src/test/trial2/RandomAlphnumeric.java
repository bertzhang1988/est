package test.trial2;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class RandomAlphnumeric {
	@Test
	public void f() {
		// SecureRandom random = new SecureRandom();
		// System.out.println(new BigInteger(130, random).toString(32));
		int i = 0;
		while (i < 20) {
			System.out.println(RandomStringUtils.randomAlphanumeric((int) ((Math.random() * 9) + 1)));
			i++;
		}
	}
}
