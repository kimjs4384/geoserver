/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csw.xml.v2_0_2;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.geoserver.ows.XmlRequestReader;
import org.geoserver.platform.ServiceException;
import org.geotools.csw.CSWConfiguration;
import org.geotools.util.Version;
import org.geotools.util.logging.Logging;
import org.geotools.xml.Parser;

/**
 * CSW XML parser
 * 
 * @author Andrea Aime - GeoSolutions
 */
public class CSWXmlReader extends XmlRequestReader {
    public Logger LOGGER = Logging.getLogger("org.geoserver.csw");

    private CSWConfiguration configuration;

    public CSWXmlReader(String element, String version, CSWConfiguration configuration) {
        super(new QName(org.geotools.csw.CSW.NAMESPACE, element), new Version("2.0.2"), "csw");
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    public Object read(Object request, Reader reader, Map kvp) throws Exception {
        Parser parser = new Parser(configuration);
        parser.setValidating(true);
        parser.setFailOnValidationError(true);
        parser.setStrict(true);

        Object parsed;
        try {
            parsed = parser.parse(reader);
        } catch (Exception e) {
            throw new ServiceException("Could not parse XML request.", e);
        }

        if (!parser.getValidationErrors().isEmpty()) {
            ServiceException exception = new ServiceException("Invalid request",
                    "InvalidParameterValue");

            for (Exception error : (List<Exception>) parser.getValidationErrors()) {
                LOGGER.warning(error.getLocalizedMessage());
                exception.getExceptionText().add(error.getLocalizedMessage());
            }
        }

        return parsed;
    }
}
