package com.novafutur.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class CollatzServiceTest {

    private CollatzService subject;

    @Before
    public void setup() {
        subject = new CollatzService();
    }

    @Test
    public void collatzCompute() {
        assertEquals(subject.computeCollatz(-1), 0);
        assertEquals(subject.computeCollatz(1), 0);
        assertEquals(subject.computeCollatz(10), 6);
        assertEquals(subject.computeCollatz(100), 25);
        assertEquals(subject.computeCollatz(100000), 128);
    }
}
