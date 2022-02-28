package com.sameer.scheduler.service;

import com.sameer.scheduler.controller.AppController;
import com.sameer.scheduler.model.TimerInfo;
import com.sameer.scheduler.model.User;
import com.sameer.scheduler.model.VaccineRequest;
import com.sameer.scheduler.storage.controller.StorageController;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class ScheduleService {

    @Autowired
    AppController appController;

    @Autowired
    StorageController storageController;

    public final String INPUT_FILE_PATH;
    public final String PROTECTED_FILE_PATH;
    public final String PROTECTED_FILE_PASSWORD;

    public ScheduleService(@Value("${file.password}")String protectedFilePassword,
                           @Value("${file.path}")String inputFilePath) {
        PROTECTED_FILE_PASSWORD = protectedFilePassword;
        INPUT_FILE_PATH=inputFilePath;
        PROTECTED_FILE_PATH=getProtectedFilePath();
    }

    public void schedule()  {

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
        Map<User, String> cronUserDetailsMap = null;
        try {
            cronUserDetailsMap = readFile(new File(INPUT_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        cronUserDetailsMap.forEach((item, cron) -> {
            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression(cron).initalOffset(0)
                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
                    .vaccineRequest(VaccineRequest.builder().age(item.getAge()).pincode(item.getPincode())
                            .userEmail(item.getUserEmail())
                            .userPhoneNumber("+91" + item.getUserPhoneNumber().substring(1))
                            .userName(item.getUserName())
                            .build()).build();
            appController.runGenericJob(timerInfo);
        });

    }

    public Map<User, String> readFile(File file) throws IOException {

            passwordProtectFile(file);
        storageController.uploadRegularFile(new File(PROTECTED_FILE_PATH));
            deleteOriginalFile();

        File encryptedFile = new File(PROTECTED_FILE_PATH);
        Map<User, String> map = new HashMap<>();
        String cronExpression = null;
        Workbook wb = WorkbookFactory.create(new FileInputStream(encryptedFile), PROTECTED_FILE_PASSWORD);
        Sheet sheet = wb.getSheetAt(0);
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
            map.put(user, cronExpression);
        }
        if(!new File(PROTECTED_FILE_PATH).delete()) {
            log.error("Unable to delete protected file");
        }
        return map;
    }

    private void deleteOriginalFile() {
        if(!new File(INPUT_FILE_PATH).delete()) {
            log.error("Unable to delete original file");
        }
    }

    public void passwordProtectFile(File file) throws IOException {

        File xlsxFile = new File(PROTECTED_FILE_PATH);
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));

        try (FileOutputStream fos = new FileOutputStream(xlsxFile)) {
            workbook.write(fos);
            fos.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (POIFSFileSystem fs = new POIFSFileSystem()) {

            EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
            Encryptor encryptor = info.getEncryptor();
            encryptor.confirmPassword(PROTECTED_FILE_PASSWORD);

            try (OPCPackage opc = OPCPackage.open(xlsxFile, PackageAccess.READ_WRITE);
                 OutputStream os = encryptor.getDataStream(fs)) {
                opc.save(os);
            }

            try (FileOutputStream fos = new FileOutputStream(xlsxFile)) {
                fs.writeFilesystem(fos);
            }
            System.out.println("Protected Excel(.xlsx) file has been created successfully.");

        } catch (InvalidFormatException | IOException |
                GeneralSecurityException e) {
            System.out.println("Exception while writing protected xlsx file");
            e.printStackTrace();
        }
    }

    private String getProtectedFilePath() {
        String[] split = INPUT_FILE_PATH.split("/");

        StringBuilder stringBuilder=new StringBuilder();

        for(int i=0;i<split.length-1;i++) {
            stringBuilder.append(split[i]);
            stringBuilder.append("/");
        }
        stringBuilder.append("protected_");
        stringBuilder.append(split[split.length-1]);
        return stringBuilder.toString();
    }
}