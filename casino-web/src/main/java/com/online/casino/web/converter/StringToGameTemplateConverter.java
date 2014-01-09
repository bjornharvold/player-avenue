package com.online.casino.web.converter;

import com.online.casino.domain.entity.GameTemplate;
import com.online.casino.service.AdministrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class StringToGameTemplateConverter implements Converter<String, GameTemplate> {
    private final AdministrationService administrationService;

    public StringToGameTemplateConverter(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
	public GameTemplate convert(String source) {
		GameTemplate result = null;

		if (StringUtils.isNotBlank(source)) {
			result = administrationService.findGameTemplate(source);
		}

		return result;
	}

}