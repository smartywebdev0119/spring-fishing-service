package isa.FishingAdventure.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Inheritance(strategy = TABLE_PER_CLASS)
public abstract class ServiceProfile {
    @Id
    @SequenceGenerator(name = "mySeqGenV1", sequenceName = "mySeqV1", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGenV1")
    private Integer serviceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private int description;

    @Column(name = "cancellationRule", nullable = false)
    private double cancellationRule;

    @Column(name = "rating", nullable = false)
    private double rating;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "locationId", referencedColumnName = "locationId")
    public Location location;

    @OneToMany(mappedBy = "serviceProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Appointment> appointments;

    @ManyToMany
    @JoinTable(name = "rulesInServiceProfile",
            joinColumns = @JoinColumn(name = "serviceId", referencedColumnName = "serviceId"),
            inverseJoinColumns = @JoinColumn(name = "ruleId", referencedColumnName = "id"))
    private Set<Rule> rules;

    @OneToMany(mappedBy = "serviceProfiles", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AdditionalService> additionalServices;

    @ManyToMany(mappedBy = "subscriptions")
    private Set<Client> subscribedClients = new HashSet<Client>();

    @OneToMany(mappedBy = "serviceProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<Image>();

    public ServiceProfile(Integer serviceId, String name, int description, double cancellationRule, double rating, Location location, Set<Appointment> appointments, Set<Rule> rules, Set<AdditionalService> additionalServices) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.cancellationRule = cancellationRule;
        this.rating = rating;
        this.location = location;
        this.appointments = appointments;
        this.rules = rules;
        this.additionalServices = additionalServices;
    }

    public ServiceProfile() {
    }

    public Integer getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDescription() {
        return this.description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public double getCancellationRule() {
        return this.cancellationRule;
    }

    public void setCancellationRule(double cancellationRule) {
        this.cancellationRule = cancellationRule;
    }

    public double getRating() {
        return this.rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Set<Rule> getRules() {
        return this.rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }

    public Set<AdditionalService> getAdditionalServices() {
        return this.additionalServices;
    }

    public void setAdditionalServices(Set<AdditionalService> additionalServices) {
        this.additionalServices = additionalServices;
    }

    public Set<Client> getSubscribedClients() {
        return subscribedClients;
    }

    public void setSubscribedClients(Set<Client> subscribedClients) {
        this.subscribedClients = subscribedClients;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }
}