package at.tuwien.ase.tripidude.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestCodeGenerator {

	private static final AtomicInteger seed = new AtomicInteger();

	public static int getFreshInt() {
		return seed.incrementAndGet();
	}

}
