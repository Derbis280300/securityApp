package com.example.securityApp.controllers;

import com.example.securityApp.entities.*;
import com.example.securityApp.services.ItemService;
import com.example.securityApp.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ItemService itemService;

    @Value("${target.url}")
    private String targetUrl;

    @Value("${loadURL}")
    private String loadURL;

    @GetMapping(value = "/")
    public String index(Model model) {
        List<Items> items=itemService.getAllItems();
        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("cats",categories);
        model.addAttribute("items",items);
        return "index";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin")
    public String adminPage(Model model) {
        List<Items>allItems=itemService.getAllItems();

        model.addAttribute("items",allItems);
        return "admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    @GetMapping(value = "/moderator")
    public String moderatorPage(Model model) {
        List<Items>allItems=itemService.getAllItems();

        model.addAttribute("items",allItems);
        return "moderator";
    }

    @GetMapping(value = "/accessDenied")
    public String deniedPage(Model model) {
        model.addAttribute("current_user", getUser());
        return "denied";
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("current_user", getUser());
        return "login";
    }

    @GetMapping(value = "/register")
    public String register() {
        return "register";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping(value = "/toRegister")
    public String signUp(@RequestParam(name = "email") String email,
                         @RequestParam(name = "password") String password,
                         @RequestParam(name = "name") String name,
                         @RequestParam(name = "re-password") String retype) {
        Users checkuser = userService.findUserByEmail(email);
        if (checkuser == null) {
            if (password.equals(retype)) {
                Set<Roles> roles = new HashSet<>();
                Roles role = userService.findByRoles("ROLE_USER");
                roles.add(role);
                Users users = new Users();
                users.setPassword(passwordEncoder.encode(password));
                users.setFull_name(name);
                users.setEmail(email);
                users.setRoles(roles);
                userService.addUser(users);
                return "redirect:/register?success";
            }


        }
        return "redirect:/register?error";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/profile")
    public String profile(Model model) {
        model.addAttribute("current_user", getUser());
        return "profile";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/toChangeProfile")
    public String changeProfile(@RequestParam(name = "name") String name,
                                @RequestParam(name = "email") String email) {
        Users checkUsers = getUser();
        if (checkUsers != null) {
            checkUsers.setFull_name(name);
            userService.saveUser(checkUsers);
            return "redirect:/profile?success";
        }
        return "redirect:/profile?error";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/toChangePassword")
    public String changePassword(@RequestParam(name = "old_password") String old_password,
                                 @RequestParam(name = "new_password") String new_password,
                                 @RequestParam(name = "re-password") String re_password) {

        if (new_password.equals(re_password)) {
            Users checkUsers = getUser();
            if (passwordEncoder.matches(old_password, checkUsers.getPassword())) {
                checkUsers.setPassword(passwordEncoder.encode(new_password));
                userService.saveUser(checkUsers);
                return "redirect:/profile?right";
            }
        }
        return "redirect:/profile?wrong";

    }

    private Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users users = (Users) authentication.getPrincipal();
            return users;
        } else {
            return null;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/uploadAvatar")
    public String uploadPic(@RequestParam(name = "pic") MultipartFile file) {
        if (file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {


            try {
                Users users = getUser();
                //String fileName="altynay"+users.getId()+".jpg";
                String fileName = DigestUtils.sha1Hex("altynay" + users.getId());
                byte[] bytes = file.getBytes();//??????
                Path path = Paths.get(targetUrl + fileName + ".jpg");
                Files.write(path, bytes);

                users.setAvatar(fileName);
                userService.saveUser(users);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/profile";
    }

    @GetMapping(value = "/viewAva/{avatarUrl}", produces = {MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    byte[] viewAva(@PathVariable(name = "avatarUrl") String avatarurl) throws IOException {
        String pictureUrl = loadURL + "user.png";
        if (avatarurl != null) {
            pictureUrl = loadURL + avatarurl + ".jpg";

        }
        InputStream inputStream;
        try {
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            inputStream = resource.getInputStream();

        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(loadURL + "user.png");
            inputStream = resource.getInputStream();
        }
        return IOUtils.toByteArray(inputStream);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    @GetMapping(value = "/addItem")
    public String addItem(Model model) {
        List<Categories>cats=itemService.getAllCategories();
        List<Brands>brands=itemService.brands();


        model.addAttribute("brands",brands);
        model.addAttribute("cats",cats);

        return "addItem";
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    @PostMapping(value = "/toAddItem")
    public String toAddItem(@RequestParam(name = "name") String name,
                            @RequestParam(name = "price") double price,
                            @RequestParam(name = "description") String description,
                            @RequestParam(name = "pic") String pic,
                            @RequestParam(name="brand")Long id

    ) {
        Items item = new Items();
        Brands brand=itemService.brands(id);
        item.setPrice(price);
        item.setName(name);
        item.setPicture(pic);
        item.setBrands(brand);
        item.setDescription(description);
        itemService.addItem(item);


        return "redirect:/";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add_comment")
    public String addCom(@RequestParam(name = "id")Long id,
                         @RequestParam(name="comment") String comment)
    {
        Items items=itemService.getItem(id);
        Comments comments=new Comments();
        comments.setItems(items);
        comments.setComment(comment);
        comments.setUsers(getUser());
        comments.setDate(new Timestamp(System.currentTimeMillis()));
        itemService.addCom(comments);
        return "redirect:/details/"+items.getId();

    }


    @GetMapping(value = "/details/{id}")
    public String details(Model model,@PathVariable(name = "id") Long id)
    {
        Items item=itemService.getItem(id);
        if(item!=null)
        {
            List<Categories>cats=itemService.getAllCategories();
            List<Brands>brands=itemService.brands();
            List<Comments> comments=itemService.getAllComment(item.getId());



            model.addAttribute("brands",brands);
            model.addAttribute("cats",cats);
            model.addAttribute("comment",comments);
            model.addAttribute("item",item);
            return "details";
        }
        return "index";
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    @GetMapping(value = "/edit/{id}")
    public String edit(Model model,@PathVariable(name = "id") Long id)
    {
        Items item=itemService.getItem(id);
        List<Brands>brands=itemService.brands();



        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("brands",brands);
        model.addAttribute("cats",categories);
        model.addAttribute("item",item);
        return "edit";
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    @PostMapping(value = "/toEdit/{id}")
    public String toEdit(@PathVariable(name = "id") Long id,
                         @RequestParam(name="name") String name,
                         @RequestParam(name="price") double price,
                         @RequestParam(name = "pic") String pic,
                         @RequestParam(name="description") String description,
                         @RequestParam(name = "brand") Long b_id)
    {
        Brands brand=itemService.brands(b_id);
        Items item=itemService.getItem(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        item.setPicture(pic);
        item.setBrands(brand);
        itemService.saveItem(item);
        return "redirect:/details/"+item.getId();
//
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    @PostMapping(value = "/delete")
    public String deleteItem(@RequestParam(name = "id") Long id)
    {
        Items item=itemService.getItem(id);
        itemService.delete(item);
        return "redirect:/";
    }

    @GetMapping(value = "/search")
    public String search(@RequestParam(name = "key") String key,
                         Model model)
    {
        List<Items>items=itemService.getItems(key);
        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("cats",categories);
        model.addAttribute("items",items);
       return "search";

    }

    @GetMapping(value = "/apple")
    public String apple(Model model)
    {
        List<Items>items=itemService.brands("Apple");
        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("cats",categories);
        model.addAttribute("items",items);
        return "apple";
    }

    @GetMapping(value = "/xiaomi")
    public String xiaomi(Model model)
    {
        List<Items>items=itemService.brands("Xiaomi");
        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("cats",categories);
        model.addAttribute("items",items);
        return "apple";
    }
    @GetMapping(value = "/huawei")
    public String huawei(Model model)
    {
        List<Items>items=itemService.brands("Huawei");
        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("cats",categories);
        model.addAttribute("items",items);
        return "apple";
    }
    @GetMapping(value = "/samsung")
    public String samsung(Model model)
    {
        List<Items>items=itemService.brands("Samsung");
        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("cats",categories);
        model.addAttribute("items",items);
        return "apple";
    }
    @GetMapping(value = "/oppo")
    public String oppo(Model model)
    {
        List<Items>items=itemService.brands("OPPO");
        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("cats",categories);
        model.addAttribute("items",items);
        return "apple";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value="/editCat/{id}")
    public String editCat(Model model,@PathVariable(name = "id") Long id)
    {
        Categories categories =itemService.getCategory(id);
        model.addAttribute("cat", categories);
        return "editCat";
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/toEditCat/{id}")
    public String toeditCat(@PathVariable(name ="id") Long id,
                          @RequestParam(name="name") String name
    )
    {
        Categories categories =itemService.getCategory(id);
        categories.setName(name);
        itemService.editCat(categories);
        return "redirect:/admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/deleteCat")
    public String deletecat(@RequestParam(name = "id") Long id)
    {
        Categories cat=itemService.getCategory(id);
        itemService.deleteCat(cat);
        return "redirect:/categories";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping (value = "/addCat")
    public String addCat()
    {
        return "addCat";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/toAddCat")
    public String toAddCat(
                            @RequestParam(name="name") String name
                            )
    {
        Categories categories =new Categories();
        categories.setName(name);
        itemService.addCategory(categories);
        return "redirect:/admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/countries")
    public String country(Model model)
    {
        List<Countries>countries=itemService.countries();
        model.addAttribute("countries",countries);
        return "country";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value="/editCountry/{id}")
    public String editCountry(Model model,@PathVariable(name = "id") Long id)
    {
       Countries country=itemService.getCountry(id);
        model.addAttribute("country",country);
        return "editCountry";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/toEditCountry/{id}")
    public String toeditCountry(@PathVariable(name = "id") Long id,
                            @RequestParam(name="name") String name,
                            @RequestParam(name="code")String code)
    {
      Countries country=itemService.getCountry(id);
      country.setName(name);
      country.setCode(code);
      itemService.editCount(country);
        return "redirect:/countries";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping (value = "/addCountry")
    public String addCountry()
    {
        return "addCountry";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/toAddCountry")
    public String toAddCountry(
            @RequestParam(name="name") String name,
            @RequestParam(name="code")String code)
    {
       Countries country=new Countries();
       country.setName(name);
       country.setCode(code);
       itemService.addCountry(country);
        return "redirect:/countries";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/deleteCountry")
    public String deletecou(@RequestParam(name = "id") Long id)
    {
       Countries co=itemService.getCountry(id);
       itemService.deleteCountry(co);
        return "redirect:/countries";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/unassignCategory")
    public String unassignCat(@RequestParam(name = "cat_id") Long cat_id,
                              @RequestParam(name="item_id") Long item_id
    ) {
        Items items = itemService.getItem(item_id);
        Categories cat = itemService.getCategory(cat_id);
        if (items != null && cat != null)
        {
            List<Categories>getAllCategories=items.getCategories();
            if(getAllCategories==null)
            {
                getAllCategories=new ArrayList<>();
            }
            if(getAllCategories.contains(cat)) {
                getAllCategories.remove(cat);
                items.setCategories(getAllCategories);
              itemService.saveItem(items);

            }
            return "redirect:/edit/" + item_id + "#postCategoriesDiv";
        }
        return "redirect:/";

    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/assignCategory")
    public String assignCat(@RequestParam(name = "cat_id") Long cat_id,
                            @RequestParam(name="item_id") Long item_id
    ) {
        Items items = itemService.getItem(item_id);
        Categories cat = itemService.getCategory(cat_id);
        if (items != null && cat != null)
        {
            List<Categories>getAllCategories=items.getCategories();
            if(getAllCategories==null)
            {
                getAllCategories=new ArrayList<>();
            }
            if(!getAllCategories.contains(cat)) {
                getAllCategories.add(cat);
                items.setCategories(getAllCategories);
                itemService.saveItem(items);

            }
            return "redirect:/edit/" + item_id + "#postCategoriesDiv";
        }
        return "redirect:/";


    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/brands")
    public String brand(Model model)
    {
        List<Brands>brands=itemService.brands();
        model.addAttribute("brands",brands);
        return "brands";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping (value = "/addBrand")
    public String addBrand(Model model)
    {
        List<Countries> countries=itemService.countries();
        model.addAttribute("countries",countries);
        return "addBrand";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/toAddBrand")
    public String toAddBrand(
            @RequestParam(name="name") String name,
            @RequestParam(name="country")Long id)
    {
        Brands brand=new Brands();
        Countries country=itemService.getCountry(id);
        brand.setName(name);
        brand.setCountry(country);
        itemService.addBrand(brand);
        return "redirect:/brands";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/categories")
    public String categories(Model model)
    {

        List<Categories>categories=itemService.getAllCategories();
        model.addAttribute("categories",categories);
        return "categories";
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value="/editBrand/{id}")
    public String editBrand(Model model,@PathVariable(name = "id") Long id)
    {
       Brands brand=itemService.brands(id);
       List<Countries>countries=itemService.countries();
       model.addAttribute("country",countries);
   model.addAttribute("brands",brand);
        return "editBrand";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/toEditBrand/{id}")
    public String toEditBrand(@PathVariable(name = "id") Long id,
                                @RequestParam(name="name") String name,
                                @RequestParam(name="country")Long c_id)
    {
     Brands brand=itemService.brands(id);
     Countries country=itemService.getCountry(c_id);
     brand.setCountry(country);
     brand.setName(name);
     itemService.editBrand(brand);
        return "redirect:/brands";
    }

    @GetMapping(value = "/it_cat/{c_id}")
    public String it_cat(Model model,@PathVariable(name = "c_id")Long id) {
        Categories category = itemService.getCategory(id);
        List<Categories>categories=itemService.getAllCategories();

        List<Items> getAllItems = itemService.getAllItems();
        List<Items> newItems=new ArrayList<>();
        for ( Items it:getAllItems){
            if (it.getCategories().contains(category)) {
                newItems.add(it);
                }
             }
        model.addAttribute("cats",categories);

        model.addAttribute("items",newItems);
        return "it_cat";

    }

}
