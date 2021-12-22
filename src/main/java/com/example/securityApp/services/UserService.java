package com.example.securityApp.services;

import com.example.securityApp.entities.Roles;
import com.example.securityApp.entities.Users;
import com.example.securityApp.repository.RoleRepository;
import com.example.securityApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
   private RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users users =findUserByEmail(s);
        if(users !=null)
        {
            return users;
        }
        else
        {
            throw new UsernameNotFoundException("Users not found");
        }
    }
    public Users findUserByEmail(String email)
    {
        Users users =userRepository.findByEmail(email);
        return users;
    }

    public Roles findByRoles(String roles)
    {
        Roles role=roleRepository.findByRole(roles);
        return  role;
    }

    public Users addUser(Users users)
    {
        return userRepository.save(users);

    }
    public Users saveUser(Users users)
    {
        return userRepository.save(users);
    }






}
