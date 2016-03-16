package com.theironyard.controllers;//Created by KevinBozic on 3/15/16.

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@RestController
public class IronGramController {
    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui = null;

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String userName, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findByName(userName);
        if (user == null) {
            user = new User(userName, PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(password, user.getPasswordHash())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("userName", userName);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        return users.findByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(MultipartFile photo, HttpSession session, HttpServletResponse response, Integer timeInput) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            throw new Exception("Not logged in");
        }

        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("You can only upload images");
        }

        User user = users.findByName(userName); // finds user by name, userName, and assigns it to user

        File photoFile = File.createTempFile("image", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());



        Photo p = new Photo(user, null, photoFile.getName(), LocalDateTime.now(), timeInput == null ? 10 : timeInput);
        photos.save(p);
        response.sendRedirect("/");

        return p;
    }

    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public List<Photo> showPhotos(HttpSession session) {
        List<Photo> tempPhotos = (List<Photo>) photos.findAll();
        for (Photo photo : tempPhotos) {
            if (photo.getDateTime().plusSeconds(photo.getTimeInput()).isBefore(LocalDateTime.now())) {
                photos.delete(photo);
                File f = new File("public", photo.getFileName());
                f.delete();
            }
        }

        tempPhotos = (List<Photo>) photos.findAll();
        List<Photo> finalPhotos = new ArrayList<Photo>();
        String userName = (String) session.getAttribute("userName");
        for (Photo photo : tempPhotos) {
            if (photo.getRecipient() == null || photo.getRecipient().getName().equals(userName)) {
                finalPhotos.add(photo);
            }
        }
        return finalPhotos;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }

//    public void setTime(long time) {
//
//    }
//
//    public boolean after(Timestamp ts) {
//
//    }
}
