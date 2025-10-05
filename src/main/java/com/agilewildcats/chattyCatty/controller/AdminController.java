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
     * TODO: Revisit this method - it gives too much power to the UI and can be a security risk
     * @param docsDir Path relative to the root for seeding documents
     * @return
     */
    @PostMapping("/seed")
    public String startSeed(@RequestParam(name="docsDir", defaultValue = "docs") String docsDir) {
        return seedService.startSeed(docsDir);
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
