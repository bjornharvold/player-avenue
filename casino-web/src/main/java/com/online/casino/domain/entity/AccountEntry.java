package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.AccountEntryType;
import com.online.casino.domain.enums.Currency;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/11/03
 */
@Entity
@Configurable
public class AccountEntry extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private static final long serialVersionUID = 3871746153001743751L;

    //~--- fields -------------------------------------------------------------

    /** Field description */
    @ManyToOne(targetEntity = Account.class, optional = false)
    private Account account;

    /**
     * Field description
     */
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    @NumberFormat(style = Style.CURRENCY)
    private BigDecimal amount;

    /**
     * Field description
     */
    private String description;

    /** When the money comes in from a game it can be useful info to track */
    private String playerId;

    /** When the money comes in from a game it can be useful info to track */
    private String pokergameId;

    /**
     * Field description
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountEntryType type;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public AccountEntry() {}

    private AccountEntry(String id) {
        this.id = id;
    }


    /**
     * Constructs ...
     *
     * @param account     account
     * @param amount      amount
     * @param description description
     * @param type        type
     */
    public AccountEntry(Account account, BigDecimal amount, String description, AccountEntryType type) {
        this.amount      = amount;
        this.description = description;
        this.type        = type;
        this.account     = account;
    }

    public AccountEntry(Account account, BigDecimal amount, String description, AccountEntryType type, String pokergameId, String playerId) {
        this.account = account;
        this.amount = amount;
        this.description = description;
        this.playerId = playerId;
        this.pokergameId = pokergameId;
        this.type = type;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new AccountEntry().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param accountId accountId
     * @return
     */
    public static BigDecimal findAccountBalance(String accountId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select sum(ae.amount) from AccountEntry as ae where ae.account.id = :accountId");

            q.setParameter("accountId", accountId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Method description
     *
     * @param playerId playerId
     * @param currency currency
     * @return
     */
    public static BigDecimal findAccountBalanceByPlayerAndCurrency(String playerId, Currency currency) {
        BigDecimal result = null;

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }
        try {
            Query q = entityManager().createQuery("select sum(ae.amount) from AccountEntry as ae "
                          + "join ae.account as acct " + "join acct.applicationUser as su " + "join su.players as p "
                          + "where acct.currency = :currency and p.id = :playerId");

            q.setParameter("currency", currency);
            q.setParameter("playerId", playerId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Method description
     *
     * @param accountId accountId
     * @return
     */
    public static BigDecimal findAccountBalanceMatchedDeposit(String accountId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select sum(ae.amount) from AccountEntry as ae where ae.account.id = :accountId and ae.type = 'MATCHED_DEPOSIT'");

            q.setParameter("accountId", accountId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Method description
     *
     * @param accountId accountId
     * @return
     */
    public static BigDecimal findAccountBalanceWithdrawableAmount(String accountId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Object> criteria = builder.createQuery();

            // from
            Root<AccountEntry> root = criteria.from(AccountEntry.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.sum(root.get(AccountEntry_.amount)));

            // conditional statement
            criteria.where(builder.equal(root.get(AccountEntry_.id), accountId),
                                       root.get(AccountEntry_.type).in(AccountEntryType.DEPOSIT,
                                           AccountEntryType.WITHDRAWAL, AccountEntryType.TRANSFER));
            result = (BigDecimal) entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Method description
     *
     * @param id id
     * @return
     */
    public static AccountEntry findAccountEntry(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of AccountEntry");
        }

        return entityManager().find(AccountEntry.class, id);
    }

    /**
     * Method description
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
     * @return
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Method description
     *
     * @return
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Method description
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getPokergameId() {
        return pokergameId;
    }

    /**
     * Method description
     *
     * @return
     */
    public AccountEntryType getType() {
        return type;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        AccountEntry merged = this.entityManager.merge(this);

        this.entityManager.flush();
        this.id = merged.getId();
    }

    /**
     * Method description
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
     */
    @Transactional
    public void remove() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            AccountEntry attached = this.entityManager.find(AccountEntry.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new AccountEntry(id).remove();
    }
    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param account account
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Method description
     *
     * @param amount amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Method description
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method description
     *
     *
     * @param playerId playerId
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     */
    public void setPokergameId(String pokergameId) {
        this.pokergameId = pokergameId;
    }

    /**
     * Method description
     *
     * @param type type
     */
    public void setType(AccountEntryType type) {
        this.type = type;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("AccountEntry [");
        builder.append(super.toString());

        if (account != null) {
            builder.append("accountId=");
            builder.append(account.getId());
            builder.append(", ");
        }

        if (amount != null) {
            builder.append("amount=");
            builder.append(amount);
            builder.append(", ");
        }

        if (description != null) {
            builder.append("description=");
            builder.append(description);
            builder.append(", ");
        }

        if (type != null) {
            builder.append("type=");
            builder.append(type);
        }

        builder.append("]");

        return builder.toString();
    }

    public static AccountEntry instantiate(Account account, BigDecimal amount, String description, AccountEntryType deposit) {
        return new AccountEntry(account, amount, description, deposit);
    }
}
