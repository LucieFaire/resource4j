package com.github.resource4j;

import static org.junit.Assert.*;

import org.junit.Test;

public abstract class OptionalValueContracts extends ResourceValueContracts {
	
	@Override
	protected abstract OptionalValue<String> createValue(ResourceKey key, String content);

	@Test
	public void testOrDefaultReturnsOptionalValueContentWhenNotNull() throws Exception {
		String content = someContent();
		OptionalValue<String> value = createValue(anyKey(), content);
		assertSame(content, value.orDefault("Another content"));
	}
	
	@Test
	public void testOrReturnsMandatoryValueWithSameContentWhenNotNull() throws Exception {
		String content = someContent();
		OptionalValue<String> value = createValue(anyKey(), content);
		MandatoryValue<String> mandatoryValue = value.or("Another content");
		assertSame(value.asIs(), mandatoryValue.asIs());
		assertSame(value.key(), mandatoryValue.key());
	}

	@Test
	public void testOrReturnsMandatoryValueWithDefaultContentWhenNull() throws Exception {
		String content = someContent();
		OptionalValue<String> value = createValue(anyKey(), null);
		MandatoryValue<String> mandatoryValue = value.or(content);
		assertSame(content, mandatoryValue.asIs());
		assertSame(value.key(), mandatoryValue.key());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOrThrowsIllegalArgumentExceptionIfDefaultValueIsNull() throws Exception {
		OptionalValue<String> value = createValue(anyKey(), someContent());
		value.or(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testOrDefaultIllegalArgumentExceptionIfDefaultValueIsNull() throws Exception {
		OptionalValue<String> value = createValue(anyKey(), someContent());
		value.orDefault(null);
	}

	@Test
	public void testNotNullReturnsMandatoryValueWithSameContentWhenNotNull() throws Exception {
		String content = someContent();
		OptionalValue<String> value = createValue(anyKey(), content);
		MandatoryValue<String> mandatoryValue = value.notNull();
		assertSame(value.asIs(), mandatoryValue.asIs());
		assertSame(value.key(), mandatoryValue.key());
	}

	@Test(expected = MissingValueException.class)
	public void testNotNullThrowsMissingValueExceptionWhenNull() throws Exception {
		OptionalValue<String> value = createValue(anyKey(), null);
		value.notNull();
	}
	
}