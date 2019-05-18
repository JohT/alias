package org.alias.axon.serializer.example.internal;

public class InternalHexDecoder {

	private final String encoded;

	public static final String decode(String encoded) {
		return new InternalHexDecoder(encoded).decode();
	}

	protected InternalHexDecoder(String encoded) {
		this.encoded = encoded;
	}

	public String decode() {
		int len = encoded.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(encoded.charAt(i), 16) << 4)
					+ Character.digit(encoded.charAt(i + 1), 16));
		}
		return new String(data);
	}

	@Override
	public String toString() {
		return "InternalHexDecoder [encoded=" + encoded + "]";
	}
}