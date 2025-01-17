package com.manage.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.manage.project.model.User;
import com.manage.project.repo.UserRepo;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);  // Updated here
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println("Username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());
        System.out.println("Role: " + user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole())
                .build();
    }
	 // Thêm phương thức tìm người dùng theo username
	    public User findUserByUsername(String username) {
	        return userRepo.findByUsername(username);
	    }
    
    public void createUser(String username, String password) {
        // Kiểm tra nếu người dùng đã tồn tại
        if (userRepo.findByUsername(username) != null) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại.");
        }

        // Tạo người dùng mới
        User user = new User();
        user.setUsername(username);
        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setRole("USER"); // Mặc định gán role là USER
        userRepo.save(user); // Lưu người dùng vào cơ sở dữ liệu
    }
}
