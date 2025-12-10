package com.example.jaipurduss_client.CommonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class ProcessUtil {

    public static boolean executeScriptFile(File file) {
        try {
            String currentDir = System.getProperty("user.dir");
            String mysqlExecutablePath = Paths.get(currentDir, "mysql.exe").toString();
            String mysqlDumpCmd = "\"" + mysqlExecutablePath + "\" --user=root -p" + "EAmcs2021" + " --port=3366 -f --default-character-set=utf8" + " < "
                    + file.getAbsolutePath();
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", mysqlDumpCmd);
            processBuilder.redirectErrorStream(true);
            Process p = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("MySQL Output: " + line);
                }
            }

            int statusCode = p.waitFor();
            System.out.println("Nimit : " + statusCode);
            return statusCode == 0;
        } catch (Exception e) {
            System.out.println("Nimit : " + e);
        }
        return false;
    }
}