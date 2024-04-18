package com.example.gpq.Controllers;

import com.example.gpq.Entities.Activite;
import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import com.example.gpq.Services.IActiviteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activites")
public class ActiviteController {
    @Autowired
private IActiviteService activiteService;

    @PostMapping("/ajouter")
    public ResponseEntity<String> ajouterActivite(@RequestBody Activite activite, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        if (user != null && user.getRole() == Role.Admin) {
            activiteService.ajouterActivite(activite);
            return ResponseEntity.ok("Activité ajoutée avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Seuls les utilisateurs avec le rôle d'Admin peuvent ajouter des activités.");
        }
    }
}

//modif role juste l'admin
//user ne peux modifier que son MDP
//chaque user possède son propre activité
//role: responsable qualité a l'accés à plusieurs activité  avec l'affectation de l'admin
//activité: qualité
//crud: activité