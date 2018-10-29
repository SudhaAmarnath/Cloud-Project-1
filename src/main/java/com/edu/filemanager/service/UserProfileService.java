package com.edu.filemanager.service;

import java.util.List;

import com.edu.filemanager.model.UserProfile;


public interface UserProfileService {

	UserProfile findById(int id);

	UserProfile findByType(String type);
	
	List<UserProfile> findAll();
	
}
