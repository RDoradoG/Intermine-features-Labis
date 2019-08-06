package org.phycomine.web.displayer;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Functionallity of Neighborhood and Clusters of the Gene Network
 *
 * @author Rodrigo Dorado
 */

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.intermine.api.InterMineAPI;
import org.intermine.api.query.PathQueryExecutor;
import org.intermine.api.profile.Profile;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.results.ResultElement;
import org.intermine.api.results.ExportResultsIterator;
import org.intermine.web.logic.config.ReportDisplayerConfig;
import org.intermine.web.logic.results.ReportObject;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.model.bio.Hrr;
import org.intermine.model.bio.Hcca;
import org.intermine.model.bio.Gene;
import org.intermine.model.bio.Ortholog;
import org.intermine.metadata.Model;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.webservice.server.core.ListManager;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.web.displayer.ReportDisplayer;


public class NetworkHCCADisplayer extends ReportDisplayer
{

    public NetworkHCCADisplayer(ReportDisplayerConfig config, InterMineAPI im) {
        super(config, im);
    }

    @Override
    public void display(HttpServletRequest request, ReportObject reportObject) {
        HttpSession session        = request.getSession();
        final InterMineAPI im      = SessionMethods.getInterMineAPI(session);
        ObjectStore os             = im.getObjectStore();
        Model model                = im.getModel();
        Profile profile            = SessionMethods.getProfile(session);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);
        Gene gene                  = (Gene) reportObject.getObject();
        Hcca hcca                  = gene.getHcca();
        String idGene              = gene.getPrimaryIdentifier();
        String json_hcca           = "[]";
        String cluster             = "";

        Set<Hrr> set_hrr           = gene.getGeneNeighborhood();
        String json_hrr            = getJson(getNeighborhood(set_hrr.iterator()));

        Set<Hrr> set_hrr_a         = gene.getHrr_a();
        String json_hrr_a          = getJson(getNeighborhood(set_hrr_a.iterator()));

        Set<Hrr> set_hrr_b         = gene.getHrr_b();
        String json_hrr_b          = getJson(getNeighborhood(set_hrr_b.iterator()));

        String json_neighborhood   = unionJson(json_hrr_a, json_hrr_b);
        json_neighborhood          = unionJson(json_neighborhood, json_hrr);

        if (hcca != null) {
            Set<Hrr> set_hcca = hcca.getHrr();
            json_hcca         = getJson(getNeighborhood(set_hcca.iterator()));
            cluster           = hcca.getCluster();
        }

        ListManager listManager                       = new ListManager(im, profile);
        Map<String, InterMineBag> intermineBags       = listManager.getListMap();
        String bagName_HCCA                           = "Cluster_" + cluster;
        String bagName_HRR                            = idGene + "_Neighborhood";

        PathQuery geneFamiliesType                    = getGeneFamiliesType(model);
        PathQuery experimentType                      = getExperimentType(model);
        PathQuery geneFamilyDataset                   = getGeneFamilyDataset(model);

        ExportResultsIterator resultGeneFamiliesType  = executeQuery(executor, geneFamiliesType);
        ExportResultsIterator resultExperimentType    = executeQuery(executor, experimentType);
        ExportResultsIterator resultGeneFamilyDataset = executeQuery(executor, geneFamilyDataset);

        String jsontGeneFamiliesType                  = loopResultLists(resultGeneFamiliesType);
        String jsontExperimentType                    = loopResultLists(resultExperimentType);
        String jsontGeneFamilyDataset                 = loopResultLists(resultGeneFamilyDataset);

        String is_bagName_HCCA                        = verifyIfBagExists(bagName_HCCA, intermineBags) ? "true" : "false";
        String is_bagName_HRR                         = verifyIfBagExists(bagName_HRR, intermineBags) ? "true" : "false";

        request.setAttribute("hrrNet", json_neighborhood);
        request.setAttribute("cluster", cluster);
        request.setAttribute("hccaNet", json_hcca);
        request.setAttribute("bagName_HRR", bagName_HRR);
        request.setAttribute("geneFamilies", jsontGeneFamiliesType);
        request.setAttribute("bagName_HCCA", bagName_HCCA);
        request.setAttribute("experimentTypes", jsontExperimentType);
        request.setAttribute("PrimaryIdentifier", idGene);
        request.setAttribute("bagNameExists_HRR", is_bagName_HRR);
        request.setAttribute("geneFamilyDataset", jsontGeneFamilyDataset);
        request.setAttribute("bagNameExists_HCCA", is_bagName_HCCA);
    }

    private Boolean verifyIfBagExists(String bagName, Map<String, InterMineBag> intermineBags) {
        Iterator iterator = intermineBags.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String actualBagName = (String) entry.getKey();
            if (actualBagName.equals(bagName)) {
                return true;
            }
        }
        return false;
    }

    private ExportResultsIterator executeQuery(PathQueryExecutor executor, PathQuery query) {
        try {
            return executor.execute(query);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }
    }

    private String loopResultLists(ExportResultsIterator result) {
        String jsonResult = "";
        while (result.hasNext()) {
            List<ResultElement> row = result.next();
            String value = (String) row.get(0).getField();
            jsonResult = addToJsonList(jsonResult, "'" + value + "'");
        }
        return "[" + jsonResult + "]";
    }

    private String addToJsonList(String value, String addition) {
        if(value.equals("")){
            return value + addition;
        } else {
            return value + ", " + addition;
        }
    }

    private PathQuery getGeneFamiliesType(Model model) {
        PathQuery query = new PathQuery(model);
        query.addViews("GeneFamilies.type.name");
        query.addOrderBy("GeneFamilies.type.name", OrderDirection.ASC);
        return query;
    }

    private PathQuery getExperimentType(Model model) {
        PathQuery query = new PathQuery(model);
        query.addViews("ExpressionTypeDiccionary.name");
        query.addOrderBy("ExpressionTypeDiccionary.name", OrderDirection.ASC);
        return query;
    }

    private PathQuery getGeneFamilyDataset(Model model) {
        PathQuery query = new PathQuery(model);
        query.addViews("GeneFamilyDataset.name");
        query.addOrderBy("GeneFamilyDataset.name", OrderDirection.ASC);
        return query;
    }

    private String unionJson(String json_a, String json_b) {
        String json_r = json_a + "|" + json_b;
        return json_r.replace("]|[",",").replace(",]","]").replace("[,","[");
    }

    private String getJson(List<List<Map<String, String>>> records) {
        String json = "";
        String additon;
        for (int i = 0; i < records.size(); i++) {
            additon = "";
            List<Map<String, String>> row = records.get(i);
            for (int j = 0; j < row.size(); j++) {
                Map<String, String> col = row.get(j);
                String add = "";
                if (col.get("type").equals("Integer")) {
                    add = "'" + col.get("header") + "': " + col.get("value");
                }

                if (col.get("type").equals("String")) {
                    add = "'" + col.get("header") + "': '" + col.get("value") + "'";
                }
                additon = addToJsonList(additon, add);
            }
            additon = "{" + additon + "}";
            json    = addToJsonList(json, additon);
        }
        return "[" + json + "]";
    }

    private Map<String, String> setCol(String header, String type, String value) {
        Map<String, String> columns = new HashMap<String, String>();
        columns.put("header", header);
        columns.put("type", type);
        columns.put("value", value);
        return columns;
    }

    private List<List<Map<String, String>>> getNeighborhood(Iterator iterator) {
        List<List<Map<String, String>>> records =  new ArrayList<List<Map<String, String>>>();
        while (iterator.hasNext()) {
            List<Map<String, String>> row = new ArrayList<Map<String, String>>();
            Hrr hrr                       = (Hrr) iterator.next();
            Gene gene_a                   = hrr.getGene_a();
            Gene gene_b                   = hrr.getGene_b();
            Ortholog ortholog_a           = gene_a.getOrtholog();
            Ortholog ortholog_b           = gene_b.getOrtholog();
            String ortholog_a_name        = (ortholog_a != null) ? ortholog_a.getName() : "";
            String ortholog_b_name        = (ortholog_b != null) ? ortholog_b.getName() : "";
            row.add(setCol("rank", "Integer", Integer.toString(hrr.getRank())));
            row.add(setCol("gene_a", "String", gene_a.getPrimaryIdentifier()));
            row.add(setCol("ortholog_a", "String", ortholog_a_name));
            row.add(setCol("id_a", "String", Integer.toString(gene_a.getId())));
            row.add(setCol("gene_b", "String", gene_b.getPrimaryIdentifier()));
            row.add(setCol("ortholog_b", "String", ortholog_b_name));
            row.add(setCol("id_b", "String", Integer.toString(gene_b.getId())));
            records.add(row);
        }
        return records;
    }
}