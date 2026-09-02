package com.bugtrack.bugtrack.service;


import com.bugtrack.bugtrack.entity.ProjectAssignment;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.BugRepository;
import com.bugtrack.bugtrack.repository.ProjectAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoAssignmentService {
    private final ProjectAssignmentRepository projectAssignmentRepository;
    private final BugRepository bugRepository;

    public User findLeastLoadedDeveloper(Integer projectId){
        List<ProjectAssignment> developers= projectAssignmentRepository.findByProject_ProjectIdAndRoleInProject(projectId,ProjectAssignment.ProjectRole.Developer);
        if(developers.isEmpty()){
            throw new RuntimeException("No developers assigned to project: "+ projectId);
        }

        List<Object[]> workloads= bugRepository.findDeveloperWorkloadByProject(projectId);

        User leastLoadedDeveloper= null;
        long minBugcount= Long.MAX_VALUE;
        for(ProjectAssignment assignment: developers){
            User developer= assignment.getUser();
            long bugCount= 0;

            for(Object[] workload: workloads){
                Integer devId= (Integer) workload[0];
                Long count= (Long) workload[1];
                if(devId.equals(developer.getUserId())){
                    bugCount= count;
                    break;
                }
            }
            if(bugCount<minBugcount){
                minBugcount= bugCount;
                leastLoadedDeveloper= developer;
            }
        }
        System.out.println("Auto-assigned to : "+ leastLoadedDeveloper.getFullName()+" (current bugs: "+ minBugcount+" )");
        return leastLoadedDeveloper;
    }
}
