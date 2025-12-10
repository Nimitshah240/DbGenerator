package com.example.jaipurduss_client.task;

import com.example.jaipurduss_client.CommonUtils.ProcessUtil;
import javafx.concurrent.Task;

import java.io.File;

public class DbBackupTask extends Task<Boolean> {
    private final File file;

    public DbBackupTask(File file) {
        this.file = file;
    }

    @Override
    protected Boolean call() throws Exception {
        try {
            return ProcessUtil.executeScriptFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
