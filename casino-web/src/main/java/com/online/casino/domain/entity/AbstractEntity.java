/*
 * Copyright (c) 2009. All rights reserved. Bjorn Harvold.
 */

package com.online.casino.domain.entity;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@SuppressWarnings("unused")
public abstract class AbstractEntity {

    @PersistenceContext
    transient EntityManager entityManager;

    //    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Id
    @Column(name = "id")
    protected String id;

    @Version
    @Column(name = "version")
    protected Integer version;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date createdDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date lastUpdate;

    @Transient
    private final static EthernetAddress nic = EthernetAddress.fromInterface();

    @Transient
    private final static TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(nic);

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append("id=").append(id).append(", ");
        }
        if (version != null) {
            sb.append("version=").append(version).append(", ");
        }
        if (createdDate != null) {
            sb.append("createdDate=").append(createdDate).append(", ");
        }
        if (lastUpdate != null) {
            sb.append("lastUpdate=").append(lastUpdate).append(", ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        return result;
    }

    // entity listeners - populate required fields if they are empty
    @PrePersist
    public void prePersist() {
        Date d = new Date();
        if (this.createdDate == null) {
            this.createdDate = d;
        }
        if (this.lastUpdate == null) {
            this.lastUpdate = d;
        }

        if (StringUtils.isBlank(this.id)) {
            // let the app and not the db generate primary keys
            UUID uuid = uuidGenerator.generate();
            this.id = uuid.toString();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdate = new Date();
    }

}