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

/**
 * Class that generates heatMap data for a list of genes.
 *
 * @author Rodrigo Dorado
 *
 */
public class HeatMapController extends TilesAction
{

    private String Type_selected = "";
    private String Experiment_selected = "";

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(ComponentContext context, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        HttpSession session        = request.getSession();
        final InterMineAPI im      = SessionMethods.getInterMineAPI(session);
        ObjectStore os             = im.getObjectStore();
        InterMineBag bag           = (InterMineBag) request.getAttribute("bag");
        Model model                = im.getModel();
        Profile profile            = SessionMethods.getProfile(session);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);
        
        String APIKey              = profile.getApiKey();
        String DayToken            = profile.getDayToken();
        String key                 = "";

        try {
            if (APIKey != null && !APIKey.isEmpty()) {
                key = APIKey;
            } else {
                key = DayToken;
            }
            findExpression(request, model, bag, executor, os, key);
        } catch (ObjectStoreException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void findExpression(HttpServletRequest request, Model model, InterMineBag bag, PathQueryExecutor executor, ObjectStore os, String token) throws ObjectStoreException {
        String expressionType = bag.getType().toLowerCase();
        //DecimalFormat df    = new DecimalFormat("#.##");
        List<String> allType  = new ArrayList<String>();
        String JsonType       = "";
        String JsonGene       = "";
        
        PathQuery queryType   = getQueryType(bag, model);

        ExportResultsIterator resultType;
        try {
            resultType = executor.execute(queryType);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }

        while (resultType.hasNext()) {
            List<ResultElement> rowType = resultType.next();
            String name                 = (String) rowType.get(0).getField();
            if(!allType.contains(name)){
                allType.add(name);
                String additon = "{name: '" + name + "'}";
                JsonType       = addToJsonList(JsonType, additon);
            }
        }

        JsonType = "[" + JsonType + "]";

        PathQuery queryGene                 = queryGenes(bag, model);

        ExportResultsIterator resultGene;
        try {
            resultGene = executor.execute(queryGene);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }

        while (resultGene.hasNext()) {
            List<ResultElement> rowGene = resultGene.next();
            String primaryIdentifier    = (String) rowGene.get(0).getField();
            String symbol               = (String) rowGene.get(1).getField();
            if (symbol == null) {
                symbol = primaryIdentifier;
            }
            //String additon  =  "'" + symbol + "'";
            String additon  =  "'" + primaryIdentifier + "'";
            JsonGene = addToJsonList(JsonGene, additon);
        }

        JsonGene = "[" + JsonGene + "]";

        String defaultValues = getDefault(bag, model, executor);

        request.setAttribute("type", JsonType);
        request.setAttribute("gene", JsonGene);
        request.setAttribute("FeatureCount", bag.getSize());
        request.setAttribute("ListName", bag.getName());
        request.setAttribute("ExpressionType", expressionType);
        request.setAttribute("APIKey", token);
        request.setAttribute("defaultValues", defaultValues);
    }

    private String addToJsonList(String value, String addition) {
        if(value.equals("")){
            return value + addition;
        } else {
            return value + ", " + addition;
        }
    }

    private PathQuery queryGenes(InterMineBag bag, Model model) {
        PathQuery query = new PathQuery(model);
        query.addViews("ExpressionValues.gene.primaryIdentifier", "ExpressionValues.gene.symbol");
        query.addOrderBy("ExpressionValues.gene.primaryIdentifier", OrderDirection.ASC);
        query.addConstraint(Constraints.in("ExpressionValues.gene", bag.getName()), "A");
        return query;
    }

    private PathQuery getQueryType(InterMineBag bag, Model model) {
        PathQuery query = new PathQuery(model);
        query.addView("ExpressionTypeDiccionary.name");
        query.addOrderBy("ExpressionTypeDiccionary.name", OrderDirection.ASC);
        query.addConstraint(Constraints.in("ExpressionTypeDiccionary.ExpressionValue.gene", bag.getName()));
        return query;
    }

    private PathQuery queryConditions(InterMineBag bag, Model model, String type, String experiment) {
        PathQuery query = new PathQuery(model);
        query.addView("ExpressionValues.condition");
        query.addOrderBy("ExpressionValues.condition", OrderDirection.ASC);
        query.addConstraint(Constraints.in("ExpressionValues.gene", bag.getName()), "A");
        query.addConstraint(Constraints.eq("ExpressionValues.experiment.name", experiment), "B");
        query.addConstraint(Constraints.eq("ExpressionValues.type.name", type), "C");
        query.setConstraintLogic("A and B and C");
        return query;
    }

    private  PathQuery getListPOfTypeExperiment(InterMineBag bag, Model model) {
        PathQuery query = new PathQuery(model);
        query.addViews("ExpressionTypeDiccionary.name", "ExpressionTypeDiccionary.ExpressionValue.experiment.name");
        query.addOrderBy("ExpressionTypeDiccionary.name", OrderDirection.ASC);
        query.addConstraint(Constraints.in("ExpressionTypeDiccionary.ExpressionValue.gene", bag.getName()));
        return query;
    }

    private int getCountofConditions(InterMineBag bag, Model model, String type, String experiment, PathQueryExecutor executor) {
        PathQuery queryConditions = queryConditions(bag, model, type, experiment);

        ExportResultsIterator resultConditions;
        try {
            resultConditions = executor.execute(queryConditions);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }

        int count = 0;

        while (resultConditions.hasNext()) {
            List<ResultElement> rowConditions = resultConditions.next();
            count++;            
        }

        return count;
    }

    private void chooseTypeExperiment(String type, String experiment) {
        Type_selected = type;
        Experiment_selected = experiment;
    }

    private String getJsonChoosedTypeExperiment() {
        return "{type: '" + Type_selected + "', experiment: '" + Experiment_selected + "'}";
    }

    private String getDefault(InterMineBag bag, Model model, PathQueryExecutor executor) {
        PathQuery queryListPOfTypeExperiment = getListPOfTypeExperiment(bag, model);

        ExportResultsIterator resultListPOfTypeExperiment;
        try {
            resultListPOfTypeExperiment = executor.execute(queryListPOfTypeExperiment);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }

        int less = 1000;

        while (resultListPOfTypeExperiment.hasNext()) {
            List<ResultElement> rowListPOfTypeExperiment = resultListPOfTypeExperiment.next();
            String Type = (String) rowListPOfTypeExperiment.get(0).getField();
            String Experiment = (String) rowListPOfTypeExperiment.get(1).getField();
            int count = getCountofConditions(bag, model, Type, Experiment, executor);
            if (Type_selected.equals("") || Experiment_selected.equals("")) {
                less = count;
                chooseTypeExperiment(Type, Experiment);
            } else {
                if (count < less) {
                    less = count;
                    chooseTypeExperiment(Type, Experiment);
                }
            }
        }
        return getJsonChoosedTypeExperiment();
    }
}
