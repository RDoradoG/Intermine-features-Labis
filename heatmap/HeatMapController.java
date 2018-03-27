package org.malariamine.web;

/*
 * Copyright (C) 2002-2014 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */
import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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

import org.json.JSONObject;

/**
 * Class that generates heatMap data for a list of genes.
 *
 * @author Rodrigo Dorado
 *
 */
public class HeatMapController extends TilesAction
{
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

        try {
            findExpression(request, model, bag, executor, os);
        } catch (ObjectStoreException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void findExpression(HttpServletRequest request, Model model, InterMineBag bag, PathQueryExecutor executor, ObjectStore os) throws ObjectStoreException {
        String expressionType                        = bag.getType().toLowerCase();
        DecimalFormat df                             = new DecimalFormat("#.##");
        String expressionScoreJSON                   = null;
        Map<String, List<Double>> expressionScoreMap = new LinkedHashMap<String, List<Double>>();
        ArrayList<String> Conditions                 = new ArrayList<String>();
        Double MaxScore                              = 0.0;
        Double MinScore                              = 0.0;
        int firstRow                                 = 0;

        PathQuery queryCondition                     = queryConditions(bag, model);
        PathQuery query                              = queryExpressionScore(bag, model);

        ExportResultsIterator resultConditions;
        try {
            resultConditions = executor.execute(queryCondition);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }

        while (resultConditions.hasNext()) {
            List<ResultElement> rowCondition = resultConditions.next();
            String setCondition              = (String) rowCondition.get(0).getField();
            if(!Conditions.contains(setCondition)){
                Conditions.add(setCondition);
            }
        }

        ExportResultsIterator result;
        try {
            result = executor.execute(query);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Error retrieving data.", e);
        }

        while (result.hasNext()) {
            List<ResultElement> row = result.next();
            String id               = (String) row.get(0).getField();
            String symbol           = (String) row.get(1).getField();
            String condition        = (String) row.get(2).getField();
            Double score            = (Double) row.get(3).getField();
            if (symbol == null) {
                symbol = id;
            }
            if(firstRow == 0){
                firstRow = 1;
                MaxScore = score;
                MinScore = score;
            }else{
                if(score > MaxScore){
                    MaxScore = score;
                }
                if(score < MinScore){
                    MinScore = score;
                }
            }
            if (!expressionScoreMap.containsKey(symbol)) {
                List<Double> ExpressionScores = new ArrayList<Double>(Collections.nCopies(Conditions.size(), 0.0));
                ExpressionScores.set(Conditions.indexOf(condition), score);
                expressionScoreMap.put(symbol, ExpressionScores);

            } else {
                expressionScoreMap.get(symbol).set(Conditions.indexOf(condition), score);
            }
        }
        expressionScoreJSON = parseToJSON(Conditions, expressionScoreMap);
        request.setAttribute("expressionScoreJSON", expressionScoreJSON);
        request.setAttribute("minExpressionScore", df.format(MinScore));
        request.setAttribute("maxExpressionScore", df.format(MaxScore));
        request.setAttribute("ExpressionType", expressionType);
        request.setAttribute("FeatureCount", bag.getSize());
    }

    private PathQuery queryExpressionScore(InterMineBag bag, Model model) {
        PathQuery query = new PathQuery(model);
        // Select the output columns:
        query.addViews("ExpressionValues.gene.primaryIdentifier", "ExpressionValues.gene.symbol", "ExpressionValues.condition", "ExpressionValues.expressionValue");
        // Add orderby
        query.addOrderBy("ExpressionValues.gene.primaryIdentifier", OrderDirection.ASC);
        // Filter the results with the following constraints:
        query.addConstraint(Constraints.in("ExpressionValues.gene", bag.getName()));
        return query;
    }

    private PathQuery queryConditions(InterMineBag bag, Model model) {
        PathQuery query = new PathQuery(model);
        // Select the output columns:
        query.addViews("ExpressionValues.condition");
        // Add orderby
        query.addOrderBy("ExpressionValues.condition", OrderDirection.ASC);
        // Filter the results with the following constraints:
        query.addConstraint(Constraints.in("ExpressionValues.gene", bag.getName()));
        return query;
    }

    /**
     * Parse expressionScoreMap to JSON string
     *
     * @param conditionType CellLine or DevelopmentalStage
     * @param geneExpressionScoreMap
     * @return json string
     */
    private String parseToJSON(ArrayList<String> vars, Map<String, List<Double>> Scores) {
        if (Scores.size() == 0) {
            return "{}";
        }

        Map<String, Object> heatmapData    = new LinkedHashMap<String, Object>();
        Map<String, Object> yInHeatmapData =  new LinkedHashMap<String, Object>();
        List<String> smps                  =  new ArrayList<String>(Scores.keySet());
        List<String> desc                  =  new ArrayList<String>();
        double[][] data                    = new double[smps.size()][vars.size()];
        double[][] rotatedData             = new double[vars.size()][smps.size()];

        desc.add("Intensity");
        for (int i = 0; i < smps.size(); i++) {
            String seqenceFeature = smps.get(i);
            for (int j = 0; j < vars.size(); j++) {
                data[i][j] = Scores.get(seqenceFeature).get(j);
            }
        }
        int ii = 0;
        for (int i = 0; i < vars.size(); i++) {
            int jj = 0;
            for (int j = 0; j < smps.size(); j++) {
                rotatedData[ii][jj] = data[j][i];
                jj++;
            }
            ii++;
        }
        yInHeatmapData.put("vars", vars);
        yInHeatmapData.put("smps", smps);
        yInHeatmapData.put("desc", desc);
        yInHeatmapData.put("data", rotatedData);
        heatmapData.put("y", yInHeatmapData);
        JSONObject jo = new JSONObject(heatmapData);
        return jo.toString();
    }
}
