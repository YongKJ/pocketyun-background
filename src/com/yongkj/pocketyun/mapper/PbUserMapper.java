package com.yongkj.pocketyun.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yongkj.pocketyun.dto.UserDto;

public interface PbUserMapper {

	@Insert("INSERT INTO py_user (userUUID, userName, password, regSex, regAge, regEmail, regPhoto, regTime, admin) VALUES (#{userUUID}, #{userName}, #{password}, #{regSex}, #{regAge}, #{regEmail}, #{regPhoto}, #{regTime}, #{admin})")
	void addUser(UserDto userDto);
	
	@Delete("DELETE FROM py_user WHERE userUUID=#{userUUID}")
	void delUserByUserUUID(@Param("userUUID") String userUUID);
	
	@Select("SELECT * FROM py_user WHERE userName=#{userName}")
	UserDto getUserByUserName(@Param("userName") String userName);
	
	@Select("SELECT userName FROM py_user WHERE userUUID=#{userUUID}")
	String getUserNameByUserUUID(@Param("userUUID") String userUUID);
	
	@Select("SELECT userUUID FROM py_user WHERE userName=#{userName}")
	String getUserUUIDByUserName(@Param("userName") String userName);
	
	@Select("SELECT * FROM py_user WHERE userUUID=#{userUUID}")
	UserDto getUserDtosByUserUUID(@Param("userUUID") String userUUID);
	
	@Select("SELECT * FROM py_user WHERE userName=#{userName} and password=#{password}")
	UserDto getUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);
	
	@Select("SELECT * FROM py_user")
	List<UserDto> getUserDtos();
	
	@Update("UPDATE py_user SET loginTime = #{loginTime} WHERE userUUID = #{userUUID}")
	void modUserLoginTimeByUserUUID(@Param("loginTime") String loginTime, @Param("userUUID") String userUUID);
	
	@Update("UPDATE py_user SET userName = #{userName}, password = #{password}, regSex = #{regSex}, regAge = #{regAge}, regEmail = #{regEmail} WHERE userUUID = #{userUUID}")
	void modUserByUserUUID(@Param("userName") String userName, @Param("password") String password, @Param("regSex") String regSex, @Param("regAge") int regAge, @Param("regEmail") String regEmail, @Param("userUUID") String userUUID);
	
}
