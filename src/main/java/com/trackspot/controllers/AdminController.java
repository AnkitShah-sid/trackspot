package com.trackspot.controllers;

import com.trackspot.entities.Admin;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;
import com.trackspot.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * Admin controller.
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * List all admins.
     *
     * @param model
     * @return
     */
    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("admins", adminService.listAllAdmin());
        System.out.println("Returning admins:");
        return "admins";
    }

    /**
     * Save admin to database.
     *
     * @RequestBody adminSingInRequest
     * @return ResponseEntity with success/error
     */
    @PostMapping(value = "/sign-in")
    public ResponseEntity<String> saveAdmin(@RequestBody Admin adminSingInRequest) {
        Admin admin = adminService.saveAdmin(adminSingInRequest);
        if (admin != null) {
            return ResponseEntity.ok("Sign-in successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/log-in")
    public ResponseEntity<?> login(@RequestBody JwtRequestModel request) {
        try {
            LoginResponseModel token = adminService.createToken(request);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error "+e);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentAdmin() {
        try {
            return ResponseEntity.ok(adminService.getCurrentAdmin());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error "+e);
        }

    }

    /**
     * View a specific admin by its id.
     *
     * @param id
     * @param model
     * @return
     */

    @GetMapping("/{id}")
    public String showAdmin(@PathVariable Integer id, Model model) {
        model.addAttribute("admin", adminService.getAdminById(id));
        return "adminshow";
    }

    @PutMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("admin", adminService.getAdminById(id));
        return "adminform";
    }

    /**
     * New admin.
     *
     * @param model
     * @return
     */
    @RequestMapping("/new")
    public String newAdmin(Model model) {
        model.addAttribute("admin", new Admin());
        return "adminform";
    }


    /**
     * Delete admin by its id.
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
        return "redirect:/admins";
    }

}
