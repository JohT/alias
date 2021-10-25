package org.alias.example.customized.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomizedFileNamePropertiesTest {

	private static final String PROPERTY_FILE = "org/alias/example/customized/properties/CustomizedFileName.properties";

	private Properties properties = new Properties();

	@BeforeEach
	public void setUp() {
		loadProperties(PROPERTY_FILE);
	}

	@Test
	public void notCustomizedStandardPropertyFileTypeRegistered() {
		assertEquals(CustomNamedPropertyFileType.class.getName(), properties.get("AliasForCustomNamedPropertyFile"));
	}

	@Test
	public void anotherNotCustomizedStandardPropertyFileTypeRegistered() {
		assertEquals(AnotherCustomNamedPropertyFileType.class.getName(),
				properties.get("AliasForAnotherCustomNamedPropertyFile"));
	}

	private void loadProperties(String filename) {
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename.replace('/', File.separatorChar))) {
			properties.load(inputStream);
		} catch (IOException e) {
			throw error(e, "The file %s couldn't be read.");
		}
	}

	private final static RuntimeException error(Exception e, String message, Object... messageParameter) {
		return new IllegalStateException(String.format(message, messageParameter), e);
	}
}