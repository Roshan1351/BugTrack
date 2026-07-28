package com.bugtrack.bugtrack.security;

import com.bugtrack.bugtrack.entity.BugStatus;
import com.bugtrack.bugtrack.entity.Priority;
import com.bugtrack.bugtrack.entity.Role;
import com.bugtrack.bugtrack.entity.Severity;
import com.bugtrack.bugtrack.repository.BugStatusRepository;
import com.bugtrack.bugtrack.repository.PriorityRepository;
import com.bugtrack.bugtrack.repository.RoleRepository;
import com.bugtrack.bugtrack.repository.SeverityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataIntializer implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BugStatusRepository bugStatusRepository;
    @Autowired
    PriorityRepository priorityRepository;
    @Autowired
    SeverityRepository severityRepository;

    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.count()==0) {
            roleRepository.saveAll(List.of(new Role(null, "Admin"),
                    new Role(null, "Project Manager"),
                    new Role(null, "Developer"),
                    new Role(null, "Tester")
            ));
            System.out.println("Role inserted");
        }
        if(bugStatusRepository.count()==0){
            bugStatusRepository.saveAll(List.of(
                    new BugStatus(null, "Open"),
                    new BugStatus(null, "Assigned"),
                    new BugStatus(null, "In Progress"),
                    new BugStatus(null, "Resolved"),
                    new BugStatus(null, "Re-Testing"),
                    new BugStatus(null, "Closed"),
                    new BugStatus(null, "Reopened"),
                    new BugStatus(null, "Rejected")
            ));
            System.out.println("Bug Statuses inserted");
        }

        if(priorityRepository.count()==0){
            priorityRepository.saveAll(List.of(
                    new Priority(null, "Low"),
                    new Priority(null, "Medium"),
                    new Priority(null, "High"),
                    new Priority(null, "Critical")
            ));
            System.out.println("Priorities inserted");
        }
        if (severityRepository.count() == 0) {
            severityRepository.saveAll(List.of(
                    new Severity(null, "Cosmetic"),
                    new Severity(null, "Minor"),
                    new Severity(null, "Major"),
                    new Severity(null, "Blocker")
            ));
            System.out.println("✅ Severities inserted");
        }
    }
}
