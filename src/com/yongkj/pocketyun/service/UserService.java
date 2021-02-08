package com.yongkj.pocketyun.service;

import java.util.List;

import com.yongkj.pocketyun.dto.UserDto;

public interface UserService {
	
	void addUser(UserDto userDto);
	
	void delUserByUserUUID(String userUUID);
	
	List<UserDto> getUserDtos();
	
	void modUserByUserUUID(String userName, String password, String regSex, int regAge, String regEmail, String userUUID);

	UserDto getUserByUserNameAndPassword(String userName, String password);
	
	String getUserNameByUserUUID(String userUUID);
	
	UserDto getUserDtosByUserUUID(String userUUID);
	
	UserDto getUserByUserName(String userName);
	
	String getUserUUIDByUserName(String userName);
	
	void modUserLoginTimeByUserUUID(String loginTime, String userUUID);
	
}
