package com.codeup.initalspring.controllers;

import com.codeup.initalspring.models.*;
import com.codeup.initalspring.services.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
class PostController {
    private final PostRepository postDao;
    private final UserRepository userDao;
    private final ImageRepository imageDao;
    private final EmailService emailService;

    public PostController(PostRepository postDao, UserRepository userDao, ImageRepository imageDao, EmailService emailService) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.imageDao = imageDao;
        this.emailService = emailService;
    }

    @GetMapping("/posts")
    public String posts(Model model) {
        model.addAttribute("posts", postDao.findAll());

        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String individualPost(@PathVariable long id, Model model) {
        Post post = postDao.getOne(id);
        model.addAttribute("post", post);
        model.addAttribute("images", post.getImages());
        return "posts/show";
    }

    @GetMapping("/posts/create")
    public String createPost(Model model) {
        model.addAttribute("post", new Post());
        return "posts/new";
    }

    @PostMapping("/posts/create")
    public String submitPost(@ModelAttribute Post post) {
        User user = userDao.getOne(1L);
        post.setOwner(user);
        PostImage imageOne = new PostImage("https://picsum.photos/200", post);
        PostImage imageTwo = new PostImage("https://picsum.photos/200", post);
        PostImage imageThree = new PostImage("https://picsum.photos/200", post);
        Post dbPost = postDao.save(post);
        PostImage dbImageOne = imageDao.save(imageOne);
        PostImage dbImageTwo = imageDao.save(imageTwo);
        PostImage dbImageThree = imageDao.save(imageThree);
        emailService.prepareAndSend(dbPost, "Well Done!", "You have successfully created a new post! The ID of your post is "+dbPost.getId()+".");

        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String editPostPage(@PathVariable long id, Model model) {
        Post post = postDao.getOne(id);
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String submitEdit(@PathVariable long id, @ModelAttribute Post post) {
        post.setOwner(userDao.getOne(1L));
        Post dbPost = postDao.save(post);
        return "redirect:/posts/";
    }

    @PostMapping("/posts/update")
    public String updatePost(@RequestParam(name = "id") long id,
                             @RequestParam(name = "title") String title,
                             @RequestParam(name = "description") String desc) {
        Post updatedPost = postDao.getOne(id);
        updatedPost.setTitle(title);
        updatedPost.setBody(desc);
        Post dbPost = postDao.save(updatedPost);
        return "redirect:/posts/?id=" + id;
    }

    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam(name = "id") int id) {
        postDao.deleteById((long) id);
        return "redirect:/posts";
    }
}
