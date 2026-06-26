// 수조 Repository 상태를 대시보드 화면에 전달하는 ViewModel 파일이다.
package com.example.dodam.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dodam.data.aquarium.AquariumRepository;
import com.example.dodam.data.aquarium.AquariumRepositoryState;
import com.example.dodam.domain.model.AquariumStatus;
import com.example.dodam.domain.model.DeviceConnectionState;
import com.example.dodam.domain.model.SensorSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardViewModel extends ViewModel {
    private final AquariumRepository repository = AquariumRepository.getInstance();
    private final MutableLiveData<DashboardUiState> uiState = new MutableLiveData<>();
    private final AquariumRepository.StateListener listener = state -> uiState.postValue(mapState(state));

    public DashboardViewModel() {
        repository.observe(listener);
    }

    public LiveData<DashboardUiState> getUiState() {
        return uiState;
    }

    public void start(String deviceId) {
        repository.startDemoDevice(deviceId);
    }

    public void toggleHeater() {
        repository.toggleHeater();
    }

    public void toggleLight() {
        repository.toggleLight();
    }

    @Override
    protected void onCleared() {
        repository.removeObserver(listener);
        super.onCleared();
    }

    private DashboardUiState mapState(AquariumRepositoryState state) {
        SensorSnapshot snapshot = state.getSnapshot();
        if (snapshot == null) {
            return new DashboardUiState("--", "--", "--", false, false, "데이터 없음",
                    state.getConnectionState(), state.getStatus(), state.getCommandMessage(), state.getErrorMessage());
        }
        String updated = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(new Date(snapshot.getTimestamp()));
        return new DashboardUiState(
                String.format(Locale.KOREA, "%.1f", snapshot.getTemperatureC()),
                String.format(Locale.KOREA, "%.2f", snapshot.getTurbidity()),
                snapshot.getWaterLevelMm() + "mm",
                snapshot.isHeaterOn(),
                snapshot.isLightOn(),
                updated,
                state.getConnectionState() == null ? DeviceConnectionState.OFFLINE : state.getConnectionState(),
                state.getStatus() == null ? AquariumStatus.EMPTY : state.getStatus(),
                state.getCommandMessage(),
                state.getErrorMessage()
        );
    }
}
