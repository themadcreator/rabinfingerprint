package org.rabinfingerprint.fingerprint;


import java.util.Random;

import junit.framework.TestCase;

import org.rabinfingerprint.fingerprint.Fingerprint;
import org.rabinfingerprint.fingerprint.RabinFingerprintLong;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.fingerprint.RabinFingerprintPolynomial;
import org.rabinfingerprint.polynomial.Polynomial;

public class RabinFingerprintTest extends TestCase {
	
	public static void testPolynomialsAndLongs() {
		// generate random data
		byte[] data = new byte[1024];
		Random random = new Random(System.currentTimeMillis());
		random.nextBytes(data);

		// generate random irreducible polynomial
		Polynomial p = Polynomial.createIrreducible(53);
		final Fingerprint<Polynomial> rabin0 = new RabinFingerprintPolynomial(p);
		final Fingerprint<Polynomial> rabin1 = new RabinFingerprintLong(p);
		rabin0.pushBytes(data);
		rabin1.pushBytes(data);
		assertEquals(0, rabin0.getFingerprint().compareTo(rabin1.getFingerprint()));
	}

	public static void testWindowing() {
		doTestWindowing(true, 5);
		doTestWindowing(false, 5);
	}
	
	public static void doTestWindowing(boolean usePolynomials, int times) {
		Random random = new Random(System.currentTimeMillis());
		int windowSize = 8;
		
		for (int i = 0; i < times; i++) {
			// Generate Random Irreducible Polynomial
			Polynomial p = Polynomial.createIrreducible(53);

			final Fingerprint<Polynomial> rabin0, rabin1;
			if (usePolynomials) {
				rabin0 = new RabinFingerprintPolynomial(p, windowSize);
				rabin1 = new RabinFingerprintPolynomial(p);
			} else {
				rabin0 = new RabinFingerprintLongWindowed(p, windowSize);
				rabin1 = new RabinFingerprintLong(p);
			}

			// Generate Random Data
			byte[] data = new byte[windowSize * 5];
			random.nextBytes(data);
			
			// Read 3 windows of data to populate one fingerprint
			for (int j = 0; j < windowSize * 3; j++) {
				rabin0.pushByte(data[j]);
			}

			// Starting from same offset, continue fingerprinting for 1 more window
			for (int j = windowSize * 3; j < windowSize * 4; j++) {
				rabin0.pushByte(data[j]);
				rabin1.pushByte(data[j]);
			}

			assertEquals(0, rabin0.getFingerprint().compareTo(rabin1.getFingerprint()));
		}
	}
}
