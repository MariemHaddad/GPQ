package com.example.gpq.Services;

import com.example.gpq.Entities.AnalyseCausale;
import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.Pourquoi;
import com.example.gpq.Entities.CauseIshikawa;
import com.example.gpq.Repositories.AnalyseCausaleRepository;
import com.example.gpq.Repositories.ChecklistRepository;
import com.example.gpq.Repositories.PourquoiRepository;
import com.example.gpq.Repositories.CauseIshikawaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;


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

    @Override
    @Transactional
    public AnalyseCausale saveAnalyseCausale(AnalyseCausale analyseCausale) {
        return analyseCausaleRepository.save(analyseCausale);
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
