package com.online.casino.web.converter;

import com.online.casino.domain.entity.Casino;
import org.springframework.core.convert.converter.Converter;

public class CasinoToStringConverter implements Converter<Casino, String> {

	@Override
	public String convert(Casino source) {
		String result = null;

		if (source != null) {
			result = source.getId().toString();
		}

		return result;
	}

}