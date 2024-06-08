package doanjava1com.example.demo1.Controllers;

import doanjava1com.example.demo1.Models.User;
import doanjava1com.example.demo1.Services.RoleService;
import doanjava1com.example.demo1.Services.UserService;
import doanjava1com.example.demo1.Utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/users")
@ComponentScan("doanjava1com.example.demo1.Services")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewAllUser(Model model) {
        List<User> listUsers = userService.listAll();
        model.addAttribute("users", listUsers);
        return "user/index";
    }

    @GetMapping("/new")
    public String showNewUserPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.listAll());
        return "user/create";
    }

    @PostMapping("/save")
    @Transactional
    public String saveUser(@ModelAttribute("user") User user, @RequestParam("image") MultipartFile multipartFile, Model model) throws IOException {
        if (user.getId() == null) {
            // Creating new user
            if (userService.findByUsername(user.getUsername()) != null) {
                model.addAttribute("error", "Username already exists.");
                model.addAttribute("roles", roleService.listAll());
                return "user/create";
            }
            if (userService.findByEmail(user.getEmail()) != null) {
                model.addAttribute("error", "Email already exists.");
                model.addAttribute("roles", roleService.listAll());
                return "user/create";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotourl(fileName);
            user.setEnabled(true);
            User savedUser = userService.save(user);
            if (!multipartFile.getOriginalFilename().isEmpty()) {
                String uploadDir = "photos/" + savedUser.getId();
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        } else {
            // Updating existing user
            User existingUser = userService.get(user.getId());
            if (existingUser != null) {
                // Check if username is already taken by another user
                User userByUsername = userService.findByUsername(user.getUsername());
                if (userByUsername != null && !userByUsername.getId().equals(user.getId())) {
                    model.addAttribute("error", "Username already exists.");
                    model.addAttribute("roles", roleService.listAll());
                    return "user/edit";
                }

                // Check if email is already taken by another user
                User userByEmail = userService.findByEmail(user.getEmail());
                if (userByEmail != null && !userByEmail.getId().equals(user.getId())) {
                    model.addAttribute("error", "Email already exists.");
                    model.addAttribute("roles", roleService.listAll());
                    return "user/edit";
                }

                existingUser.setUsername(user.getUsername());
                existingUser.setEmail(user.getEmail());
                if (!user.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setPhoneNumber(user.getPhoneNumber());
                existingUser.setAddress(user.getAddress());
                existingUser.setRoles(user.getRoles());
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    existingUser.setPhotourl(fileName);
                    String uploadDir = "photos/" + existingUser.getId();
                    FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
                }
                userService.save(existingUser);
            }
        }
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserPage(@PathVariable("id") Long id, Model model) {
        User user = userService.get(id);
        if (user == null) {
            return "notfound";
        } else {
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.listAll());
            return "user/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteRUser(@PathVariable("id") Long id) {
        User user = userService.get(id);
        user.setRoles(null);
        if (user == null) {
            return "notfound";
        } else {
            userService.delete(id);
            return "redirect:/users";
        }
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.findByUsername(currentUser.getUsername());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile")
    @Transactional
    public String updateProfile(@ModelAttribute("user") User user, @RequestParam("image") MultipartFile multipartFile,
                                @AuthenticationPrincipal UserDetails currentUser) throws IOException {
        User existingUser = userService.findByUsername(currentUser.getUsername());
        if (existingUser == null) {
            return "notfound";
        }

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setAddress(user.getAddress());

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            existingUser.setPhotourl(fileName);
            String uploadDir = "photos/" + existingUser.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        userService.save(existingUser);
        return "redirect:/users/profile";
    }
}
