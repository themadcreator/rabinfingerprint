package org.rabinfingerprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.rabinfingerprint.Args.ArgParseException;
import org.rabinfingerprint.Args.ArgsModel;
import org.rabinfingerprint.fingerprint.RabinFingerprintLong;
import org.rabinfingerprint.polynomial.Polynomial;

import com.google.common.io.ByteStreams;

public class Main {

	public void fingerprintFiles(List<String> paths, Polynomial p) throws FileNotFoundException,
			IOException {
		final RabinFingerprintLong rabin = new RabinFingerprintLong(p);
		System.out.println(p.toHexString());
		for (String path : paths) {
			File file = new File(path);
			if (file.exists()) {
				rabin.reset();
				rabin.pushBytes(ByteStreams.toByteArray(new FileInputStream(file)));
				System.out.println(String.format("%X %s", rabin.getFingerprintLong(), file.getAbsolutePath()));
				System.out.flush();
			} else {
				System.err.print(String.format("Could not find file %s", path));
				System.err.flush();
			}
		}
	}

	public void fingerprintStdin(Polynomial p) throws IOException {
		final RabinFingerprintLong rabin = new RabinFingerprintLong(p);
		System.out.println(p.toHexString());
		rabin.pushBytes(ByteStreams.toByteArray(System.in));
		System.out.println(String.format("%X", rabin.getFingerprintLong()));
	}

	public void generatePolynomial(int deg) {
		Polynomial p = Polynomial.createIrreducible(deg);
		System.out.println(p.toHexString());
	}

	public void printUsage() throws IOException {
		ByteStreams.copy(getClass().getResourceAsStream("usage.txt"), System.out);
	}

	public Polynomial checkPolynomial(Long l) throws ArgParseException {
		Polynomial p = Polynomial.createFromLong(l);
		if (p.isReducible()) {
			throw new ArgParseException(
					"The specified polynomial is not irreducible and therefore invalid for the rabin fingerprint method. Please use -polygen to generate an irreducible polynomial.");
		}
		return p;
	}

	private ArgsModel model;

	private Main(ArgsModel model) {
		this.model = model;

	}

	private void run() throws Exception {
		switch (model.mode) {
		case FINGERPRINT_FILES:
			fingerprintFiles(model.unflagged, checkPolynomial(model.polynomial));
			break;
		case FINGERPRINT_STDIN:
			fingerprintStdin(checkPolynomial(model.polynomial));
			break;
		case HELP:
			printUsage();
			break;
		case POLYGEN:
			generatePolynomial(model.degree);
			break;
		}
	}

	public static void main(String[] args) {
		try {
			try {
				new Main(new Args().parse(args)).run();
			} catch (ArgParseException e) {
				System.err.println(e.getMessage());
				new Main(null).printUsage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
