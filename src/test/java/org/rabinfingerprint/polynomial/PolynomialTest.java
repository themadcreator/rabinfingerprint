package org.rabinfingerprint.polynomial;

import junit.framework.TestCase;

import org.rabinfingerprint.polynomial.Polynomial;
import org.rabinfingerprint.polynomial.Polynomial.Reducibility;

public class PolynomialTest extends TestCase {
	/**
	 * Tests loading and printing out of polynomials.
	 * 
	 * The polys used are from here:
	 * http://en.wikipedia.org/wiki/Finite_field_arithmetic#Rijndael.27s_finite_field
	 */
	public void testPolynomialArithmetic() {
		Polynomial pa = Polynomial.createFromLong(0x53);
		Polynomial pb = Polynomial.createFromLong(0xCA);
		Polynomial pm = Polynomial.createFromLong(0x11B);
		Polynomial px = pa.multiply(pb);
		assertEquals(0x3F7E, px.toBigInteger().longValue());
		Polynomial pabm = px.mod(pm);
		assertEquals(0x1, pabm.toBigInteger().longValue());
	}

	/**
	 * According to Rabin, the expected number of tests required to find an
	 * irreducible polynomial from a randomly chosen monic polynomial of degree
	 * k is k (neat, huh!).
	 * 
	 * Therefore, we should see an average spread of k reducible polynomials
	 * between irreducible ones. This test computes the running average of these
	 * spreads for verification.
	 * 
	 * This is not a perfect correctness verification, but it is a good "mine
	 * canary".
	 */
	public void testIrreducibleSpread() {
		int degree = 15;
		Stats stats = getSpread(degree, 200);
		double spread = Math.abs(stats.average() - degree);
		assertTrue("Spread of irreducible polynomials is out of expected range: " + spread, spread < 3);
	}
	
	public Stats getSpread(int degree, int tests) {
		int i = 0;
		int last_i = 0;
		Stats stats = new Stats();
		while (tests > 0) {
			Polynomial f = Polynomial.createRandom(degree);
			Reducibility r = f.getReducibility();
			if (r == Reducibility.IRREDUCIBLE) {
				int spread = i - last_i;
				stats.add(spread);
				last_i = i;
				tests--;
			}
			i++;
		}
		return stats;
	}

}
