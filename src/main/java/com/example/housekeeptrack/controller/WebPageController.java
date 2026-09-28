package com.example.housekeeptrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class WebPageController {

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        model.addAttribute("activeNav", "home");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "index";
    }

    @GetMapping("/rooms")
    public String rooms(Model model, Principal principal) {
        model.addAttribute("activeNav", "rooms");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "rooms";
    }

    @GetMapping("/room-status")
    public String roomStatus(Model model, Principal principal) {
        model.addAttribute("activeNav", "room-status");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "room-status";
    }

    @GetMapping("/housekeeping")
    public String housekeeping() {
        return "redirect:/cleaning-tasks";
    }

    @GetMapping("/housekeepers")
    public String housekeepers(Model model, Principal principal) {
        model.addAttribute("activeNav", "housekeepers");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "housekeepers";
    }

    @GetMapping("/cleaning-tasks")
    public String cleaningTasks(Model model, Principal principal) {
        model.addAttribute("activeNav", "cleaning-tasks");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "cleaning-tasks";
    }

    @GetMapping("/inspections")
    public String inspections(Model model, Principal principal) {
        model.addAttribute("activeNav", "inspections");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "inspections";
    }

    @GetMapping("/bookings")
    public String bookings(Model model, Principal principal) {
        model.addAttribute("activeNav", "bookings");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "bookings";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("username", principal != null ? principal.getName() : "Administrator");
        return "admin-dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model model, Principal principal) {
        model.addAttribute("activeNav", "reports");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "reports";
    }

    @GetMapping("/audit-logs")
    public String auditLogs(Model model, Principal principal) {
        model.addAttribute("activeNav", "audit-logs");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "audit-logs";
    }

    @GetMapping("/gallery")
    public String gallery(Model model, Principal principal) {
        model.addAttribute("activeNav", "gallery");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "gallery";
    }

    @GetMapping("/contact")
    public String contact(Model model, Principal principal) {
        model.addAttribute("activeNav", "contact");
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "contact";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("activeNav", "login");
        return "login";
    }
}
