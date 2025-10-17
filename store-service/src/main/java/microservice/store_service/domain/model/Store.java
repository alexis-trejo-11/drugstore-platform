package microservice.store_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class Store {
    private StoreID id;
    private String code;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phone;
    private String email;
    private StoreStatus status;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void activate() {
        this.status = StoreStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = StoreStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInformation(String name, String address, String city, String state,
                                  String zipCode, String phone, String email) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.phone = phone;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSchedule(LocalDateTime openingTime, LocalDateTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.updatedAt = LocalDateTime.now();
    }
}
