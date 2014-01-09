package com.online.casino.web.converter;

import com.online.casino.service.AdministrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

import com.online.casino.domain.entity.Stake;

public class StringToStakeConverter implements Converter<String, Stake> {
    private final AdministrationService administrationService;

    public StringToStakeConverter(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
	public Stake convert(String source) {
		Stake result = null;
		
		if (StringUtils.isNotBlank(source)) {
			result = administrationService.findStake(source);
		}
		
		return result;
	}

}
