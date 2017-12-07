package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.DeprecateLocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.IdentityFactory;
import nu.postnummeruppror.insamlingsappen.transactions.version_0_0_6.CreateLocationSample;
import se.kodapan.osm.domain.Node;
import se.kodapan.osm.domain.Way;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import java.util.*;

/**
 * @author kalle
 * @since 2017-01-30 18:58
 */
public class Helsingborgsimport {

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {


      // 1. deprecate old data within the hull of the imported data

      GeometryFactory geometryFactory = new GeometryFactory();


      Set<String> postorterOnlyInHelsingborgKommun = new HashSet<>();
      postorterOnlyInHelsingborgKommun.add("GANTOFTA");
      postorterOnlyInHelsingborgKommun.add("ÖDÅKRA");
      postorterOnlyInHelsingborgKommun.add("MÖRARP");
      postorterOnlyInHelsingborgKommun.add("FLENINGE");
      postorterOnlyInHelsingborgKommun.add("PÅARP");
      postorterOnlyInHelsingborgKommun.add("KATTARP");
      postorterOnlyInHelsingborgKommun.add("HASSLARP");
      postorterOnlyInHelsingborgKommun.add("ALLERUM");
      postorterOnlyInHelsingborgKommun.add("ÖDÅKRA-VÄLA");
      postorterOnlyInHelsingborgKommun.add("RÅÅ");
      postorterOnlyInHelsingborgKommun.add("RYDEBÄCK");
      postorterOnlyInHelsingborgKommun.add("VALLÅKRA");
      postorterOnlyInHelsingborgKommun.add("DOMSTEN");
      postorterOnlyInHelsingborgKommun.add("HELSINGBORG");
      postorterOnlyInHelsingborgKommun.add("RAMLÖSA");



      Set<LocationSample> deprecationSamples = new HashSet<>();

      PojoRoot hullRoot = new PojoRoot();
      {
        InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
        parser.setRoot(hullRoot);
        parser.parse(Helsingborgsimport.class.getResourceAsStream("/helsingborg hull.osm.xml"));
      }
      Way hullWay = hullRoot.getWays().values().iterator().next();
      com.vividsolutions.jts.geom.Coordinate[] hullCoordinates = new com.vividsolutions.jts.geom.Coordinate[hullWay.getNodes().size()];
      List<Node> nodes = hullWay.getNodes();
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        hullCoordinates[i] = new com.vividsolutions.jts.geom.Coordinate(node.getX(), node.getY());
      }
      LinearRing hullShell = geometryFactory.createLinearRing(hullCoordinates);
      Polygon hullPolygon = new Polygon(hullShell, null, geometryFactory);
      for (LocationSample sample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
        if (sample.getCoordinate() != null
            && sample.getCoordinate().getLatitude() != null
            && sample.getCoordinate().getLongitude() != null) {

          if (hullPolygon.contains(geometryFactory.createPoint(
              new com.vividsolutions.jts.geom.Coordinate(
                  sample.getCoordinate().getLongitude(),
                  sample.getCoordinate().getLatitude()
              )))) {
            deprecationSamples.add(sample);
          }
        } else {
          String addrCity = sample.getTag("addr:city");
          if (addrCity != null
              && postorterOnlyInHelsingborgKommun.contains(addrCity.toUpperCase().trim())) {
            deprecationSamples.add(sample);
          }
//          String postnummer = sample.getTag("addr:postcode");
//          if (postnummer != null
//              && postnummerOnlyInHelsingborgKommun.contains(postnummer.trim())) {
//            deprecationSamples.add(sample);
//          }

        }
      }

      Map<String, List<LocationSample>> samplesByEmail = new HashMap<>();
      Map<String, Set<Account>> accountsByEmail = new HashMap<>();
      for (LocationSample sample : deprecationSamples) {
        if (sample.getAccount().getEmailAddress() != null) {
          String normalizedEmailAddress = sample.getAccount().getEmailAddress().toLowerCase();
          Set<Account> accounts = accountsByEmail.get(normalizedEmailAddress);
          if (accounts == null) {
            accounts = new HashSet<>();
            accountsByEmail.put(normalizedEmailAddress, accounts);
          }
          accounts.add(sample.getAccount());
          List<LocationSample> emailSamples = samplesByEmail.get(normalizedEmailAddress);
          if (emailSamples == null) {
            emailSamples = new ArrayList<>();
            samplesByEmail.put(normalizedEmailAddress, emailSamples);
          }
          emailSamples.add(sample);
        }
      }

      for (LocationSample sample : deprecationSamples) {
        Insamlingsappen.getInstance().getPrevayler().execute(
            new DeprecateLocationSample(
                sample.getIdentity(),
                "Overridden by import of official data from Helsingborg kommun"
            )
        );
      }


      // 2. import helsingborgdata


      PojoRoot dataRoot = new PojoRoot();
      {
        InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
        parser.setRoot(dataRoot);
        parser.parse(Helsingborgsimport.class.getResourceAsStream("/helsingborg adresspunkter.osm.xml"));
      }


      for (Node node : dataRoot.getNodes().values()) {
        CreateLocationSample createLocationSample = new CreateLocationSample();

        createLocationSample.setLocationSampleIdentity(Insamlingsappen.getInstance().getPrevayler().execute(new IdentityFactory()));
        createLocationSample.setAccountIdentity("Helsingborgimport version 0.0.1");

        createLocationSample.setApplication("Helsingborgimport");
        createLocationSample.setApplicationVersion("0.0.1");

        createLocationSample.setCoordinate(new Coordinate());
        createLocationSample.getCoordinate().setLatitude(node.getLatitude());
        createLocationSample.getCoordinate().setLongitude(node.getLongitude());
        createLocationSample.getCoordinate().setAccuracy(1d);
        createLocationSample.getCoordinate().setProvider("geojson converted");
        String postcode = node.getTag("addr:postcode");
        if (postcode != null) {
          postcode = postcode.replaceAll("\\s", "");
          if (postcode.matches("[0-9]{5}")) {
            createLocationSample.getTags().put("addr:postcode", postcode);
          }
        }
        String city = node.getTag("addr:city");
        if (city != null) {
          createLocationSample.getTags().put("addr:city", city);
        }
        if (!createLocationSample.getTags().isEmpty()) {
          Insamlingsappen.getInstance().getPrevayler().execute(createLocationSample);

        } else {
          System.currentTimeMillis();
        }
      }

      System.currentTimeMillis();
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }
}