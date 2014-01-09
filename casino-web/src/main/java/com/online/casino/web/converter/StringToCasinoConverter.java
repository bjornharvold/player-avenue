package com.online.casino.web.converter;

import com.online.casino.domain.entity.Casino;
import com.online.casino.service.AdministrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class StringToCasinoConverter implements Converter<String, Casino> {
    private final AdministrationService administrationService;

    public StringToCasinoConverter(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
	public Casino convert(String source) {
		Casino result = null;
		
		if (StringUtils.isNotBlank(source)) {
			result = administrationService.findCasino(source);
		}
		
		return result;
	}

}