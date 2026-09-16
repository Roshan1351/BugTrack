package com.bugtrack.bugtrack.config;

import com.bugtrack.bugtrack.entity.Role;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.RoleRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository){
        this.userRepository= userRepository;
        this.passwordEncoder= passwordEncoder;
        this.roleRepository= roleRepository;
    }
    @Value("${Admin_Email}")
    private String AdminEmail;
    @Value("${Admin_Password}")
    private String AdminPassword;
    @Value("${Admin_Name}")
    private String FullName;


    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.existsByEmail(AdminEmail)){
            Role role= roleRepository.findByRoleName("Admin").orElseThrow(()-> new RuntimeException("Admin role not found"));
            User user= new User();
            user.setEmail(AdminEmail);
            user.setFullName(FullName);
            user.setPasswordHash(passwordEncoder.encode(AdminPassword));
            user.setRole(role);
            user.setIsActive(true);
            userRepository.save(user);
            System.out.println("Default admin created "+ AdminEmail);
        }else{
            System.out.println("Admin already created!");
        }
    }
}
