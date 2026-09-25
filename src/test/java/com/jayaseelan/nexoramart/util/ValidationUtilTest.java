package com.jayaseelan.nexoramart.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {
    @Test void validatesEmail() {
        assertTrue(ValidationUtil.email("student@example.com"));
        assertFalse(ValidationUtil.email("bad-email"));
    }
    @Test void validatesNameAndPassword() {
        assertTrue(ValidationUtil.name("Jaya"));
        assertFalse(ValidationUtil.name("A"));
        assertTrue(ValidationUtil.password("123456"));
        assertFalse(ValidationUtil.password("123"));
    }
}
