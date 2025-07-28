package example.userdemo.address.service;

import example.userdemo.address.dto.AddressDTO;
import example.userdemo.address.entity.AddressEntity;
import example.userdemo.address.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressDTO createAddress(AddressDTO addressDTO) {
        logger.info("Đang tạo địa chỉ: {}", addressDTO);

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setProvince(addressDTO.getProvince());
        addressEntity.setDistrict(addressDTO.getDistrict());
        addressEntity.setCommune(addressDTO.getCommune());
        addressEntity.setDetail(addressDTO.getDetail());

        AddressEntity savedAddress = addressRepository.save(addressEntity);
        logger.info("Đã lưu địa chỉ: {}", savedAddress);

        AddressDTO result = new AddressDTO();
        result.setId(savedAddress.getId());
        result.setProvince(savedAddress.getProvince());
        result.setDistrict(savedAddress.getDistrict());
        result.setCommune(savedAddress.getCommune());
        result.setDetail(savedAddress.getDetail());

        return result;
    }

    public List<AddressDTO> getAllAddresses() {
        logger.info("Lấy danh sách tất cả địa chỉ");
        return addressRepository.findAll().stream().map(address -> {
            AddressDTO dto = new AddressDTO();
            dto.setId(address.getId());
            dto.setProvince(address.getProvince());
            dto.setDistrict(address.getDistrict());
            dto.setCommune(address.getCommune());
            dto.setDetail(address.getDetail());
            return dto;
        }).collect(Collectors.toList());
    }

    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        logger.info("Đang cập nhật địa chỉ ID: {}", id);
        AddressEntity addressEntity = addressRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy địa chỉ với ID: {}", id);
                    return new RuntimeException("Address not found");
                });

        addressEntity.setProvince(addressDTO.getProvince());
        addressEntity.setDistrict(addressDTO.getDistrict());
        addressEntity.setCommune(addressDTO.getCommune());
        addressEntity.setDetail(addressDTO.getDetail());

        AddressEntity updatedAddress = addressRepository.save(addressEntity);
        logger.info("Đã cập nhật địa chỉ: {}", updatedAddress);

        AddressDTO result = new AddressDTO();
        result.setId(updatedAddress.getId());
        result.setProvince(updatedAddress.getProvince());
        result.setDistrict(updatedAddress.getDistrict());
        result.setCommune(updatedAddress.getCommune());
        result.setDetail(updatedAddress.getDetail());

        return result;
    }

    public void deleteAddress(Long id) {
        logger.info("Đang xóa địa chỉ ID: {}", id);
        if (!addressRepository.existsById(id)) {
            logger.error("Không tìm thấy địa chỉ với ID: {}", id);
            throw new RuntimeException("Address not found");
        }
        addressRepository.deleteById(id);
        logger.info("Đã xóa địa chỉ ID: {}", id);
    }
}