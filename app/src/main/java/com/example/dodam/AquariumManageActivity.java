// 수조 등록, 수정, 삭제 기능을 제공하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dodam.data.aquarium.UserAquariumRepository;
import com.example.dodam.domain.model.Aquarium;
import com.example.dodam.domain.usecase.AquariumInputValidator;

import java.util.List;

public class AquariumManageActivity extends AppCompatActivity {
    private UserAquariumRepository repository;
    private EditText aquariumName;
    private EditText deviceId;
    private TextView aquariumList;
    private String editingAquariumId = "";
    private final AquariumInputValidator validator = new AquariumInputValidator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 수조 관리 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_aquarium_manage);

        repository = new UserAquariumRepository(this);
        aquariumName = findViewById(R.id.et_aquarium_name);
        deviceId = findViewById(R.id.et_device_id);
        aquariumList = findViewById(R.id.tv_aquarium_list);
        Button save = findViewById(R.id.btn_save_aquarium);
        Button delete = findViewById(R.id.btn_delete_aquarium);
        Button back = findViewById(R.id.btn_back);

        save.setOnClickListener(v -> saveAquarium());
        delete.setOnClickListener(v -> deleteSelected());
        back.setOnClickListener(v -> finish());

        render();
    }

    private void saveAquarium() {
        String name = aquariumName.getText().toString().trim();
        String device = deviceId.getText().toString().trim();
        if (!validator.isValidAquariumName(name)) {
            Toast.makeText(this, "수조 이름은 1~30자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validator.isValidDeviceId(device)) {
            Toast.makeText(this, "장치 ID는 영문, 숫자, -, _ 조합 3~40자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Aquarium saved;
        try {
            saved = repository.saveAquarium(editingAquariumId, name, device);
        } catch (IllegalArgumentException error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        editingAquariumId = saved.getAquariumId();
        Toast.makeText(this, "수조가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        render();
    }

    private void deleteSelected() {
        if (editingAquariumId.trim().isEmpty()) {
            Toast.makeText(this, "삭제할 수조가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        repository.deleteAquarium(editingAquariumId);
        editingAquariumId = "";
        aquariumName.setText("");
        deviceId.setText("");
        Toast.makeText(this, "수조가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        render();
    }

    private void render() {
        List<Aquarium> aquariums = repository.loadAquariums();
        if (!aquariums.isEmpty() && editingAquariumId.trim().isEmpty()) {
            Aquarium first = aquariums.get(0);
            editingAquariumId = first.getAquariumId();
            aquariumName.setText(first.getName());
            deviceId.setText(first.getDeviceId());
        }
        StringBuilder builder = new StringBuilder();
        for (Aquarium aquarium : aquariums) {
            builder.append(aquarium.getName())
                    .append(" / ")
                    .append(aquarium.getDeviceId())
                    .append('\n');
        }
        aquariumList.setText(builder.length() == 0 ? "등록된 수조가 없습니다." : builder.toString());
    }
}
