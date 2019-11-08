package com.actiontech.dble.manager.dump;

import com.actiontech.dble.backend.mysql.store.fs.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

/**
 * @author Baofengqi
 */
public final class DumpFileReader {

    public static final Logger LOGGER = LoggerFactory.getLogger(DumpFileReader.class);
    public static final String EOF = "dump file eof";
    private String tempStr;
    private BlockingQueue<String> readQueue;
    private FileChannel fileChannel;

    public void open(String fileName) throws IOException {
        this.fileChannel = FileUtils.open(fileName, "r");
    }

    public void start(BlockingQueue<String> queue) throws IOException {
        try {
            this.readQueue = queue;
            ByteBuffer buffer = ByteBuffer.allocate(0x20000);
            int byteRead = fileChannel.read(buffer);
            while (byteRead != -1) {
                readSQLByEOF(buffer.array(), byteRead);
                buffer.clear();
                byteRead = fileChannel.read(buffer);
            }
            if (tempStr != null) {
                this.readQueue.put(tempStr);
                this.tempStr = null;
            }
        } catch (IOException e) {
            throw e;
        } catch (InterruptedException e) {
            // ignore
            LOGGER.warn(e.getMessage());
        } finally {
            try {
                this.readQueue.put(EOF);
                if (fileChannel != null) {
                    fileChannel.close();
                }
            } catch (IOException | InterruptedException e) {
                // ignore
                LOGGER.warn("close dump file error:" + e.getMessage());
            }
        }
    }

    // read one statement by ;
    private void readSQLByEOF(byte[] linesByte, int byteRead) throws InterruptedException {
        String stmts = new String(linesByte, 0, byteRead, StandardCharsets.UTF_8);
        boolean endWithEOF = stmts.endsWith(";");
        String[] lines = stmts.split(";");
        int len = lines.length;

        int i = 0;
        if (tempStr != null) {
            if (len > 1) {
                this.readQueue.put(tempStr + lines[0]);
                this.tempStr = null;
            } else {
                tempStr += lines[0];
            }
            i = 1;
        }
        if (!endWithEOF) {
            this.tempStr = lines[len - 1];
            len = len - 1;
        }
        for (; i < len; i++) {
            this.readQueue.put(lines[i]);
        }
    }

}