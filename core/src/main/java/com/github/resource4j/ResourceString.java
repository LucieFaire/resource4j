package com.github.resource4j;

import java.text.Format;

import com.github.resource4j.util.TypeCastException;

/**
 * Specialized representation of {@link ResourceValue} of type String that supports type conversions and formatting.
 * @author Ivan Gammel
 * @since 1.0
 */
public interface ResourceString extends ResourceValue<String> {

	/**
	 * Converts this value to resource value of given type.
     * @param type class object for the expected type of value
     * @param <T> the expected type of value
	 * @return resource value of given type
	 * @throws TypeCastException if conversion failed
	 */
    <T> ResourceValue<T> ofType(Class<T> type) throws TypeCastException;
    
	/**
	 * Converts this value to resource value of given type using given formatting pattern.
     * @param type class object for the expected type of value
     * @param format the formatting pattern
     * @param <T> the expected type of value
	 * @return resource value of given type
     * @since 2.0
	 * @throws TypeCastException if conversion failed
     */
    <T> ResourceValue<T> ofType(Class<T> type, String format) throws TypeCastException;
    
	/**
	 * Converts this value to resource value of given type using given format.
     * @param type class object for the expected type of value
     * @param format the formatter object
     * @param <T> the expected type of value
	 * @return resource value of given type
     * @since 2.0
	 * @throws TypeCastException if conversion failed
     */
    <T> ResourceValue<T> ofType(Class<T> type, Format format) throws TypeCastException;


}
