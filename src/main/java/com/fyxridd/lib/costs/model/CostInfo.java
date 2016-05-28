package com.fyxridd.lib.costs.model;

import com.fyxridd.lib.costs.api.model.Coster;

import java.util.List;

public class CostInfo {
    private List<Coster> costers;

    public CostInfo(List<Coster> costers) {
        this.costers = costers;
    }

    public List<Coster> getCosters() {
        return costers;
    }
}
