package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. Sao Paulo
 *
 * Functionallity of Labis Gene Metadata Converter
 *
 * @author Rodrigo Dorado
 */

import java.io.File;
import java.io.Reader;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.util.FormattedTextParser;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.dataconversion.ItemWriter;

public class LabisGeneMetadataConverter extends BioFileConverter
{
    private static final String DATASET_TITLE                                = "LabisGeneMetadata";
    private static final String DATA_SOURCE_NAME                             = "labis-gene-metadata";
    private static final String LOGFILE                                      = "/home/rdorado/logs/MyJavaLog.log"; //Change to another file that already exists

    private ErrorLog loggable;
    private Iterator<?> tsvIter;

    private static Logger LOGGER                                             = null;
    private Boolean genesSaved                                               = false;
    private Boolean familiesSaved                                            = false;

    private Map<String, Hcca> hccas                                          = new HashMap<String, Hcca>();
    private Map<String, Gene> geneItems                                      = new HashMap<String, Gene>();
    private Map<String, Pathway> pathways                                    = new HashMap<String, Pathway>();
    private Map<String, Sequence> sequences                                  = new HashMap<String, Sequence>();
    private Map<String, Ortholog> orthologs                                  = new HashMap<String, Ortholog>();
    private Map<String, Organism> organisms                                  = new HashMap<String, Organism>();
    private Map<String, String> familiesItems                                = new HashMap<String, String>();
    private Map<String, Publication> pubItems                                = new HashMap<String, Publication>();
    private Map<String, Map<String, Hrr>> hrrs                               = new HashMap<String, Map<String, Hrr>>();
    private Map<String, Transcript> transcripts                              = new HashMap<String, Transcript>();
    private Map<String, String> familiesGeneItems                            = new HashMap<String, String>();
    private Map<String, String> sequencesProteins                            = new HashMap<String, String>();
    private Map<String, GeneFamilies> geneFamilies                           = new HashMap<String, GeneFamilies>();
    private Map<String, Map<String, String>> geneIds                         = new HashMap<String, Map<String, String>>();
    private Map<String, GenomicProtein> genomicProteins                      = new HashMap<String, GenomicProtein>();
    private Map<String, TypeGeneFamily> geneFamiliesType                     = new HashMap<String, TypeGeneFamily>();
    private Map<String, PathwayCategory> pathwayCategories                   = new HashMap<String, PathwayCategory>();
    private Map<String, GeneFamilyDataset> geneFamilyDatasets                = new HashMap<String, GeneFamilyDataset>();
    private Map<String, ExperimentCondition> experimentConditions            = new HashMap<String, ExperimentCondition>();
    private Map<String, ExperimentDescription> experimentDescriptions        = new HashMap<String, ExperimentDescription>();
    private Map<String, ExpressionTypeDiccionary> expressionTypeDiccionaries = new HashMap<String, ExpressionTypeDiccionary>();

    private String[] prefixFileName                                          = {"A-Genes-",
                                                                                "B-Orthologs-",
                                                                                "C-Kegg-Categories",
                                                                                "D-Kegg-Pathways",
                                                                                "E-Kegg-Genes-",
                                                                                "F-HCCA-",
                                                                                "G-ExperimentDescription-",
                                                                                "H-ExperimentGene-",
                                                                                "I-HRR-Genes-",
                                                                                "J-HRR-",
                                                                                "K-ExpressionTypeDiccionary",
                                                                                "L-ExperimentDescriptionColumns-",
                                                                                "M-ExpressionValues-",
                                                                                "N-geneFamiliesType-",
                                                                                "O-geneFamilyAnnotations-",
                                                                                "P-geneFamily-",
                                                                                "Q-GenomicProteins-Proteins-",
                                                                                "R-GenomicProteinsGene-",
                                                                                "S-Transcript-"};



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
        String taxonId;

        loggable.setActualFile(fileName);
        loggable.makeLog("Start (" + fileName + ")");

        try {
            this.tsvIter = FormattedTextParser.parseTabDelimitedReader(reader);
        } catch (Exception e) {
            throw new BuildException("cannot parse file: " + getCurrentFile(), e);
        }

        int opt = getOptionFileName(fileName);

        switch(opt) {
            case 1:
                saveGenesExecute();
            break;

            case 2:
                OrthologsExecute();
            break;

            case 3:
                PathwayCategoriesExecute();
            break;

            case 4:
                PathwaysExecute();
            break;

            case 5:
                KeggGenesPathwaysExecute();
            break;

            case 6:
                HccaExecute();
            break;

            case 7:
                ExperimentDescriptionExecute();
            break;

            case 8:
                experimentGeneExecute();
            break;

            case 9:
                saveGenes(); // Save all the genes before running RNA-seq
                taxonId = getTaxonIdFromFile(fileName, 3);
                hrrSaveGenesId(taxonId);
            break;

            case 10:
                taxonId = getTaxonIdFromFile(fileName, 2);
                hrrExecute(taxonId);
                saveHrr();
            break;

            case 11:
                ExpressionTypeDiccionaryExecute();
            break;

            case 12:
                ExperimentDescriptionColumnsExecute();
            break;

            case 13:
                ExpressionValuesExecute();
            break;

            case 14:
                geneFamiliesTypeExecute();
            break;

            case 15:
                geneFamilyAnnotationsExecute();
            break;

            case 16:
                geneFamilyExecute();
            break;

            case 17:
                saveFamilies();
                GenomicProteinsSequenceExecute();
            break;

            case 18:
                taxonId = getTaxonIdFromFile(fileName, 2);
                GenomicProteinsGeneExecute(taxonId);
            break;

            case 19:
                transcriptExecute();
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

    private String getTaxonIdFromFile(String fileName, Integer pos) throws Exception {
        String[] partsFileName = fileName.split("-");
        return partsFileName[pos];
    }

    private int getOptionFileName(String fileName) {
        for (int i = 0; i < this.prefixFileName.length; i++) {
            if (fileName.indexOf(this.prefixFileName[i]) == 0) {
                return i + 1;
            }

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
        } else {
            loggable.makeErrorLog("The family type: " + type + " already exists.");
        }
    }

    public GeneFamilyDataset saveGeneFamilyDataset(String dataset) throws ObjectStoreException, Exception {
        if (geneFamilyDatasets.containsKey(dataset)) {
            return geneFamilyDatasets.get(dataset);
        } else{
            GeneFamilyDataset geneFamilyDataset = new GeneFamilyDataset();
            geneFamilyDataset.setName(dataset);
            Item score = createItem(geneFamilyDataset.getClassName());
            score      = geneFamilyDataset.save(score);
            store(score);
            geneFamilyDataset.setUniqueId(score.getIdentifier());
            geneFamilyDatasets.put(dataset, geneFamilyDataset);
            return geneFamilyDataset;
        }
    }

    public void saveGeneFamilyAnnotations(String family, String type, String dataset, String annotation) throws ObjectStoreException, Exception {
        if (!geneFamilies.containsKey(family)) {
            GeneFamilies geneFamily = new GeneFamilies();
            geneFamily.setName(family);
            if (geneFamiliesType.containsKey(type)) {
                geneFamily.setType(geneFamiliesType.get(type));
                if (!StringUtils.isBlank(dataset)) {
                    GeneFamilyDataset geneFamilyDataset = saveGeneFamilyDataset(dataset);
                    geneFamily.setDataset(geneFamilyDataset);
                }
                geneFamily.setAnnotation(annotation);
                geneFamilies.put(family, geneFamily);
            } else {
                loggable.makeErrorLog("The family type: " + type + " does not exists.");
            }
        } else {
            loggable.makeErrorLog("The gene family: " + family + " already exists.");
        }
    }

    private void saveFamilies() throws ObjectStoreException, Exception {
        if (!familiesSaved) {
            for (Map.Entry<String, GeneFamilies> item : geneFamilies.entrySet()) {
                GeneFamilies geneFamily = item.getValue();
                Item score              = createItem(geneFamily.getClassName());
                score                   = geneFamily.save(score);
                store(score);
                geneFamily.setUniqueId(score.getIdentifier());
                item.setValue(geneFamily);
            }
            familiesSaved = true;
        }
    }

    private void savePathwayGene(String pathway, Gene gene) throws ObjectStoreException, Exception {
        if (pathways.containsKey(pathway)) {
            gene.addPathway(pathways.get(pathway));
            geneItems.put(gene.getPrimaryIdentifier(), gene);
        } else {
            loggable.makeErrorLog("The pathway " + pathway + " does not exists.");
        }
    }

    public void saveFamilyGene(String family, Gene gene, String description) throws ObjectStoreException, Exception {
        if (geneFamilies.containsKey(family)) {
            GeneFamilies geneFamily = geneFamilies.get(family);
            geneFamily.addGene(gene);
            geneFamilies.put(family, geneFamily);
        } else {
            loggable.makeErrorLog("The family: " + family + " does not exists.");
        }
    }

    private void saveExpressionValues(String expressionValue, String condition, Gene gene, String primaryIdExperiment, String typeDiccionary) throws ObjectStoreException, Exception {
        Boolean save                      = true;
        ExpressionValues expressionValues = new ExpressionValues();
        expressionValues.setExpressionValue(expressionValue);
        expressionValues.setGene(gene);
        if (verifyCondition(condition)) {
            expressionValues.setExperimentCondition(experimentConditions.get(condition));
        } else {
            save = false;
            loggable.makeErrorLog("The condition: " + condition + " does not exists.");
        }
        if (verifyExperiment(primaryIdExperiment)) {
            expressionValues.setExperimentDescription(experimentDescriptions.get(primaryIdExperiment));
        } else {
            save = false;
            loggable.makeErrorLog("The experiment does not exists.");
        }
        if (verifyTypeDicionary(typeDiccionary)) {
            expressionValues.setExpressionTypeDiccionary(expressionTypeDiccionaries.get(typeDiccionary));
        } else {
            save = false;
            loggable.makeErrorLog("The experiment type does not exists.");
        }
        if (save) {
            Item score = createItem(expressionValues.getClassName());
            score      = expressionValues.save(score);
            store(score);
            expressionValues.setUniqueId(score.getIdentifier());
        }
    }

    private void saveExperimentDescription(String name, Publication[] pubMedIds, String Accession, String experimentTitle, String Description) throws ObjectStoreException, Exception {
        if (!experimentDescriptions.containsKey(name)) {
            ExperimentDescription experimentDescription = new ExperimentDescription();
            experimentDescription.setName(name);
            experimentDescription.setDescription(Description);
            experimentDescription.setExperimentTitle(experimentTitle);
            experimentDescription.setAccession(Accession);
            for(int i = 0; i < pubMedIds.length; i++) {
                experimentDescription.addPublication(pubMedIds[i]);
            }
            Item score = createItem(experimentDescription.getClassName());
            score      = experimentDescription.save(score);
            store(score);
            experimentDescription.setUniqueId(score.getIdentifier());
            experimentDescriptions.put(name, experimentDescription);
        } else {
            loggable.makeErrorLog("The experiment: " + name + " already exists.");
        }
    }

    private void saveExpressionType(String name) throws ObjectStoreException, Exception {
        if (!expressionTypeDiccionaries.containsKey(name)) {
            ExpressionTypeDiccionary expressionTypeDiccionary = new ExpressionTypeDiccionary();
            expressionTypeDiccionary.setName(name);
            Item score = createItem(expressionTypeDiccionary.getClassName());
            score      = expressionTypeDiccionary.save(score);
            store(score);
            expressionTypeDiccionary.setUniqueId(score.getIdentifier());
            expressionTypeDiccionaries.put(name, expressionTypeDiccionary);
        } else {
            loggable.makeErrorLog("The experiment type: " + name + " already exists.");
        }
    }

    private void saveExperimentDescriptionColumns(String name, String description, String instrument, String strategy, String source, String selection, String layout, String primaryIdExperiment, String srrNumber) throws ObjectStoreException, Exception {
        if (!experimentConditions.containsKey(name)) {
            ExperimentCondition experimentCondition = new ExperimentCondition();
            experimentCondition.setName(name);
            experimentCondition.setDescription(description);
            experimentCondition.setInstrument(instrument);
            experimentCondition.setStrategy(strategy);
            experimentCondition.setSource(source);
            experimentCondition.setSelection(selection);
            experimentCondition.setLayout(layout);
            experimentCondition.setSrrNumber(srrNumber);
            Item score = createItem(experimentCondition.getClassName());
            score      = experimentCondition.save(score);
            store(score);
            experimentCondition.setUniqueId(score.getIdentifier());
            experimentConditions.put(name, experimentCondition);
        } else {
            loggable.makeErrorLog("The condition: " + name + " already exists.");
        }
    }

    private void saveTranscript(String transcriptName, String deffline)  throws ObjectStoreException, Exception {
        if (!transcripts.containsKey(transcriptName)) {
            Transcript transcript = new Transcript();
            transcript.setPrimaryIdentifier(transcriptName);
            transcript.setDescription(deffline);
            Item score = createItem(transcript.getClassName());
            score      = transcript.save(score);
            store(score);
            transcript.setUniqueId(score.getIdentifier());
            transcripts.put(transcriptName, transcript);
        } else {
            loggable.makeErrorLog("The transcript: " + transcriptName + " already exists.");
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
        } else {
            loggable.makeErrorLog("The protein: " + genomicProtein + " already exists.");
        }
    }

    private void saveGenomicProteins(String protein, Gene[] genes, Organism organism) throws ObjectStoreException, Exception {
        if (!genomicProteins.containsKey(protein)) {
            GenomicProtein genomicProtein = new GenomicProtein();
            if (sequencesProteins.containsKey(protein)) {
                String md5 = sequencesProteins.get(protein);
                if (sequences.containsKey(md5)) {
                    genomicProtein.setSequence(sequences.get(md5));
                } else {
                    loggable.makeErrorLog("The sequence for protein: " + protein + " does not exists. Protein is going to be saved empty.");
                }
            } else {
                loggable.makeErrorLog("The protein: " + protein + " does not exists. Protein is going to be saved empty.");
            }
            for (int i = 0; i < genes.length; i++) {
                if (genes[i] != null) {
                    genomicProtein.addGene(genes[i]);
                }
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
        } else {
            loggable.makeErrorLog("The protein: " + protein + " already exists.");
        }
    }

    private void savePathwayCategory(String category) throws Exception {
        if (!pathwayCategories.containsKey(category)) {
            PathwayCategory pathwayCategory = new PathwayCategory();
            pathwayCategory.setCategory(category);
            Item score = createItem(pathwayCategory.getClassName());
            score      = pathwayCategory.save(score);
            store(score);
            pathwayCategory.setUniqueId(score.getIdentifier());
            pathwayCategories.put(category, pathwayCategory);
        } else {
           loggable.makeErrorLog("The pathway category " + category + " already exists.");
        }
    }

    private void savePathway(String pathwayId, String name, String category, String description) throws Exception {
        if (!pathways.containsKey(pathwayId)) {
            Pathway pathway = new Pathway();
            pathway.setIdentifier(pathwayId);
            pathway.setName(name);
            pathway.setDescription(description);
            if (pathwayCategories.containsKey(category)) {
                pathway.setCategory(pathwayCategories.get(category));
            }
            Item score = createItem(pathway.getClassName());
            score      = pathway.save(score);
            store(score);
            pathway.setUniqueId(score.getIdentifier());
            pathways.put(pathwayId, pathway);
        } else {
            loggable.makeErrorLog("The pathway id: " + pathwayId + " already exists.");
        }
    }

    private void addOrthologGenes(String primaryId, Organism organism, Ortholog ortholog) throws ObjectStoreException, Exception {
        Gene gene = getGene(primaryId);
        if (gene != null) {
            gene.setOrtholog(ortholog);
            gene.setOrganism(organism);
            geneItems.put(primaryId, gene);
        }
    }

    private void addHccaGenes(String primaryId, Hcca hcca) throws Exception {
        Gene gene = getGene(primaryId);
        if (gene != null) {
            gene.setHcca(hcca);
            geneItems.put(primaryId, gene);
        }
    }

    private void addPubsGenes(String primaryId, String experimentName) throws Exception {
        if (experimentDescriptions.containsKey(experimentName)) {
            Gene gene = getGene(primaryId);
            if (gene != null) {
                ExperimentDescription experimentDescription = experimentDescriptions.get(experimentName);
                gene.addPublications(experimentDescription.getPublications());
            }
        } else {
            loggable.makeErrorLog("The experiment: " + experimentName + " does not exists.");
        }
    }

    private void addHrrRelation(String geneIdA, String geneIdB, String rank, String taxonId) throws Exception {
        String primaryId_A = getGeneIds(geneIdA, taxonId);
        String primaryId_B = getGeneIds(geneIdB, taxonId);
        if (!StringUtils.isBlank(primaryId_A) && !StringUtils.isBlank(primaryId_B)) {
            Map<String, Hrr> relationA;
            Map<String, Hrr> relationB;
            if (!verifyRelation(primaryId_A, primaryId_B) && !verifyRelation(primaryId_B, primaryId_A)) {
                String firstGene  = (hrrs.containsKey(primaryId_B)) ? primaryId_B : primaryId_A;
                String secondGene = (firstGene != primaryId_A) ? primaryId_A : primaryId_B;
                Gene gene_a       = getGene(firstGene);
                Gene gene_b       = getGene(secondGene);
                if (gene_a != null && gene_b != null) {
                    if (hrrs.containsKey(firstGene)) {
                        relationA = hrrs.get(firstGene);
                    } else {
                        relationA = new HashMap<String, Hrr>();
                    }

                    if (hrrs.containsKey(secondGene)) {
                        relationB = hrrs.get(secondGene);
                    } else {
                        relationB = new HashMap<String, Hrr>();
                    }

                    Hrr hrrA = new Hrr();
                    Hrr hrrB = new Hrr();

                    hrrA.setGeneA(gene_a);
                    hrrB.setGeneA(gene_b);

                    hrrA.setGeneB(gene_b);
                    hrrB.setGeneB(gene_a);

                    Hcca hcca_a      = gene_a.getHcca();
                    Hcca hcca_b      = gene_b.getHcca();

                    String cluster_a = hcca_a.getCluster();
                    String cluster_b = hcca_b.getCluster();

                    if (cluster_a.equals(cluster_b)) {
                        hrrA.setHcca(hcca_a);
                        hrrB.setHcca(hcca_a);
                    }

                    Map<String, Hrr> neighborhoodsA = new HashMap<String, Hrr>();
                    Map<String, Hrr> neighborhoodsB = new HashMap<String, Hrr>();

                    for (Map.Entry<String,  Hrr> itemA : relationA.entrySet()) {
                        String keyA = itemA.getKey();
                        for (Map.Entry<String,  Hrr> itemB : relationB.entrySet()) {
                            String keyB = itemB.getKey();
                            if (keyB.equals(keyA)) {
                                hrrA.addNeighborhood(getGene(keyA));
                                hrrB.addNeighborhood(getGene(keyA));

                                neighborhoodsA.put(keyA, setNeighborhoodExisting(itemA.getValue(), gene_b));
                                neighborhoodsB.put(keyB, setNeighborhoodExisting(itemB.getValue(), gene_a));

                                setNeighborhood(keyA, firstGene, gene_b);
                                setNeighborhood(keyB, secondGene, gene_a);
                            }
                        }
                    }

                    for (Map.Entry<String,  Hrr> item : neighborhoodsA.entrySet()) {
                        relationA.put(item.getKey(), item.getValue());
                    }

                    for (Map.Entry<String,  Hrr> item : neighborhoodsB.entrySet()) {
                        relationB.put(item.getKey(), item.getValue());
                    }

                    hrrA.setRank(rank);
                    hrrB.setRank(rank);

                    hrrA.setSaveIt(true);
                    hrrB.setSaveIt(false);

                    relationA.put(secondGene, hrrA);
                    hrrs.put(firstGene, relationA);

                    relationB.put(firstGene, hrrB);
                    hrrs.put(secondGene, relationB);
                }
            }
        }
    }

    private  Hrr setNeighborhoodExisting(Hrr hrr, Gene gene) throws Exception {
        hrr.addNeighborhood(gene);
        return hrr;
    }

    private  void setNeighborhood(String key, String primaryIdentifier, Gene gene) throws Exception {
        Map<String, Hrr> neighborhood = hrrs.get(key);
        Hrr hrrNeighborhood           = neighborhood.get(primaryIdentifier);
        hrrNeighborhood.addNeighborhood(gene);
        neighborhood.put(primaryIdentifier, hrrNeighborhood);
        hrrs.put(key, neighborhood);
    }

    private void saveHrr() throws Exception {
        for (Map.Entry<String,  Map<String, Hrr>> itemHrr : hrrs.entrySet()) {
            String keyA = itemHrr.getKey();
            for (Map.Entry<String,  Hrr> item : itemHrr.getValue().entrySet()) {
                String keyB = item.getKey();
                Hrr hrr     = item.getValue();
                if (hrr.getSaveIt()) {
                    Item score = createItem(hrr.getClassName());
                    score      = hrr.save(score);
                    store(score);
                    hrr.setUniqueId(score.getIdentifier());
                    item.setValue(hrr);
                }
            }
        }
    }

    private Boolean verifyRelation(String firstPrimary, String secondPrimary) throws Exception {
        if (hrrs.containsKey(firstPrimary)) {
            if (hrrs.get(firstPrimary).containsKey(secondPrimary)) {
                return true;
            }
        }
        return false;
    }

    private Gene getGeneCreate(String primaryId) throws Exception {
        if (geneItems.containsKey(primaryId)) {
            return geneItems.get(primaryId);
        } else {
            Gene gene = new Gene();
            gene.setPrimaryIdentifier(primaryId);
            return gene;
        }
    }

    private Integer getGeneSize() throws Exception {
        return geneItems.size();
    }

    private Gene getGene(String primaryId) throws Exception {
        if (geneItems.containsKey(primaryId)) {
            return geneItems.get(primaryId);
        } else {
            loggable.makeErrorLog("The gene with primary Identifier: " + primaryId + " does not exists in the files of genes (A-Genes-). Please make sure that this gene exists in the GFF3 or is correct the primary identifier.");
            return null;
        }
    }

    public Hcca saveHcca(String cluster) throws Exception {
        if (!hccas.containsKey(cluster)) {
            Hcca hcca  = new Hcca(cluster);
            Item score = createItem(hcca.getClassName());
            score      = hcca.save(score);
            store(score);
            hcca.setUniqueId(score.getIdentifier());
            hccas.put(cluster, hcca);
            return hcca;
        } else {
            return hccas.get(cluster);
        }
    }

    public void saveGeneIds(String primaryId, String id, String taxonId) throws Exception {
        if (!geneIds.containsKey(taxonId)) {
            geneIds.put(taxonId, new HashMap<String, String>());
        }
        Map<String, String> geneId = geneIds.get(taxonId);
        if (!geneId.containsKey(id)) {
            geneId.put(id, primaryId);
        } else {
            loggable.makeErrorLog("The gene: " + primaryId + " already has an id to HRR.");
        }
        geneIds.put(taxonId, geneId);
    }

    public String getGeneIds(String id, String taxonId) throws Exception {
        if (geneIds.containsKey(taxonId)) {
            Map<String, String> geneId = geneIds.get(taxonId);
            if (geneId.containsKey(id)) {
                return geneId.get(id);
            } else {
               loggable.makeErrorLog("The id does not exists.");
            }
        } else {
            loggable.makeErrorLog("Error in the taxon id.");
        }
        return null;
    }

    public Ortholog saveOrtholog(String name) throws Exception {
        if (!orthologs.containsKey(name)) {
            Ortholog ortholog = new Ortholog(name);
            Item score        = createItem(ortholog.getClassName());
            score             = ortholog.save(score);
            store(score);
            ortholog.setUniqueId(score.getIdentifier());
            orthologs.put(name, ortholog);
            return ortholog;
        } else {
            loggable.makeErrorLog("The ortholog: " + name + " already exists, adding genes.");
            return orthologs.get(name);
        }
    }

    private void geneFamilyExecute() throws ObjectStoreException, Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line         = (String[]) this.tsvIter.next();
                String primaryIdGenes = (line.length > 0) ? line[0] : "";
                String family         = (line.length > 2) ? line[2] : "";
                String description    = (line.length > 3) ? line[3] : "-";
                if (!StringUtils.isBlank(primaryIdGenes) && !StringUtils.isBlank(family)) {
                    String[] families = family.split(",");
                    Gene gene         = getGene(primaryIdGenes);
                    if (gene != null) {
                        for( int i = 0; i < families.length; i++) {
                            saveFamilyGene(families[i], gene, description);
                        }
                    }
                } else {
                    loggable.makeErrorLog("The gene or the family are empty: gene: " + primaryIdGenes + "; family: " + family);
                }
            }
        }  else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void geneFamilyAnnotationsExecute() throws ObjectStoreException, Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line     = (String[]) this.tsvIter.next();
                String family     = (line.length > 0) ? line[0] : "";
                String type       = (line.length > 1) ? line[1] : "";
                String dataset    = (line.length > 2) ? line[2] : "";
                String annotation = (line.length > 3) ? line[3] : "";
                if (!StringUtils.isBlank(family) && !StringUtils.isBlank(type)) {
                    saveGeneFamilyAnnotations(family, type, dataset, annotation);
                } else {
                    loggable.makeErrorLog("The family name or the family type are empty: family: " + family + "; family type: " + type);
                }
            }
        }  else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void geneFamiliesTypeExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String type   = (line.length > 0) ? line[0] : "";
                String dbLink = (line.length > 1) ? line[1] : "";
                if (!StringUtils.isBlank(type)) {
                    saveGeneFamiliesType(type, dbLink);
                } else {
                    loggable.makeErrorLog("The family type is empty.");
                }
            }
        }
    }

    private void ExperimentDescriptionColumnsExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line      = (String[]) this.tsvIter.next();
                String experiment  = (line.length > 0) ? line[0] : "";
                String name        = (line.length > 1) ? line[1] : "";
                String description = (line.length > 2) ? line[2] : "-";
                String instrument  = (line.length > 3) ? line[3] : "-";
                String strategy    = (line.length > 4) ? line[4] : "-";
                String source      = (line.length > 5) ? line[5] : "-";
                String selection   = (line.length > 6) ? line[6] : "-";
                String layout      = (line.length > 7) ? line[7] : "-";
                String srrNumber   = (line.length > 8) ? line[8] : "-";
                if (!StringUtils.isBlank(name) && !StringUtils.isBlank(experiment)) {
                    saveExperimentDescriptionColumns(name, description, instrument, strategy, source, selection, layout, experiment, srrNumber);
                } else {
                    loggable.makeErrorLog("The experiment name or the name of the condition are empty: experiment: " + experiment + "; condition: " + name);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExpressionTypeDiccionaryExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String name   = (line.length > 0) ? line[0] : "";
                if (!StringUtils.isBlank(name)) {
                    saveExpressionType(name);
                } else {
                    loggable.makeErrorLog("The experiment type is empty.");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void saveGenesExecute() throws Exception{
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String gene   = (line.length > 0) ? line[0] : "";
                if (!StringUtils.isBlank(gene)) {
                    createGene(gene);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void KeggGenesPathwaysExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line         = (String[]) this.tsvIter.next();
                String primaryIdGenes = (line.length > 0) ? line[0] : "";
                String pathwayStr     = (line.length > 1) ? line[1] : "";
                if (!StringUtils.isBlank(primaryIdGenes) && !StringUtils.isBlank(pathwayStr)) {
                    String[] pathways = pathwayStr.split(" ");
                    Gene gene         = getGene(primaryIdGenes);
                    if (gene != null) {
                        for (int i = 0; i < pathways.length; i++) {
                            savePathwayGene(pathways[i], gene);
                        }
                    }
                } else {
                    loggable.makeErrorLog("Gene Pathway wrong relation --> gene: " + primaryIdGenes + "; pathways: " + pathwayStr);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void PathwaysExecute() throws Exception {
      if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line      = (String[]) this.tsvIter.next();
                String pathway     = (line.length > 0) ? line[0] : "";
                String name        = (line.length > 1) ? line[1] : "";
                String category    = (line.length > 2) ? line[2] : "";
                String description = (line.length > 3) ? line[3] : "";
                if (!StringUtils.isBlank(pathway) && !StringUtils.isBlank(name)) {
                    savePathway(pathway, name, category, description);
                } else {
                    loggable.makeErrorLog("Pathway Category without name or id --> id: " + pathway + "; name: " + name);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void PathwayCategoriesExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String category   = (line.length > 0) ? line[0] : "";
                if (!StringUtils.isBlank(category)) {
                    savePathwayCategory(category);
                } else {
                    loggable.makeErrorLog("Pathway Category without name");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void OrthologsExecute() throws Exception { //In the head the taxon id
        boolean hasInfo            = false;
        Organism[] organisms_order = null;
        int number_organism        = 0;

        if (this.tsvIter.hasNext()) {
            String[] line = (String[]) this.tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (line.length > 0) {
                    hasInfo = true;
                }
                number_organism = line.length - 1;
                organisms_order = new Organism[number_organism];
                for (int i = 0; i < number_organism; i++) {
                    organisms_order[i] = this.getAOrganism(line[i + 1]);
                }
            }
        }

        //Only if there is the correct info
        if (hasInfo) {
            while (this.tsvIter.hasNext()) {
                String[] line        = (String[]) this.tsvIter.next();
                String ortholog_name = (line.length > 0) ? line[0] : "";
                //Verify that the name of the ortholog is not empty
                if (!StringUtils.isBlank(ortholog_name)) {
                    Ortholog ortholog = saveOrtholog(ortholog_name);
                    for (int i = 0; i < number_organism; i++) {
                        String genes = (line.length > i + 1) ? line[i + 1] : "";
                        if (!StringUtils.isBlank(genes)) {
                            String[] a_genes = genes.split(",");
                            for (int j = 0; j < a_genes.length; j++) {
                                addOrthologGenes(a_genes[j], organisms_order[i], ortholog);
                            }
                        }
                    }
                } else {
                    loggable.makeErrorLog("Ortholog without name");
                }
            }
        }
    }

    private void HccaExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line  = (String[]) this.tsvIter.next();
                String gene    = (line.length > 0) ? line[0] : "";
                String cluster = (line.length > 1) ? line[1] : "";
                if (!StringUtils.isBlank(gene) && !StringUtils.isBlank(cluster)) {
                    Hcca hcca = saveHcca(cluster);
                    addHccaGenes(gene, hcca);
                } else {
                    loggable.makeErrorLog("Gene or Cluster empty > gene: " + gene + "; cluster: " + cluster);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExperimentDescriptionExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line          = (String[]) this.tsvIter.next();
                String name            = (line.length > 0) ? line[0] : "";
                String pubText         = (line.length > 1) ? line[1] : "";
                String Accession       = (line.length > 2) ? line[2] : "-";
                String experimentTitle = (line.length > 3) ? line[3] : "-";
                String Description     = (line.length > 4) ? line[4] : "-";
                if (!StringUtils.isBlank(name)) {
                    String[] pubIds;
                    if (StringUtils.isBlank(pubText)) {
                        pubIds = new String[0];
                    } else {
                        pubIds = pubText.split(",");
                    }
                    Publication[] pubMedId   = createPublications(pubIds);
                    saveExperimentDescription(name, pubMedId, Accession, experimentTitle, Description);
                } else {
                    loggable.makeErrorLog("Experiment without name");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void experimentGeneExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String gene   = (line.length > 0) ? line[0] : "";
                if (!StringUtils.isBlank(gene)) {
                    if (line.length > 2) {
                        for (int i = 1; i < line.length; i++) {
                            addPubsGenes(gene, line[i]);
                        }
                    }
                } else {
                    loggable.makeErrorLog("Experiment gene without gene name");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void hrrSaveGenesId(String taxonId) throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String gene   = (line.length > 0) ? line[0] : "";
                String id     = (line.length > 1) ? line[1] : "";
                if (!StringUtils.isBlank(gene) && !StringUtils.isBlank(id)) {
                    saveGeneIds(gene, id, taxonId);
                } else {
                    loggable.makeErrorLog("Gene or Id empty > gene: " + gene + "; id: " + id);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void hrrExecute(String taxonId) throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String gene   = (line.length > 0) ? line[0] : "";
                if (!StringUtils.isBlank(gene)) {
                    if (line.length > 2) {
                        for (int i = 1; i < line.length; i++) {
                            String[] nodeStr =  line[i].split("\\+");
                            if (nodeStr.length > 1) {
                                String node = nodeStr[0];
                                String rank = nodeStr[1];
                                addHrrRelation(gene, node, rank, taxonId);
                            }
                        }
                    }
                } else {
                    loggable.makeErrorLog("First id empty");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExpressionValuesExecute() throws Exception {
        String [] heads = null;
        int end         = 0;

        if (this.tsvIter.hasNext()) {
            String[] line = (String[]) this.tsvIter.next();
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

        while (this.tsvIter.hasNext()) {
            String[] line              = (String[]) this.tsvIter.next();
            int sizeLine               = line.length;
            String primaryIdGenes      = (line.length > 0) ? line[0] : "";
            String primaryIdExperiment = (line.length > 1) ? line[1] : "";
            String typeDiccionary      = (line.length > 2) ? line[2] : "";
            if (!StringUtils.isBlank(primaryIdGenes) && !StringUtils.isBlank(primaryIdExperiment) && !StringUtils.isBlank(typeDiccionary)) {
                Gene gene = getGene(primaryIdGenes);
                if (gene != null) {
                    for (int i = 3; i < end; i++) {
                        if (i >= sizeLine) {
                            break;
                        }
                        saveExpressionValues(line[i], heads[i], gene, primaryIdExperiment, typeDiccionary);
                    }
                }
            } else {
                loggable.makeErrorLog("Gene id or the experiment or the condition are empty > gene: " + primaryIdGenes + "; experiment: " + primaryIdExperiment + "; condition: " + typeDiccionary + ".");
            }
        }
    }

    private void transcriptExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line     = (String[]) this.tsvIter.next();
                String transcript = (line.length > 0) ? line[0] : "";
                String deffline   = (line.length > 1) ? line[1] : "";
                if (!StringUtils.isBlank(transcript)) {
                    saveTranscript(transcript, deffline);
                } else {
                    loggable.makeErrorLog("The transcript id is empty.");
                }
            }
        }
    }

    private void GenomicProteinsSequenceExecute() throws Exception {
        Boolean id     = true;
        String protein = "";
        String seq     = "";
        while (this.tsvIter.hasNext()) {
            String[] line = (String[]) this.tsvIter.next();
            if (line[0].indexOf(">") == 0) {
                if (!StringUtils.isBlank(protein)) {
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

        if (!StringUtils.isBlank(protein)) {
            saveSequence(seq, protein.trim());
            seq     = "";
            protein = "";
        }
    }

    private void GenomicProteinsGeneExecute(String taxonId) throws Exception {
        Organism organism          = this.getAOrganism(taxonId);
        String organism_identifier = organism.getUniqueId();
        if (!StringUtils.isBlank(organism_identifier)) {
            if (watchHeader()) {
                while (this.tsvIter.hasNext()) {
                    String[] line   = (String[]) this.tsvIter.next();
                    String protein  = (line.length > 0) ? line[0] : "";
                    String idsGenes = (line.length > 1) ? line[1] : "";
                    if (!StringUtils.isBlank(protein) && !StringUtils.isBlank(idsGenes)) {
                        String[] primaryIdGenes = idsGenes.split(",");
                        Gene[] genes            = new Gene[primaryIdGenes.length];
                        for (int i = 0; i < primaryIdGenes.length; i++) {
                            genes[i] = getGene(primaryIdGenes[i]);
                        }
                        saveGenomicProteins(protein.trim(), genes, organism);
                    } else {
                        loggable.makeErrorLog("Protein id or the gene id are empty > protein: " + protein + "; gene: " + idsGenes + ".");
                    }
                }
            }
        } else {
            loggable.makeErrorLog("Error with the organism.");
        }
    }

    private Publication[] createPublications(String[] pubIds) throws ObjectStoreException, Exception {
        Publication[] publications = new Publication[pubIds.length];
        for (int i = 0; i < pubIds.length; i++) {
            publications[i] = createPublication(pubIds[i]);
        }
        return publications;
    }

    private Publication createPublication(String pubId) throws ObjectStoreException, Exception {
        if (!pubItems.containsKey(pubId)) {
            Publication publication = new Publication();
            publication.setPubMedId(pubId);
            Item score  = createItem(publication.getClassName());
            score       = publication.save(score);
            store(score);
            publication.setUniqueId(score.getIdentifier());
            pubItems.put(pubId, publication);
            return publication;
        } else {
            return pubItems.get(pubId);
        }
    }

    private void saveGenes() throws ObjectStoreException, Exception {
        if (!genesSaved) {
            for (Map.Entry<String, Gene> item : geneItems.entrySet()) {
                Gene gene  = item.getValue();
                Item score = createItem(gene.getClassName());
                score      = gene.save(score);
                store(score);
                gene.setUniqueId(score.getIdentifier());
                item.setValue(gene);
            }
            genesSaved = true;
        }
    }

    private void createGene(String primaryIdentifier) throws Exception {
        if (!geneItems.containsKey(primaryIdentifier)) {
            Gene gene = new Gene();
            gene.setPrimaryIdentifier(primaryIdentifier);
            geneItems.put(primaryIdentifier, gene);
        } else {
            loggable.makeErrorLog("The gene: " + primaryIdentifier + " already exists.");
        }
    }

    private Boolean watchHeader() throws Exception {
        int end = 0;
        if (this.tsvIter.hasNext()) {
            String[] line = (String[]) this.tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
                return false;
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean verifyExperiment(String name) throws ObjectStoreException, Exception {
        return experimentDescriptions.containsKey(name);
    }

    private boolean verifyTypeDicionary(String name) throws ObjectStoreException, Exception {
        return expressionTypeDiccionaries.containsKey(name);
    }

    private boolean verifyCondition(String name) throws ObjectStoreException, Exception {
        return experimentConditions.containsKey(name);
    }
}