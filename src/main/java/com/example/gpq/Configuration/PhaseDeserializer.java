package com.example.gpq.Configuration;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.EtatPhase;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.StatusChecklist;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

public class PhaseDeserializer extends JsonDeserializer<Phase> {
    @Override
    public Phase deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        Phase phase = new Phase();

        // Assigner les valeurs des champs
        JsonNode idNode = node.get("idPh");
        if (idNode != null && !idNode.isNull()) {
            phase.setIdPh(idNode.asLong());
        }

        JsonNode descriptionNode = node.get("description");
        if (descriptionNode != null && !descriptionNode.isNull()) {
            phase.setDescription(descriptionNode.asText());
        }



        JsonNode objectifsNode = node.get("objectifs");
        if (objectifsNode != null && !objectifsNode.isNull()) {
            phase.setObjectifs(objectifsNode.asText());
        }

        JsonNode plannedStartDateNode = node.get("plannedStartDate");
        if (plannedStartDateNode != null && !plannedStartDateNode.isNull()) {
            phase.setPlannedStartDate(mapper.treeToValue(plannedStartDateNode, Date.class));
        }

        JsonNode plannedEndDateNode = node.get("plannedEndDate");
        if (plannedEndDateNode != null && !plannedEndDateNode.isNull()) {
            phase.setPlannedEndDate(mapper.treeToValue(plannedEndDateNode, Date.class));
        }

        JsonNode effectiveStartDateNode = node.get("effectiveStartDate");
        if (effectiveStartDateNode != null && !effectiveStartDateNode.isNull()) {
            phase.setEffectiveStartDate(mapper.treeToValue(effectiveStartDateNode, Date.class));
        }

        JsonNode effectiveEndDateNode = node.get("effectiveEndDate");
        if (effectiveEndDateNode != null && !effectiveEndDateNode.isNull()) {
            phase.setEffectiveEndDate(mapper.treeToValue(effectiveEndDateNode, Date.class));
        }


        return phase;
    }
}
