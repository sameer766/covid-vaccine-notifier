package com.sameer.scheduler.utils;

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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class FileUtils {

    public static void passwordProtectExcelFile(File file, String protectedFilePassword) throws IOException {

        File xlsxFile = new File(protectedFilePassword);
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
}
