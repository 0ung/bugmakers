package com.bugmaker.apt;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class AptApplicationTests {

	@Test
	void run() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            AptApplication.main(new String[0]);

            mocked.verify(() -> SpringApplication.run(AptApplication.class, new String[0]));
        }
	}

}
