package com.varanegar.framework.base.logging;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import timber.log.Timber;

/**
 * Created by atp on 12/20/2016.
 */
public class FilePrinter {
    private static final boolean USE_WORKER = true;
    private final String folderPath;
    private Writer writer;
    private FileNameGenerator fileNameGenerator;
    private volatile Worker worker;
    String lastFileName;

    public FilePrinter(String folderPath, FileNameGenerator fileNameGenerator) {
        this.folderPath = folderPath;
        this.fileNameGenerator = fileNameGenerator;
        writer = new Writer();
        if (USE_WORKER) {
            worker = new Worker();
        }
        checkLogFolder();
        lastFileName = getLastFileName();
    }

    private String getLastFileName() {
        File dir = new File(folderPath);
        String[] files = dir.list();
        if (files == null)
            return null;
        if (files.length == 0)
            return null;
        Arrays.sort(files);
        return files[files.length - 1];
    }

    /**
     * Make sure the folder of log file exists.
     */
    private void checkLogFolder() {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void println(int logLevel, String tag, String msg) {
        if (USE_WORKER) {
            if (!worker.isStarted()) {
                worker.start();
            }
            worker.enqueue(new LogItem(logLevel, tag, msg));
        } else {
            doPrintln(logLevel, tag, msg);
        }
    }

    /**
     * Do the real job of writing log to file.
     */
    void doPrintln(int logLevel, String tag, String msg) {

        String newFileName = fileNameGenerator.generateFileName(System.currentTimeMillis());
        if (lastFileName == null) {
            if (newFileName == null || newFileName.trim().length() == 0) {
                throw new IllegalArgumentException("File name should not be empty.");
            }
            lastFileName = newFileName;
        }
        if (newFileName.equals(lastFileName)) {
            if (!writer.isOpened() || !writer.exists(newFileName)) {
                writer.open(newFileName);
            }
        } else {
            if (writer.isOpened()) {
                writer.close();
            }
            writer.open(newFileName);
            lastFileName = newFileName;
        }
        String flattenedLog = flattenLog(logLevel, tag, msg);
        writer.appendLog(flattenedLog);
    }

    String flattenLog(int logLevel, String tag, String msg) {
        String flattenedLog = "";
        switch (logLevel) {
            case Log.ASSERT:
                flattenedLog += "A/";
                break;
            case Log.DEBUG:
                flattenedLog += "D/";
                break;
            case Log.ERROR:
                flattenedLog += "E/";
                break;
            case Log.INFO:
                flattenedLog += "I/";
                break;
            case Log.VERBOSE:
                flattenedLog += "V/";
                break;
            case Log.WARN:
                flattenedLog += "W/";
                break;
        }
        flattenedLog += tag + "  : " + msg;
        return flattenedLog;
    }

    private class LogItem {

        int level;
        String tag;
        String msg;

        LogItem(int level, String tag, String msg) {
            this.level = level;
            this.tag = tag;
            this.msg = msg;
        }
    }

    /**
     * Work in background, we can enqueue the logs, and the worker will dispatch them.
     */
    private class Worker implements Runnable {

        private BlockingQueue<LogItem> logs = new LinkedBlockingQueue<>();

        private volatile boolean started;

        /**
         * Enqueue the log.
         *
         * @param log the log to be written to file
         */
        void enqueue(LogItem log) {
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Whether the worker is started.
         *
         * @return true if started, false otherwise
         */
        boolean isStarted() {
            synchronized (this) {
                return started;
            }
        }

        /**
         * Start the worker.
         */
        void start() {
            synchronized (this) {
                new Thread(this).start();
                started = true;
            }
        }

        @Override
        public void run() {
            LogItem log;
            try {
                while ((log = logs.take()) != null) {
                    doPrintln(log.level, log.tag, log.msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                synchronized (this) {
                    started = false;
                }
            }
        }
    }

    /**
     * Used to write the flattened logs to the log file.
     */
    private class Writer {

        /**
         * The current log file.
         */
        private File logFile;

        private BufferedWriter bufferedWriter;

        /**
         * Whether the log file is opened.
         *
         * @return true if opened, false otherwise
         */
        boolean isOpened() {
            return bufferedWriter != null;
        }


        /**
         * Get the current log file.
         *
         * @return the current log file, maybe null
         */
        File getFile() {
            return logFile;
        }

        /**
         * Open the file of specific name to be written into.
         *
         * @param newFileName the specific file name
         * @return true if opened successfully, false otherwise
         */
        boolean open(String newFileName) {
            logFile = new File(folderPath, newFileName);

            // Create log file if not exists.
            if (!logFile.exists()) {
                try {
                    File parent = logFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    logFile = null;
                    return false;
                }
            }

            // Create buffered writer.
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            } catch (Exception e) {
                e.printStackTrace();
                logFile = null;
                return false;
            }
            return true;
        }

        boolean exists(String newFileName) {
            File file = new File(folderPath, newFileName);
            return file.exists();
        }
        /**
         * Close the current log file if it is opened.
         *
         * @return true if closed successfully, false otherwise
         */
        boolean close() {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    bufferedWriter = null;
                    logFile = null;
                }
            }
            return true;
        }

        /**
         * Append the flattened log to the end of current opened log file.
         *
         * @param flattenedLog the flattened log
         */
        void appendLog(String flattenedLog) {
            try {
                bufferedWriter.write(flattenedLog);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (Exception e) {

            }
        }
    }
}
