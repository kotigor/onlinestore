package com.konstantinov.onlinestore.rest.controller;


import com.konstantinov.onlinestore.bd.goods.CakeService;
import com.konstantinov.onlinestore.bd.orders.OrderService;
import com.konstantinov.onlinestore.bd.users.UserServiceImpl;
import com.konstantinov.onlinestore.rest.dto.Cake;
import com.konstantinov.onlinestore.rest.dto.CakeDetail;
import com.konstantinov.onlinestore.rest.dto.Order;
import com.konstantinov.onlinestore.rest.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImpl userService;
    private final OrderService orderService;
    private final CakeService cakeService;

    @Value("${pathname.upload}")
    private String pathnameUpload;

    @Autowired
    public AdminController(UserServiceImpl userService, OrderService orderService, CakeService cakeService){
        this.userService = userService;
        this.orderService = orderService;
        this.cakeService = cakeService;
    }

    @GetMapping("")
    public String addUserForm(){
        return "admin";
    }

    @GetMapping("/user")
    public String addUserForm(@RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "") String number, Model model){
        User user = new User();
        List<User> userList;
        if(!number.equals(""))
            userList = Stream.of(userService.getUserByNumber(number)).collect(Collectors.toList());
        else
            userList = userService.getSomeUsers(Integer.valueOf(page));
        model.addAttribute("user", user);
        model.addAttribute("usersList", userList);
        model.addAttribute("page", Integer.valueOf(page));
        return "user";
    }

    @PostMapping("/user")
    public String addUserSubmit(@ModelAttribute User user, Model model){
        model.addAttribute("user", user);
        userService.getOrCreateUser(user);
        return "addUserResult";
    }

    @PostMapping("/updateCommonInfoAboutUser")
    public String updateCommonInfoAboutUser(@ModelAttribute User user){
        userService.updateUser(user);
        return "redirect:/admin/user";
    }

    @GetMapping("/orders")
    public String orgerPage(@RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "") String number, Model model){
        Order order = new Order();
        List<Order> orderList;
        if(!number.equals(""))
            orderList = orderService.getOrderByNumber(number, Integer.valueOf(page));
        else
            orderList = orderService.getSomeOrder(Integer.valueOf(page));
        List<Cake> cakeList = cakeService.getSomeCake(0, 100);
        model.addAttribute("cakeList", cakeList);
        model.addAttribute("order", order);
        model.addAttribute("orderList", orderList);
        model.addAttribute("page", Integer.valueOf(page));
        return "orders";
    }

    @GetMapping("/order/{id}")
    public String order(@PathVariable Long id, Model model){
        Order order = orderService.getOrderById(id);
        List<Cake> cakeList = cakeService.getSomeCake(0, 100);
        model.addAttribute("cakeList", cakeList);
        model.addAttribute("order", order);
        return "order";
    }

    @PostMapping("/addOrder")
    public String addOrder(@ModelAttribute Order order){
        orderService.createOrder(order);
        return "redirect:/admin/orders";
    }

    @PostMapping("/updateCommonInfoAboutOrder")
    public String updateOrderWithoutCakes(@ModelAttribute Order order){
        orderService.updateOrderWithoutCakes(order);
        return "redirect:/admin/orders";
    }

    @PostMapping("/updateOrder")
    public String updateOrder(@ModelAttribute Order order){
        orderService.updateOrder(order);
        return "redirect:/admin/orders";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:http://localhost:9091/auth/realms/OnlineStore/protocol/openid-connect/logout?redirect_uri=http://localhost:8080/admin";
    }

    @GetMapping("/cakes")
    public String cakes(@RequestParam(defaultValue = "0") String page, Model model){
        CakeDetail cake = new CakeDetail();
        List<Cake> cakes = cakeService.getSomeCake(Integer.valueOf(page), 100);
        model.addAttribute("cake", cake);
        model.addAttribute("cakeList", cakes);
        return "cakes";
    }

    @PostMapping("/updateCommonInfoAboutCake")
    public String updateCommonInfoAboutCake(@ModelAttribute Cake cake){
        cakeService.updateCake(cake);
        return "redirect:/admin/cakes";
    }

    @PostMapping("/cake")
    public String createOrUpdateCake(@ModelAttribute CakeDetail cakeDetail,
                          @RequestParam MultipartFile imgFile,
                          @RequestParam String composition){
        if(!imgFile.isEmpty()){
            String fileName = uploadImage(imgFile);
            cakeDetail.setImage(fileName);
        }
        Set<String> compositionSet = Arrays.stream(composition.split("\n")).map(String::trim).collect(Collectors.toSet());
        cakeDetail.setComposition(compositionSet);
        cakeService.updateOrCreateCakeDetail(cakeDetail);
        return "redirect:/admin/cakes";
    }

    @GetMapping("/cake/{id}")
    public String cake(@PathVariable Long id, Model model){
        CakeDetail cakeDetail = cakeService.getCakeById(id);
        model.addAttribute("cake", cakeDetail);
        model.addAttribute("lineSep", "\n");
        return "cake";
    }

    private String uploadImage(MultipartFile imgFile){
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        File file = new File(pathnameUpload + fileName);
        try {
            imgFile.transferTo(file);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
