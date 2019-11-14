package com.novafutur.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class DotProductServiceTest {

    private DotProductService subject;

    @Before
    public void setup() {
        subject = new DotProductService();
    }

    @Test
    public void dotProduct() {
        assertEquals(subject.dotProduct(new Integer[]{1, 2}, new Integer[]{3, 4}), asList(3, 8));
        assertEquals(subject.dotProduct(new Integer[]{1, 2}, new Integer[]{3, 4, 5}), asList(3, 8));
        assertEquals(subject.dotProduct(new Integer[]{7, 8}, new Integer[]{9, 10}), asList(63, 80));
    }
}
