package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.service.SeedService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final SeedService seedService;

    public AdminController(SeedService seedService) {
        this.seedService = seedService;
    }

    /**
     * Starts the seed service to refresh all seeded docs to support the RAG system
     * @return
     */
    @PostMapping("/seed")
    public String startSeed() {
        return seedService.startSeed();
    }

    /**
     * Gets the current seeding job status
     * @return
     */
    @GetMapping("/seed/status")
    public SeedService.JobStatus getSeedStatus() {
        return seedService.getStatus();
    }
}
