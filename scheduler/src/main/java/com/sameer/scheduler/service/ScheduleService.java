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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class ScheduleService {

    public static int count = 0;

    @Autowired
    AppController appController;



    public List<User> schedule(MultipartFile file) throws IOException {
        List<User> users = readFile(file);
        AtomicInteger job = new AtomicInteger(count);
        //exception while executing for runJob
//        users.forEach(item->
//        {
//            count++;
//            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression("0/10 * * * * ? *").initalOffset(0)
//                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
//                    .vaccineRequest(VaccineRequest.builder().age(item.getAge()).pincode(item.getPincode())
//                            .userEmail(item.getUserEmail())
//                            .userPhoneNumber(item.getUserPhoneNumber())
//                            .userName(item.getUserName())
//                            .build()).build();
//            appController.runJob3(timerInfo,"job"+ job.getAndIncrement());
//        });

        IntStream.range(0, 10).forEach(value -> {
            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression("0/30 0/1 * 1/1 * ? *").initalOffset(0)
                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
                    .vaccineRequest(VaccineRequest.builder().age(value + 22).pincode("522019")
                            .userEmail("royrahul7666@gmail.com")
                            .userPhoneNumber("+919479895240")
                            .userName("name" + value)
                            .build()).build();
            appController.runJob3(timerInfo, "job" + job.getAndIncrement());
        });
        return users;
    }

    public List<User> readFile(MultipartFile file) throws IOException {
        List<User> tableModels = new LinkedList<>();
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (row.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = row.cellIterator();
            User tableModel = new User();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (cell.getColumnIndex() == 0) {
                    tableModel.setPincode(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 1) {
                    tableModel.setUserName(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 2) {
                    tableModel.setUserPhoneNumber(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 3) {
                    tableModel.setUserEmail(cell.getStringCellValue());
                }
                if (cell.getColumnIndex() == 4) {
                    tableModel.setAge((int) cell.getNumericCellValue());
                }

            }
            String fileName = "/Users/sameer/Desktop/" + file.getOriginalFilename().replace("{file}", "covid-writer");
            File newFile = new File(fileName);
            boolean newFileCreated = newFile.createNewFile();
            if (newFileCreated) {
                FileOutputStream fout = new FileOutputStream(fileName);
                fout.write(file.getBytes());
                fout.close();
            }
            tableModels.add(tableModel);
        }
        return tableModels;
    }
}
