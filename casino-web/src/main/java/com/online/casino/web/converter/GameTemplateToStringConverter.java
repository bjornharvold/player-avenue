package com.online.casino.web.converter;

import com.online.casino.domain.entity.GameTemplate;
import org.springframework.core.convert.converter.Converter;

public class GameTemplateToStringConverter implements Converter<GameTemplate, String> {

	@Override
	public String convert(GameTemplate source) {
		String result = null;

		if (source != null) {
			result = source.getId().toString();
		}

		return result;
	}

}