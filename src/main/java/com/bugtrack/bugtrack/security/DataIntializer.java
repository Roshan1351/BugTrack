package com.bugtrack.bugtrack.security;

import com.bugtrack.bugtrack.repository.BugStatusRepository;
import com.bugtrack.bugtrack.repository.PriorityRepository;
import com.bugtrack.bugtrack.repository.RoleRepository;
import com.bugtrack.bugtrack.repository.SeverityRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

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

    }
}
