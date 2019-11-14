package com.novafutur.api.service;

import org.springframework.stereotype.Service;

/**
 * A service for returning the value after Collatz computation is applied.
 *
 * @author Chinedu Ekwunife
 */
@Service
public class CollatzService {

    /*
     *  Start with any positive integer n. Then each term is obtained from the previous term as follows: if the previous term is even,
     *  the next term is one half the previous term. If the previous term is odd, the next term is 3 times the previous term plus 1.
     *  The conjecture is that no matter what value of n, the sequence will always reach 1
     * */
    public int computeCollatz(int number) {
        return compute(number, 0);
    }

    private int compute(int number, int iterations) {
        return (number == 1 || number < 1)
                ? iterations
                : (number % 2) == 0 ? compute(number / 2, ++iterations) : compute((3 * number) + 1, ++iterations);
    }
}
