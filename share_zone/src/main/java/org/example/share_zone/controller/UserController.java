package org.example.share_zone.controller;

import org.example.share_zone.model.*;
import org.example.share_zone.service.admin.AdminService;
import org.example.share_zone.service.comment.CommentService;
import org.example.share_zone.service.media.MediaService;
import org.example.share_zone.service.user.UserService;
import org.example.share_zone.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/home-page")
class UserController {
    private final UserService userService;
    private final MediaService mediaService;
    private final CommentService commentService;
    private final ResourceLoader resourceLoader;
    private final AdminService adminService;
    private Location location = Location.HOME;

    @Autowired
    public UserController(UserService userService, CommentService commentService,
                          ResourceLoader resourceLoader, MediaService mediaService
            , AdminService adminService) {
        this.userService = userService;
        this.mediaService = mediaService;
        this.resourceLoader = resourceLoader;
        this.commentService = commentService;
        this.adminService = adminService;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        session.setAttribute("location", location.toString());
        long userId = session.getAttribute("userId") == null ? 0 : (long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        if (user != null) model.addAttribute("avatar", user.getAvatar());

        switch (location) {
            case HOME:
                model.addAttribute("media_displays", mediaService.getAllMedias(user));
                break;
            case DETAIL:
                long mediaId = (long) session.getAttribute("mediaId");
                User userUploadThis = mediaService.getUserUploadedByMedia(mediaId);
                Media media = mediaService.getMediaById(mediaId);
                System.err.println("User upload file" + media.getUploadedBy().getId());
                System.err.println("Current User" + session.getAttribute("userId"));
                model.addAttribute("media_display", mediaService.getMediaDisplay(media, user));
                model.addAttribute("name_user_upload", userUploadThis.getName());
                model.addAttribute("avatar_user_upload", userUploadThis.getAvatar());
                model.addAttribute("comments", commentService.findByMediaId(mediaId));
                break;
            case PROFILE:
                model.addAttribute("name", Objects.requireNonNull(user).getName());
                if (session.getAttribute("target").equals("uploaded")) {
                    model.addAttribute("media_displays", mediaService.getMediaUploadedByUser(user));
                } else if (session.getAttribute("target").equals("favourite")) {
                    model.addAttribute("media_displays", mediaService.getFavouriteMediaByUser(userId));
                }
                break;
            case DASHBOARD:
                model.addAttribute("name", Objects.requireNonNull(user).getName());
                model.addAttribute("all_data",adminService.getALlData());
        }
        return "home";
    }

    @GetMapping("/profile/{target}")
    public String toProfileUpload(HttpSession session, @PathVariable String target) {
        session.setAttribute("target", target);
        String role = session.getAttribute("role").toString();
        if (role.equals("USER")) location = Location.PROFILE;
        else if (role.equals("ADMIN")) location = Location.DASHBOARD;

        return "redirect:/home-page";
    }

    @GetMapping("/go-home")
    public String toHome() {
        location = Location.HOME;
        return "redirect:/home-page";
    }

    @GetMapping("/detail/{mediaId}")
    public String toDetail(HttpSession session, @PathVariable long mediaId) {
        location = Location.DETAIL;
        session.setAttribute("mediaId", mediaId);
        return "redirect:/home-page";
    }

    @PostMapping("/detail/add-comment")
    public String addComment(@RequestParam("content") String content, @RequestParam("mediaId") long mediaId, HttpSession session) {
        long userId = (long) session.getAttribute("userId");
        commentService.addComment(userId, mediaId, content);
        return "redirect:/home-page";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@ModelAttribute("uploadForm") UploadForm uploadForm, HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        for (MultipartFile file : uploadForm.getFiles()) {
            uploadFile(file, redirectAttributes, session);
        }

        if (redirectAttributes.getFlashAttributes().containsKey("error")) {
            redirectAttributes.addFlashAttribute("notify_color", "red");
        } else {
            redirectAttributes.addFlashAttribute("notify_color", "green");
        }

        location = Location.HOME;
        return "redirect:/home-page";
    }

    @PostMapping("/add-favourite")
    public String handleMediaFavourite(@RequestParam("mediaId") Long mediaId, HttpSession session) {
        long userId = (long) session.getAttribute("userId");
        userService.handleMediaFavorite(userId, mediaId);
        return "redirect:/home-page";
    }

    @PostMapping("/sign-in")
    public String handleSignIn(@RequestParam("email") String email, @RequestParam("password") String password,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Email Not Exist");
        } else if (!user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Password Wrong");
            session.setAttribute("signInEmail", email);
        } else {
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());
            redirectAttributes.addFlashAttribute("message", user.getName() + " - Sign In Success");

            session.removeAttribute("signInEmail");
        }

        if (redirectAttributes.getFlashAttributes().containsKey("error")) {
            redirectAttributes.addFlashAttribute("notify_color", "red");
        } else {
            redirectAttributes.addFlashAttribute("notify_color", "green");
        }

        location = Location.HOME;
        return "redirect:/home-page";
    }

    @PostMapping("/sign-up")
    public String handleSignUp(@RequestPart("file") MultipartFile file, @RequestParam("name") String name, @Email @RequestParam("email") String email,
                               @RequestParam("password") String password, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
            redirectAttributes.addFlashAttribute("error", "Email Already Exist");
            session.setAttribute("signUpName", name);
        } else {
            User userByName = userService.getUserByName(name);
            if (userByName != null) {
                redirectAttributes.addFlashAttribute("error", "Name Already Exist");
                session.setAttribute("signUpEmail", email);
            } else {
                String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
                String saveFileName = UUID.randomUUID() + filename;

                if (file.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "File is Empty");
                } else {
                    Media.MediaType mediaType = Utils.validExtensions(filename.split("\\.")[1]);
                    if (mediaType == null) {
                        redirectAttributes.addFlashAttribute("error", "Media Extensions Invalid");
                    } else {
                        try {
                            Path uploadPath = Paths.get(resourceLoader.getResource("classpath:static/media/").getFile().getPath());
                            Files.createDirectories(uploadPath);

                            Path filePath = uploadPath.resolve(saveFileName);
                            file.transferTo(filePath.toFile());

                            //check file is saved
                            File savedFile = filePath.toFile();
                            if (savedFile.exists() && savedFile.length() > 0) {
                                //save to db
                                User newUser = new User();
                                newUser.setName(name);
                                newUser.setEmail(email);
                                newUser.setPassword(password);
                                newUser.setRole(User.Role.USER);
                                newUser.setAvatar(saveFileName);

                                userService.saveUser(newUser);
                                session.setAttribute("userId", newUser.getId());
                                session.setAttribute("role", newUser.getRole());
                                redirectAttributes.addFlashAttribute("message", "Sign up success");
                                session.removeAttribute("signUpName");
                                session.removeAttribute("signUpEmail");
                            } else {
                                redirectAttributes.addFlashAttribute("error", "Tải avatar lên thất bại: file không được lưu đúng cách.");
                            }
                        } catch (IOException e) {
                            redirectAttributes.addFlashAttribute("error", e.getMessage());
                        }
                    }
                }
            }
        }

        if (redirectAttributes.getFlashAttributes().containsKey("error")) {
            redirectAttributes.addFlashAttribute("notify_color", "red");
        } else {
            redirectAttributes.addFlashAttribute("notify_color", "green");
        }

        location = Location.HOME;
        return "redirect:/home-page";
    }

    @GetMapping("/sign-out")
    public String signOutUser(HttpSession session) {
        location = Location.HOME;
        session.removeAttribute("userId");
        return "redirect:/home-page";
    }

    private void uploadFile(MultipartFile file, RedirectAttributes redirectAttributes, HttpSession session) {
        String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
        String saveFileName = UUID.randomUUID() + filename;

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File is Empty");
        } else {
            Media.MediaType mediaType = Utils.validExtensions(filename.split("\\.")[1]);
            if (mediaType == null) {
                redirectAttributes.addFlashAttribute("error", "Media Extensions Invalid");
            } else {
                try {
                    long userId = (long) session.getAttribute("userId");

                    Path uploadPath = Paths.get(resourceLoader.getResource("classpath:static/media/").getFile().getPath());
                    Files.createDirectories(uploadPath);

                    Path filePath = uploadPath.resolve(saveFileName);
                    file.transferTo(filePath.toFile());

                    //check file is saved
                    File savedFile = filePath.toFile();
                    if (savedFile.exists() && savedFile.length() > 0) {
                        //save to db
                        Media media = new Media();
                        media.setUploadedAt(LocalDateTime.now());
                        media.setUploadedBy(userService.getUserById(userId));
                        media.setType(mediaType);
                        media.setUrl(saveFileName);
                        media.setComments(new ArrayList<>());
                        media.setTags(new HashSet<>());
                        mediaService.saveMedia(media);

                        redirectAttributes.addFlashAttribute("message", "Tải lên thành công: " + filename.substring(filename.length() - 10));
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Tải lên thất bại: file không được lưu đúng cách.");
                    }
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", e.getMessage());
                }
            }
        }
    }

    enum Location {
        HOME, PROFILE, DETAIL, DASHBOARD
    }
}