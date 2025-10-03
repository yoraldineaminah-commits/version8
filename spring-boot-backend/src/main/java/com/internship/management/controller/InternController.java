package com.internship.management.controller;

import com.internship.management.dto.ApiResponse;
import com.internship.management.dto.CreateInternRequest;
import com.internship.management.dto.InternDTO;
import com.internship.management.entity.Intern;
import com.internship.management.service.InternService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InternController {

    private final InternService internService;

    @PostMapping
    public ResponseEntity<ApiResponse<InternDTO>> createIntern(@RequestBody CreateInternRequest request) {
        try {
            InternDTO intern = internService.createIntern(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Stagiaire créé avec succès", intern));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InternDTO>>> getAllInterns(
            @RequestParam(required = false) Long encadreurId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status
    ) {
        if (encadreurId != null) {
            return ResponseEntity.ok(ApiResponse.success(internService.getInternsByEncadreur(encadreurId)));
        }
        if (department != null) {
            return ResponseEntity.ok(ApiResponse.success(internService.getInternsByDepartment(department)));
        }
        if (status != null) {
            Intern.InternshipStatus internStatus = Intern.InternshipStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(ApiResponse.success(internService.getInternsByStatus(internStatus)));
        }
        return ResponseEntity.ok(ApiResponse.success(internService.getAllInterns()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternDTO>> getInternById(@PathVariable Long id) {
        try {
            InternDTO intern = internService.getInternById(id);
            return ResponseEntity.ok(ApiResponse.success(intern));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InternDTO>> updateIntern(
            @PathVariable Long id,
            @RequestBody CreateInternRequest request
    ) {
        try {
            InternDTO intern = internService.updateIntern(id, request);
            return ResponseEntity.ok(ApiResponse.success("Stagiaire mis à jour avec succès", intern));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InternDTO>> updateInternStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        try {
            String statusStr = request.get("status");
            Intern.InternshipStatus status = Intern.InternshipStatus.valueOf(statusStr.toUpperCase());
            InternDTO intern = internService.updateInternStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Statut mis à jour avec succès", intern));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIntern(@PathVariable Long id) {
        try {
            internService.deleteIntern(id);
            return ResponseEntity.ok(ApiResponse.success("Stagiaire supprimé avec succès", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
