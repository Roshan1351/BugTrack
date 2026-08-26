package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.response.BugResponse;
import com.bugtrack.bugtrack.dto.response.DuplicateResult;
import com.bugtrack.bugtrack.entity.Bug;
import com.bugtrack.bugtrack.repository.BugRepository;
import com.bugtrack.bugtrack.util.StringSimilarityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DuplicateDetectionService {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private StringSimilarityUtil stringSimilarityUtil;

    @Autowired
    private BugService bugService;

    public static final double ThresholdValue= 70.0;
    public List<DuplicateResult> findSimilarBugs(Integer projectid, String newTitle){
        List<Bug> openBugs= bugRepository.findOpenBugsByProject(projectid);
        List<DuplicateResult> similarBugs= new ArrayList<>();
        for(Bug existingBug: openBugs){
            double similarity= stringSimilarityUtil.similarpercentage(newTitle, existingBug.getTitle());
            if(similarity>=ThresholdValue){
                similarBugs.add(new DuplicateResult(bugService.mapToResponse(existingBug), Math.round(similarity*10.0)/10.0)); //multiply and divide by 10 for round of 1 decimal value;
            }
        }
        similarBugs.sort((a, b)->Double.compare(b.getSimilarityPercent(), a.getSimilarityPercent()));
        return similarBugs;
    }
}

