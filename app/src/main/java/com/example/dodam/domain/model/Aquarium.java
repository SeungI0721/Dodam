// 사용자 수조와 실제 장치 ID 연결 정보를 표현하는 모델 파일이다.
package com.example.dodam.domain.model;

public class Aquarium {
    private final String aquariumId;
    private final String ownerUid;
    private final String name;
    private final String deviceId;

    public Aquarium(String aquariumId, String ownerUid, String name, String deviceId) {
        this.aquariumId = aquariumId;
        this.ownerUid = ownerUid;
        this.name = name;
        this.deviceId = deviceId;
    }

    public String getAquariumId() {
        return aquariumId;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public String getName() {
        return name;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
