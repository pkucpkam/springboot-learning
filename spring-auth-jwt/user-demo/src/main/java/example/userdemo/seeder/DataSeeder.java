package example.userdemo.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    @Autowired
    private RoleSeeder roleSeeder;
    @Autowired
    private UserSeeder userSeeder;

    @Override
    public void run(String... args) throws Exception {
        userSeeder.run(args);
        roleSeeder.run(args);
    }
}
