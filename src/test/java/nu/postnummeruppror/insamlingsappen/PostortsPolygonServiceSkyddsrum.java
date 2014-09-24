package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import se.kodapan.osm.jts.voronoi.AdjacentClassVoronoiClusterer;
import se.kodapan.osm.jts.voronoi.GeoJSONVoronoiFactory;

import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-20 22:32
 */
public class PostortsPolygonServiceSkyddsrum {

  public static void main(String[] args) throws Exception {
    new PostortsPolygonServiceSkyddsrum().factory();

  }

  public void factory() throws Exception {

    JSONArray skyddsrumJSON = new JSONArray(new JSONTokener(new InputStreamReader(getClass().getResourceAsStream("/skyddsrum-ej-cc0.json"), "UTF8")));


    AdjacentClassVoronoiClusterer<String> voronoiClusterer = new AdjacentClassVoronoiClusterer<>(new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326));
    voronoiClusterer.setNumberOfThreads(4);

    for (int i = 0; i < skyddsrumJSON.length(); i++) {

      JSONObject skyddsrum = skyddsrumJSON.getJSONObject(i);

      if (skyddsrum.has("kommunnamn")) {
        String postalTown = skyddsrum.getString("kommunnamn").trim().toUpperCase();
        if (!postalTown.isEmpty()) {
          voronoiClusterer.addCoordinate(postalTown, skyddsrum.getDouble("longitud"), skyddsrum.getDouble("latitud"));
        }
      }
    }

    Map<String, List<Polygon>> voronoiClusters = voronoiClusterer.build();


    Writer writer = new OutputStreamWriter(new FileOutputStream("src/test/resources/postalTowns-skyddsrum.geo.json"), "UTF8");
    GeoJSONVoronoiFactory<String> geojsonFactory = new GeoJSONVoronoiFactory<String>() {
    };
    geojsonFactory.factory(voronoiClusters);
    geojsonFactory.getRoot().writeJSON(writer);
    writer.close();


  }
}
