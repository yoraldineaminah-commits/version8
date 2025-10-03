package com.internship.management.service;

import com.internship.management.dto.CreateInternRequest;
import com.internship.management.dto.InternDTO;
import com.internship.management.entity.Encadreur;
import com.internship.management.entity.Intern;
import com.internship.management.entity.User;
import com.internship.management.repository.EncadreurRepository;
import com.internship.management.repository.InternRepository;
import com.internship.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternService {

    private final InternRepository internRepository;
    private final UserRepository userRepository;
    private final EncadreurRepository encadreurRepository;

    @Transactional
    public InternDTO createIntern(CreateInternRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User user = User.builder()
                .email(request.getEmail())
                .prenom(request.getFirstName())
                .nom(request.getLastName())
                .phone(request.getPhone())
                .role(User.Role.STAGIAIRE)
                .accountStatus(User.AccountStatus.PENDING)
                .build();
        user = userRepository.save(user);

        Encadreur encadreur = null;
        if (request.getEncadreurId() != null) {
            encadreur = encadreurRepository.findById(request.getEncadreurId())
                    .orElseThrow(() -> new RuntimeException("Encadreur non trouvé"));
        }

        Intern intern = Intern.builder()
                .user(user)
                .encadreur(encadreur)
                .school(request.getSchool())
                .department(request.getDepartment())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(Intern.InternshipStatus.PENDING)
                .build();

        intern = internRepository.save(intern);
        return InternDTO.fromEntity(intern);
    }

    @Transactional(readOnly = true)
    public List<InternDTO> getAllInterns() {
        return internRepository.findAll().stream()
                .map(InternDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InternDTO getInternById(Long id) {
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        return InternDTO.fromEntity(intern);
    }

    @Transactional(readOnly = true)
    public List<InternDTO> getInternsByEncadreur(Long encadreurId) {
        return internRepository.findByEncadreurId(encadreurId).stream()
                .map(InternDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InternDTO> getInternsByDepartment(String department) {
        return internRepository.findAll().stream()
                .filter(intern -> intern.getUser().getDepartment() != null &&
                        intern.getUser().getDepartment().equalsIgnoreCase(department))
                .map(InternDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InternDTO> getInternsByStatus(Intern.InternshipStatus status) {
        return internRepository.findByStatus(status).stream()
                .map(InternDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public InternDTO updateIntern(Long id, CreateInternRequest request) {
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));

        User user = intern.getUser();

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setNom(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setPrenom(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        userRepository.save(user);

        if (request.getEncadreurId() != null) {
            Encadreur encadreur = encadreurRepository.findById(request.getEncadreurId())
                    .orElseThrow(() -> new RuntimeException("Encadreur non trouvé"));
            intern.setEncadreur(encadreur);
        }

        if (request.getSchool() != null) {
            intern.setSchool(request.getSchool());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
            userRepository.save(user);
        }
        if (request.getStartDate() != null) {
            intern.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            intern.setEndDate(request.getEndDate());
        }

        intern = internRepository.save(intern);
        return InternDTO.fromEntity(intern);
    }

    @Transactional
    public InternDTO updateInternStatus(Long id, Intern.InternshipStatus status) {
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        intern.setStatus(status);
        intern = internRepository.save(intern);
        return InternDTO.fromEntity(intern);
    }

    @Transactional
    public void deleteIntern(Long id) {
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        userRepository.delete(intern.getUser());
        internRepository.delete(intern);
    }
}
