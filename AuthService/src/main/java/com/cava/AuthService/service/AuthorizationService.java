package com.cava.AuthService.service;

import com.cava.AuthService.model.RegisterDTO;
import com.cava.AuthService.model.Users;
import com.cava.AuthService.repository.UserRepository;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorizationService implements UserDetailsService {

    private static final UUID NAMESPACE = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8")

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    @Transactional
    public void registerUser(RegisterDTO data){
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Users newUser = new Users(data.login(), encryptedPassword);
        this.userRepository.save(newUser);

    }

    public UserResponseDTO getUserById(String id){
        Optional<Users> user = userRepository.findById(UUID.fromString(id));
        if(user.isPresent()){
            return new UserResponseDTO(user.get().getId().toString(),user.get() user.get().getName());
        }
        return null;
    }

}
