// 수조 이벤트와 관리 기록을 조회하는 기록 Repository 파일이다.
package com.example.dodam.data.history;

import com.example.dodam.data.aquarium.AquariumRepository;
import com.example.dodam.domain.model.AquariumEvent;
import com.example.dodam.domain.model.MaintenanceRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AquariumHistoryRepository {
    private final List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

    public List<AquariumEvent> loadRecentEvents() {
        LocalEventStore store = LocalEventStore.getInstanceOrNull();
        if (store != null) {
            return store.load();
        }
        return AquariumRepository.getInstance().getEvents();
    }

    public void addMaintenanceRecord(String aquariumId, String category, String memo) {
        MaintenanceRecord record = new MaintenanceRecord(UUID.randomUUID().toString(), aquariumId,
                System.currentTimeMillis(), category, memo);
        maintenanceRecords.add(0, record);
        LocalMaintenanceStore store = LocalMaintenanceStore.getInstanceOrNull();
        if (store != null) {
            store.append(record);
        }
    }

    public List<MaintenanceRecord> loadMaintenanceRecords() {
        LocalMaintenanceStore store = LocalMaintenanceStore.getInstanceOrNull();
        if (store != null) {
            return store.load();
        }
        return new ArrayList<>(maintenanceRecords);
    }
}
