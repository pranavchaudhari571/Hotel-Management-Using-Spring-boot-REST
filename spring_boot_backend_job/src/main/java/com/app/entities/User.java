package com.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonIgnore
    private Role role = Role.USER;

    @JsonManagedReference
    @OneToMany(mappedBy = "addedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hotel> addedHotels;

    @JsonIgnore
    @OneToMany(mappedBy = "addedBy", cascade = CascadeType.ALL)
    private List<Room> addedRooms;
}
