package com.sameer.coviddatafetcher.service;

import com.sameer.coviddatafetcher.model.Content;
import com.sameer.coviddatafetcher.model.VaccineRequest;
import com.sameer.coviddatafetcher.model.VaccineResponse;
import com.sameer.coviddatafetcher.repo.ContentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    private static final String TEMPLATE_NAME = "MAIN";
    private static final Integer FIRST_CHAR_INDEX = 0;
    private static final String SPLITTER = " ";
    private String content= "Vaccine is available on date %s  with slots %s of vaccine %s at %s";

    @Autowired
    ContentRepo contentRepo;

    public String getMessage(VaccineRequest vaccineRequest, VaccineResponse vaccineResponse) {
        List<String> slots = vaccineResponse.getSlots();
        Optional<Content> contentFromDb = getContentFromDb();

        contentFromDb.ifPresent(value -> content = value.getContent());
        return String.format(
                "Hi, "
                        + getUserNameCamelCase(vaccineRequest.getUserName())
                        + content,
                vaccineResponse.getDate(),
                slots.toString(),
                vaccineResponse.getVaccine(),
                vaccineRequest.getPincode());
    }

    public String getUserNameCamelCase(String userName) {
        String[] userString = userName.split(SPLITTER);
        StringBuilder stringBuilder = new StringBuilder();
        for (String user : userString) {
            stringBuilder.append(Character.toUpperCase(user.charAt(FIRST_CHAR_INDEX)));
            stringBuilder.append(user.substring(FIRST_CHAR_INDEX + 1));
            stringBuilder.append(" ");
        }
        //ignore last space
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public Optional<Content> getContentFromDb() {
        return contentRepo.findByTemplate(TEMPLATE_NAME);
    }
}
