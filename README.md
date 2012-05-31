rabinfingerprint
================

A Java implementation of the rabin fingerprinting method. (http://en.wikipedia.org/wiki/Rabin_fingerprint)

Optimized for use on a stream including a sliding window fingerprint.

Includes arbitrary-precision-polynomial hashing as well as very fast long-based hashing implementations, which are best for most hashing uses.

### Command line ###

Generate a new irreducible polynomial
```
% java -jar rabinfingerprint.jar -polygen 53
3DE9DD57CA448B
```

Fingerprint a file
```
% java -jar rabinfingerprint.jar -p 3DE9DD57CA448B file.test
```

Fingerprint STDIN
```
% cat file.test | java -jar rabinfingerprint.jar -p 3DE9DD57CA448B
```