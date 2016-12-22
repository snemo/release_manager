package com.nuxplanet.releasemanager.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Installation.
 */
@Entity
@Table(name = "installation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "installation")
public class Installation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "notes")
    private String notes;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "adapter_errors")
    private String adapterErrors;

    @Column(name = "config_errors")
    private String configErrors;

    @Column(name = "other_errors")
    private String otherErrors;

    @ManyToOne
    private Release release;

    @ManyToOne
    private Instance instance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Installation date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public Installation notes(String notes) {
        this.notes = notes;
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean isSuccess() {
        return success;
    }

    public Installation success(Boolean success) {
        this.success = success;
        return this;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getAdapterErrors() {
        return adapterErrors;
    }

    public Installation adapterErrors(String adapterErrors) {
        this.adapterErrors = adapterErrors;
        return this;
    }

    public void setAdapterErrors(String adapterErrors) {
        this.adapterErrors = adapterErrors;
    }

    public String getConfigErrors() {
        return configErrors;
    }

    public Installation configErrors(String configErrors) {
        this.configErrors = configErrors;
        return this;
    }

    public void setConfigErrors(String configErrors) {
        this.configErrors = configErrors;
    }

    public String getOtherErrors() {
        return otherErrors;
    }

    public Installation otherErrors(String otherErrors) {
        this.otherErrors = otherErrors;
        return this;
    }

    public void setOtherErrors(String otherErrors) {
        this.otherErrors = otherErrors;
    }

    public Release getRelease() {
        return release;
    }

    public Installation release(Release release) {
        this.release = release;
        return this;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public Instance getInstance() {
        return instance;
    }

    public Installation instance(Instance instance) {
        this.instance = instance;
        return this;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Installation installation = (Installation) o;
        if(installation.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, installation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Installation{" +
            "id=" + id +
            ", date='" + date + "'" +
            ", notes='" + notes + "'" +
            ", success='" + success + "'" +
            ", adapterErrors='" + adapterErrors + "'" +
            ", configErrors='" + configErrors + "'" +
            ", otherErrors='" + otherErrors + "'" +
            '}';
    }
}
