package com.familyteacher.backend.controller;

import com.familyteacher.backend.entity.User;
import com.familyteacher.backend.service.FavoriteService;
import com.familyteacher.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/add")
    public Map<String, Object> addFavorite(@RequestBody Map<String, Object> favoriteData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return error;
        }
        
        String resourceType = (String) favoriteData.get("resourceType");
        Long resourceId = ((Number) favoriteData.get("resourceId")).longValue();
        
        return favoriteService.addFavorite(user, resourceType, resourceId);
    }
    
    @DeleteMapping("/remove")
    public Map<String, Object> removeFavorite(@RequestBody Map<String, Object> favoriteData, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No token provided");
            return error;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return error;
        }
        
        String resourceType = (String) favoriteData.get("resourceType");
        Long resourceId = ((Number) favoriteData.get("resourceId")).longValue();
        
        return favoriteService.removeFavorite(user, resourceType, resourceId);
    }
    
    @GetMapping("/list")
    public List<Map<String, Object>> getFavorites(@RequestParam(required = false) String resourceType, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return List.of();
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            return List.of();
        }
        
        return favoriteService.getFavorites(user, resourceType);
    }
    
    @GetMapping("/check")
    public Map<String, Object> checkFavorite(@RequestParam String resourceType, @RequestParam Long resourceId, HttpServletRequest request) {
        String token = extractToken(request);
        Map<String, Object> response = new HashMap<>();
        
        if (token == null) {
            response.put("isFavorite", false);
            return response;
        }
        
        User user = userService.getUserFromToken(token);
        if (user == null) {
            response.put("isFavorite", false);
            return response;
        }
        
        response.put("isFavorite", favoriteService.isFavorite(user, resourceType, resourceId));
        return response;
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
