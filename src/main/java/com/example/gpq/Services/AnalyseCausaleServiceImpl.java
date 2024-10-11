package com.example.gpq.Services;

import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class AnalyseCausaleServiceImpl implements IAnalyseCausaleService {
    @Autowired
    private PlanActionServiceImpl planActionService;
    @Autowired
    private AnalyseCausaleRepository analyseCausaleRepository;
    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private PourquoiRepository pourquoiRepository;

    @Autowired
    private CauseIshikawaRepository causeIshikawaRepository;
    @Autowired
    private PlanActionRepository planActionRepository;
    @Override
    public void saveAnalyseCausale(AnalyseCausale analyseCausale) {
        // Créer le plan d'action
        PlanAction planAction = new PlanAction();
        planAction.setAnalyseCausale(analyseCausale);
        analyseCausale.setPlanAction(planAction);

        // Ajouter les actions à partir des 5 Pourquoi
        if (analyseCausale.getMethodeAnalyse() == MethodeAnalyse.FIVE_WHYS) {
            List<Action> actions = analyseCausale.getCinqPourquoi().stream()
                    .map(pourquoi -> {
                        Action action = new Action();
                        action.setDescription(pourquoi.getAction()); // Action associée à chaque8/// ourquoi
                        action.setType(null); // Ajoutez votre logique pour définir le type, si nécessaire
                        // Initialiser les autres champs pour mise à jour ultérieure
                        action.setResponsable(null);
                        action.setDatePlanification(null);
                        action.setDateRealisation(null);
                        action.setCritereEfficacite(null);
                        action.setEfficace(null);
                        action.setCommentaire(null);
                        action.setPlanAction(planAction); // Associe l'action au plan d'action
                        return action;
                    })
                    .collect(Collectors.toList());
            planAction.setActions(actions);
        } else if (analyseCausale.getMethodeAnalyse() == MethodeAnalyse.ISHIKAWA) {
            List<Action> actions = analyseCausale.getCausesIshikawa().stream()
                    .map(causeIshikawa -> {
                        Action action = new Action();
                        action.setDescription(causeIshikawa.getAction()); // Action associée à chaque8/// ourquoi
                        action.setType(null); // Ajoutez votre logique pour définir le type, si nécessaire
                        // Initialiser les autres champs pour mise à jour ultérieure
                        action.setResponsable(null);
                        action.setDatePlanification(null);
                        action.setDateRealisation(null);
                        action.setCritereEfficacite(null);
                        action.setEfficace(null);
                        action.setCommentaire(null);
                        action.setPlanAction(planAction); // Associe l'action au plan d'action
                        return action;
                    })
                    .collect(Collectors.toList());
            planAction.setActions(actions);
        }

        analyseCausaleRepository.save(analyseCausale);
    }

    @Override
    public Checklist getChecklistById(Long id) {
        return checklistRepository.findById(id).orElse(null);
    }
    @Override
    public AnalyseCausale getAnalyseCausaleByChecklist(Long checklistId) {
        return analyseCausaleRepository.findByChecklistIdCh(checklistId)
                .orElseThrow(() -> new NoSuchElementException("Analyse causale not found for checklist id " + checklistId));
    }

    @Override
    public AnalyseCausale getAnalyseCausaleById(Long id) {
        return analyseCausaleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Analyse causale not found with id " + id));
    }

    @Override
    public List<AnalyseCausale> getAllAnalysesCausales() {
        return analyseCausaleRepository.findAll();
    }

    @Override
    public Pourquoi addPourquoi(Long analyseCausaleId, Pourquoi pourquoi) {
        AnalyseCausale analyseCausale = getAnalyseCausaleById(analyseCausaleId);
        pourquoi.setAnalyseCausale(analyseCausale);
        return pourquoiRepository.save(pourquoi);
    }

    @Override
    public CauseIshikawa addCauseIshikawa(Long analyseCausaleId, CauseIshikawa causeIshikawa) {
        AnalyseCausale analyseCausale = getAnalyseCausaleById(analyseCausaleId);
        causeIshikawa.setAnalyseCausale(analyseCausale);
        return causeIshikawaRepository.save(causeIshikawa);
    }

    @Override
    public void deleteAnalyseCausale(Long id) {
        analyseCausaleRepository.deleteById(id);
    }
}