package com.cursojhipster.puebajhipster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.cursojhipster.puebajhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DirectorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Director.class);
        Director director1 = new Director();
        director1.setId(1L);
        Director director2 = new Director();
        director2.setId(director1.getId());
        assertThat(director1).isEqualTo(director2);
        director2.setId(2L);
        assertThat(director1).isNotEqualTo(director2);
        director1.setId(null);
        assertThat(director1).isNotEqualTo(director2);
    }
}
