package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.util.PdfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SeedService {

    private final DocumentIngestionService ingestionService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicReference<JobStatus> status = new AtomicReference<>(JobStatus.idle());
    private final static Logger logger = LoggerFactory.getLogger(SeedService.class);

    public SeedService(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    public synchronized String startSeed(String docsDirPath) {
        logger.debug("startSeed : docsDirPath={}", docsDirPath);

        JobStatus current = status.get();
        if (current.isRunning()) {
            return "already_running";
        }
        status.set(JobStatus.running("Starting seed..."));
        executor.submit(() -> {
            logger.debug("startSeed : STARTED");
            try {
                File docsDir = new File(docsDirPath);
                if (!docsDir.exists() || !docsDir.isDirectory()) {
                    status.set(JobStatus.failed("docs directory not found: " + docsDirPath));
                    return;
                }
                File[] files = docsDir.listFiles();
                if (files == null) files = new File[0];
                int total = files.length;
                int processed = 0;
                for (File f : files) {
                    if (!f.isFile()) continue;
                    logger.debug("startSeed : SEEDING STARTED {}", f.getName());

                    status.set(JobStatus.running("Processing " + f.getName() + " (" + (processed+1) + "/" + total + ")"));
                    String content;
                    String name = f.getName();
                    if (name.toLowerCase().endsWith(".pdf")) {
                        content = PdfUtil.extractTextFromPdf(f);
                    } else {
                        content = java.nio.file.Files.readString(f.toPath(), StandardCharsets.UTF_8);
                    }
                    ingestionService.addOrUpdateDocument(content, f.getAbsolutePath(), name);

                    logger.debug("startSeed : SEEDING COMPLETED {}", f.getName());

                    processed++;
                }
                logger.debug("startSeed : COMPLETED");

                status.set(JobStatus.completed("Seed completed: processed " + processed + " files"));
            } catch (Exception ex) {
                status.set(JobStatus.failed("Seed failed: " + ex.getMessage()));
            }
        });
        return "started";
    }

    public synchronized JobStatus getStatus() {
        return status.get();
    }

    // POJO for reporting
    public static class JobStatus {
        private final String state;
        private final String message;
        private final boolean running;

        private JobStatus(String state, String message, boolean running) {
            this.state = state;
            this.message = message;
            this.running = running;
        }
        public static JobStatus idle() { return new JobStatus("idle","idle",false); }
        public static JobStatus running(String msg) { return new JobStatus("running",msg,true); }
        public static JobStatus completed(String msg) { return new JobStatus("completed",msg,false); }
        public static JobStatus failed(String msg) { return new JobStatus("failed",msg,false); }

        public String getState() { return state; }
        public String getMessage() { return message; }
        public boolean isRunning() { return running; }
    }
}
