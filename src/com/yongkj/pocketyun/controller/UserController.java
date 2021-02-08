package com.yongkj.pocketyun.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.yongkj.pocketyun.basic.controller.BasicController;
import com.yongkj.pocketyun.dto.PathsDto;
import com.yongkj.pocketyun.dto.UserDto;
import com.yongkj.pocketyun.service.PathsService;
import com.yongkj.pocketyun.service.UserService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/userController")
public class UserController extends BasicController {

	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
	@Autowired
	@Qualifier("pathsService")
	private PathsService pathsService;
	
	@ModelAttribute
	@RequestMapping("/login")
	public void userLogin(HttpServletRequest request,HttpServletResponse response, String userName, String password) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		String md5Password=this.md5(password);
		UserDto userDto = userService.getUserByUserNameAndPassword(userName, md5Password);
		
		if(userDto == null) {
			json.put("message", "用户名或密码错误！");
		}else {
//			HttpSession session = request.getSession();
//			
//			session.setAttribute("username", userDto.getUserName());
//			session.setAttribute("admin", userDto.getAdmin());
//			session.setAttribute("id", userDto.getUserUUID());
			
//			Date datetime = this.getDate();
//			if(userDto.getLoginTime() == null) {
//				session.setAttribute("datatime", datetime);
//			}else{
//				session.setAttribute("datatime", userDto.getLoginTime());
//			}
			
			String loginTime = this.getStringDate(new Date());
			userService.modUserLoginTimeByUserUUID(loginTime, userDto.getUserUUID());
			json.put("userUUID", userDto.getUserUUID());
		}
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping(value="/loginOut")
	public void loginOut(HttpServletRequest request,HttpServletResponse response) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/register")
	public void userRegister(HttpServletRequest request,HttpServletResponse response, String userName, String password, String regSex, String regAge, String regEmail) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		UserDto judgeUser = userService.getUserByUserName(userName);
		
		if(judgeUser != null) {
			json.put("message", "用户名已存在！");
		}else {
			UserDto userDto = new UserDto();
			String userUUID = this.getUUID();
			String md5Password = this.md5(password);
			String regPhoto = "/pocketyun/headPhoto/default/default.jpg";
			String regTime=this.getStringDate(new Date());
			
			userDto.setUserUUID(userUUID);
			userDto.setUserName(userName);
			userDto.setPassword(md5Password);
			userDto.setRegSex(regSex);
			userDto.setRegAge(Integer.parseInt(regAge));
			userDto.setRegEmail(regEmail);
			userDto.setRegPhoto(regPhoto);
			userDto.setRegTime(regTime);
			userDto.setAdmin("0");
			
			userService.addUser(userDto);
		}
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/delUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response, String userName) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");

		String userUUID = userService.getUserUUIDByUserName(userName);
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		File file = new File(rootPath);
		if (file.exists()) {
//			String cmd = "rm -r " + rootPath;
//			Process process = Runtime.getRuntime().exec(cmd);
//			process.waitFor();
			
			FileUtils.deleteQuietly(new File(rootPath));
		}
		
		pathsService.delPathsByUserUUID(userUUID);
		userService.delUserByUserUUID(userUUID);
		
		List<UserDto> userDtosList = userService.getUserDtos();
		json.put("usersList", userDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/getUser")
	public void getUsersByUUID(HttpServletRequest request,HttpServletResponse response, String userUUID) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		if(userDto != null) {
			json.put("userUUID", userDto.getUserUUID());
			json.put("userName", userDto.getUserName());
			json.put("regSex", userDto.getRegSex());
			json.put("regAge", userDto.getRegAge());
			json.put("regEmail", userDto.getRegEmail());
		}else {
			json.put("message", "无用户信息！");
		}
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/getUsers")
	public void getUsers(HttpServletRequest request,HttpServletResponse response) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		List<UserDto> userDtosList = userService.getUserDtos();
		json.put("usersList", userDtosList);
		
		String[] userSpace = new String[userDtosList.size()];
		for(int i = 0; i < userDtosList.size(); i++) {
			double totalSizes = 0;
			List<PathsDto> pathsDtosListBySize = pathsService.getFilesByUserUUIDAndSize(userDtosList.get(i).getUserUUID());
			for(int j = 0; j < pathsDtosListBySize.size(); j++) {
				double fileSize = Double.parseDouble(pathsDtosListBySize.get(j).getSize().replace("MB", ""));
				totalSizes += fileSize;
			}
			userSpace[i] = String.valueOf(totalSizes);
		}
		json.put("userSpace", userSpace);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/modUser")
	public void modUser(HttpServletRequest request,HttpServletResponse response, String userName, String password, String regSex, String regAge, String regEmail, String userUUID) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String md5Password = this.md5(password);
		userService.modUserByUserUUID(userName, md5Password, regSex, Integer.parseInt(regAge), regEmail, userUUID);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/uploadHead")
	public void uploadHead(HttpServletRequest request,HttpServletResponse response, MultipartFile regPhoto) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		if(sessionTimeout(request)){
			json.put("message", "页面过期，请重新登录");
		}else{
			
		}
		
		this.writeJson(json.toString(), response);
	}
	
}
