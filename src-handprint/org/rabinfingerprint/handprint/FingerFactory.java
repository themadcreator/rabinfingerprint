package org.rabinfingerprint.handprint;

import java.io.IOException;
import java.io.InputStream;

import org.rabinfingerprint.datastructures.Interval;
import org.rabinfingerprint.fingerprint.RabinFingerprintLong;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;

public class FingerFactory {
	public static interface BoundaryDetectorStrategy{
		public boolean isBoundary(RabinFingerprintLong fingerprint);
	}
	
	public static class ByteMaskBoundaryDetectoryStrategy implements BoundaryDetectorStrategy {
		private final long chunkBoundaryMask;
		private final long chunkPattern;

		public ByteMaskBoundaryDetectoryStrategy(long chunkBoundaryMask, long chunkPattern) {
			this.chunkBoundaryMask = chunkBoundaryMask;
			this.chunkPattern = chunkPattern;
		}

		public boolean isBoundary(RabinFingerprintLong fingerprint) {
			return (fingerprint.getFingerprintLong() & chunkBoundaryMask) == chunkPattern;
		}
	}
	
	private final RabinFingerprintLong finger;
	private final RabinFingerprintLongWindowed fingerWindow;
	private final BoundaryDetectorStrategy boundaryDetector;

	public FingerFactory(Polynomial p, long bytesPerWindow, BoundaryDetectorStrategy boundaryDetector) {
		this.finger = new RabinFingerprintLong(p);
		this.fingerWindow = new RabinFingerprintLongWindowed(p, bytesPerWindow);
		this.boundaryDetector = boundaryDetector;
	}

	private RabinFingerprintLong newFingerprint() {
		return new RabinFingerprintLong(finger);
	}

	private RabinFingerprintLongWindowed newWindowedFingerprint() {
		return new RabinFingerprintLongWindowed(fingerWindow);
	}

	/**
	 * Fingerprint the file into chunks called "Fingers". The chunk boundaries
	 * are determined using a windowed fingerprinter such as
	 * {@link RabinFingerprintLongWindowed}. This guarantees that a long chunk
	 * of data will always contains some fingers that hash to same value. This
	 * is the KEY to the utility of the handprinting scheme for file similarity.
	 * Even if you re-arrange a file's contents or corrupt parts of it, the hand
	 * print will be able to find all the parts that are similar in a very
	 * efficient manner.
	 */
	public Multimap<Long, Interval> getAllFingers(InputStream is) throws IOException {
		// windowing fingerprinter for finding chunk boundaries. this is only
		// reset at the beginning of the file
		final RabinFingerprintLong window = newWindowedFingerprint();

		// fingerprinter for chunks. this is reset after each chunk
		final RabinFingerprintLong finger = newFingerprint();

		// counters
		long chunkStart = 0;
		long chunkEnd = 0;

		/*
		 * fingerprint one byte at a time. we have to use this granularity to
		 * ensure that, for example, a one byte offset at the beginning of the
		 * file won't effect the chunk boundaries
		 */
		final Multimap<Long, Interval> chunks = ArrayListMultimap.create();
		is.reset();
		for (byte b : ByteStreams.toByteArray(is)) {
			// push byte into fingerprints
			window.pushByte(b);
			finger.pushByte(b);
			chunkEnd++;

			/*
			 * if we've reached a boundary (which we will at some probability
			 * based on the boundary pattern and the size of the fingerprint
			 * window), we store the current chunk fingerprint and reset the
			 * chunk fingerprinter.
			 */
			if (boundaryDetector.isBoundary(window)) {
				chunks.put(finger.getFingerprintLong(), new Interval(chunkStart, chunkEnd));
				finger.reset();
				
				// store last chunk offset
				chunkStart = chunkEnd;
			}
		}

		// final chunk
		chunks.put(finger.getFingerprintLong(), new Interval(chunkStart, chunkEnd));
		return chunks;
	}

	/**
	 * Rapidly fingerprint an entire file's contents.
	 * 
	 * We use the term "Palm" to describe the fingerprint of the entire file,
	 * instead of chunks, which are referred to as the file's "Fingers".
	 */
	public long getPalm(InputStream is) throws IOException {
		final RabinFingerprintLong finger = newFingerprint();
		is.reset();
		finger.pushBytes(ByteStreams.toByteArray(is));
		return finger.getFingerprintLong();
	}
}