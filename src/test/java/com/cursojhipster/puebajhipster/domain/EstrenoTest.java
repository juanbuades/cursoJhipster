package com.cursojhipster.puebajhipster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.cursojhipster.puebajhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EstrenoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Estreno.class);
        Estreno estreno1 = new Estreno();
        estreno1.setId(1L);
        Estreno estreno2 = new Estreno();
        estreno2.setId(estreno1.getId());
        assertThat(estreno1).isEqualTo(estreno2);
        estreno2.setId(2L);
        assertThat(estreno1).isNotEqualTo(estreno2);
        estreno1.setId(null);
        assertThat(estreno1).isNotEqualTo(estreno2);
    }
}
