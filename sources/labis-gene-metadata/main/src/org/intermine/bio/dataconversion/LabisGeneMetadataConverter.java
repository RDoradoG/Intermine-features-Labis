package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Functionallity of Labis Gene Metadata Converter  
 * 
 * @author Rodrigo Dorado
 */

import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.Random;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;

public class LabisGeneMetadataConverter extends BioFileConverter
{
    private static final String DATASET_TITLE                         = "LabisGeneMetadata";
    private static final String DATA_SOURCE_NAME                      = "labis-gene-metadata";
    private static final String LOGFILE                               = "/home/rdorado/logs/MyJavaLog.log"; //Change to another file that already exists

    private ErrorLog loggable;

    private static Logger LOGGER                                      = null;

    private Map<String, Gene> geneItems                               = new HashMap<String, Gene>();
    private Map<String, ExperimentDescription> experimentItems        = new HashMap<String, ExperimentDescription>();
    private Map<String, ExpressionTypeDiccionary> typeDiccionaryItmes = new HashMap<String, ExpressionTypeDiccionary>();
    private Map<String, ExperimentConditions> verifyConditionItems    = new HashMap<String, ExperimentConditions>();
    private Map<String, Publication> pubItems                         = new HashMap<String, Publication>();
    private Map<String, String> familiesItems                         = new HashMap<String, String>();
    private Map<String, GeneFamilies> newFamiliesItems                = new HashMap<String, GeneFamilies>();
    private Map<String, String> familiesGeneItems                     = new HashMap<String, String>();
    private Map<String, TypeGeneFamily> geneFamiliesType              = new HashMap<String, TypeGeneFamily>();
    private Map<String, Transcript> transcriptsDefflines              = new HashMap<String, Transcript>();
    private Map<String, Sequence> sequences                           = new HashMap<String, Sequence>();
    private Map<String, String> sequencesProteins                     = new HashMap<String, String>();
    private Map<String, GenomicProtein> genomicProteins               = new HashMap<String, GenomicProtein>();
    private Map<String, Organism> organisms                           = new HashMap<String, Organism>();
    private Map<String, Ortholog> orthologs                           = new HashMap<String, Ortholog>();

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public LabisGeneMetadataConverter(ItemWriter writer, Model model) throws IOException {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
        loggable = new ErrorLog(LOGFILE);
    }

    /**
     *
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
        File currentFile = getCurrentFile();
        String fileName  = currentFile.getName();

        loggable.setActualFile(fileName);

        loggable.makeLog("Start (" + fileName + ")");

        Iterator<?> tsvIter;
        try {
            tsvIter = FormattedTextParser.parseTabDelimitedReader(reader);
        } catch (Exception e) {
            throw new BuildException("cannot parse file: " + getCurrentFile(), e);
        }

        int opt = getOptionFileName(fileName);

        switch(opt) {
            case 1:
            case 2:
                OrthologsExecute(tsvIter);
            break;

            case 3:
                ExperimentDescriptionExecute(tsvIter);
            break;

            case 4:
                ExpressionTypeDiccionaryExecute(tsvIter);
            break;

            case 5:
                ExperimentDescriptionColumnsExecute(tsvIter);
            break;

            case 6:
                ExpressionValuesExecute(tsvIter);
            break;

            case 7:
                geneFamiliesTypeExecute(tsvIter);
            break;

            case 8:
                geneFamilyExcute(tsvIter);
            break;
            
            case 9:
                GenomicProteinsSequenceExecute(tsvIter);
            break;

            case 10:
                String taxonId = getTaxonIdFromFile(fileName);
                GenomicProteinsGeneExecute(tsvIter, taxonId);
            break;
                
            case 11:
                transcriptExecute(tsvIter);
            break;
            
            default:
                loggable.makeErrorLog("Name of the file invalid.");
            break;
        }

        loggable.makeLog("End (" + fileName + ")");
    }

    /**
     *
     * @param taxonIds the taxon ids of the organisms
     */
    public void setMetadataOrganisms(String taxonIds) throws Exception {
        String[] aTaxonIds = taxonIds.split(",");
        for (int i = 0; i < aTaxonIds.length; i++) {
            setOrganism(aTaxonIds[i]);
        }
    }

    public void setOrganism(String taxonId) throws Exception {
        if (!organisms.containsKey(taxonId)) {
            Organism organism = new Organism(taxonId);
            Item score        = createItem(organism.getClassName());
            score             = organism.save(score);
            store(score);
            organism.setUniqueId(score.getIdentifier());
            organisms.put(taxonId, organism);
        }
    }

    private Organism getAOrganism(String taxonId) throws Exception {
        if (organisms.containsKey(taxonId)) {
            return organisms.get(taxonId);
        } else {
            loggable.makeErrorLog("Taxon id: " + taxonId + " does not exists in the data base. Please make sure that all the taxon id are setted in the 'metadata.organisms' prperty in the xml file.");
            return new Organism("");
        }
    }

    private String getTaxonIdFromFile(String fileName) throws Exception {
        String[] partsFileName = fileName.split("-");
        return partsFileName[2];
    }

    private int getOptionFileName(String fileName) {
        if (fileName.indexOf("A-Orthologs-") == 0) {
            return 1;
        }

        if (fileName.indexOf("B-Orthologs-UnassignedGenes-") == 0) {
            return 2;
        }

        if (fileName.indexOf("C-ExperimentDescription-") == 0) {
            return 3;
        }

        if (fileName.indexOf("D-ExpressionTypeDiccionary-") == 0) {
            return 4;
        }

        if (fileName.indexOf("E-ExperimentDescriptionColumns-") == 0) {
            return 5;
        }

        if (fileName.indexOf("F-ExpressionValues-") == 0) {
            return 6;
        }

        if (fileName.indexOf("G-geneFamiliesType-") == 0) {
            return 7;
        }

        if (fileName.indexOf("H-geneFamily-") == 0) {
            return 8;
        }
        
        if (fileName.indexOf("I-GenomicProteins-Proteins-") == 0) {
            return 9;
        }

        if (fileName.indexOf("J-GenomicProteinsGene-") == 0) {
            return 10;
        }

        if (fileName.indexOf("K-Transcript-") == 0) {
            return 11;
        }
        
        return 0;
    }

    public void saveGeneFamiliesType(String type, String dbLink) throws ObjectStoreException, Exception {
        if (!geneFamiliesType.containsKey(type)) {
            TypeGeneFamily typeGeneFamily = new TypeGeneFamily();
            typeGeneFamily.setName(type);
            typeGeneFamily.setDbLink(dbLink);
            Item score = createItem(typeGeneFamily.getClassName());
            score      = typeGeneFamily.save(score);
            store(score);
            typeGeneFamily.setUniqueId(score.getIdentifier());
            geneFamiliesType.put(type, typeGeneFamily);
        }
    }

    public void saveFamilyGene(String family, String gene, String description) throws ObjectStoreException, Exception {
        GeneFamiliesGenes geneFamiliesGenes = new GeneFamiliesGenes();
        geneFamiliesGenes.setDescription(description);
        geneFamiliesGenes.setGeneFamily(newFamiliesItems.get(family));
        geneFamiliesGenes.setGene(geneItems.get(gene));
        Item score = createItem(geneFamiliesGenes.getClassName());
        score      = geneFamiliesGenes.save(score);
        store(score);
        geneFamiliesGenes.setUniqueId(score.getIdentifier());
    }

    private void saveFamily(String family, String type) throws ObjectStoreException, Exception {
        if (!newFamiliesItems.containsKey(family)) {
            GeneFamilies geneFamilies     = new GeneFamilies();
            TypeGeneFamily typeGeneFamily = geneFamiliesType.get(type);
            geneFamilies.setName(family);
            geneFamilies.setType(geneFamiliesType.get(type));
            Item score = createItem(geneFamilies.getClassName());
            score      = geneFamilies.save(score);
            store(score);
            geneFamilies.setUniqueId(score.getIdentifier());
            newFamiliesItems.put(family, geneFamilies);
        }
    }

    private void saveExpressionValues(String expressionValue, String condition, String primaryIdGenes, String primaryIdExperiment, String typeDiccionary) throws ObjectStoreException, Exception {
        ExpressionValues expressionValues = new ExpressionValues();
        expressionValues.setExpressionValue(expressionValue);
        expressionValues.setGene(geneItems.get(primaryIdGenes));      
        if (verifyCondition(condition)) {
            expressionValues.setExperimentConditions(verifyConditionItems.get(condition));
        }
        if (verifyExperiment(primaryIdExperiment)) {
            expressionValues.setExperimentDescription(experimentItems.get(primaryIdExperiment));
        }
        if (verifyTypeDicionary(typeDiccionary)) {
            expressionValues.setExpressionTypeDiccionary(typeDiccionaryItmes.get(typeDiccionary));
        }
        Item score = createItem(expressionValues.getClassName());
        score      = expressionValues.save(score);
        store(score);
        expressionValues.setUniqueId(score.getIdentifier());
    }

    private void saveExperimentDescription(String name, Publication pubMedId, String Accession, String experimentTitle, String Description) throws ObjectStoreException, Exception {
        ExperimentDescription experimentDescription = new ExperimentDescription();
        experimentDescription.setName(name);
        experimentDescription.setDescription(Description);
        experimentDescription.setExperimentTitle(experimentTitle);
        experimentDescription.setAccession(Accession);
        experimentDescription.setPublication(pubMedId);
        Item score = createItem(experimentDescription.getClassName());
        score      = experimentDescription.save(score);
        store(score);
        experimentDescription.setUniqueId(score.getIdentifier());
        experimentItems.put(name, experimentDescription);
    }

    private void saveExpressionType(String name) throws ObjectStoreException, Exception {
        ExpressionTypeDiccionary expressionTypeDiccionary = new ExpressionTypeDiccionary();
        expressionTypeDiccionary.setName(name);
        Item score = createItem(expressionTypeDiccionary.getClassName());
        score      = expressionTypeDiccionary.save(score);
        store(score);
        expressionTypeDiccionary.setUniqueId(score.getIdentifier());
        typeDiccionaryItmes.put(name, expressionTypeDiccionary);
    }

    private void saveExperimentDescriptionColumns(String name, String description, String instrument, String strategy, String source, String selection, String layout, String primaryIdExperiment, String srrNumber) throws ObjectStoreException, Exception {
        ExperimentConditions experimentConditions = new ExperimentConditions();
        experimentConditions.setName(name);
        experimentConditions.setDescription(description);
        experimentConditions.setInstrument(instrument);
        experimentConditions.setStrategy(strategy);
        experimentConditions.setSource(source);
        experimentConditions.setSelection(selection);
        experimentConditions.setLayout(layout);
        experimentConditions.setSrrNumber(srrNumber);
        Item score = createItem(experimentConditions.getClassName());
        score      = experimentConditions.save(score);
        store(score);
        experimentConditions.setUniqueId(score.getIdentifier());
        verifyConditionItems.put(name, experimentConditions);
    }

    private void saveTranscript(String transcriptName, String deffline)  throws ObjectStoreException, Exception {
        if (!transcriptsDefflines.containsKey(transcriptName)) {
            Transcript transcript = new Transcript();
            transcript.setPrimaryIdentifier(transcriptName);
            transcript.setDescription(deffline);
            Item score = createItem(transcript.getClassName());
            score      = transcript.save(score);
            store(score);
            transcript.setUniqueId(score.getIdentifier());
            transcriptsDefflines.put(transcriptName, transcript);
        }
    }

    private void saveSequence(String residues, String genomicProtein)  throws ObjectStoreException, Exception {
        Sequence sequence = new Sequence();
        sequence.setResidues(residues);
        String md5        = sequence.getMd5checksum();
        if (!sequences.containsKey(md5)) {
            sequence.setLength();
            Item score = createItem(sequence.getClassName());
            score      = sequence.save(score);
            store(score);
            sequence.setUniqueId(score.getIdentifier());
            sequences.put(md5, sequence);
        }
        if (!sequencesProteins.containsKey(genomicProtein)) {
            sequencesProteins.put(genomicProtein, md5);
        }
    }

    private void saveGenomicProteins(String protein, String[] primaryIdGenes, Organism organism) throws ObjectStoreException, Exception {
        if (!genomicProteins.containsKey(protein)) {
            GenomicProtein genomicProtein = new GenomicProtein();
            if (sequencesProteins.containsKey(protein)) {
                String md5 = sequencesProteins.get(protein);
                if (sequences.containsKey(md5)) {
                    genomicProtein.setSequence(sequences.get(md5));
                }
            }
            for (int i = 0; i < primaryIdGenes.length; i++) {
                genomicProtein.addGene(geneItems.get(primaryIdGenes[i]));
            }
            genomicProtein.setOrganism(organism);
            genomicProtein.setPrimaryIdentifier(protein);
            genomicProtein.setMolecularWeight();
            genomicProtein.setLength();
            Item score = createItem(genomicProtein.getClassName());
            score      = genomicProtein.save(score);
            store(score);
            genomicProtein.setUniqueId(score.getIdentifier());
            genomicProteins.put(protein, genomicProtein);
        }
    }

    private void createBioEntityWithOrthoog(String primaryId, Organism organism, Ortholog ortholog) throws ObjectStoreException, Exception {
        if (!geneItems.containsKey(primaryId)) {
            Gene gene  = new Gene();
            gene.setPrimaryIdentifier(primaryId);
            gene.setOrganism(organism);
            gene.setOrtholog(ortholog);
            Item score = createItem(gene.getClassName());
            score      = gene.save(score);
            store(score);
            gene.setUniqueId(score.getIdentifier());
            geneItems.put(primaryId, gene);
        }
    }

    public void saveOrtholog(String name) throws Exception {
        if (!orthologs.containsKey(name)) {
            Ortholog ortholog = new Ortholog(name);
            Item score        = createItem(ortholog.getClassName());
            score             = ortholog.save(score);
            store(score);
            ortholog.setUniqueId(score.getIdentifier());
            orthologs.put(name, ortholog);
        }
    }

    private void geneFamilyExcute(Iterator tsvIter) throws ObjectStoreException, Exception {
        int end = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line      = (String[]) tsvIter.next();
                String gene        = (line.length > 0) ? line[0] : "-";
                String type        = (line.length > 1) ? line[1] : "-";
                String family      = (line.length > 2) ? line[2] : "-";
                String description = (line.length > 3) ? line[3] : "-";
                String[] families  = family.split(",");
                for( int i = 0; i < families.length; i++) {
                    saveFamily(families[i], type);
                    createBioEntity(gene);
                    saveFamilyGene(families[i], gene, description);
                }
            }
        }
    }

    private void geneFamiliesTypeExecute(Iterator tsvIter) throws Exception {
       if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            }
        }

        while (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            String type   = (line.length > 0) ? line[0] : "";
            String dbLink = (line.length > 1) ? line[1] : "";
            if (StringUtils.isBlank(type) == false) {
                saveGeneFamiliesType(type, dbLink);
            }
        }
    }

    private void ExperimentDescriptionColumnsExecute(Iterator tsvIter) throws Exception {
        int end = 0;
        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line              = (String[]) tsvIter.next();
                String primaryIdExperiment = (line.length > 0) ? line[0] : "-";
                String name                = (line.length > 1) ? line[1] : "-";
                String description         = (line.length > 2) ? line[2] : "-";
                String instrument          = (line.length > 3) ? line[3] : "-";
                String strategy            = (line.length > 4) ? line[4] : "-";
                String source              = (line.length > 5) ? line[5] : "-";
                String selection           = (line.length > 6) ? line[6] : "-";
                String layout              = (line.length > 7) ? line[7] : "-";
                String srrNumber           = (line.length > 8) ? line[8] : "-";
                if (StringUtils.isBlank(name) == false) {
                    saveExperimentDescriptionColumns(name, description, instrument, strategy, source, selection, layout, primaryIdExperiment, srrNumber);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExpressionTypeDiccionaryExecute(Iterator tsvIter) throws Exception {
       int end = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line = (String[]) tsvIter.next();
                String name   = (line.length > 0) ? line[0] : "";
                if (StringUtils.isBlank(name) == false) {
                    saveExpressionType(name);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void OrthologsExecute(Iterator tsvIter) throws Exception {
        int end                    = 0;
        Organism[] organisms_order = null;
        int number_organism        = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (line.length > 0) {
                    end++;
                }
                number_organism = line.length - 1;
                organisms_order = new Organism[number_organism];
                for (int i = 0; i < number_organism; i++) {
                    organisms_order[i] = this.getAOrganism(line[i + 1]);
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line        = (String[]) tsvIter.next();
                String ortholog_name = (line.length > 0) ? line[0] : "-";
                saveOrtholog(ortholog_name);
                Ortholog ortholog    = orthologs.get(ortholog_name);
                for (int i = 0; i < number_organism; i++) {
                    String genes = (line.length > i + 1) ? line[i + 1] : "";
                    if (!genes.isEmpty()) {
                        String[] a_genes = genes.split(",");
                        for (int j = 0; j < a_genes.length; j++) {
                            createBioEntityWithOrthoog(a_genes[j], organisms_order[i], ortholog);
                        }
                    }
                }
            }
        }
    }

    private void ExperimentDescriptionExecute(Iterator tsvIter) throws Exception {
        int end = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line          = (String[]) tsvIter.next();
                String name            = (line.length > 0) ? line[0] : "-";
                String PubId           = (line.length > 1) ? line[1] : "-";
                String Accession       = (line.length > 2) ? line[2] : "-";
                String experimentTitle = (line.length > 3) ? line[3] : "-";
                String Description     = (line.length > 4) ? line[4] : "-";
                Publication pubMedId   = createPublication(PubId);
                if (StringUtils.isBlank(name) == false) {
                    saveExperimentDescription(name, pubMedId, Accession, experimentTitle, Description);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExpressionValuesExecute(Iterator tsvIter) throws Exception {
        String [] heads = null;
        int end         = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                for (int i = 0; i < line.length; i++) {
                    if (StringUtils.isBlank(line[i])) {
                        break;
                    }
                    end++;
                }
                heads = new String[end];
                System.arraycopy(line, 0, heads, 0, end);
            }
        } else {
           loggable.makeErrorLog("File Empty of data.");
        }

        while (tsvIter.hasNext()) {
            String[] line              = (String[]) tsvIter.next();
            int sizeLine               = line.length;
            String primaryIdGenes      = line[0];
            String primaryIdExperiment = line[1];
            String typeDiccionary      = line[2];
            createBioEntity(primaryIdGenes);
            for (int i = 3; i < end; i++) {
                if (i >= sizeLine) {
                    break;
                }
                saveExpressionValues(line[i], heads[i], primaryIdGenes, primaryIdExperiment, typeDiccionary);
            }
        }
    }

    private void transcriptExecute(Iterator tsvIter) throws Exception {
        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            }
        }

        while (tsvIter.hasNext()) {
            String[] line     = (String[]) tsvIter.next();
            String transcript = (line.length > 0) ? line[0] : "";
            String deffline   = (line.length > 1) ? line[1] : "";
            if (StringUtils.isBlank(transcript) == false) {
                saveTranscript(transcript, deffline);
            }
        }
    }

    private void GenomicProteinsSequenceExecute(Iterator tsvIter) throws Exception {
        Boolean id     = true;
        String protein = "";
        String seq     = "";
        while (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line[0].indexOf(">") == 0) {
                if (!protein.isEmpty()) {
                    saveSequence(seq, protein.trim());
                    seq     = "";
                    protein = "";
                }
                String[] parts = line[0].split(" ");
                protein        = parts[0].replace(">", "");
            } else {
                seq = seq.concat(line[0]);
            }
        }

        if (!protein.isEmpty()) {
            saveSequence(seq, protein.trim());
            seq     = "";
            protein = "";
        }
    }

    private void GenomicProteinsGeneExecute(Iterator tsvIter, String taxonId) throws Exception {
        Organism organism          = this.getAOrganism(taxonId);
        String organism_identifier = organism.getUniqueId();
        if (organism_identifier != null && organism_identifier.length() > 0) {
            if (tsvIter.hasNext()) {
                String[] line = (String[]) tsvIter.next();
                if (line.length < 1) {
                    loggable.makeErrorLog("No data.");
                }
            }

            while (tsvIter.hasNext()) {
                String[] line           = (String[]) tsvIter.next();
                String protein          = (line.length > 0) ? line[0] : "";
                String idsGenes         = (line.length > 1) ? line[1] : "";
                String[] primaryIdGenes = idsGenes.split(",");
                createBioEntity(primaryIdGenes);
                saveGenomicProteins(protein.trim(), primaryIdGenes, organism);
            }
        }

    }

    private void createBioEntity(String[] primaryIds) throws Exception {
        for (int i = 0; i < primaryIds.length; i++) {
            createBioEntity(primaryIds[i]);
        }
    }

    private void createBioEntity(String primaryId) throws ObjectStoreException, Exception {
        if (!geneItems.containsKey(primaryId)) {
            Gene gene  = new Gene();
            gene.setPrimaryIdentifier(primaryId);
            Item score = createItem(gene.getClassName());
            score      = gene.save(score);
            store(score);
            gene.setUniqueId(score.getIdentifier());
            geneItems.put(primaryId, gene);
        }
    }

    private Publication createPublication(String PubId) throws ObjectStoreException, Exception {
        Publication publication = null;
        if (!pubItems.containsKey(PubId)) {
            publication = new Publication();
            publication.setPubMedId(PubId);
            Item score  = createItem(publication.getClassName());
            score       = publication.save(score);
            store(score);
            publication.setUniqueId(score.getIdentifier());
            pubItems.put(PubId, publication);
        } else {
            publication = pubItems.get(PubId);;
        }
        return publication;
    }

    private boolean verifyExperiment(String name) throws ObjectStoreException, Exception {
        return experimentItems.containsKey(name);
    }

    private boolean verifyTypeDicionary(String name) throws ObjectStoreException, Exception {
        return typeDiccionaryItmes.containsKey(name);
    }

    private boolean verifyCondition(String name) throws ObjectStoreException, Exception {
        return verifyConditionItems.containsKey(name);
    }

    private Boolean verifyBioEntity(String primaryId) throws Exception {
        if (!geneItems.containsKey(primaryId)) {
            loggable.makeErrorLog("Gene " + primaryId + " does not exists.");
            return false;
        }
        return true;
    }
}