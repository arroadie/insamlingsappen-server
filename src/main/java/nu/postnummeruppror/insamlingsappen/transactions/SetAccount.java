package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;

/**
 * Creates the account if not already existing
 * and updates user specifiable data (email address, etc).
 *
 * @author kalle
 * @since 2014-09-06 20:23
 */
public class SetAccount implements TransactionWithQuery<Root, Account>, Serializable {

  private static final long serialVersionUID = 1l;

  public SetAccount() {
  }

  private String accountIdentity;

  private String emailAddress;
  private Boolean acceptingCcZero;
  private String firstName;
  private String lastName;


  @Override
  public Account executeAndQuery(Root root, Date executionTime) throws Exception {

    Account account = root.getAccounts().get(accountIdentity);
    if (account == null) {
      account = new Account();
      account.setIdentity(accountIdentity);
      account.setTimestampCreated(executionTime.getTime());
      root.getAccounts().put(account.getIdentity(), account);
    }

    if (emailAddress != null) {
      account.setEmailAddress(emailAddress);
    }
    if (acceptingCcZero != null) {
      account.setAcceptingCcZero(acceptingCcZero);
    }
    if (firstName != null) {
      account.setFirstName(firstName);
    }
    if (lastName != null) {
      account.setLastName(lastName);
    }

    return account;
  }

  public String getAccountIdentity() {
    return accountIdentity;
  }

  public void setAccountIdentity(String accountIdentity) {
    this.accountIdentity = accountIdentity;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public Boolean getAcceptingCcZero() {
    return acceptingCcZero;
  }

  public void setAcceptingCcZero(Boolean acceptingCcZero) {
    this.acceptingCcZero = acceptingCcZero;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
