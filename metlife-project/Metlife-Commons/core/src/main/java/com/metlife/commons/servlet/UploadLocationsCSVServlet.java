package com.metlife.commons.servlet;

import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Asset;

import com.google.common.collect.Maps;
import com.metlife.poc.util.MetLifeConstants;

import com.day.cq.commons.jcr.JcrUtil;

import com.google.gson.Gson;
import com.xumak.base.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.NodeIterator;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by pgarcia on 7/19/16.
 */

@Component
@Service
@Properties({
        @Property(name = "service.description", value = "MetLife CSV Locations Upload " + "Servlet"),
        @Property(name = "sling.servlet.selectors", value = "csv-locations"),
        @Property(name = "sling.servlet.extensions", value = "json"),
        @Property(name = "sling.servlet.resourceTypes",
        value = {"metlifeCommons/components/section/choose-locations-csv-file"})
        })
public class UploadLocationsCSVServlet extends SlingSafeMethodsServlet {

    /**
     * EMPTY_TEXT CONSTANT.
     */
    private static final String EMPTY_TEXT = "";
    /**
     * SPLIT_CHAR CONSTANT.
     */
    private static final String SPLIT_CHAR = ";";
    /**
     * SUCCESS FLAG.
     */
    public static final String SUCCESS = "success";
    /**
     * RESPONSE BODY.
     */
    public static final String BODY = "BODY";
    /**
     * RESULTS.
     */
    public static final String RESULT = "result";
    /**
     * CSV FILE PATH.
     */
    public static final String FILE_PATH = "filePath";
    /**
     * VAR_DIR.
     * Constant String that references the /var directory
     */
    private static final String VAR_DIR = "/var";
    /**
     * DAM_DIR.
     * Constant String that references the /dam directory
     */
    private static final String DAM_DIR = "/dam";
    /**
     * JCR_DATA.
     * Constant String that references the jcr:data node name
     */
    private static final String JCR_DATA = "jcr:data";
    /**
     * JCR_ENCODING.
     * Constant String that references the jcr:encoding node name
     */
    private static final String JCR_ENCODING = "jcr:encoding";
    /**
     * LOCATIONS_PATH.
     * Constant String that references the csv uploaded locations.
     */
    private static final String LOCATIONS_PATH = "/etc/metlife/locations";
    /**
     * EXPECTED_HEADER_FIELDS.
     * Immutable list containing the name of the accepted header fields
     */
    private static final List<String> EXPECTED_HEADER_FIELDS = Arrays.asList("title", "address1", "address2", "state",
            "type of office", "city", "zipcode", "telephone", "telephone2", "fax", "contact person", "speciality1",
            "speciality2", "speciality3", "speciality4", "speciality5", "working hours", "latitude", "longitude",
            "additional info", "zoomin index");
    /**
     * COMPONENT_HEADER_FIELDS.
     * Immutable list containing the name of the accepted header fields
     */
    private static final List<String> COMPONENT_HEADER_FIELDS = Arrays.asList("officeName", "address", "address2",
            "state", "typeOfOffice", "city", "zip", "phone", "alternatePhone", "fax", "mainContact", "speciality1",
            "speciality2", "speciality3", "speciality4", "speciality5", "workingHours", "latitude", "longitude",
            "additionalInfo", "initialZoom");
    /**
     * LOCATION COMPONENT PATH.
     * Constant String that references the location component path
     */
    private static final String LOCATION_COMPONENT_PATH =  "metlifeCommons/components/section/location-csv";
    /**
     * LOGGER.
     */

    @SuppressWarnings("unchecked")
    @Override
        protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {

        Boolean responseStatus;
        String responseMessage;

        try {
            final String filePath = request.getParameter(FILE_PATH);
            final Gson gson = new Gson();
            final ArrayList<List<String>> officesUploaded = new ArrayList<List<String>>();

            final ResourceResolver resourceResolver = request.getResourceResolver();
            final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            Asset file = null;

            final String damPath = VAR_DIR + filePath.substring(filePath.indexOf(DAM_DIR));
            file = assetManager.getAssetForBinary(damPath);

            final Object fileBinary = file.getOriginal().getProperties().get(JCR_DATA);
            final String fileEncoding = (String) file.getOriginal().getProperties().get(JCR_ENCODING);
            final String fileContent = IOUtils.toString((InputStream) fileBinary, fileEncoding);
            final StringReader stringReader = new StringReader(fileContent);
            final BufferedReader bufferedReader = new BufferedReader(stringReader);
            String line = EMPTY_TEXT;

            line = bufferedReader.readLine();
            final List<String> columnHeaders = Arrays.asList(line.split(SPLIT_CHAR));  //First line - column headers

            final String columnHeadersAreOk = checkColumnHeaders(columnHeaders);

            if (columnHeadersAreOk.length() > 1) {
                responseStatus = false;
                responseMessage = "File couldn't be uploaded. <br> Please check that the column headers "
                        + "are in a valid format and "
                        + "have the correct names.  <br> " + columnHeadersAreOk;
            } else {

                final ArrayList<String> componentColumnHeaders = checkColumnHeadersNames(columnHeaders);

                if (!componentColumnHeaders.isEmpty()) {

                    final String locationsPath = request.getHeader("referer");
                    final String[] locationsPathArray = locationsPath.split(Constants.SLASH);
                    final String locationPageNode = locationsPathArray[locationsPathArray.length - 1].split("\\.")[0];

                    final Session session = resourceResolver.adaptTo(Session.class);
                    Integer locationId = 1;

                    //Removing old location nodes
                    final Resource resource = resourceResolver.getResource(LOCATIONS_PATH + Constants.SLASH
                            + locationPageNode
                            + Constants.SLASH + Constants.JCR_CONTENT + Constants.SLASH
                            + MetLifeConstants.LOCATIONS);
                    if (resource != null) {
                        final Node locationsPage = resource.adaptTo(Node.class);
                        final NodeIterator locationPageChildren = locationsPage.getNodes();

                        //iterates over the locations page to remove the old locations
                        while (locationPageChildren.hasNext()) {
                            locationPageChildren.nextNode().remove();
                        }
                    } else {
                        final Node rootNode = JcrUtil.createPath(LOCATIONS_PATH + Constants.SLASH
                                + locationPageNode + Constants.SLASH + Constants.JCR_CONTENT
                                + Constants.SLASH + MetLifeConstants.LOCATIONS + Constants.SLASH,
                                JcrConstants.NT_UNSTRUCTURED, session);

                        rootNode.setProperty("sling:resourceType", "foundation/components/parsys");
                    }

                    line = bufferedReader.readLine();

                    officesUploaded.add(componentColumnHeaders);

                    //Creating the new content nodes for new locations
                    //ArrayList<String> lines = new ArrayList<String>();
                    while (line != null) {
                        final String[] lineSplitted = line.split(SPLIT_CHAR);

                        final Node newNode = JcrUtil.createPath(LOCATIONS_PATH + Constants.SLASH + locationPageNode
                                + Constants.SLASH + Constants.JCR_CONTENT
                                + Constants.SLASH + MetLifeConstants.LOCATIONS
                                + Constants.SLASH + locationId.toString(), JcrConstants.NT_UNSTRUCTURED, session);

                        newNode.setProperty("sling:resourceType", LOCATION_COMPONENT_PATH);

                        final String[] officeSpecialties =  getOfficeSpecialties(lineSplitted);

                        if (officeSpecialties != null) {
                            newNode.setProperty(MetLifeConstants.SPECIALTIES, officeSpecialties);
                        }

                        lineLoop: for (int i = 0; i < lineSplitted.length; i++) {
                            if (i > NUMBER.TEN.getValue() && i < NUMBER.SIXTEEN.getValue()) {
                                continue lineLoop;
                            }
                            newNode.setProperty(componentColumnHeaders.get(i), lineSplitted[i]);
                        }

                        officesUploaded.add(Arrays.asList(lineSplitted));

                        line = bufferedReader.readLine();
                        locationId++;
                    }

                    session.save();
                    responseMessage = "Locations uploaded!!!";
                    responseStatus = true;
                } else {
                    responseMessage = "Please check the names of the column headers";
                    responseStatus = false;
                }

            }
            final Map<String, Object> wrapper = Maps.newHashMap();
            wrapper.put(BODY, officesUploaded);
            wrapper.put(SUCCESS, responseStatus);
            wrapper.put(RESULT, responseMessage);

            response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);

            response.getWriter().write(gson.toJson(wrapper));
        } catch (Exception e) {
            e.printStackTrace(response.getWriter());
            //.write();
        }
    }

    /**
     * @param columnHeaders is a list containing the headers of the file
     * @return response is a string value.
     * Function to check that the column headers are valid
     */
    protected final String checkColumnHeaders(final List<String> columnHeaders) {

        String response = "";

        if (columnHeaders.size() > EXPECTED_HEADER_FIELDS.size()) {
            response = "Number of columns should be of " + EXPECTED_HEADER_FIELDS.size();
        }

        return response;
    }

    /**
     * @param columnHeaders is a list containing the headers of the file
     * @return response is a string value.
     * Function to check that the column headers names are valid
     */
    protected final ArrayList<String> checkColumnHeadersNames(final List<String> columnHeaders) {

        ArrayList<String> response = new ArrayList<>();
        boolean errors = false;

        for (int i = 0; i < columnHeaders.size(); i++) {
            if (columnHeaders.get(i).equalsIgnoreCase(EXPECTED_HEADER_FIELDS.get(i))) {
                response.add(COMPONENT_HEADER_FIELDS.get(i));
            } else {
                errors = true;
            }
        }

        if (errors) {
            response = new ArrayList<>(0);
        }

        return response;
    }

    /**
     * @param fileLineSplitted is a line of the CSV file, splitted
     * @return officeSpecialties is a Array String.
     * Function to get the office specialties from the csv line.
     */
    protected final String[] getOfficeSpecialties(final String[] fileLineSplitted) {

        final String[] officeSpecialties = new String[NUMBER.TEN.getValue()];
        int counter = 0;

        //Specialties are in columns 11 to 15 of the CSV File
        for (int i = NUMBER.ELEVEN.getValue(); i < NUMBER.SIXTEEN.getValue(); i++) {
            if (!fileLineSplitted[i].isEmpty()) {
                officeSpecialties[counter++] = fileLineSplitted[i];
            }
        }
        return officeSpecialties;
    }

    /**
     * @return value value of enum.
     * ENUM to manage numbers
     */
    private enum NUMBER {
        CERO(0),
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        ELEVEN(11),
        TWELVE(12),
        THIRTEEN(13),
        FOURTEEN(14),
        FIFTEEN(15),
        SIXTEEN(16),
        SEVENTEEN(17),
        EIGHTEEN(18),
        NINETEEN(19),
        TWENTY(20),
        TWENTY_ONE(21),
        TWENTY_TWO(22);

        private final int value;

        /**
         * @param value of the enum
         * ENUM constructor.
         */
        NUMBER(final int value) {
            this.value = value;
        }

        /**
         * getValue of the enum.
         * @return value of the enum
         */
        public int getValue() {
            return value;
        }
    }
}
