package com.ziplly.app.client.view.factory;

import java.util.HashMap;
import java.util.Map;

import com.ziplly.app.model.NeighborhoodDTO;

public class ConverterFactory {
	private static Map<Key, Converter<?,?>> converters = new HashMap<Key, Converter<?,?>>();
	
	static {
		converters.put(new Key(NeighborhoodDTO.class, String.class), new NeighborhoodToStringConverter());
	}
	
	
	public static Converter<?,?> getConverter(Class<?> value, Class<?> result) {
		return ConverterFactory.converters.get(new Key(value, result));
	}
	
	private static class Key {
		Class<?> value;
		Class<?> result;
		
		Key(Class<?> value, Class<?> result) {
			this.value = value;
			this.result = result;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			
			if (!(o instanceof Key)) {
				return false;
			}
			
			Key key = (Key)o;
			return value == key.value && result == key.result; 
		}
		
		@Override
		public int hashCode() {
			return value.hashCode() + result.hashCode();
		}
	}
}

