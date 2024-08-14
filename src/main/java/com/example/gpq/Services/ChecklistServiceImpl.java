package com.example.gpq.Services;

import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.ChecklistRepository;
import com.example.gpq.Repositories.ItemChecklistRepository;
import com.example.gpq.Repositories.PhaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class ChecklistServiceImpl implements IChecklistService {
    private static final Logger logger = LoggerFactory.getLogger(ChecklistServiceImpl.class);

    private final ChecklistRepository checklistRepository;
    private final ItemChecklistRepository itemChecklistRepository;
    private final PhaseRepository phaseRepository;

    @Autowired
    public ChecklistServiceImpl(ChecklistRepository checklistRepository, ItemChecklistRepository itemChecklistRepository, PhaseRepository phaseRepository) {
        this.checklistRepository = checklistRepository;
        this.itemChecklistRepository = itemChecklistRepository;
        this.phaseRepository = phaseRepository;
    }

    @Override
    public Checklist initializeChecklist(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid phase ID"));
        return createChecklist(phase);
    }

    @Override
    public Checklist createChecklist(Phase phase) {
        Checklist checklist = new Checklist();
        checklist.setPhase(phase);

        List<ChecklistItem> items = new ArrayList<>();
        String phaseName = phase.getDescription();

        if (CHECKLIST_ITEMS_MAP.containsKey(phaseName)) {
            for (String itemDescription : CHECKLIST_ITEMS_MAP.get(phaseName)) {
                items.add(new ChecklistItem(itemDescription, checklist));
            }
        }

        checklist.setItems(items);
        checklist = checklistRepository.save(checklist);
        itemChecklistRepository.saveAll(items);
        return checklist;
    }

    @Override
    public void saveChecklist(Checklist checklist) {
        checklistRepository.save(checklist);
    }

    @Override
    public Checklist updateChecklistStatus(Long checklistId, StatusChecklist status, String remarque) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid checklist ID"));
        checklist.setStatus(status);
        checklist.setRemarque(remarque);
        return checklistRepository.save(checklist);
    }

    @Override
    public void updateChecklistItems(Long checklistId, List<ChecklistItem> updatedItems) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid checklist ID"));
        List<ChecklistItem> existingItems = checklist.getItems();

        for (ChecklistItem updatedItem : updatedItems) {
            for (ChecklistItem existingItem : existingItems) {
                if (existingItem.getIdCi().equals(updatedItem.getIdCi())) {
                    // Only update resultat and commentaire fields
                    existingItem.setResultat(updatedItem.getResultat());
                    existingItem.setCommentaire(updatedItem.getCommentaire());
                }
            }
        }
        itemChecklistRepository.saveAll(existingItems);
    }

    @Override
    public Checklist findByPhaseId(Long phaseId) {
        return checklistRepository.findByPhaseIdPh(phaseId).orElse(null);
    }

    private static final Map<String, List<String>> CHECKLIST_ITEMS_MAP = new HashMap<>();
    static {
        CHECKLIST_ITEMS_MAP.put("La conception préliminaire", Arrays.asList(
                "Le niveau de détail de la conception préliminaire permet l'élaboration de la phase suivante de conception",
                "Chaque composant est défini de manière complète, permettant sa vérification",
                "La décomposition modulaire est consistante",
                "Toutes les constantes globales sont paramétrées",
                "Toutes les données sont correctement définies",
                "Les noms de tous les éléments et les structures des données sont significatifs et respectent les règles de nommage",
                "La conception préliminaire prend en compte toutes les exigences de la spécification",
                "L'infrastructure entière du système est couverte par la conception préliminaire",
                "La conception préliminaire prend en considération toutes les exigences de performance et de qualité",
                "La conception préliminaire traite de manière adéquate les considérations d'internationalisation" ,
                "Les mécanismes d'identification des priorités des processus sont décrits",
                "Une analyse de faisabilité et des coûts est effectuée pour s'assurer que les objectifs techniques et de performance seront atteints",
                "La taille mémoire allouée tient compte de l'espace de stockage estimé pour les éléments de la conception (modules, tables, fichiers…)",
                "La conception préliminaire prend en considération toutes les mesures de sécurité",
                "La conception préliminaire prend en considération toutes les contraintes existantes",
                "La conception préliminaire est dépourvue de la redondance non nécessaire",
                "Les considérations de maintenabilité sont prises en compte de manière adéquate",
                "La conception préliminaire est complète, correcte et non ambigüe",
                "Toutes les règles et normes de conception sont respectées",
                "Toutes les interfaces utilisateur suivent les règles applicables au projet",
                "La traçabilité de chaque partie de la conception préliminaire vers la spécification est possible",
                "La matrice de traçabilité des exigences est mise à jour jusqu'à la phase de conception préliminaire",
                "Toutes les interfaces sont claires et bien définies",
                "Toutes les données nécessaires sont transmises au niveau de chaque interface",
                "Un système de données globales est mis à jour ou créé lors de la conception",
                "Toutes les exigences liées aux interfaces définies lors de la phase de spécification sont traitées dans la conception",
                "Les exigences du mode dégradé, de l'auto vérification et de la gestion des échecs sont prises en compte",
                "Un mécanisme de gestion des erreurs est défini",
                "Les cas particuliers et les cas extrêmes sont gérés de manière raisonnable et non destructive",
                "La conception préliminaire précise les composants acquis et réutilisés ",
                "S'il ne s'agit pas de la première revue, les anciennes sections sont revérifiées en s'assurant que la cohérence du document n'est pas affectée par les modifications apportées"
        ));
        CHECKLIST_ITEMS_MAP.put("Manuel d'utilisation", Arrays.asList(
                "Toutes les activités utilisateur sont clairement expliquées",
                "Le manuel est compréhensible du point de vue de l'utilisateur",
                "Le manuel explique clairement comment installer et configurer le système",
                "Le manuel est compréhensible pour une première utilisation du système",
                "Le manuel identifie toutes les possibilités de fonctionnement du système",
                "Le manuel spécifie clairement les caractéristiques de sécurité du système",
                "Le manuel est dépourvu des erreurs de grammaire et d'orthographe",
                "S'il ne s'agit pas de la première revue, les anciennes sections sont revérifiées en s'assurant que la cohérence du document n'est pas affectée par les modifications apportées"
        ));
        CHECKLIST_ITEMS_MAP.put("Tests unitaires", Arrays.asList(
                "Chaque test unitaire est suffisamment documenté de façon à rendre clair l'objectif et l'approche générale du test",
                "Chaque instruction du programme est exécutée au moins une fois durant les tests unitaires",
                "Pour chaque point de décision, chaque branche possible est exécutée au moins une fois",
                "Au niveau de chaque expression booléenne, toutes les combinaisons possibles des prédicats élémentaires sont exécutées au moins une fois",
                "Chaque chemin logique du programme est testé",
                "Au niveau de chaque fonction, les valeurs maximales, minimales et triviales sont testées pour tous les paramètres (si l'ordre des éléments d'entrée a de l'importance, les différents cas doivent être testés)",
                "Au niveau de chaque fonction, la gestion des erreurs suite aux valeurs particulières est vérifiée",
                "Toutes les procédures sont appelées ou utilisées au moins une fois, et aucun bout de code n'est naccessible",
                "Aucune variable n'est redondante ou non utilisée",
                "Toutes les divisions sont vérifiées quant à la division par zéro",
                "Toutes les opérations qui génèrent des erreurs sont testées (racine carrée d'un nombre négatif, …)",
                "Tous les cas possibles des blocs 'If-Else', 'Elseif' et 'Case', incluant les 'Default case' sont testés",
                "Les indices des boucles sont testés quant à leur initialisation avant le début de la boucle",
                "L'absence de manipulation des variables d'indice à l'intérieur ou à la sortie de la boucle est testée",
                "Les arguments d'entrée sont testés quant à leur validité et complétude et l'affectation de toutes les variables de sortie est testée",
                "Toutes les mémoires allouées sont libérées",
                "Les erreurs et les timeouts sont gérés avant d'accéder aux périphériques externes",
                "L'existence d'un fichier est vérifiée avant de tenter d'y accéder",
                "Tous les fichiers et les périphériques sont laissés à l'état correct avant la fin du programme",
                "Chaque unité s'exécute tel que souhaité (des paramètres corrects génèrent des résultats corrects)",
                "Le comportement de chaque unité est correct en cas de paramètres spéciaux",
                "Le test de chaque unité peut être répété et des résultats identiques sont obtenus",
                "Aucune erreur à l'issue des tests de non régression"
        ));
        CHECKLIST_ITEMS_MAP.put("Le Plan d'Integration", Arrays.asList(
                "Tous les documents de référence ont été spécifiés (CDC, spécifications client, besoin client …)",
                "L'environnement de validation est décrit",
                "Les fonctionnalités du produit sont spécifiées",
                "Les tests des interfaces du produit sont définis",
                "La description des tests de validation est claire",
                "Les résultats attendus des tests sont décrits",
                "Les tests définis prennent en compte l'environnement de validation spécifié",
                "Des tests sont définis pour tous les modules de produit",
                "Des tests sont définis pour toutes les fonctionnalités du produit en cohérence avec le besoin du client et les spécifications",
                "S'il ne s'agit pas de la première revue, les anciennes sections sont revérifiées en s'assurant que la cohérence du document n'est pas affectée par les modifications apportées"
        ));
        CHECKLIST_ITEMS_MAP.put("Le Plan de Validation", Arrays.asList(
                "Tous les documents de référence ont été spécifiés (CDC, spécifications client, besoin client …)",
                "L'environnement de validation est décrit",
                "Les fonctionnalités du produit sont spécifiées",
                "Les tests des interfaces du produit sont définis",
                "La description des tests de validation est claire",
                "Les résultats attendus des tests sont décrits",
                "Les tests définis prennent en compte l'environnement de validation spécifié",
                "Des tests sont définis pour tous les modules de produit",
                "Des tests sont définis pour toutes les fonctionnalités du produit en cohérence avec le besoin du client et les spécifications",
                "S'il ne s'agit pas de la première revue, les anciennes sections sont revérifiées en s'assurant que la cohérence du document n'est pas affectée par les modifications apportées"
        ));
        CHECKLIST_ITEMS_MAP.put("Le Plan de Management (PM)", Arrays.asList(
                "Le Plan de Management ( PM) est stocké dans le système documentaire du projet",
                "L'historique de changement du Plan de Management (MP) entre deux versions est décrit",
                "Les documents applicables et de référence sont identifiés",
                "Les documents externes listés dans le Plan de Management dont valables",
                " Les rôles du développement logiciel sont décrits",
                "Les rôles de developpement logiciel sont affectés à des personnes identifiées",
                "Diagramme de l'organisation client est décrit",
                "Les intervenants internes et externes du projet sont identifiés.",
                "Les ressources techniques nécessaire sont identifiées.",
                "Les interfaces entre les personnes impliquées dans le processus de conception et de développement sont maitrisées.",
                "Les connaissances et les compétences des ressources correspond aux exigences du client.",
                "Toutes les compétences requises par le projet ont été toutes identifiées.",
                "Les formations techniques nécessaires sont définis.",
                "Les mécanismes de communication avec le client et les fournisseurs externes, si applicables, sont idéntifiés",
                "Les objectifs qualité liés aux exigences du client sont définis.",
                "Les valeurs des objectifs qualitié sont mentionnés.",
                "Les valeurs des objectifs de performance sont mentionnés",
                "Les ajustements du projet pour tailoriser les procerssus sont définis." ,
                "Le cycle de vie et les options sélectonnées pour tailoriser les processus du projet sont décrits. La stratégie est décrite.",
                "La procèdure pour accepter les work products internes ou externes du projet est décrites . Les critères d'acceptance sont spécifiés.",
                "Les livrables et les jalons projet sont identifiés.",
                "Les outils et les matériels qui vont être utilisés sur le projet sont identifiés",
                "Les règles spécifiques ( conception et codage …), bonne pratiques ,guides et procédures sont identifiés.",
                "Les termes et les conditions de la garantie sont identifiés",
                "Les exigences non fonctionnelles ( reglementaire, qualité , sécurité ….) qui sont applicables au projet sont identifiées .",
                "Les différentes réunions du projet sont identifiées et planifiées.",
                "Les standards et/ou les normes imposés par le client sont identifiés et bien decrits",
                "Les participants à la réunion d'avancement , la fréquence et l'agenda sont identifiés.",
                "Les outils de gestion des actions sont définis.",
                "Les participants à la gestion des risques , les outils et la fréquence sont définis .",
                "Les activités pour gérer les fournisseurs sont définies et planifiées, si applicable.",
                "La gestion des issues du projet est idebntifiée.",
                "Pour un projet spécifique , les valeurs des champs de l'outil de gestion de configuration sont définies",
                "Les activités de vérification sont planifiées",
                "Les méthodes/outils de vérification sont identifiés",
                "Pour chaque action de vérification, le vérificateur et la date initialement planifiée sont précisés",
                "Le plan d'échantillonnage est défini si applicable pour toute ou une partie des activités de vérification",
                "La vérification Software tient compte des stantards et/ou normes imposés par le client",
                "Le Plan de gestion de configuration est défini, complet et tenu à jour",
                "Les outils qui vont être utilisés pour la gestion de configuration sont identifiés",
                "Tous les éléments de configuration (CI) sont identifiés .",
                "La poliique de gestion de configuration est décrite",
                "La gestion des branche est décrite",
                "Les baselines sont définies",
                "La gestion des versions est décrite.",
                "La gestion des données est décrite.",
                "Le mécanisme de sauvegarde et de stockage est défini.",
                "Les activités de revue et d'audit de configuration sont planifiées.",
                "La stratégie qualité est définie.",
                "Les activités qualité sont définies",
                "Les activités de gestion des activités qualité sont définies.",
                "Le mécanisme d'escalade est décrit",
                "S'il ne s'agit pas d'une première revue , est ce que toutes les anciennes sections sont vérifiées pour s'assurer que le docuement n'est impacté par les nouvelles modifications ?"


        ));
        CHECKLIST_ITEMS_MAP.put("Code", Arrays.asList(
                "La matrice de traçabilité des exigences est renseignée jusqu'à la phase de codage",
                "La taille des fichiers est optimisée (nombre de fonctions et nombre de lignes)",
                "La longueur des lignes est optimisée",
                "Les commentaires d'en-tête des fichiers sont présents",
                "Les commentaires d'en-tête des fonctions sont présents",
                "Les algorithmes complexes sont bien commentés",
                "Les commentaires sont explicites et clairs",
                "Les variables sont documentées (unités de mesure, limites, et valeurs possibles)",
                "Le code ne contient pas des instructions commentées",
                "Si c'est le cas, les instructions commentées sont bien justifiées",
                "L'historique des modifications est documenté",
                "Les règles de codage sont définies et respectées",
                "Les règles de nommage sont définies et respectées",
                "Les tests de non régression ont été effectués15La logique de codage est correcte",
                "La sémantique du code est conforme à la conception",
                "Le code est modulaire",
                "Le code est réutilisable",
                "Le code est optimisé du point de vue performance",
                "Le code est dépourvu des avertissements (warnings) ",
                "Le code est dépourvu du code en dur",
                "Les allocations mémoire sont libérées après utilisation",
                "Les noms des variables et des fonctions sont significatifs",
                "Toutes les variables sont déclarées au début des fonctions",
                "Il n'y a pas de variables non utilisées",
                "La portabilité des variables est optimisée (locale, globale, statique…)",
                "Les types des variables sont optimisés",
                "Toutes les variables sont initialisées",
                "L'initialisation des variables est faite suivant des branches",
                "Toutes les branches du code sont exécutées",
                "Toutes les déclarations de boucles se terminent",
                "Tous les indices des boucles ne débordent pas",
                "Le nombre de boucles des instructions d'itération est correct",
                "Les tableaux sont vérifiés quant aux indices hors limites",
                "Les opérations redondantes sont facturées",
                "Les cas par défaut existent dans les instructions de type",
                "Les valeurs aux limites des variables sont gérées",
                "Les types des arguments sont corrects dans les appels des fonctions",
                "Les exceptions sont gérées pour les arguments non valides",
                "Les exceptions sont gérées dans toutes les branches du code",
                "Les exceptions sont gérées dans les retours des fonctions",
                "La division par zéro est vérifiée"

        ));
        CHECKLIST_ITEMS_MAP.put("Spécification", Arrays.asList(
                "Toutes les références internes des exigences sont correctes",
                "Le niveau de détail de description des exigences est suffisant, consistent et convenable" ,
                "Les exigences fournissent une base adéquate pour les travaux de conception",
                "Toutes les interfaces externes logicielles, matérielles et de communication sont décrites",
                "La spécification couvre tous les besoins du client et du système connus",
                "Toutes les exigences sont nécessaires et suffisantes (couvrent tout ce qu'il faut accomplir)",
                "La spécification est documentée de manière qu'une éventuelle modification nécessaire ait un impact minimal sur le reste du document",
                "Chaque modification de la spécification est bien tracée dans l'historique des modifications du document.",
                "L'ensemble des exigences est consistent (pas de conflit ou de duplication)",
                "Chaque exigence est décrite e manière claire, concise et non ambigüe",
                "Chaque exigence est vérifiable par test, démonstration, revue ou analyse",
                "Toutes les exigences font partie du périmètre du projet",
                "Les exigences décrites représentent des résultats de réalisation",
                "Les exigences sont reconnues en tant que tel (par l'utilisation du mot 'doit' par exemple)",
                "Toutes les exigences peuvent être implémentées tenant compte des contraintes connues (coût, délais, compétences…)",
                "La description des exigences s'arrête bien au niveau de la spécification, pas de solutions de conception ou d'implémentation fournies",
                "Le périmètre du système est clairement défini",
                "Les informations manquantes relatives à une exigence donnée sont bien marquées en tant que tel (avec la note 'à compléter' par exemple)",
                "Chaque exigence décrite est dépourvu des erreurs de syntaxe et de grammaire/orthographe",
                "Les objectifs de performance sont proprement spécifiés",
                "Toutes les mesures de sécurité sont proprement spécifiées",
                "Chaque exigence est identifiée de manière correcte et unique",
                "La traçabilité de chaque exigence fonctionnelle au niveau supérieur (exigence système ou exigence client) est possible",
                "La matrice de traçabilité contient exactement le même nombre d'exigences que dans le document de spécification",
                "Les références des exigences définies dans le document de spécification sont les mêmes utilisées dans la matrice des exigences",
                "Le document de spécification est conforme au template défini",
                "Chaque acronyme utilisé est défini lors de la première utilisation et repris dans le paragraphe de la liste des acronymes",
                "Le processus d'approbation des changements d'exigences est clairement décrit dans le plan de management du projet",
                "La matrice de traçabilité des exigences est mise à jour selon les changements d'exigence",
                "S'il ne s'agit pas de la première revue, les anciennes sections sont revérifiées en s'assurant que la cohérence du document n'est pas affectée par les modifications apportées"
        ));
        CHECKLIST_ITEMS_MAP.put("Conception détaillée", Arrays.asList(
                "Le document de conception détaillée est enregistré dans l'espace projet",
                "L'objectif et le périmètre du document sont spécifiés",
                "La liste de diffusion est correcte",
                "Les documents de référence sont identifiés",
                "L'historique des modifications est maintenu avec des descriptions claires et explicites des modifications apportées entre deux éditions du document",
                "La table des matières est cohérente avec le contenu du document",
                "La conception détaillée est présentée selon un modèle graphique (ex. UML)",
                "La configuration des composants est décrite, lorsque c'est applicable",
                "La vue statique est décrite",
                "La vue dynamique est décrite",
                "Un diagramme d'environnement ou un lien vers sa représentation graphique existe",
                "Un diagramme des classes ou un lien vers sa représentation graphique existe",
                "Un diagramme d'activités ou un lien vers sa représentation graphique existe",
                "Un diagramme des états ou un lien vers sa représentation graphique existe",
                "Un diagramme des séquences ou un lien vers sa représentation graphique existe",
                "Toutes les suppositions et les parties incomplètes sont identifiées",
                "La structure des composants et des sous composants est décrite",
                "L'initialisation des composants est décrite",
                "Toutes les données définies sont utilisées",
                "Les noms des éléments de données et leurs types sont conformes au dictionnaire des données du projet",
                "Les cas par défaut utilisés sont décrits",
                "La conception détaillée est une implémentation complète de la conception préliminaire",
                "Les spécifications externes de chaque module sont complètes",
                "Les procédures de 'sleep' et 'wake up' de chaque composant sont décrites",
                "Toutes les machines d'état sont décrites",
                "Les erreurs liées au temps d'exécution sont décrites",
                "Les codes de diagnostic des perturbations sont décrits, si applicable",
                "Les points critiques d'utilisation sont définis",
                "Les points de conception répondant particulièrement aux contraintes de temps sont décrits",
                "Les différentes variantes des composants sont décrites en cas de plusieurs lignes de production",
                "Les différents modes des composants, incluant la production, sont décrits si applicable",
                "Le rôle de chaque composant est décrit",
                "La conception détaillée est une implémentation pertinente de la conception générale",
                "Les interfaces de chaque composant sont décrites",
                "Le diagramme d'environnement montre tous les composants externes liés à chaque composant du produit avec description des éléments d'entrée et de sortie",
                "Les techniques de numérotation ont été analysées quant à leur pertinence",
                "Les temps critiques ont été analysés (les aspects de conception spécifiques pour répondre aux contraintes de temps sont décrites)",
                "La mémoire allouée lors de la conception préliminaire est détaillée et mise à jour si nécessaire",
                "Les aspects de maitenabilité sont clairement décrits",
                "Les conditions de fin de boucles sont réalisables",
                "Les éléments d'entrée et de sortie des composants sont définis",
                "Les interactions entre composants sont définies",
                "Le comportement de chaque composant est défini",
                "Les ressources utilisées par le composant sont définies",
                "Les indications de débogage sont clairement définies et justifiées",
                "Les règles et les contraintes de conception fournies par la conception préliminaire sont respectées",
                "La conception détaillée respecte les règles et les conventions de nommage pour le développement logiciel",
                "La traçabilité de chaque partie de la conception détaillée vers la conception préliminaire est possible",
                "La matrice de traçabilité des exigences est mise à jour jusqu'à la phase de conception détaillée",
                "Les conditions d'erreur sont gérées de manière non destructive",
                "Des actions correctives peuvent être engagées par le module en cas d'erreur de fonctionnement",
                "Les cas particuliers et les cas extrêmes sont gérés de manière raisonnable et non destructive",
                "La conception préliminaire précise les composants acquis et réutilisés",
                "S'il ne s'agit pas de la première revue, les anciennes sections sont revérifiées en s'assurant que la cohérence du document n'est pas affectée par les modifications apportées"
        ));
        // Ajoutez ici d'autres phases et leurs items de checklist
    }
}