package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.*;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kalle
 * @since 2014-09-06 20:30
 */
public class CreateLocationSample implements TransactionWithQuery<Root, LocationSample>, Serializable {

  private static final long serialVersionUID = 1l;

  public CreateLocationSample() {
  }

  private String accountIdentity;
  private Long locationSampleIdentity;

  private String application;
  private String applicationVersion;

  private String name;
  private PostalAddress postalAddress;
  private Coordinate coordinate;

  private String ipAddress;
  private String ipAddressHost;

  @Override
  public LocationSample executeAndQuery(Root root, Date executionTime) throws Exception {

    Account account = root.getAccounts().get(accountIdentity);
    if (account == null) {
      // create new account if not existing
      account = new Account();
      account.setTimestampCreated(executionTime.getTime());
      account.setIdentity(accountIdentity);
      root.getAccounts().put(account.getIdentity(), account);
    }

    LocationSample locationSample = new LocationSample();
    locationSample.setIdentity(locationSampleIdentity);

    locationSample.setTimestamp(executionTime.getTime());

    locationSample.setApplication(root.getApplicationIntern().intern(application));
    locationSample.setApplicationVersion(root.getApplicationVersionIntern().intern(applicationVersion));

    if (postalAddress != null) {

      if (postalAddress.getPostalTown() != null) {
        postalAddress.setPostalTown(postalAddress.getPostalTown().trim());
        if (postalAddress.getPostalTown().isEmpty()) {
          postalAddress.setPostalTown(null);
        } else {
          locationSample.setTag(root.getTagsIntern().internKey("addr:city"), root.getTagsIntern().internValue("addr:city", postalAddress.getPostalTown()));
        }
      }

      if (postalAddress.getPostalCode() != null) {
        postalAddress.setPostalCode(postalAddress.getPostalCode().trim());
        if (postalAddress.getPostalCode().isEmpty()) {
          postalAddress.setPostalCode(null);
        } else {
          locationSample.setTag(root.getTagsIntern().internKey("addr:postcode"), root.getTagsIntern().internValue("addr:postcode", postalAddress.getPostalCode()));
        }
      }

      if (postalAddress.getStreetName() != null) {
        postalAddress.setStreetName(postalAddress.getStreetName().trim());
        if (postalAddress.getStreetName().isEmpty()) {
          postalAddress.setStreetName(null);
        } else {
          locationSample.setTag(root.getTagsIntern().internKey("addr:street"), root.getTagsIntern().internValue("addr:street", postalAddress.getStreetName()));

        }
      }

      if (postalAddress.getHouseNumber() != null) {
        postalAddress.setHouseNumber(postalAddress.getHouseNumber().trim());
        if (postalAddress.getHouseNumber().isEmpty()) {
          postalAddress.setHouseNumber(null);
        } else {
          locationSample.setTag(root.getTagsIntern().internKey("addr:housenumber"), root.getTagsIntern().internValue("addr:housenumber", postalAddress.getHouseNumber()));
        }
      }

      if (postalAddress.getHouseName() != null) {
        postalAddress.setHouseName(postalAddress.getHouseName().trim());
        if (postalAddress.getHouseName().isEmpty()) {
          postalAddress.setHouseName(null);
        } else {
          locationSample.setTag(root.getTagsIntern().internKey("addr:housename"), root.getTagsIntern().internValue("addr:housename", postalAddress.getHouseName()));
        }
      }
    }

    if (coordinate != null) {

      if (coordinate.getProvider() != null) {
        coordinate.setProvider(coordinate.getProvider().trim());
        if (coordinate.getProvider().isEmpty()) {
          coordinate.setProvider(null);
        } else {
          coordinate.setProvider(root.getProviderIntern().intern(coordinate.getProvider()));
        }
      }

      if (coordinate.getLatitude() == null
          && coordinate.getLongitude() == null) {
        coordinate = null;
      }

      locationSample.setCoordinate(coordinate);

    }

    if (name != null) {
      name = name.trim();
      if (!name.isEmpty()) {
        locationSample.setTag(root.getTagsIntern().internKey("name"), root.getTagsIntern().internValue("name", name));
      }
    }


    locationSample.setAccount(account);
    account.getLocationSamples().add(locationSample);


    root.getLocationSamples().put(locationSample.getIdentity(), locationSample);

    return locationSample;
  }

  public String getAccountIdentity() {
    return accountIdentity;
  }

  public void setAccountIdentity(String accountIdentity) {
    this.accountIdentity = accountIdentity;
  }

  public Long getLocationSampleIdentity() {
    return locationSampleIdentity;
  }

  public void setLocationSampleIdentity(Long locationSampleIdentity) {
    this.locationSampleIdentity = locationSampleIdentity;
  }


  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getApplicationVersion() {
    return applicationVersion;
  }

  public void setApplicationVersion(String applicationVersion) {
    this.applicationVersion = applicationVersion;
  }

  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddress postalAddress) {
    this.postalAddress = postalAddress;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getIpAddressHost() {
    return ipAddressHost;
  }

  public void setIpAddressHost(String ipAddressHost) {
    this.ipAddressHost = ipAddressHost;
  }
}
