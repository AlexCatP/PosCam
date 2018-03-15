package com.psy.model;

import java.util.List;

/**
 * Created by ppssyyy on 2016-08-05.
 */
public class Param {
    private List<Integer> tag_id;
    private List<Double> weight;

    public List<Integer> getTag_id() {
        return tag_id;
    }

    public void setTag_id(List<Integer> tag_id) {
        this.tag_id = tag_id;
    }

    public List<Double> getWeight() {
        return weight;
    }

    public void setWeight(List<Double> weight) {
        this.weight = weight;
    }
}
