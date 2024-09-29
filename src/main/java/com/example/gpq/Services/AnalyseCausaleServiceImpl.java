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
    private AnalyseCausaleRepository analyseCausaleRepository;
    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private PourquoiRepository pourquoiRepository;

    @Autowired
    private CauseIshikawaRepository causeIshikawaRepository;
    @Autowired
    private PlanActionRepository planActionRepository;

    public void saveAnalyseCausale(AnalyseCausale analyseCausale) {
        // Sauvegarder l'analyse causale
        AnalyseCausale savedAnalyseCausale = analyseCausaleRepository.save(analyseCausale);

        // Créer un PlanAction associé
        PlanAction planAction = new PlanAction();
        planAction.setAnalyseCausale(savedAnalyseCausale);

        // Ajouter les actions au plan d'action
        List<Action> actions = analyseCausale.getCausesIshikawa().stream()
                .map(cause -> {
                    Action action = new Action();
                    action.setDescription(cause.getAction()); // Description de l'action est le champ "action" dans la cause
                    action.setType(null); // Utilisation de l'énumération CategorieIshikawa
                    // Laisser les autres champs pour mise à jour ultérieure
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

        // Sauvegarder le plan d'action avec les actions associées
        planActionRepository.save(planAction);
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
