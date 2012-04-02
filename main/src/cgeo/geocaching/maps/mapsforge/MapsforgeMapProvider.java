package cgeo.geocaching.maps.mapsforge;

import cgeo.geocaching.IWaypoint;
import cgeo.geocaching.R;
import cgeo.geocaching.cgeoapplication;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.geopoint.Geopoint;
import cgeo.geocaching.go4cache.Go4CacheUser;
import cgeo.geocaching.maps.MapProviderFactory;
import cgeo.geocaching.maps.interfaces.CachesOverlayItemImpl;
import cgeo.geocaching.maps.interfaces.GeoPointImpl;
import cgeo.geocaching.maps.interfaces.MapProvider;
import cgeo.geocaching.maps.interfaces.OtherCachersOverlayItemImpl;

import org.apache.commons.lang3.StringUtils;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.header.FileOpenResult;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapsforgeMapProvider implements MapProvider {

    public final static int MAPNIK = 1;
    public final static int CYCLEMAP = 3;
    public final static int OFFLINE = 4;

    private final Map<Integer, String> mapSources;

    private final int baseId;

    public MapsforgeMapProvider(int _baseId) {
        baseId = _baseId;
        final Resources resources = cgeoapplication.getInstance().getResources();

        mapSources = new HashMap<Integer, String>();
        mapSources.put(baseId + MAPNIK, resources.getString(R.string.map_source_osm_mapnik));
        mapSources.put(baseId + CYCLEMAP, resources.getString(R.string.map_source_osm_cyclemap));
        mapSources.put(baseId + OFFLINE, resources.getString(R.string.map_source_osm_offline));
    }

    @Override
    public Map<Integer, String> getMapSources() {

        return mapSources;
    }

    @Override
    public boolean isMySource(int sourceId) {
        return sourceId >= baseId + MAPNIK && sourceId <= baseId + OFFLINE;
    }

    public static int getMapsforgeSource(int sourceId) {
        MapProvider mp = MapProviderFactory.getMapProvider(sourceId);
        if (mp instanceof MapsforgeMapProvider) {
            MapsforgeMapProvider mfp = (MapsforgeMapProvider) mp;
            return sourceId - mfp.baseId;
        }
        return 0;
    }

    public static boolean isValidMapFile(String mapFileIn) {

        if (StringUtils.isEmpty(mapFileIn)) {
            return false;
        }

        MapDatabase mapDB = new MapDatabase();
        FileOpenResult result = mapDB.openFile(new File(mapFileIn));
        mapDB.closeFile();

        return result.isSuccess();
    }

    @Override
    public Class<? extends Activity> getMapClass() {
        return MapsforgeMapActivity.class;
    }

    @Override
    public int getMapViewId() {
        return R.id.mfmap;
    }

    @Override
    public int getMapLayoutId() {
        return R.layout.map_mapsforge;
    }

    @Override
    public GeoPointImpl getGeoPointBase(final Geopoint coords) {
        return new MapsforgeGeoPoint(coords.getLatitudeE6(), coords.getLongitudeE6());
    }

    @Override
    public CachesOverlayItemImpl getCachesOverlayItem(final IWaypoint coordinate, final CacheType type) {
        MapsforgeCacheOverlayItem baseItem = new MapsforgeCacheOverlayItem(coordinate, type);
        return baseItem;
    }

    @Override
    public OtherCachersOverlayItemImpl getOtherCachersOverlayItemBase(Context context, Go4CacheUser userOne) {
        MapsforgeOtherCachersOverlayItem baseItem = new MapsforgeOtherCachersOverlayItem(context, userOne);
        return baseItem;
    }

}
