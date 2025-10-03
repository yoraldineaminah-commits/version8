package com.internship.management.controller;

import com.internship.management.dto.ApiResponse;
import com.internship.management.dto.ProjectDTO;
import com.internship.management.entity.User;
import com.internship.management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects(
            @RequestParam(required = false) Long encadreurId,
            @RequestParam(required = false) Long stagiaireId
    ) {
        if (encadreurId != null) {
            return ResponseEntity.ok(ApiResponse.success(projectService.getProjectsByEncadreur(encadreurId)));
        }
        if (stagiaireId != null) {
            return ResponseEntity.ok(ApiResponse.success(projectService.getProjectsByStagiaire(stagiaireId)));
        }
        return ResponseEntity.ok(ApiResponse.success(projectService.getAllProjects()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(projectService.getProjectById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@RequestBody ProjectDTO projectDTO) {
        try {
            ProjectDTO createdProject = projectService.createProject(projectDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Projet créé avec succès", createdProject));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectDTO projectDTO
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Projet mis à jour avec succès", projectService.updateProject(id, projectDTO)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(ApiResponse.success("Projet supprimé avec succès", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/assign-interns")
    public ResponseEntity<ApiResponse<ProjectDTO>> assignInterns(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> request
    ) {
        try {
            List<Long> internIds = request.get("internIds");
            return ResponseEntity.ok(ApiResponse.success("Stagiaires assignés avec succès", projectService.assignInterns(id, internIds)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
