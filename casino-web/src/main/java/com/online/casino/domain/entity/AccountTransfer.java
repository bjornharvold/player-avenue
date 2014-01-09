package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.AccountEntryType;
import com.online.casino.domain.enums.AccountTransferAction;
import com.online.casino.domain.enums.AccountTransferStatus;
import com.online.casino.domain.enums.AccountTransferType;
import com.online.casino.domain.enums.Country;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 11/02/18
 * @author         Bjorn Harvold
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class AccountTransfer extends AbstractEntity implements Serializable {
    private final static Logger log = LoggerFactory.getLogger(AccountTransfer.class);

    /** Field description */
    private static final long serialVersionUID = -3973995294291261317L;

    //~--- fields -------------------------------------------------------------

    /** Field description */
    @NotNull
    private String accountId;

    /** Field description */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AccountTransferAction action;

    /** Field description */
    private String address1;

    /** Field description */
    private String address2;

    /** Field description */
    private String address3;

    /** Field description */
    @NotNull
    @Digits(fraction = 2, integer = 10)
    @Min(1L)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** Field description */
    private String bicswift;

    /** Field description */
    private String city;

    /** Field description */
    @Enumerated(EnumType.STRING)
    private Country country;

    /** Field description */
    private String firstName;

    /** Field description */
    private String iban;

    /** Field description */
    private String instructions;

    /** Field description */
    private String lastName;

    /** Field description */
    private String state;

    /** Field description */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AccountTransferStatus status;

    /** Field description */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AccountTransferType type;

    /** Field description */
    private String zip;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     */
    public AccountTransfer() {}

    /**
     * Constructs ...
     *
     */
    private AccountTransfer(String id) {
        this.id = id;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public static final EntityManager entityManager() {
        EntityManager em = new AccountTransfer().entityManager;

        if (em == null) {
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return Return value
     */
    public static AccountTransfer findAccountTransfer(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of AccountTransfer");
        }

        return entityManager().find(AccountTransfer.class, id);
    }

    /**
     * Method description
     *
     *
     * @param accountId accountId
     *
     * @return Return value
     */
    public static Long findAccountTransferCount(String accountId) {
        Long result = null;

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        try {

            // grab criteria builder
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // create a criteria for Player entity
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // this is the query root
            Root<AccountTransfer> root = criteria.from(AccountTransfer.class);

            // we want to select what we are returning
            criteria.select(builder.count(root));

            // conditional statement - player needs to have a nickname like specified
            criteria.where(builder.equal(root.get(AccountTransfer_.id), accountId));
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param accountId accountId
     * @param index index
     * @param maxResults maxResults
     *
     * @return Return value
     */
    public static List<AccountTransfer> findAccountTransfers(String accountId, Integer index, Integer maxResults) {
        List<AccountTransfer> result = null;

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        try {

            // grab criteria builder
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // create a criteria for Player entity
            CriteriaQuery<AccountTransfer> criteria = builder.createQuery(AccountTransfer.class);

            // this is the query root
            Root<AccountTransfer> root = criteria.from(AccountTransfer.class);

            // we want to select what we are returning
            criteria.select(root);

            // conditional statement - player needs to have a nickname like specified
            criteria.where(builder.equal(root.get(AccountTransfer_.accountId), accountId));

            Query q = entityManager().createQuery(criteria);

            if ((index != null) && (maxResults != null)) {
                q.setFirstResult(index * maxResults);
                q.setMaxResults(maxResults);
            }

            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     */
    @Transactional
    public void flush() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        this.entityManager.flush();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public AccountTransferAction getAction() {
        return action;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getAddress3() {
        return address3;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getBicswift() {
        return bicswift;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getCity() {
        return city;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getIban() {
        return iban;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getState() {
        return state;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public AccountTransferStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public AccountTransferType getType() {
        return type;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getZip() {
        return zip;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        AccountTransfer merged = this.entityManager.merge(this);

        this.entityManager.flush();
        this.id = merged.getId();
    }

    /**
     * Method description
     *
     */
    @Transactional
    public void persist() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        this.entityManager.persist(this);
    }

    /**
     * Method description
     *
     */
    @Transactional
    public void remove() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            AccountTransfer attached = this.entityManager.find(AccountTransfer.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new AccountTransfer(id).remove();
    }
    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param accountId accountId
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Method description
     *
     *
     * @param action action
     */
    public void setAction(AccountTransferAction action) {
        this.action = action;
    }

    /**
     * Method description
     *
     *
     * @param address1 address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Method description
     *
     *
     * @param address2 address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Method description
     *
     *
     * @param address3 address3
     */
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    /**
     * Method description
     *
     *
     * @param amount amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Method description
     *
     *
     * @param bicswift bicswift
     */
    public void setBicswift(String bicswift) {
        this.bicswift = bicswift;
    }

    /**
     * Method description
     *
     *
     * @param city city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Method description
     *
     *
     * @param country country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Method description
     *
     *
     * @param firstName firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Method description
     *
     *
     * @param iban iban
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Method description
     *
     *
     * @param instructions instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Method description
     *
     *
     * @param lastName lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Method description
     *
     *
     * @param state state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Method description
     *
     *
     * @param status status
     */
    public void setStatus(AccountTransferStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     *
     * @param type type
     */
    public void setType(AccountTransferType type) {
        this.type = type;
    }

    /**
     * Method description
     *
     *
     * @param zip zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("AccountTransfer [");
        builder.append(super.toString());

        if (accountId != null) {
            builder.append("account=");
            builder.append(accountId);
            builder.append(", ");
        }

        if (action != null) {
            builder.append("action=");
            builder.append(action);
            builder.append(", ");
        }

        if (address1 != null) {
            builder.append("address1=");
            builder.append(address1);
            builder.append(", ");
        }

        if (address2 != null) {
            builder.append("address2=");
            builder.append(address2);
            builder.append(", ");
        }

        if (address3 != null) {
            builder.append("address3=");
            builder.append(address3);
            builder.append(", ");
        }

        if (amount != null) {
            builder.append("amount=");
            builder.append(amount);
            builder.append(", ");
        }

        if (bicswift != null) {
            builder.append("bicswift=");
            builder.append(bicswift);
            builder.append(", ");
        }

        if (city != null) {
            builder.append("city=");
            builder.append(city);
            builder.append(", ");
        }

        if (country != null) {
            builder.append("country=");
            builder.append(country);
            builder.append(", ");
        }

        if (firstName != null) {
            builder.append("firstName=");
            builder.append(firstName);
            builder.append(", ");
        }

        if (iban != null) {
            builder.append("iban=");
            builder.append(iban);
            builder.append(", ");
        }

        if (instructions != null) {
            builder.append("instructions=");
            builder.append(instructions);
            builder.append(", ");
        }

        if (lastName != null) {
            builder.append("lastName=");
            builder.append(lastName);
            builder.append(", ");
        }

        if (state != null) {
            builder.append("state=");
            builder.append(state);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (type != null) {
            builder.append("type=");
            builder.append(type);
            builder.append(", ");
        }

        if (zip != null) {
            builder.append("zip=");
            builder.append(zip);
        }

        builder.append("]");

        return builder.toString();
    }

}
