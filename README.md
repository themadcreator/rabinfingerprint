rabinfingerprint
================

A Java implementation of the rabin fingerprinting method. (http://en.wikipedia.org/wiki/Rabin_fingerprint)

Optimized for use on a stream including a sliding window fingerprint.

Includes arbitrary-precision-polynomial hashing as well as very fast long-based hashing implementations, which are best for most hashing uses.

### API Usage ###
```Java
// Create new random irreducible polynomial
// These can also be created from Longs or hex Strings
Polynomial polynomial = Polynomial.createIrreducible(53);
		
// Create a fingerprint object
RabinFingerprintLong rabin = new RabinFingerprintLong(polynomial);
		
// Push bytes from file stream
rabin.pushBytes(ByteStreams.toByteArray(new FileInputStream("file.test")));
		
// Get fingerprint value and output
System.out.println(Long.toString(rabin.getFingerprintLong(), 16));
```

### Command line ###

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
```
43A39C59491F