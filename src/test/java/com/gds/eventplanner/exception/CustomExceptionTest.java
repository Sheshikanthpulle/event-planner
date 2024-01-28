package com.gds.eventplanner.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class CustomExceptionTest {

	@Test
	void customExceptionTestCases() throws Exception {
		CustomException customException = new CustomException("No Records", "Data not found", HttpStatus.NOT_FOUND);
		assertThat(customException.getMessage()).isEqualTo("No Records");
		assertThat(customException.getDetails()) .isEqualTo("Data not found");
		assertThat(customException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
