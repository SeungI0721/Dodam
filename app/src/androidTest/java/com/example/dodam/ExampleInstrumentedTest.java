// Android 기기에서 앱 패키지 컨텍스트를 확인하는 계측 테스트 파일이다.
package com.example.dodam;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Android 기기에서 실행되는 기본 계측 테스트이다.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // 테스트 대상 앱의 컨텍스트를 가져온다.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.dodam", appContext.getPackageName());
    }
}
