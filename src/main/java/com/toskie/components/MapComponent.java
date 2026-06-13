package com.toskie.components;
import com.microsoft.playwright.*;
import com.toskie.utils_Layer.*;

public class MapComponent {
    private final UtilLayer<?> util;
    private final Locator mapContainer, clusters, pins, zoomIn, zoomOut;

    public MapComponent(UtilLayer<?> util) {
        this.util = util;
        Page page = BrowserManager.getPage();
        mapContainer = page.locator("[class*='map-container'], #map, [class*='leaflet']").first();
        clusters     = page.locator("[class*='cluster'], [class*='marker-cluster']");
        pins         = page.locator("[class*='map-pin'], [class*='marker']:not([class*='cluster'])");
        zoomIn       = page.locator("[class*='zoom-in'], [aria-label='Zoom in']").first();
        zoomOut      = page.locator("[class*='zoom-out'], [aria-label='Zoom out']").first();
    }

    public boolean isMapVisible()     { try { return mapContainer.isVisible(); } catch (Exception e) { return false; } }
    public int getClusterCount()      { return (int) clusters.count(); }
    public void clickCluster(int idx) { clusters.nth(idx).click(); }
    public void clickPin(int idx)     { pins.nth(idx).click(); }
    public void zoomIn()              { util.click(zoomIn, "Map Zoom In"); }
    public void zoomOut()             { util.click(zoomOut, "Map Zoom Out"); }
}
