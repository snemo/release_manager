package com.nuxplanet.releasemanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Instance.
 */
@Entity
@Table(name = "instance")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "instance")
public class Instance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "instance")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Installation> installations = new HashSet<>();

    @ManyToOne
    private Project project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Instance name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public Instance url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public Instance description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Installation> getInstallations() {
        return installations;
    }

    public Instance installations(Set<Installation> installations) {
        this.installations = installations;
        return this;
    }

    public Instance addInstallations(Installation installation) {
        installations.add(installation);
        installation.setInstance(this);
        return this;
    }

    public Instance removeInstallations(Installation installation) {
        installations.remove(installation);
        installation.setInstance(null);
        return this;
    }

    public void setInstallations(Set<Installation> installations) {
        this.installations = installations;
    }

    public Project getProject() {
        return project;
    }

    public Instance project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Instance instance = (Instance) o;
        if(instance.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, instance.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Instance{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", url='" + url + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
