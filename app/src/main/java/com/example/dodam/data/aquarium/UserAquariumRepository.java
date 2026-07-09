// 사용자별 수조 등록, 수정, 삭제를 처리하는 Repository 파일이다.
package com.example.dodam.data.aquarium;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dodam.data.firebase.DodamFirebaseDatabase;
import com.example.dodam.domain.model.Aquarium;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserAquariumRepository {
    private static final String FILE_NAME = "dodam_aquariums";
    private final SharedPreferences preferences;

    public UserAquariumRepository(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public List<Aquarium> loadAquariums() {
        String uid = currentUid();
        String raw = preferences.getString(uid + "_items", "");
        List<Aquarium> result = new ArrayList<>();
        if (raw.trim().isEmpty()) {
            return result;
        }
        String[] rows = raw.split("\n");
        for (String row : rows) {
            String[] parts = row.split("\\|", -1);
            if (parts.length == 4) {
                result.add(new Aquarium(parts[0], parts[1], parts[2], parts[3]));
            }
        }
        return result;
    }

    public Aquarium saveAquarium(String existingId, String name, String deviceId) {
        String uid = currentUid();
        String aquariumId = existingId == null || existingId.trim().isEmpty()
                ? "aq-" + UUID.randomUUID().toString().substring(0, 8)
                : existingId;
        Aquarium aquarium = new Aquarium(aquariumId, uid, name.trim(), deviceId.trim());
        List<Aquarium> aquariums = loadAquariums();
        for (Aquarium existing : aquariums) {
            if (!existing.getAquariumId().equals(aquariumId)
                    && existing.getDeviceId().equalsIgnoreCase(deviceId.trim())) {
                throw new IllegalArgumentException("이미 등록된 장치 ID입니다.");
            }
        }
        boolean replaced = false;
        for (int i = 0; i < aquariums.size(); i++) {
            if (aquariums.get(i).getAquariumId().equals(aquariumId)) {
                aquariums.set(i, aquarium);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            aquariums.add(aquarium);
        }
        persist(aquariums);
        syncToFirebase(aquarium);
        return aquarium;
    }

    public void deleteAquarium(String aquariumId) {
        List<Aquarium> aquariums = loadAquariums();
        List<Aquarium> kept = new ArrayList<>();
        for (Aquarium aquarium : aquariums) {
            if (!aquarium.getAquariumId().equals(aquariumId)) {
                kept.add(aquarium);
            }
        }
        persist(kept);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DodamFirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid())
                    .child("aquariums")
                    .child(aquariumId)
                    .removeValue();
        }
    }

    public Aquarium loadSelectedOrDefault() {
        List<Aquarium> aquariums = loadAquariums();
        if (!aquariums.isEmpty()) {
            return aquariums.get(0);
        }
        return saveAquarium("", "도담 수조", "tank-demo-001");
    }

    private void persist(List<Aquarium> aquariums) {
        StringBuilder builder = new StringBuilder();
        for (Aquarium aquarium : aquariums) {
            builder.append(aquarium.getAquariumId()).append('|')
                    .append(aquarium.getOwnerUid()).append('|')
                    .append(aquarium.getName().replace("|", " ")).append('|')
                    .append(aquarium.getDeviceId().replace("|", " "))
                    .append('\n');
        }
        preferences.edit().putString(currentUid() + "_items", builder.toString()).apply();
    }

    private void syncToFirebase(Aquarium aquarium) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        DodamFirebaseDatabase.getInstance().getReference("Users")
                .child(user.getUid())
                .child("aquariums")
                .child(aquarium.getAquariumId())
                .setValue(aquarium);
    }

    private String currentUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user == null ? "demo-user" : user.getUid();
    }
}
