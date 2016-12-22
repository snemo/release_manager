package com.nuxplanet.releasemanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Release.
 */
@Entity
@Table(name = "release")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "release")
public class Release implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "commit_id", nullable = false)
    private String commitId;

    @Column(name = "config")
    private String config;

    @Column(name = "adapters")
    private String adapters;

    @Column(name = "other_steps")
    private String otherSteps;

    @Lob
    @Column(name = "file")
    private byte[] file;

    @Column(name = "file_content_type")
    private String fileContentType;

    @OneToMany(mappedBy = "release")
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

    public Release name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Release description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public Release date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCommitId() {
        return commitId;
    }

    public Release commitId(String commitId) {
        this.commitId = commitId;
        return this;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getConfig() {
        return config;
    }

    public Release config(String config) {
        this.config = config;
        return this;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getAdapters() {
        return adapters;
    }

    public Release adapters(String adapters) {
        this.adapters = adapters;
        return this;
    }

    public void setAdapters(String adapters) {
        this.adapters = adapters;
    }

    public String getOtherSteps() {
        return otherSteps;
    }

    public Release otherSteps(String otherSteps) {
        this.otherSteps = otherSteps;
        return this;
    }

    public void setOtherSteps(String otherSteps) {
        this.otherSteps = otherSteps;
    }

    public byte[] getFile() {
        return file;
    }

    public Release file(byte[] file) {
        this.file = file;
        return this;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public Release fileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
        return this;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public Set<Installation> getInstallations() {
        return installations;
    }

    public Release installations(Set<Installation> installations) {
        this.installations = installations;
        return this;
    }

    public Release addInstallations(Installation installation) {
        installations.add(installation);
        installation.setRelease(this);
        return this;
    }

    public Release removeInstallations(Installation installation) {
        installations.remove(installation);
        installation.setRelease(null);
        return this;
    }

    public void setInstallations(Set<Installation> installations) {
        this.installations = installations;
    }

    public Project getProject() {
        return project;
    }

    public Release project(Project project) {
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
        Release release = (Release) o;
        if(release.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, release.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Release{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", date='" + date + "'" +
            ", commitId='" + commitId + "'" +
            ", config='" + config + "'" +
            ", adapters='" + adapters + "'" +
            ", otherSteps='" + otherSteps + "'" +
            ", file='" + file + "'" +
            ", fileContentType='" + fileContentType + "'" +
            '}';
    }
}
