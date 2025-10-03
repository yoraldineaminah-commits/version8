package com.internship.management.controller;

import com.internship.management.dto.ApiResponse;
import com.internship.management.dto.InternDTO;
import com.internship.management.dto.UserDTO;
import com.internship.management.service.EncadreurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encadreurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EncadreurController {

    private final EncadreurService encadreurService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllEncadreurs() {
        return ResponseEntity.ok(ApiResponse.success(encadreurService.getAllEncadreurs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getEncadreurById(@PathVariable Long id) {
        try {
            UserDTO encadreur = encadreurService.getEncadreurById(id);
            return ResponseEntity.ok(ApiResponse.success(encadreur));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/interns")
    public ResponseEntity<ApiResponse<List<InternDTO>>> getEncadreurInterns(@PathVariable Long id) {
        try {
            List<InternDTO> interns = encadreurService.getEncadreurInterns(id);
            return ResponseEntity.ok(ApiResponse.success(interns));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/intern-count")
    public ResponseEntity<ApiResponse<Long>> getEncadreurInternCount(@PathVariable Long id) {
        try {
            Long count = encadreurService.getEncadreurInternCount(id);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createEncadreur(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdEncadreur = encadreurService.createEncadreur(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Encadreur créé avec succès", createdEncadreur));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateEncadreur(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO
    ) {
        try {
            UserDTO updatedEncadreur = encadreurService.updateEncadreur(id, userDTO);
            return ResponseEntity.ok(ApiResponse.success("Encadreur mis à jour avec succès", updatedEncadreur));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEncadreur(@PathVariable Long id) {
        try {
            encadreurService.deleteEncadreur(id);
            return ResponseEntity.ok(ApiResponse.success("Encadreur supprimé avec succès", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
