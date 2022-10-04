package com.example.companyemployeespring.controller;

import com.example.companyemployeespring.entity.User;
import com.example.companyemployeespring.repository.UserRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${task.management.images.folder}")
    private String folderPath;

    @GetMapping(value = "/users")
    public String showUsers(ModelMap modelMap){
        List<User> all = userRepository.findAll();
        modelMap.addAttribute("users",all);
        return "/users";

    }

    @GetMapping(value = "/user/add")
    public  String addUserPage(ModelMap modelMap){
        List<User> users = userRepository.findAll();
        modelMap.addAttribute("users", users);
        return "addUser";
    }

     @PostMapping (value = "/user/add")
    public String addUser(@ModelAttribute User user,
                          @RequestParam("userImage")MultipartFile file) throws IOException {
         if (!file.isEmpty() && file.getSize() > 0) {
             String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
             File newFile = new File(folderPath + File.separator + filename);
             file.transferTo(newFile);
             user.setProfilePic(filename);
         }
         user.setPassword(passwordEncoder.encode(user.getPassword()));

         userRepository.save(user);
         return "redirect:/users";

     }

    @GetMapping(value = "/users/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("fileName") String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(folderPath + File.separator + fileName);
        return IOUtils.toByteArray(inputStream);

    }
}
