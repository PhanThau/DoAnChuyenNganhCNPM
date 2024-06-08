package doanjava1com.example.demo1.Controllers;

import doanjava1com.example.demo1.Models.User;
import doanjava1com.example.demo1.Services.RoleService;
import doanjava1com.example.demo1.Services.SendMailService;
import doanjava1com.example.demo1.Services.UserService;
import doanjava1com.example.demo1.Utils.FileUploadUtil;
import doanjava1com.example.demo1.Utils.Utilities;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class RegisterController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showNewUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.listAll());
        return "home/register";
    }

    @PostMapping("/process_register")
    public String processRegister(User user, @RequestParam("image") MultipartFile multipartFile, HttpServletRequest request, Model model) throws UnsupportedEncodingException, MessagingException, Exception {
        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists.");
            return "home/register";
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already exists.");
            return "home/register";
        }




        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.addRoles(roleService.getbyName("USER"));
        user.setVerificationCode(RandomString.make(30));

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotourl(fileName);

            User savedUser = userService.save(user);

            String uploadDir = "photos/" + savedUser.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            user.setPhotourl(null);
            userService.save(user);
        }

        sendMailService.sendVerificationEmail(user, Utilities.getSiteURL(request));

        return "auth/register_success";
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code, Model model) {
        if (userService.verify(code)) {
            model.addAttribute("message", "Congratulations, your account has been verified.");
        } else {
            model.addAttribute("error", "Sorry, we could not verify account. It maybe already verified, or verification code is incorrect.");
        }
        return "auth/result_Verify_form";
    }
}
