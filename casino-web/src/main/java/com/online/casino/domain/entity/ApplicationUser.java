package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.UserException;
import com.online.casino.domain.enums.UserStatus;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/11/20
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class ApplicationUser extends AbstractEntity implements Serializable, UserDetails {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(ApplicationUser.class);

    /**
     * Field description
     */
    private static final long serialVersionUID = -5887586128805071157L;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicationUser")
    private Set<Player> players = new HashSet<Player>();

    /**
     * Field description
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicationUser")
    private Set<ApplicationUserRole> roles = new HashSet<ApplicationUserRole>();

    /**
     * Field description
     */
    @Transient
    private Boolean kaptchaEnabled = true;

    /**
     * Field description
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicationUser")
    private Set<Account> accounts = new HashSet<Account>();

    /**
     * Field description
     */
    @Transient
    private String currentPassword;

    /**
     * Field description
     */
    @NotNull
    @NotEmpty
    @Email
    @Column(nullable = false)
    private String email;

    /**
     * Field description
     */
    @Transient
    private String kaptcha;

    /**
     * Field description
     */
    private String locale;

    /**
     * Field description
     */
//    @NotNull
//    @NotEmpty
//    @Size(min = 8, max = 15)
    @Transient
    private String newPassword;

    /**
     * Field description
     */
    @Column(nullable = false)
    private String password;

    /**
     * Field description
     */
//    @NotNull
//    @NotEmpty
//    @Size(min = 8, max = 15)
    @Transient
    private String passwordConfirm;

    // Spring IoC

    /**
     * Field description
     */
    @Transient
    private String requiredKaptcha;

    /**
     * Field description
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * Field description
     */
    @NotNull
    @NotEmpty
    @Size(min = 5, max = 15)
    @Column(nullable = false)
    private String username;

    public ApplicationUser() {
    }

    private ApplicationUser(String id) {
        this.id = id;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param userId id
     * @return Return value
     * @throws UserException UserException
     */
    public static ApplicationUser activateUser(String userId) throws UserException {
        ApplicationUser result = findApplicationUser(userId);

        if ((result != null) && result.getStatus().equals(UserStatus.REGISTERED)) {
            result.setStatus(UserStatus.ACTIVE);
            result.merge();
        } else {
            log.error("ApplicationUser with id: " + userId
                    + " could either not be found or user had the wrong status. Expected REGISTERED, found: "
                    + result.getStatus().name());

            throw new UserException("error.user.activation", userId, result.getStatus().name());
        }

        return result;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public static final EntityManager entityManager() {
        EntityManager em = new ApplicationUser().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                    "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param maxResults maxResults
     * @return Return value
     */
    public static List<ApplicationUser> findLastModifiedUsers(Integer maxResults) {
        List<ApplicationUser> result = null;

        try {
            Query q = entityManager().createQuery("select su from ApplicationUser as su order by su.lastUpdate");

            if (maxResults != null) {
                q.setMaxResults(maxResults);
            }

            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    public static ApplicationUser findApplicationUser(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of ApplicationUser");
        }

        return entityManager().find(ApplicationUser.class, id);
    }

    /**
     * Get ApplicationUser by email!
     *
     * @param email
     * @return boolean
     */
    public static ApplicationUser findApplicationUserByEmail(String email) {
        ApplicationUser result = null;

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("email cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select su from ApplicationUser as su where su.email = :email");

            q.setParameter("email", email);
            result = (ApplicationUser) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Get user by username
     *
     * @param username
     * @return boolean
     */
    @Transactional
    public static ApplicationUser findApplicationUserByUsername(String username) {
        ApplicationUser result = null;

        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select su from ApplicationUser as su where su.username = :username");

            q.setParameter("username", username);
            result = (ApplicationUser) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param name name
     * @return Return value
     */
    public static Long findApplicationUserCount(String name) {
        Long result = 0L;

        try {

            CriteriaBuilder builder = entityManager().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<ApplicationUser> root = criteria.from(ApplicationUser.class);
            criteria.select(builder.count(root));

            if (StringUtils.isNotBlank(name)) {
                criteria.where(
                        builder.or(
                                builder.like(builder.lower(root.get(ApplicationUser_.username)), name.toLowerCase() + "%"),
                                builder.like(builder.lower(root.get(ApplicationUser_.email)), name.toLowerCase() + "%")
                        )
                );
            }
            
            TypedQuery<Long> query = entityManager().createQuery(criteria);

            result = query.getSingleResult();

        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Returns a paged set of users. Name can be first name, last name
     *
     * @param name
     * @param index
     * @param maxResults
     * @return
     */
    public static List<ApplicationUser> findApplicationUsers(String name, Integer index, Integer maxResults) {
        List<ApplicationUser> result = null;

        try {

            CriteriaBuilder builder = entityManager().getCriteriaBuilder();
            CriteriaQuery<ApplicationUser> criteria = builder.createQuery(ApplicationUser.class);
            Root<ApplicationUser> root = criteria.from(ApplicationUser.class);
            criteria.select(root);

            if (StringUtils.isNotBlank(name)) {
                criteria.where(
                        builder.or(
                                builder.like(builder.lower(root.get(ApplicationUser_.username)), name.toLowerCase() + "%"),
                                builder.like(builder.lower(root.get(ApplicationUser_.email)), name.toLowerCase() + "%")
                        )
                );
            }

            TypedQuery<ApplicationUser> query = entityManager().createQuery(criteria);
            if ((index != null) && (maxResults != null)) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }
            result = query.getResultList();

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
     * @return Return value
     */
    public Set<Account> getAccounts() {
        return accounts;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Override
    @Transient
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> result = null;

        if (roles != null) {
            result = new ArrayList<GrantedAuthority>();

            for (ApplicationUserRole ur : this.roles) {
                for (ApplicationRight sr : ur.getApplicationRole().getRights()) {
                    result.add(new SimpleGrantedAuthority(sr.getStatusCode().name()));
                }
            }
        }

        return result;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getEmail() {
        return email;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getKaptcha() {
        return kaptcha;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Set<Player> getPlayers() {
        return players;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getRequiredKaptcha() {
        return requiredKaptcha;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Set<ApplicationUserRole> getRoles() {
        return roles;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getUsername() {
        return username;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        boolean result = false;

        if (status != null) {
            result = !status.equals(UserStatus.EXPIRED);
        }

        return result;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Override
    @Transient
    public boolean isAccountNonLocked() {
        boolean result = false;

        if (status != null) {
            result = !status.equals(UserStatus.LOCKED);
        }

        return result;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        boolean result = false;

        if (status != null) {
            result = !status.equals(UserStatus.CREDENTIALS_EXPIRED);
        }

        return result;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Override
    @Transient
    public boolean isEnabled() {
        boolean result = false;

        if (status != null) {
            result = status.equals(UserStatus.ACTIVE);
        }

        return result;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Boolean isKaptchaEnabled() {
        return kaptchaEnabled;
    }

    /**
     * checks the db and the user_tbl that the unique id and the email coming in
     * are truly unique If a user is returned it means this user is not unique
     *
     * @param userId
     * @param email  String
     * @return boolean
     */
    public static ApplicationUser isApplicationUserUniqueByEmail(String userId, String email) {
        ApplicationUser result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("email cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                    "select su from ApplicationUser as su where su.id <> :userId and su.email = :email");

            q.setParameter("email", email);
            q.setParameter("userId", userId);
            result = (ApplicationUser) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * checks the db and the user_tbl that the unique id and the username coming
     * in are truly unique If a user is returned it means this user is not
     * unique
     *
     * @param userId
     * @param username String
     * @return boolean
     */
    public static ApplicationUser isApplicationUserUniqueByUsername(String userId, String username) {
        ApplicationUser result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                    "select su from ApplicationUser as su where su.id <> :userId and su.username = :username");

            q.setParameter("username", username);
            q.setParameter("userId", userId);
            result = (ApplicationUser) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

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

        ApplicationUser merged = this.entityManager.merge(this);

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
            ApplicationUser attached = this.entityManager.find(ApplicationUser.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new ApplicationUser(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param accounts accounts
     */
    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * Method description
     *
     * @param currentPassword currentPassword
     */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    /**
     * Method description
     *
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Method description
     *
     * @param kaptcha kaptcha
     */
    public void setKaptcha(String kaptcha) {
        this.kaptcha = kaptcha;
    }

    /**
     * Method description
     *
     * @param enabled enabled
     */
    public void setKaptchaEnabled(Boolean enabled) {
        this.kaptchaEnabled = enabled;
    }

    /**
     * Method description
     *
     * @param locale locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Method description
     *
     * @param newPassword newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Method description
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Method description
     *
     * @param passwordConfirm passwordConfirm
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * Method description
     *
     * @param players players
     */
    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    /**
     * Method description
     *
     * @param requiredKaptcha requiredKaptcha
     */
    public void setRequiredKaptcha(String requiredKaptcha) {
        this.requiredKaptcha = requiredKaptcha;
    }

    /**
     * Method description
     *
     * @param roles roles
     */
    public void setRoles(Set<ApplicationUserRole> roles) {
        this.roles = roles;
    }

    /**
     * Method description
     *
     * @param status status
     */
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return Return value
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("ApplicationUser [");
        builder.append(super.toString());

        if (currentPassword != null) {
            builder.append("currentPassword=");
            builder.append(currentPassword);
            builder.append(", ");
        }

        if (email != null) {
            builder.append("email=");
            builder.append(email);
            builder.append(", ");
        }

        if (kaptcha != null) {
            builder.append("kaptcha=");
            builder.append(kaptcha);
            builder.append(", ");
        }

        if (kaptchaEnabled != null) {
            builder.append("kaptchaEnabled=");
            builder.append(kaptchaEnabled);
            builder.append(", ");
        }

        if (newPassword != null) {
            builder.append("newPassword=");
            builder.append(newPassword);
            builder.append(", ");
        }

        if (password != null) {
            builder.append("password=");
            builder.append(password);
            builder.append(", ");
        }

        if (passwordConfirm != null) {
            builder.append("passwordConfirm=");
            builder.append(passwordConfirm);
            builder.append(", ");
        }

        if (requiredKaptcha != null) {
            builder.append("requiredKaptcha=");
            builder.append(requiredKaptcha);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (username != null) {
            builder.append("username=");
            builder.append(username);
        }

        builder.append("]");

        return builder.toString();
    }
}
