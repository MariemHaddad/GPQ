package com.example.gpq.Services;

import com.example.gpq.Entities.AnalyseCausale;
import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.Pourquoi;
import com.example.gpq.Entities.CauseIshikawa;

import java.util.List;

public interface IAnalyseCausaleService {
    void saveAnalyseCausale(AnalyseCausale analyseCausale);
    Checklist getChecklistById(Long id);
    AnalyseCausale getAnalyseCausaleById(Long id);
    List<AnalyseCausale> getAllAnalysesCausales();
    Pourquoi addPourquoi(Long analyseCausaleId, Pourquoi pourquoi);
    CauseIshikawa addCauseIshikawa(Long analyseCausaleId, CauseIshikawa causeIshikawa);
    void deleteAnalyseCausale(Long id); AnalyseCausale getAnalyseCausaleByChecklist(Long checklistId);
}