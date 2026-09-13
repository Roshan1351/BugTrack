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
        List.of(
                "Open",
                "Assigned",
                "In Progress",
                "Resolved",
                "Re-Testing",
                "Closed",
                "Reopened",
                "Rejected"
        ).forEach(this::ensureStatus);

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

    private void ensureStatus(String statusName) {
        if (bugStatusRepository.findByStatusName(statusName).isEmpty()) {
            bugStatusRepository.save(new BugStatus(null, statusName));
            System.out.println("Bug status added: " + statusName);
        }
    }
}
