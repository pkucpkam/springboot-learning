package example.userdemo.address.seeder;

import example.userdemo.address.entity.AddressEntity;
import example.userdemo.address.repository.AddressRepository;
import example.userdemo.user.seeder.DataSeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AddressSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final AddressRepository addressRepository;

    public AddressSeeder(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Bắt đầu tạo dữ liệu mẫu...");

        // Tạo address mẫu
        AddressEntity address1 = new AddressEntity();
        address1.setProvince("Hà Nội");
        address1.setDistrict("Ba Đình");
        address1.setCommune("Phúc Xá");
        address1.setDetail("Số 123, ngõ 45");
        addressRepository.save(address1);
        logger.info("Đã tạo address 1");

        AddressEntity address2 = new AddressEntity();
        address2.setProvince("TP. Hồ Chí Minh");
        address2.setDistrict("Quận 1");
        address2.setCommune("Bến Nghé");
        address2.setDetail("Số 456, đường Lê Lợi");
        addressRepository.save(address2);
        logger.info("Đã tạo address 2");
    }
}