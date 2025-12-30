package lk.ijse.etecmanagementsystem.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupUtil {

    public static boolean backupDatabase(String dbName, String dbUser, String dbPass, String path) {
        // 1. Generate a unique filename with a timestamp (e.g., backup_2025-12-31_14-30.sql)
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
        String fileName = "backup_" + timeStamp + ".sql";
        String savePath = path + File.separator + fileName;

// --- FIX STARTS HERE ---
        // 1. Define the full path to mysqldump.exe
        // IMPORTANT: Verify this path matches YOUR computer's installation!
        String mysqldumpPath = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";

        // Check if the file actually exists to avoid the crash
        if (!new File(mysqldumpPath).exists()) {
            System.err.println("CRITICAL ERROR: mysqldump.exe not found at " + mysqldumpPath);
            System.err.println("Please update the path in BackupUtil.java");
            return false;
        }
        // --- FIX ENDS HERE ---
        // 3. Construct the command
        // mysqldump -u [user] -p[password] --add-drop-database -B [dbName] -r [savePath]
        String[] command = new String[]{
                mysqldumpPath,
                "-u" + dbUser,
                "-p" + dbPass,
                "--add-drop-database",
                "-B",
                dbName,
                "-r",
                savePath
        };

        try {
            // 4. Execute the command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Wait for the process to finish
            int processComplete = process.waitFor();

            return processComplete == 0; // Returns true if successful

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}