package org.objectquery.jdo.domain;

import java.util.List;

public class Person {
	private Long id;
	private String name;
	private List<Person> friends;
	private Person mom;
	private Person dud;
	private Home home;
	private Dog dog;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Person> getFriends() {
		return friends;
	}

	public void setFriends(List<Person> friends) {
		this.friends = friends;
	}

	public Person getMom() {
		return mom;
	}

	public void setMum(Person mum) {
		this.mom = mum;
	}

	public Person getDud() {
		return dud;
	}

	public void setDud(Person dud) {
		this.dud = dud;
	}

	public Home getHome() {
		return home;
	}

	public void setHome(Home home) {
		this.home = home;
	}

	public Dog getDog() {
		return dog;
	}

	public void setDog(Dog dog) {
		this.dog = dog;
	}

}
