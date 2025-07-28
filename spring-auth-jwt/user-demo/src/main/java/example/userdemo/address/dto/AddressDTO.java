package example.userdemo.address.dto;

public class AddressDTO {
    private Long id;
    private String province;
    private String district;
    private String commune;
    private String detail;

    // Constructors
    public AddressDTO() {
    }

    public AddressDTO(Long id, String province, String district, String commune, String detail) {
        this.id = id;
        this.province = province;
        this.district = district;
        this.commune = commune;
        this.detail = detail;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}