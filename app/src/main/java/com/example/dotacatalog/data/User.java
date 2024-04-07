package com.example.dotacatalog.data;

import java.util.List;

public class User {
    private static User currentUser = new User();
    private String uid;
    private String fav_arcana;
    private String age;
    private String fav_persona;
    private String fav_team;
    private String hated_hero;
    private String mmr;
    private String name;
    private String surname;
    private String nickname;
    private String sign_hero;
    private List<String> favourites;

    public User(String uid, String fav_arcana, String age, String fav_persona, String fav_team, String hated_hero, String mmr, String name, String surname, String nickname, String sign_hero, List<String> favourites) {
        this.uid = uid;
        this.fav_arcana = fav_arcana;
        this.age = age;
        this.fav_persona = fav_persona;
        this.fav_team = fav_team;
        this.hated_hero = hated_hero;
        this.mmr = mmr;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.sign_hero = sign_hero;
        this.favourites = favourites;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFav_arcana() {
        return fav_arcana;
    }

    public void setFav_arcana(String fav_arcana) {
        this.fav_arcana = fav_arcana;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getFav_persona() {
        return fav_persona;
    }

    public void setFav_persona(String fav_persona) {
        this.fav_persona = fav_persona;
    }

    public String getFav_team() {
        return fav_team;
    }

    public void setFav_team(String fav_team) {
        this.fav_team = fav_team;
    }

    public String getHated_hero() {
        return hated_hero;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }

    public void setHated_hero(String hated_hero) {
        this.hated_hero = hated_hero;
    }

    public String getMmr() {
        return mmr;
    }

    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }

    public void setMmr(String mmr) {
        this.mmr = mmr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSign_hero() {
        return sign_hero;
    }

    public void setSign_hero(String sign_hero) {
        this.sign_hero = sign_hero;
    }
}
