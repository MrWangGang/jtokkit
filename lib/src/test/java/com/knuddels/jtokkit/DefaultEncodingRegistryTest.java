package com.knuddels.jtokkit;

import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.GptBytePairEncodingParams;
import com.knuddels.jtokkit.api.ModelType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEncodingRegistryTest {

	private final DefaultEncodingRegistry registry = new DefaultEncodingRegistry();

	@Test
	public void getEncodingReturnsCorrectEncoding() {
		registry.initializeDefaultEncodings();

		for (final EncodingType type : EncodingType.values()) {
			final Encoding encoding = registry.getEncoding(type);
			assertNotNull(encoding);
			assertEquals(type.getName(), encoding.getName());
		}
	}

	@Test
	public void getEncodingForModelReturnsCorrectsEncoding() {
		registry.initializeDefaultEncodings();

		for (final ModelType modelType : ModelType.values()) {
			final Encoding encoding = registry.getEncodingForModel(modelType);
			assertNotNull(encoding);
			assertEquals(modelType.getEncodingType().getName(), encoding.getName());
		}
	}

	@Test
	public void canRegisterCustomEncoding() {
		final Encoding encoding = new DummyEncoding();

		registry.registerCustomEncoding(encoding);

		final Optional<Encoding> retrievedEncoding = registry.getEncoding(encoding.getName());

		assertTrue(retrievedEncoding.isPresent());
		assertEquals(encoding, retrievedEncoding.get());
	}

	@Test
	public void canRegisterCustomGptBpe() {
		final GptBytePairEncodingParams params = new GptBytePairEncodingParams(
				"custom",
				Pattern.compile("test"),
				Collections.emptyMap(),
				Collections.emptyMap()
		);

		registry.registerGptBytePairEncoding(params);

		final Optional<Encoding> retrievedEncoding = registry.getEncoding(params.getName());

		assertTrue(retrievedEncoding.isPresent());
		assertEquals(params.getName(), retrievedEncoding.get().getName());
	}

	@Test
	public void throwsIfCustomEncodingIsAlreadyRegistered() {
		final Encoding encoding = new DummyEncoding();

		registry.registerCustomEncoding(encoding);

		assertThrows(IllegalStateException.class, () -> registry.registerCustomEncoding(encoding));
	}

	private static class DummyEncoding implements Encoding {

		@Override
		public List<Integer> encode(final String text) {
			return null;
		}

		@Override
		public int countTokens(final String text) {
			return 0;
		}

		@Override
		public String decode(final List<Integer> tokens) {
			return null;
		}

		@Override
		public byte[] decodeBytes(final List<Integer> tokens) {
			return new byte[0];
		}

		@Override
		public String getName() {
			return "dummy";
		}
	}
}
