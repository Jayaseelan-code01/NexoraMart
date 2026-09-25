package com.jayaseelan.nexoramart.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test void hashIsNotPlaintextAndMatches() {
        String raw = "Secret123";
        String hash = PasswordUtil.hash(raw);
        assertNotEquals(raw, hash);
        assertTrue(PasswordUtil.matches(raw, hash));
        assertFalse(PasswordUtil.matches("Wrong123", hash));
    }
}
