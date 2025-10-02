package com.example.serverapi.seeder;

import com.example.serverapi.seeder.base.BaseSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeederOrchestrator implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Starting Data Seeding Process ===");
        
        try {
            // Get all seeder beans
            List<BaseSeeder> seeders = new ArrayList<>(applicationContext.getBeansOfType(BaseSeeder.class).values());
            
            // Sort seeders by order annotation
            seeders.sort(AnnotationAwareOrderComparator.INSTANCE);
            
            log.info("Found {} seeders to execute", seeders.size());
            
            // Execute seeders sequentially to respect dependencies
            for (BaseSeeder seeder : seeders) {
                if (seeder.isEnabled()) {
                    log.info("Executing seeder: {}", seeder.getSeederName());
                    seeder.seed().join(); // Wait for completion before starting next seeder
                } else {
                    log.info("Skipping disabled seeder: {}", seeder.getSeederName());
                }
            }
            
            log.info("=== Data Seeding Process Completed Successfully ===");
            
        } catch (Exception e) {
            log.error("=== Data Seeding Process Failed ===", e);
            throw new RuntimeException("Data seeding failed", e);
        }
    }
}
