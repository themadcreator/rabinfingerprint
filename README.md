rabinfingerprint
================

A Java implementation of the rabin fingerprinting method. (http://en.wikipedia.org/wiki/Rabin_fingerprint)

Optimized for use on a stream including a sliding window fingerprint.

Includes arbitrary-precision-polynomial hashing as well as very fast long-based hashing implementations, which are best for most hashing uses.

### Generating a fingerprint ###
```Java
// Create new random irreducible polynomial
// These can also be created from Longs or hex Strings
Polynomial polynomial = Polynomial.createIrreducible(53);

// Create a fingerprint object
RabinFingerprintLong rabin = new RabinFingerprintLong(polynomial);

// Push bytes from a file stream
rabin.pushBytes(ByteStreams.toByteArray(new FileInputStream("file.test")));

// Get fingerprint value and output
System.out.println(Long.toString(rabin.getFingerprintLong(), 16));
```

### Generating a sliding-window fingerprint ###
```Java
// Create new random irreducible polynomial
// These can also be created from Longs or hex Strings
Polynomial polynomial = Polynomial.createIrreducible(53);

// Create a windowed fingerprint object with a window size of 48 bytes.
RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(polynomial, 48);
for (byte b : ByteStreams.toByteArray(new FileInputStream("file.test"))) {
	// Push in one byte. Old bytes are automatically popped.
	window.pushByte(b);
	// Output current window's fingerprint
	System.out.println(Long.toString(window.getFingerprintLong(), 16));
}
```

### Building ###

This project uses Maven for dependency management. To build this project's runnable jar, sources and javadoc, run this command:

```
% mvn clean install
```

### Command line ###

[Full Usage](https://github.com/themadcreator/rabinfingerprint/blob/master/src/main/resources/usage.txt)

Generate a new irreducible polynomial
```
% java -jar rabinfingerprint.jar -polygen 53
3DE9DD57CA448B
```

Fingerprint a file
```
% java -jar rabinfingerprint.jar -p 3DE9DD57CA448B file.test
43A39C59491F /[path to file]/file.test
```

Fingerprint STDIN
```
% cat file.test | java -jar rabinfingerprint.jar -p 3DE9DD57CA448B
43A39C59491F
```
