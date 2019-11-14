package com.novafutur.api.service;

import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service for returning the Dot Product using the zip, map and reduce operations.
 *
 * @author Chinedu Ekwunife
 */
@Service
public class DotProductService {

    /**
     * In computer graphics an operation called the ​dotproduct i​s used to manipulate vectors. The dot product of
     * (a1, a2,···, an) and (b1, b2,···, bn) is a1b1+a2b2+···+anbn; using the zip, map and reduce operations, write a function ​dotProduct ​that computes the
     * dot product of two vectors in any language that implements zip, map and reduce functions on your choice.
     */

    public List<Integer> dotProduct(Integer[] firstList, Integer[] secondList) {
        /*
           Seq.of(1, 2, 3).zip(Seq.of("a", "b", "c")) ==> (tuple(1, "a"), tuple(2, "b"), tuple(3, "c"));
        */
        return Seq.of(firstList)
                .zip(Seq.of(secondList))
                .map(tuple -> tuple.v1 * tuple.v2)
                .toList();
    }
}
