/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.wazeegroup.physhun.framework.ProcessObject;
import com.wazeegroup.physhun.framework.ProcessObjectPersistenceSupport;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/12/18
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class FsmProcessObject extends AbstractEntity implements ProcessObject, ProcessObjectPersistenceSupport, Serializable {
    private final static Logger log = LoggerFactory.getLogger(FsmProcessObject.class);
    private static final long serialVersionUID = 4603527585769166603L;

    /**
     * comma separated list of values
     */
    private String activeStates = null;

    /**
     * Field description
     */
    private String handId = null;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public FsmProcessObject() {
    }

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     */
    public FsmProcessObject(String pokergameId) {
        this.id = pokergameId;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return Return value
     */
    public static final EntityManager entityManager() {
        EntityManager em = new FsmProcessObject().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                    "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    public static FsmProcessObject findFsmProcessObject(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Bet");
        }

        return entityManager().find(FsmProcessObject.class, id);
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public static List<FsmProcessObject> findFsmProcessObjects() {
        List<FsmProcessObject> result = null;

        try {
            // grab criteria builder
            Query q = entityManager().createQuery("select fsm from FsmProcessObject fsm");
            result = q.getResultList();
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
    public String getActiveState() {
        return activeStates;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Transient
    @Override
    public String[] getActiveStates() {
        String[] result = null;

        if (StringUtils.isNotEmpty(activeStates)) {
            result = activeStates.split(",");
        }

        return result;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getHandId() {
        return handId;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Transient
    @Override
    public final String getID() {
        return getId();
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @Transient
    @Override
    public ProcessObject getPersistedObject(String id) {
        return findFsmProcessObject(id);
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @Transient
    @Override
    public ProcessObject[] getPersistedObjects() {
        ProcessObject[] result;
        List<FsmProcessObject> list = FsmProcessObject.findFsmProcessObjects();

        if ((list != null) && !list.isEmpty()) {
            result = list.toArray(new FsmProcessObject[list.size()]);
        } else {
            result = null;
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

            FsmProcessObject merged = this.entityManager.merge(this);

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
     *
     * @param processObject processObject
     */
    @Transient
    @Override
    public void persistState(ProcessObject processObject) {
        if (processObject instanceof FsmProcessObject) {
            FsmProcessObject fpo = (FsmProcessObject) processObject;

            if (fpo.getId() == null) {
                fpo.persist();
            } else {
                fpo.merge();
            }
        }
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
            FsmProcessObject attached = this.entityManager.find(FsmProcessObject.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param state state
     */
    public void setActiveState(String state) {
        this.activeStates = state;
    }

    /**
     * Method description
     *
     * @param states states
     */
    @Transient
    @Override
    public void setActiveStates(String[] states) {
        List<String> currentStates = null;

        if (StringUtils.isNotEmpty(this.activeStates)) {
            currentStates = new ArrayList<String>(Arrays.asList(getActiveStates()));
        } else {
            currentStates = new ArrayList<String>();
        }

        for (String state : states) {
            boolean exists = false;

            for (String currentState : currentStates) {
                if (StringUtils.equals(currentState, state)) {
                    exists = true;
                }
            }

            if (!exists) {
                currentStates.add(state);
            }
        }

        // back to string
        createActivateStates(currentStates);
    }

    /**
     * Method description
     *
     * @param handId handId
     */
    public void setHandId(String handId) {
        this.handId = handId;
    }

    /**
     * Method description
     *
     * @param id id
     */
    @Transient
    @Override
    public void setID(String id) {
        log.error("SOMEONE IS SETTING THE ID ON THE FSM_PROCESS_OBJECT: " + id);
        setId(id);
    }

    /**
     * Method description
     *
     * @param state state
     */
    @Transient
    @Override
    public final void setStateActive(String state) {
        List<String> currentStates = null;

        if (StringUtils.isNotEmpty(this.activeStates)) {
            currentStates = new ArrayList<String>(Arrays.asList(getActiveStates()));
        } else {
            currentStates = new ArrayList<String>();
        }

        boolean exists = false;

        for (String currentState : currentStates) {
            if (StringUtils.equals(state, currentState)) {
                exists = true;
            }
        }

        if (!exists) {
            currentStates.add(state);
        }

        setActiveStates(currentStates.toArray(new String[currentStates.size()]));
    }

    /**
     * Inactivates the specified state.  Generally called when the ProcessObject makes
     * a state transition out of the specified state.
     *
     * @param state
     */
    @Transient
    @Override
    public final void setStateInactive(String state) {
        List<String> currentStates = null;

        if (StringUtils.isNotEmpty(this.activeStates)) {
            currentStates = new ArrayList<String>(Arrays.asList(getActiveStates()));
        } else {
            currentStates = new ArrayList<String>();
        }

        currentStates.remove(state);

        // back to string
        createActivateStates(currentStates);
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param currentStates currentStates
     */
    private void createActivateStates(List<String> currentStates) {
        StringBuilder sb = new StringBuilder();

        for (String currentState : currentStates) {
            sb.append(currentState);
            sb.append(",");
        }

        // remove last comma
        if ((sb.length() > 1) && (sb.charAt(sb.length() - 1) == ',')) {
            sb.deleteCharAt(sb.length() - 1);
        }

        this.activeStates = sb.toString();
    }
}
