package com.agilewildcats.chattyCatty.controller;

import com.agilewildcats.chattyCatty.service.SeedService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final SeedService seedService;

    public AdminController(SeedService seedService) {
        this.seedService = seedService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/seed")
    public String startSeed(@RequestParam(defaultValue = "docs") String docsDir) {
        return seedService.startSeed(docsDir);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/seed/status")
    public SeedService.JobStatus getSeedStatus() {
        return seedService.getStatus();
    }
}
