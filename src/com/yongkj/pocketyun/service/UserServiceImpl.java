package com.yongkj.pocketyun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yongkj.pocketyun.dto.UserDto;
import com.yongkj.pocketyun.mapper.PbUserMapper;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Autowired
	private PbUserMapper pbUserMapper;
	
	@Override
	public void addUser(UserDto userDto) {
		pbUserMapper.addUser(userDto);
	}
	
	@Override
	public void delUserByUserUUID(String userUUID) {
		pbUserMapper.delUserByUserUUID(userUUID);
	}
	
	@Override
	public List<UserDto> getUserDtos() {
		return pbUserMapper.getUserDtos();
	}
	
	@Override
	public void modUserLoginTimeByUserUUID(String loginTime, String userUUID) {
		pbUserMapper.modUserLoginTimeByUserUUID(loginTime, userUUID);
	}
	
	@Override
	public void modUserByUserUUID(String userName, String password, String regSex, int regAge, String regEmail, String userUUID) {
		pbUserMapper.modUserByUserUUID(userName, password, regSex, regAge, regEmail, userUUID);
	}
	
	@Override
	public UserDto getUserByUserNameAndPassword(String userName, String password) {
		return pbUserMapper.getUserByUserNameAndPassword(userName, password);
	}
	
	@Override
	public UserDto getUserByUserName(String userName) {
		return pbUserMapper.getUserByUserName(userName);
	}
	
	@Override
	public String getUserNameByUserUUID(String userUUID) {
		return pbUserMapper.getUserNameByUserUUID(userUUID);
	}
	
	@Override
	public UserDto getUserDtosByUserUUID(String userUUID) {
		return pbUserMapper.getUserDtosByUserUUID(userUUID);
	}
	
	@Override
	public String getUserUUIDByUserName(String userName) {
		return pbUserMapper.getUserUUIDByUserName(userName);
	}

}
