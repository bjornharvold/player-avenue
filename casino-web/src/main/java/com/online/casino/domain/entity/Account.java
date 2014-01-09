package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.AccountStatus;
import com.online.casino.domain.enums.Currency;
import org.apache.commons.lang.RandomStringUtils;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/11/03
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class Account extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(Account.class);

    /**
     * Field description
     */
    private static final long serialVersionUID = -7847867175690112880L;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @NotNull
    @Column(unique = true, nullable = false)
    private String accountNumber;

    /**
     * Field description
     */
    @Transient
    private BigDecimal balance;

    /**
     * Field description
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Currency currency;

    /**
     * Field description
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AccountStatus status;

    /**
     * Field description
     */
    @ManyToOne(optional = false, targetEntity = ApplicationUser.class)
    @JoinColumn
    private ApplicationUser applicationUser;

    public Account() {
    }

    private Account(String id) {
        this.id = id;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new Account().entityManager;

        if (em == null) {
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param id id
     * @return
     */
    public static Account findAccount(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Account");
        }

        return entityManager().find(Account.class, id);
    }

    /**
     * Returns the account matching the user with that player id and the currency
     *
     * @param playerId playerId
     * @param currency currency
     * @return
     */
    public static Account findAccountByPlayerAndCurrency(String playerId, Currency currency) {
        Account result = null;

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select acct from Account acct " + "join acct.applicationUser as su " +
                    "join su.players as p " +
                    "where acct.currency = :currency and p.id = :playerId");

            q.setParameter("currency", currency);
            q.setParameter("playerId", playerId);
            result = (Account) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param userId   userId
     * @param currency currency
     * @return
     */
    public static Account findAccountByUserAndCurrency(String userId, Currency currency) {
        Account result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select acct from Account acct where acct.currency = :currency and acct.applicationUser.id = :userId");

            q.setParameter("currency", currency);
            q.setParameter("userId", userId);
            result = (Account) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param accountId accountId
     * @return Return value
     */
    public static Account findAccountWithBalance(String accountId) {
        Account result = null;

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        try {
            result = findAccount(accountId);

            if (result != null) {
                result.setBalance(AccountEntry.findAccountBalance(result.getId()));
            }
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param userId userId
     * @param status status
     * @return
     */
    public static List<Account> findAccounts(String userId, AccountStatus status) {
        List<Account> result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();
            CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
            Root<Account> root = criteria.from(Account.class);

            criteria.select(root);

            Join<Account, ApplicationUser> applicationUserJoin = root.join(Account_.applicationUser);

            Predicate applicationUserEquals = builder.equal(applicationUserJoin.get(ApplicationUser_.id), userId);

            if (status != null) {
                Predicate statusEquals = builder.equal(root.get(Account_.status), status);
                criteria.where(applicationUserEquals, statusEquals);
            } else {
                criteria.where(applicationUserEquals);
            }

            TypedQuery<Account> query = entityManager().createQuery(criteria);
            result = query.getResultList();

            // add on balances
            if ((result != null) && (result.size() > 0)) {
                for (Account acct : result) {
                    acct.setBalance(AccountEntry.findAccountBalance(acct.getId()));
                }
            }
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
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
     * @return
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Method description
     *
     * @return
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Method description
     *
     * @return
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Method description
     *
     * @return
     */
    public AccountStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     * @return
     */
    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    /**
     * Method description
     *
     * @param account account
     * @return
     */
    public static Boolean isDuplicateAccount(Account account) {
        Boolean result = null;

        if (account.getCurrency() == null) {
            throw new IllegalArgumentException("account.currency cannot be null");
        }
        if (account.getApplicationUser() == null) {
            throw new IllegalArgumentException("account.applicationUser cannot be null");
        }
        if (account.getApplicationUser().getId() == null) {
            throw new IllegalArgumentException("account.applicationUser.id cannot be null");
        }

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Account>   root     = criteria.from(Account.class);

            criteria.select(builder.count(root));

            Join<Account, ApplicationUser> applicationUserJoin = root.join(Account_.applicationUser);

            Predicate equalCurrency = builder.equal(root.get(Account_.currency), account.getCurrency());
            Predicate equalApplicationUser = builder.equal(applicationUserJoin.get(ApplicationUser_.id), account.getApplicationUser().getId());
            Predicate notEqualIdentifier = null;

            if (StringUtils.isNotBlank(account.getId())) {
                notEqualIdentifier = builder.notEqual(root.get(Account_.id), account.getId());
            }

            if (notEqualIdentifier != null) {
                criteria.where(equalCurrency, equalApplicationUser, notEqualIdentifier);
            } else {
                criteria.where(equalCurrency, equalApplicationUser);
            }

            Long count = entityManager().createQuery(criteria).getSingleResult();

            result = (count > 0) ? Boolean.TRUE : Boolean.FALSE;
        } catch (EmptyResultDataAccessException e) {}

        return result;
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

        Account merged = this.entityManager.merge(this);

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
            Account attached = this.entityManager.find(Account.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new Account(id).remove();
    }
    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param accountNumber accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Method description
     *
     * @param balance balance
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Method description
     *
     * @param currency currency
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Method description
     *
     * @param status status
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     * @param applicationUser applicationUser
     */
    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
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

        builder.append("Account [");
        builder.append(super.toString());

        if (accountNumber != null) {
            builder.append("accountNumber=");
            builder.append(accountNumber);
            builder.append(", ");
        }

        if (balance != null) {
            builder.append("balance=");
            builder.append(balance);
            builder.append(", ");
        }

        if (currency != null) {
            builder.append("currency=");
            builder.append(currency);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (applicationUser != null) {
            builder.append("applicationUser=");
            builder.append(applicationUser.getId());
        }

        builder.append("]");

        return builder.toString();
    }

    @PrePersist
    public void prePersistAccount() {
        if (StringUtils.isBlank(accountNumber)) {
            accountNumber = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        }
    }
}
