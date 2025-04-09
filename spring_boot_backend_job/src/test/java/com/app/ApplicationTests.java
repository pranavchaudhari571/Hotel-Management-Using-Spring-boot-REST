package com.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void testAddition(){
		int a = 10;
		int b = 20;
		int sum = a + b;
		assertEquals(sum, 30);
	}

}
