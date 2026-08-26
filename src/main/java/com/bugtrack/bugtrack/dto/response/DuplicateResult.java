package com.bugtrack.bugtrack.dto.response;

public class DuplicateResult{
    private BugResponse bugResponse;
    private Double similarityPercent;

    public DuplicateResult(BugResponse bugResponse, Double similarityPercent){
        this.bugResponse= bugResponse;
        this.similarityPercent=similarityPercent;
    }

    public BugResponse getBug(){
        return bugResponse;
    }
    public Double getSimilarityPercent(){
        return similarityPercent;
    }
}
