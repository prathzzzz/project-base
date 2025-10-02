package com.example.serverapi.seeder.sample;

import com.example.serverapi.seeder.base.AbstractSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(100) // Low priority - runs after auth seeders
public class SampleDataSeeder extends AbstractSeeder {

    @Value("${app.seeder.sample-data.enabled:false}")
    private boolean sampleDataEnabled;

    @Override
    public String getSeederName() {
        return "Sample Data";
    }

    @Override
    public boolean isEnabled() {
        return sampleDataEnabled;
    }

    @Override
    protected boolean shouldSkipSeeding() {
        // Add your logic to check if sample data already exists
        // For example: return productRepository.count() > 0;
        return false;
    }

    @Override
    protected void performSeeding() {
        log.info("Creating sample data...");
        
        // Add your sample data creation logic here
        // For example:
        // createSampleCategories();
        // createSampleProducts();
        // createSampleOrders();
        
        log.info("Sample data creation completed");
    }

    // Example methods that you can implement based on your domain
    
    /*
    private void createSampleCategories() {
        // Create sample categories
    }
    
    private void createSampleProducts() {
        // Create sample products
    }
    
    private void createSampleOrders() {
        // Create sample orders
    }
    */
}
