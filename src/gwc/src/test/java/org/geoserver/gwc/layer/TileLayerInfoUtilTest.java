/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.geoserver.gwc.GWC.tileLayerName;
import static org.geoserver.gwc.GWCTestHelpers.mockGroup;
import static org.geoserver.gwc.GWCTestHelpers.mockLayer;

import org.geoserver.catalog.impl.LayerGroupInfoImpl;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.config.GWCConfig;
import org.geowebcache.filter.parameters.FloatParameterFilter;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.geowebcache.filter.parameters.RegexParameterFilter;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Unit test suite for {@link TileLayerInfoUtil}
 * 
 */
public class TileLayerInfoUtilTest {

    private GWCConfig defaults;

    private GeoServerTileLayerInfo defaultVectorInfo;

    @Before
    public void setup() throws Exception {
        defaults = GWCConfig.getOldDefaults();
        defaultVectorInfo = TileLayerInfoUtil.create(defaults);
        defaultVectorInfo.getMimeFormats().clear();
        defaultVectorInfo.getMimeFormats().addAll(defaults.getDefaultVectorCacheFormats());
    }

    @Test
    public void testCreateLayerInfo() {
        LayerInfoImpl layer = mockLayer("testLayer",new String[]{}, LayerInfoImpl.Type.RASTER);
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        defaultVectorInfo.setId(layer.getId());
        defaultVectorInfo.setName(tileLayerName(layer));
        assertNotNull(info);
        assertEquals(defaultVectorInfo, info);
    }

    @Test
    public void testCreateLayerGroupInfo() {
        LayerGroupInfoImpl group = mockGroup("testGroup", mockLayer("testLayer",new String[]{}, LayerInfoImpl.Type.RASTER));

        defaults.getDefaultOtherCacheFormats().clear();
        defaults.getDefaultOtherCacheFormats().add("image/png8");
        defaults.getDefaultOtherCacheFormats().add("image/jpeg");

        GeoServerTileLayerInfo expected = TileLayerInfoUtil.create(defaults);
        expected.setId(group.getId());
        expected.setName(GWC.tileLayerName(group));

        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(group, defaults);
        assertNotNull(info);
        assertEquals(expected, info);
    }

    @Test
    public void testCreateLayerInfoAutoCacheStyles() {
        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.setAutoCacheStyles(true);

        defaults.setCacheNonDefaultStyles(true);

        LayerInfoImpl layer = mockLayer("testLayer", new String[]{"style1", "style2"}, LayerInfoImpl.Type.RASTER);

        GeoServerTileLayerInfo actual;
        actual = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        
        TileLayerInfoUtil.checkAutomaticStyles(layer, info);

        TileLayerInfoUtil.setCachedStyles(info, "default", ImmutableSet.of("style1", "style2"));

        layer.setDefaultStyle(null);
        TileLayerInfoUtil.setCachedStyles(info, "", ImmutableSet.of("style1", "style2"));

        actual = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        assertEquals(ImmutableSet.of("style1", "style2"), actual.cachedStyles());
    }

    @Test
    public void testCreateLayerGroup() {
        LayerGroupInfoImpl lg = mockGroup("tesGroup", mockLayer("L1",new String[]{}, LayerInfoImpl.Type.RASTER), mockLayer("L2",new String[]{}, LayerInfoImpl.Type.RASTER));

        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.setId(lg.getId());
        info.setName(GWC.tileLayerName(lg));
        info.getMimeFormats().clear();
        info.getMimeFormats().addAll(defaults.getDefaultOtherCacheFormats());

        GeoServerTileLayerInfo actual;
        actual = TileLayerInfoUtil.loadOrCreate(lg, defaults);

        assertEquals(info, actual);
    }

    @Test
    public void testUpdateAcceptAllRegExParameterFilter() {
        GeoServerTileLayerInfo info = defaultVectorInfo;

        // If createParam is false and there isn't already a filter, don't create one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", false);
        assertNull(TileLayerInfoUtil.findParameterFilter("ENV", info.getParameterFilters()));
        
        // If createParam is true and there isn't already a filter, create one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", true);
        ParameterFilter filter = TileLayerInfoUtil.findParameterFilter("ENV",
                info.getParameterFilters());
        assertTrue(filter instanceof RegexParameterFilter);
        assertEquals(".*", ((RegexParameterFilter) filter).getRegex());

        // If createParam is true and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", true);
        ParameterFilter filter2 = TileLayerInfoUtil.findParameterFilter("ENV",
                info.getParameterFilters());
        assertNotSame(filter, filter2);
        assertEquals(filter, filter2);
        
        // If createParam is false and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", false);
        ParameterFilter filter3 = TileLayerInfoUtil.findParameterFilter("ENV",
                info.getParameterFilters());
        assertNotSame(filter2, filter3);
        assertEquals(filter, filter3);
    }

    @Test
    public void testUpdateAcceptAllFloatParameterFilter() {
        GeoServerTileLayerInfo info = defaultVectorInfo;
        
        // If createParam is false and there isn't already a filter, don't create one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", false);
        assertNull(TileLayerInfoUtil.findParameterFilter("ELEVATION", info.getParameterFilters()));
        
        
        // If createParam is true and there isn't already a filter, create one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", true);
        ParameterFilter filter = TileLayerInfoUtil.findParameterFilter("ELEVATION",
                info.getParameterFilters());
        assertTrue(filter instanceof FloatParameterFilter);
        assertEquals(0, ((FloatParameterFilter) filter).getValues().size());

        // If createParam is true and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", true);
        ParameterFilter filter2 = TileLayerInfoUtil.findParameterFilter("ELEVATION",
                info.getParameterFilters());
        assertNotSame(filter, filter2);
        assertEquals(filter, filter2);

        // If createParam is false and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", false);
        ParameterFilter filter3 = TileLayerInfoUtil.findParameterFilter("ELEVATION",
                info.getParameterFilters());
        assertNotSame(filter2, filter3);
        assertEquals(filter, filter3);
        
    }

}
