package com.edu.filemanager.dao;

import java.util.List;

import com.edu.filemanager.model.UserProfile;


public interface UserProfileDao {

	List<UserProfile> findAll();
	
	UserProfile findByType(String type);
	
	UserProfile findById(int id);
}
