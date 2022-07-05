package com.sameer.scheduler.utils;

import com.sameer.scheduler.model.User;
import com.sameer.scheduler.storage.controller.StorageController;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@Slf4j
public class FileUtils {

    @Autowired
    StorageController storageController;

    public final String INPUT_FILE_PATH;
    public final String PROTECTED_FILE_PATH;
    public final String PROTECTED_FILE_PASSWORD;

    public FileUtils(@Value("${file.password}") String protectedFilePassword,
                     @Value("${file.path}") String inputFilePath) {
        PROTECTED_FILE_PASSWORD = protectedFilePassword;
        INPUT_FILE_PATH = inputFilePath;
        PROTECTED_FILE_PATH = FileUtils.getProtectedFilePath(INPUT_FILE_PATH);
    }

    public static void passwordProtectExcelFile(File file, String protectedFilePath, String protectedFilePassword) throws IOException {

        File xlsxFile = new File(protectedFilePath);
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
            encryptor.confirmPassword(protectedFilePassword);

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



    public static void passwordProtectPdfFile(File file, String protectedFilePassword) throws IOException {

        PDDocument pdd = PDDocument.load(file);

        // step 2.Creating instance of AccessPermission
        // class
        AccessPermission ap = new AccessPermission();

        // step 3. Creating instance of
        // StandardProtectionPolicy
        StandardProtectionPolicy stpp
                = new StandardProtectionPolicy(protectedFilePassword, protectedFilePassword, ap);

        // step 4. Setting the length of Encryption key
        stpp.setEncryptionKeyLength(128);

        // step 5. Setting the permission
        stpp.setPermissions(ap);

        // step 6. Protecting the PDF file
        pdd.protect(stpp);

        // step 7. Saving and closing the the PDF Document
        pdd.save(file.getAbsolutePath());
        pdd.close();

        System.out.println("PDF Encrypted successfully...");
    }

    public static String getProtectedFilePath(String inputFilePath) {
        String[] split = inputFilePath.split("/");

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < split.length - 1; i++) {
            stringBuilder.append(split[i]);
            stringBuilder.append("/");
        }
        stringBuilder.append("protected_");
        stringBuilder.append(split[split.length - 1]);
        return stringBuilder.toString();
    }

    public Map<User, String> readFile() throws IOException {

        FileUtils.passwordProtectExcelFile(new File(INPUT_FILE_PATH), PROTECTED_FILE_PATH, PROTECTED_FILE_PASSWORD);
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
                    user.setAge(Integer.parseInt(cell.getStringCellValue()));
                }
                if (cell.getColumnIndex() == 5) {
                    cronExpression = cell.getStringCellValue();
                }
            }
            map.put(user, cronExpression);
        }
        if (!new File(PROTECTED_FILE_PATH).delete()) {
            log.error("Unable to delete protected file");
        }
        return map;
    }

    private void deleteOriginalFile() {
        if (!new File(INPUT_FILE_PATH).delete()) {
            log.error("Unable to delete original file");
        }
    }

}
