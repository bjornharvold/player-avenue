package com.online.casino.web.converter;

import org.springframework.core.convert.converter.Converter;

import com.online.casino.domain.entity.Stake;

public class StakeToStringConverter implements Converter<Stake, String> {

	@Override
	public String convert(Stake source) {
		String result = null;
		
		if (source != null) {
			result = source.getId().toString();
		}
        
		return result;
	}

}
