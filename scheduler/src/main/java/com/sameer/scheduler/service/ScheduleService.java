package com.sameer.scheduler.service;

import com.sameer.scheduler.controller.AppController;
import com.sameer.scheduler.model.User;
import com.sameer.scheduler.model.TimerInfo;
import com.sameer.scheduler.model.VaccineRequest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ScheduleService {

    @Autowired
    AppController appController;

    public void schedule(MultipartFile file) throws IOException {

//        IntStream.range(0,100).forEach((item)->
//        {
//            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression("0/7 * * * * ? *").initalOffset(0)
//                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
//                    .vaccineRequest(VaccineRequest.builder().age(item+25).pincode("522019")
//                            .userEmail("pandesameer76@gmail.com")
//                            .userPhoneNumber("+919479895240")
//                            .userName("Sameer pande")
//                            .build()).build();
//            appController.runGenericJob(timerInfo);
//        });
        Map<User,String> listMap = readFile(file);

        listMap.forEach((item,cron)-> {
            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression(cron).initalOffset(0)
                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
                    .vaccineRequest(VaccineRequest.builder().age(item.getAge()).pincode(item.getPincode())
                            .userEmail(item.getUserEmail())
                            .userPhoneNumber("+91"+item.getUserPhoneNumber().substring(1))
                            .userName(item.getUserName())
                            .build()).build();
            appController.runGenericJob(timerInfo);
        });

    }

    public Map<User, String> readFile(MultipartFile file) throws IOException {

        Map<User,String> map=new HashMap<>();

        String cronExpression = null;

        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (row.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = row.cellIterator();
            User user = new User();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (cell.getColumnIndex() == 0) {
                    user.setPincode(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 1) {
                    user.setUserName(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 2) {
                    user.setUserPhoneNumber(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 3) {
                    user.setUserEmail(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 4) {
                    user.setAge((int) cell.getNumericCellValue());
                }
                if (cell.getColumnIndex() == 5) {
                    cronExpression=cell.getStringCellValue();
                }
            }
            map.put(user,cronExpression);
        }
        String fileName = "/Users/sameer/Desktop/" + file.getOriginalFilename().replace("{file}", "covid-writer");
        File newFile = new File(fileName);
        boolean newFileCreated = newFile.createNewFile();
        if (newFileCreated) {
            FileOutputStream fout = new FileOutputStream(fileName);
            fout.write(file.getBytes());
            fout.close();
        }

        return map;
    }
}
