package org.phycomine.web;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.actions.TilesAction;
import org.apache.struts.tiles.ComponentContext;

import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.api.query.PathQueryExecutor;
import org.intermine.api.results.ExportResultsIterator;
import org.intermine.api.results.ResultElement;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

public class PhenotypeController extends TilesAction
{

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(ComponentContext context, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        HttpSession session        = request.getSession();
        final InterMineAPI im      = SessionMethods.getInterMineAPI(session);
        ObjectStore os             = im.getObjectStore();
        Model model                = im.getModel();
        Profile profile            = SessionMethods.getProfile(session);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);
        
        String APIKey              = profile.getApiKey();
        String DayToken            = profile.getDayToken();
        String key = "";

        try {
            if (APIKey != null && !APIKey.isEmpty()) {
                key = APIKey;
            } else {
                key = DayToken;
            }
            findPhenotypes(request, model, executor, os, key);
        } catch (ObjectStoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void findPhenotypes(HttpServletRequest request, Model model, PathQueryExecutor executor, ObjectStore os, String token) throws ObjectStoreException {
        String jsonOrganisms                  = "";
        String jsonTimeMeasure                = "";
        
        PathQuery queryOrganisms              = getQueryOrganisms(model);
        
        ExportResultsIterator resultOragnisms = executeQuery(executor, queryOrganisms);

        while (resultOragnisms.hasNext()) {
            List<ResultElement> rowOrganism = resultOragnisms.next();
            Integer taxId                   = (Integer) rowOrganism.get(0).getField();
            String taxonId                  = Integer.toString(taxId); 
            String name                     = (String) rowOrganism.get(1).getField();
            String additon                  = "{taxonId: " + taxonId + ", name: '" + name + "'}";
            jsonOrganisms                   = addToJsonList(jsonOrganisms, additon);
        }
        
        jsonOrganisms                           = "[" + jsonOrganisms + "]";
        
        PathQuery queryPTimeMeasure             = getQueryTimeMeasure(model);
        
        ExportResultsIterator resultTimeMeasure = executeQuery(executor, queryPTimeMeasure);

        while (resultTimeMeasure.hasNext()) {
            List<ResultElement> rowTimeMeasure = resultTimeMeasure.next();
            String timeMeasure                 = (String) rowTimeMeasure.get(0).getField();
            String rate                        = (String) rowTimeMeasure.get(1).getField();
            Float root                        = (Float) rowTimeMeasure.get(2).getField();
            String rootStr = Float.toString(root);
            String additon                     = "{timeMeasure: '" + timeMeasure + "', rate: '" + rate + "', root: '" + rootStr + "'}";
            jsonTimeMeasure                    = addToJsonList(jsonTimeMeasure, additon);
        }

        jsonTimeMeasure = "[" + jsonTimeMeasure + "]";

        request.setAttribute("organisms", jsonOrganisms);
        request.setAttribute("timeMeasures", jsonTimeMeasure);
        request.setAttribute("APIKey", token);
    }

    private ExportResultsIterator executeQuery(PathQueryExecutor executor, PathQuery query) {
        try {
            return executor.execute(query);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }
    }

    private PathQuery getQueryOrganisms(Model model) {
        PathQuery query = new PathQuery(model);
        query.addViews("Organism.taxonId", "Organism.name");
        query.addOrderBy("Organism.taxonId", OrderDirection.ASC);
        query.addConstraint(Constraints.isNotNull("Organism.phenotypeValue.phenotypeValue"));
        return query;
    }

    private PathQuery getQueryTimeMeasure(Model model) {
        PathQuery query = new PathQuery(model);
        query.addViews("TimeMeasure.timeMeasure", "TimeMeasure.rate", "TimeMeasure.root");
        query.addOrderBy("TimeMeasure.rate", OrderDirection.ASC);
        return query;
    }

    private String addToJsonList(String value, String addition) {
        if(value.equals("")){
            return value + addition;
        } else {
            return value + ", " + addition;
        }
    }

}
