/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.player;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Player;
import com.online.casino.domain.enums.CmsType;
import com.online.casino.domain.enums.ContentType;
import com.online.casino.exception.CmsException;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.CmsService;
import com.online.casino.utils.ImageResizer;
import com.online.casino.web.WebConstants;
import com.online.casino.web.utils.SimpleError;
import com.online.casino.web.validator.ImageFileUploadValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 1/2/11
 * Time: 1:08 AM
 * Responsibility:
 */
@Controller
public class AvatarController {

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;


    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param administrationService administrationService
     */
    @Autowired
    public AvatarController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param playerId playerId
     * @param file file
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/persona/{playerId}/avatar", method = RequestMethod.POST)
    public String uploadAvatar(@PathVariable("playerId") String playerId, @RequestParam("file") MultipartFile file,
                               Model map)
            throws Exception {
        String view   = "player.persona.show";
        Player player = administrationService.findPlayer(playerId);

        if (!file.isEmpty()) {
            BeanPropertyBindingResult be = new BeanPropertyBindingResult(file, "file");

            new ImageFileUploadValidator().validate(file, be);

            if (!be.hasErrors()) {
                try {
                    administrationService.uploadAvatarImage(playerId, file.getOriginalFilename(),
                            file.getInputStream(), file.getSize());

                    view = "redirect:/app/player/persona/" + playerId;
                } catch (CmsException ex) {
                    List<SimpleError> errors = new ArrayList<SimpleError>();

                    errors.add(new SimpleError(ex.getMessage(), ex.getParams()));
                    map.addAttribute(WebConstants.ERRORS, errors);
                }
            } else {
                List<SimpleError> errors = new ArrayList<SimpleError>();

                for (FieldError error : be.getFieldErrors()) {
                    errors.add(new SimpleError(error.getCode(), (String[]) error.getArguments()));
                }

                map.addAttribute(WebConstants.ERRORS, errors);
            }
        } else {
            List<SimpleError> errors = new ArrayList<SimpleError>();

            errors.add(new SimpleError("error.cms.unavailable"));
            map.addAttribute(WebConstants.ERRORS, errors);
        }

        map.addAttribute("player", player);

        return view;
    }
}
