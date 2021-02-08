package com.yongkj.pocketyun.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.yongkj.pocketyun.basic.controller.BasicController;
import com.yongkj.pocketyun.dto.PathsDto;
import com.yongkj.pocketyun.dto.UserDto;
import com.yongkj.pocketyun.service.PathsService;
import com.yongkj.pocketyun.service.UserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/pathsController")
public class PathsController extends BasicController {

	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
	@Autowired
	@Qualifier("pathsService")
	private PathsService pathsService;
	
	@ModelAttribute
	@RequestMapping("/saveText")
	public void saveText(HttpServletRequest request,HttpServletResponse response, String userUUID,String folderPathsUUID, String pathsUUID, String textContent) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto pathsDto = pathsService.getFilesByPathsUUID(pathsUUID);
		PathsDto folderPathsDto = pathsService.getFilesByPathsUUID(folderPathsUUID);
		
		String filePath = rootPath + pathsDto.getPath();
		
		FileWriter writeFile = new FileWriter(filePath);
        BufferedWriter writer = new BufferedWriter(writeFile);
        
        writer.write(textContent);
        
        writer.flush();
        writeFile.close();
        
        File file = new File(filePath);
        double size = (double) file.length() / 1048576;
		String nSize =  String.format("%.2f", size) + "MB";
		if(size < 0.005) {
			size = (double) file.length() / 1024;
			nSize =  String.format("%.2f", size) + "KB";
		}
        
        String modTime = this.getStringDate(new Date());
        
        pathsService.modPathsModTimeAndSizeByPathsUUID(nSize, modTime, pathsUUID);
        
        double totalSizes = 0;
		List<PathsDto> pathsDtosListBySize = pathsService.getFilesByUserUUIDAndSize(userUUID);
		for(int i = 0; i < pathsDtosListBySize.size(); i++) {
			double fileSize = Double.parseDouble(pathsDtosListBySize.get(i).getSize().replace("MB", ""));
			totalSizes += fileSize;
		}
		json.put("totalSizes", totalSizes);
        
        List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (folderPathsDto.getDepth() + 1 == 1 ? folderPathsDto.getPath() + "_%" : folderPathsDto.getPath() + "/_%"), folderPathsDto.getDepth() + 1);
        json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/getText")
	public void getText(HttpServletRequest request,HttpServletResponse response, String userUUID, String pathsUUID) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto pathsDto = pathsService.getFilesByPathsUUID(pathsUUID);
		
		String filePath = rootPath + pathsDto.getPath();
		
		File file = new File(filePath);
        FileReader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s = "";
        while ((s =bReader.readLine()) != null) {
            sb.append(s + "\n");
        }
        bReader.close();
        String str = sb.toString();
        
        json.put("textContent", str);
        this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/download")
	public void uploadFile(HttpServletRequest request,HttpServletResponse response, String pathsUUID) throws Exception {
		PathsDto pathsDto = pathsService.getFilesByPathsUUID(pathsUUID);
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(pathsDto.getUserUUID());
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		String filePath = rootPath + pathsDto.getPath();
		String fileName = URLEncoder.encode(pathsDto.getFilename(),"UTF-8");
		fileName = fileName.replaceAll("\\+", "%20");
		
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        File f= new File(filePath);
        response.setHeader("Content-Length",String.valueOf(f.length()));
        response.setContentType("multipart/form-data");
        
        FileInputStream in = new FileInputStream(filePath);
        OutputStream out = response.getOutputStream();
        byte buffer[] = new byte[1024];
        int len = 0;
        while((len = in.read(buffer)) > 0){
        	out.write(buffer, 0, len);
        }
        in.close();
        out.close();
        
	}
	
	@ModelAttribute
	@RequestMapping("/getHttpFile")
	public void getHttpFile(HttpServletRequest request,HttpServletResponse response, String pathsUUID, String fileUrl) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		PathsDto pathsDto = pathsService.getFilesByPathsUUID(pathsUUID);
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(pathsDto.getUserUUID());
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		URL url = new URL(fileUrl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		
		String fileName = urlConnection.getHeaderField("Content-Disposition");
		if(fileName == null) {
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
        }else {
            fileName = new String(fileName.getBytes("ISO-8859-1"), "GBK");
            fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename=")+9),"UTF-8");
        }
        
		String newPath = "";
		String nPath = "";
		if(pathsDto.getPath().equals("/")) {
			newPath = rootPath + pathsDto.getPath() + fileName;
			nPath = pathsDto.getPath() + fileName;
		}else {
			newPath = rootPath + pathsDto.getPath() + "/" + fileName;
			nPath = pathsDto.getPath() + "/" + fileName;
		}
		
		File file = new File(newPath);
		
		if(!file.exists()) {
			FileUtils.copyURLToFile(url, new File(newPath));
			
			if(file.exists()) {
				String nPathsUUID = this.getUUID();
				
				double size = (double) urlConnection.getContentLengthLong() / 1048576;
				String nSize =  String.format("%.2f", size) + "MB";
				if(size < 0.005) {
					size = (double) urlConnection.getContentLengthLong() / 1024;
					nSize =  String.format("%.2f", size) + "KB";
				}
				int nDepth = pathsDto.getDepth() + 1;
				String addTime = this.getStringDate(new Date());
				String modTime = addTime;
				
				PathsDto nPathsDto = new PathsDto();
				nPathsDto.setPathsUUID(nPathsUUID);
				nPathsDto.setUserUUID(pathsDto.getUserUUID());
				nPathsDto.setPath(nPath);
				nPathsDto.setFilename(fileName);
				nPathsDto.setSize(nSize);
				nPathsDto.setDepth(nDepth);
				nPathsDto.setAddTime(addTime);
				nPathsDto.setModTime(modTime);
	            pathsService.addPaths(nPathsDto);
	            
	            json.put("fileName", fileName);
			}else {
				json.put("message", "服务器网络错误！");
			}
		}else {
			json.put("message", "文件已存在！");
		}
		
		
		double totalSizes = 0;
		List<PathsDto> pathsDtosListBySize = pathsService.getFilesByUserUUIDAndSize(pathsDto.getUserUUID());
		for(int i = 0; i < pathsDtosListBySize.size(); i++) {
			double fileSize = Double.parseDouble(pathsDtosListBySize.get(i).getSize().replace("MB", ""));
			totalSizes += fileSize;
		}
		json.put("totalSizes", totalSizes);
		
		List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(pathsDto.getUserUUID(), (pathsDto.getDepth() + 1 == 1 ? pathsDto.getPath() + "_%" : pathsDto.getPath() + "/_%"), pathsDto.getDepth() + 1);
        json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/uploads")
	public void uploadFiles(HttpServletRequest request,HttpServletResponse response, @RequestParam("userUUID")String userUUID, @RequestParam("pathsUUID") String pathsUUID, @RequestParam("files") MultipartFile[] files) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
//		json.put("filesSize", files.length);
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto pathsDto = pathsService.getFilesByPathsUUID(pathsUUID);
		
		for(int i = 0; i < files.length; i++) {
			
			String nPathsUUID = this.getUUID();
			String newPath = "";
			String nPath = "";
			if(pathsDto.getPath().equals("/")) {
				newPath = rootPath + pathsDto.getPath() + files[i].getOriginalFilename();
				nPath = pathsDto.getPath() + files[i].getOriginalFilename();
			}else {
				newPath = rootPath + pathsDto.getPath() + "/" + files[i].getOriginalFilename();
				nPath = pathsDto.getPath() + "/" + files[i].getOriginalFilename();
			}
			double size = (double) files[i].getSize() / 1048576;
			String nSize =  String.format("%.2f", size) + "MB";
			if(size < 0.005) {
				size = (double) files[i].getSize() / 1024;
				nSize =  String.format("%.2f", size) + "KB";
			}
			int nDepth = pathsDto.getDepth() + 1;
			String addTime = this.getStringDate(new Date());
			String modTime = addTime;
			
			File file = new File(newPath);
			if(!file.exists()) {
				PathsDto nPathsDto = new PathsDto();
				nPathsDto.setPathsUUID(nPathsUUID);
				nPathsDto.setUserUUID(userUUID);
				nPathsDto.setPath(nPath);
				nPathsDto.setFilename(files[i].getOriginalFilename());
				nPathsDto.setSize(nSize);
				nPathsDto.setDepth(nDepth);
				nPathsDto.setAddTime(addTime);
				nPathsDto.setModTime(modTime);
	            pathsService.addPaths(nPathsDto);
	            
	            files[i].transferTo(new File(newPath));
			}else {
				json.put("message", "文件已存在！");
			}
			
		}
		
		double totalSizes = 0;
		List<PathsDto> pathsDtosListBySize = pathsService.getFilesByUserUUIDAndSize(userUUID);
		for(int i = 0; i < pathsDtosListBySize.size(); i++) {
			double fileSize = Double.parseDouble(pathsDtosListBySize.get(i).getSize().replace("MB", ""));
			totalSizes += fileSize;
		}
		json.put("totalSizes", totalSizes);
		
		List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (pathsDto.getDepth() + 1 == 1 ? pathsDto.getPath() + "_%" : pathsDto.getPath() + "/_%"), pathsDto.getDepth() + 1);
        json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/mkdir")
	public void mkdirFolder(HttpServletRequest request,HttpServletResponse response, String userUUID, String path, int depth, String folder) {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		String folderPath = "";
		if(path.equals("/")) {
			folderPath = rootPath + path + folder;
		}else {
			folderPath = rootPath + path + "/" + folder;
		}
		
		File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
            
            String pathsUUID = this.getUUID();
            String size = "--";
            String addTime = this.getStringDate(new Date());
            String modTime = this.getStringDate(new Date());
            
            String folderRelativePath = "";
    		if(path.equals("/")) {
    			folderRelativePath = path + folder;
    		}else {
    			folderRelativePath = path + "/" + folder;
    		}
            
            PathsDto pathsDto = new PathsDto();
            pathsDto.setPathsUUID(pathsUUID);
            pathsDto.setUserUUID(userUUID);
            pathsDto.setPath(folderRelativePath);
            pathsDto.setFilename("/" + folder);
            pathsDto.setSize(size);
            pathsDto.setDepth(depth);
            pathsDto.setAddTime(addTime);
            pathsDto.setModTime(modTime);
            pathsService.addPaths(pathsDto);
        }else {
        	json.put("message", "文件夹已存在！");
        }
        
        List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (depth == 1 ? path + "_%" : path + "/_%"), depth);
        json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/delFile")
	public void delFileOrFolder(HttpServletRequest request,HttpServletResponse response, String userUUID, String lastPathsUUID, String pathsUUID) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto lastPathsDto = pathsService.getFilesByPathsUUID(lastPathsUUID);
		PathsDto pathsDto = pathsService.getFilesByPathsUUID(pathsUUID);
		if(pathsDto.getFilename().indexOf("/") != -1) {
			String path = pathsDto.getPath();
			String folderPath = rootPath + path;
			
//			String cmd = "rm -r " + folderPath;
//			Process process = Runtime.getRuntime().exec(cmd);
//			process.waitFor();
			
			FileUtils.deleteQuietly(new File(folderPath));
			
			pathsService.delPathsByPathsUUID(pathsUUID);
			pathsService.delPathsByUserUUIDAndFilePathAndDepth(userUUID, pathsDto.getPath() + "/_%", pathsDto.getDepth() + 1);
		}else {
			String path = pathsDto.getPath();
			String filePath = rootPath + path;
			File file = new File(filePath);
			if (!file.isDirectory()) {
				file.delete();
			}
			pathsService.delPathsByPathsUUID(pathsUUID);
		}
		
		double totalSizes = 0;
		List<PathsDto> pathsDtosListBySize = pathsService.getFilesByUserUUIDAndSize(userUUID);
		for(int i = 0; i < pathsDtosListBySize.size(); i++) {
			double fileSize = Double.parseDouble(pathsDtosListBySize.get(i).getSize().replace("MB", ""));
			totalSizes += fileSize;
		}
		json.put("totalSizes", totalSizes);
		
		List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (lastPathsDto.getDepth() + 1 == 1 ? lastPathsDto.getPath() + "_%" : lastPathsDto.getPath() + "/_%"), lastPathsDto.getDepth() + 1);
		json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/delFiles")
	public void delFilesOrFolders(HttpServletRequest request,HttpServletResponse response, String userUUID, String lastPathsUUID, String filesJsonArray) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto lastPathsDto = pathsService.getFilesByPathsUUID(lastPathsUUID);
		
		JSONArray jsonArray = JSONArray.fromObject(filesJsonArray);
		for(int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = JSONObject.fromObject(jsonArray.get(i));
			if(jsonObject.getString("filename").indexOf("/") != -1) {
				String path = jsonObject.getString("path");
				String folderPath = rootPath + path;
				
//				String cmd = "rm -r " + folderPath;
//				Process process = Runtime.getRuntime().exec(cmd);
//				process.waitFor();
				
				FileUtils.deleteQuietly(new File(folderPath));
				
				pathsService.delPathsByPathsUUID(jsonObject.getString("pathsUUID"));
				pathsService.delPathsByUserUUIDAndFilePathAndDepth(userUUID, jsonObject.getString("path") + "/_%", jsonObject.getInt("depth") + 1);
			}else {
				String path = jsonObject.getString("path");
				String filePath = rootPath + path;
				File file = new File(filePath);
				if (!file.isDirectory()) {
					file.delete();
				}
				pathsService.delPathsByPathsUUID(jsonObject.getString("pathsUUID"));
			}
		}
		
		double totalSizes = 0;
		List<PathsDto> pathsDtosListBySize = pathsService.getFilesByUserUUIDAndSize(userUUID);
		for(int i = 0; i < pathsDtosListBySize.size(); i++) {
			double fileSize = Double.parseDouble(pathsDtosListBySize.get(i).getSize().replace("MB", ""));
			totalSizes += fileSize;
		}
		json.put("totalSizes", totalSizes);
		
		List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (lastPathsDto.getDepth() + 1 == 1 ? lastPathsDto.getPath() + "_%" : lastPathsDto.getPath() + "/_%"), lastPathsDto.getDepth() + 1);
		json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/moveFiles")
	public void moveFilesOrFolders(HttpServletRequest request,HttpServletResponse response, String userUUID, String oldPathsUUID, String newPathsUUID, String filesJsonArray) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto oldPathsDto = pathsService.getFilesByPathsUUID(oldPathsUUID);
		PathsDto newPathsDto = pathsService.getFilesByPathsUUID(newPathsUUID);
		
		JSONArray jsonArray = JSONArray.fromObject(filesJsonArray);
		for(int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = JSONObject.fromObject(jsonArray.get(i));
			
			String oldPath = rootPath + jsonObject.getString("path");
			String newPath = "";
			String nPath = "";
			if(newPathsDto.getPath().equals("/")) {
				newPath = rootPath + newPathsDto.getPath() + jsonObject.getString("filename").replace("/", "");
				nPath = newPathsDto.getPath() + jsonObject.getString("filename").replace("/", "");
			}else {
				newPath = rootPath + newPathsDto.getPath() + "/" + jsonObject.getString("filename").replace("/", "");
				nPath = newPathsDto.getPath() + "/" + jsonObject.getString("filename").replace("/", "");
			}
			
			int nDepth = jsonObject.getInt("depth") - oldPathsDto.getDepth() + newPathsDto.getDepth();
			String modTime = this.getStringDate(new Date());
			
			if(jsonObject.getString("filename").indexOf("/") != -1) {
				
//				String cmd = "";
//				if(System.getProperty("os.name").toLowerCase().contains("windows")) {
//					cmd = "mv \"" + oldPath + "\" \"" + newPath + "\"";
//					writeToLog(request, cmd);
//				}else {
//					cmd = "mv " + oldPath.replace(" ", "\\ ") + " " + newPath.replace(" ", "\\ ");
//					writeToLog(request, cmd);
//				}
//				Process process = Runtime.getRuntime().exec(cmd);
//				process.waitFor();
				
				FileUtils.moveDirectory(new File(oldPath), new File(newPath));
				
				List<PathsDto> pathsDtosList = pathsService.getReNameOrDeleteOrMoveFilesByUserUUIDAndFileNameANDDepth(userUUID, jsonObject.getString("path") + "/_%", jsonObject.getInt("depth") + 1);
				for(int j = 0; j < pathsDtosList.size(); j++) {
					
					String _nPath = pathsDtosList.get(j).getPath().replaceFirst(jsonObject.getString("path"), nPath);
					int _nDepth = pathsDtosList.get(j).getDepth() - jsonObject.getInt("depth") + nDepth;
					
					pathsService.movePathsByPathsUUID(_nPath, _nDepth, modTime, pathsDtosList.get(j).getPathsUUID());
				}
			}else {
//				String cmd = "mv '" + oldPath + "' '" + newPath + "'";
//				Process process = Runtime.getRuntime().exec(cmd);
//				process.waitFor();
				
				File oldName = new File(oldPath);
		        File newName = new File(newPath);
		        oldName.renameTo(newName);
			}
			
			pathsService.movePathsByPathsUUID(nPath, nDepth, modTime, jsonObject.getString("pathsUUID"));
		}
		
		List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (newPathsDto.getDepth() + 1 == 1 ? newPathsDto.getPath() + "_%" : newPathsDto.getPath() + "/_%"), newPathsDto.getDepth() + 1);
		json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	public void writeToLog(HttpServletRequest request, String i) throws Exception {
        String fileName = request.getSession().getServletContext().getRealPath("") + "Log.txt";
        FileWriter writeFile = new FileWriter(fileName, true);
        BufferedWriter writer = new BufferedWriter(writeFile);
        writer.write(i + "\n");
        
        writer.flush();
        writeFile.close();
    }
	
	@ModelAttribute
	@RequestMapping("/getFiles")
	public void getFileAndFolder(HttpServletRequest request, HttpServletResponse response, String userUUID, String path, int depth) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		File file = new File(rootPath);
		if(!file.exists()) {
			
			file.mkdirs();
	        
	        String pathsUUID = this.getUUID();
	        String thePath = "/";
	        String thefilename = "/";
	        String size = "--";
	        int theDepth = 0;
	        String addTime = this.getStringDate(new Date());
	        String modTime = this.getStringDate(new Date());
	        
	        PathsDto pathsDto = new PathsDto();
	        pathsDto.setPathsUUID(pathsUUID);
	        pathsDto.setUserUUID(userUUID);
	        pathsDto.setPath(thePath);
	        pathsDto.setFilename(thefilename);
	        pathsDto.setSize(size);
	        pathsDto.setDepth(theDepth);
	        pathsDto.setAddTime(addTime);
	        pathsDto.setModTime(modTime);
	        pathsService.addPaths(pathsDto);
		}

		List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (depth == 1 ? path + "_%" : path + "/_%"), depth);
		json.put("pathsDtosList", pathsDtosList);

        if(depth == 1) {
			json.put("rootPathUUID", pathsService.getRootPathsUUIDByUserUUID(userUUID));
        }
        
        double totalSizes = 0;
		List<PathsDto> pathsDtosListBySize = pathsService.getFilesByUserUUIDAndSize(userUUID);
		for(int i = 0; i < pathsDtosListBySize.size(); i++) {
			double fileSize = Double.parseDouble(pathsDtosListBySize.get(i).getSize().replace("MB", ""));
			totalSizes += fileSize;
		}
		
		json.put("totalSizes", totalSizes);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/renameFile")
	public void renameFileOrFolder(HttpServletRequest request,HttpServletResponse response, String userUUID, String lastPathsUUID, String pathsUUID, String newFilename) throws Exception {
		JSONObject json = new JSONObject();
		json.put("message", "");
		
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto lastPathsDto = pathsService.getFilesByPathsUUID(lastPathsUUID);
		PathsDto pathsDto = pathsService.getFilesByPathsUUID(pathsUUID);
		
		String oldFilePath = rootPath + pathsDto.getPath();
		String newFilePath = "";
		if(lastPathsDto.getPath().equals("/")) {
			newFilePath = rootPath + lastPathsDto.getPath() + newFilename;
		}else {
			newFilePath = rootPath + lastPathsDto.getPath() + "/" + newFilename;
		}
		
		File oldfile = new File(oldFilePath);
		File newfile = new File(newFilePath);
		oldfile.renameTo(newfile);
		
//		String cmd = "mv " + oldFilePath + " " + newFilePath;
//		Process process = Runtime.getRuntime().exec(cmd);
//		process.waitFor();
		
		String modTime = this.getStringDate(new Date());
		if(pathsDto.getFilename().indexOf("/") != -1) {
			
			if(lastPathsDto.getPath().equals("/")) {
				pathsService.modPathsByPathsUUID(lastPathsDto.getPath() + newFilename, "/" + newFilename, modTime, pathsUUID);
			}else {
				pathsService.modPathsByPathsUUID(lastPathsDto.getPath() + "/" + newFilename, "/" + newFilename, modTime, pathsUUID);
			}
			
			
			List<PathsDto> pathsDtosList = pathsService.getReNameOrDeleteOrMoveFilesByUserUUIDAndFileNameANDDepth(userUUID, pathsDto.getPath() + "/_%", pathsDto.getDepth() + 1);
			for(int i = 0; i < pathsDtosList.size(); i++) {
				
				String nPath = "";
				if(lastPathsDto.getPath().equals("/")) {
					nPath = pathsDtosList.get(i).getPath().replaceFirst(pathsDto.getPath(), lastPathsDto.getPath() + newFilename);
				}else {
					nPath = pathsDtosList.get(i).getPath().replaceFirst(pathsDto.getPath(), lastPathsDto.getPath() + "/" + newFilename);
				}
				
				pathsService.modPathsByPathsUUID(nPath, pathsDtosList.get(i).getFilename(), modTime, pathsDtosList.get(i).getPathsUUID());
			}
		}else {
			pathsService.modPathsByPathsUUID(lastPathsDto.getPath() + "/" + newFilename, newFilename, modTime, pathsUUID);
		}
		
		List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, (lastPathsDto.getDepth() + 1 == 1 ? lastPathsDto.getPath() + "_%" : lastPathsDto.getPath() + "/_%"), lastPathsDto.getDepth() + 1);
		json.put("pathsDtosList", pathsDtosList);
		
		this.writeJson(json.toString(), response);
	}
	
	@ModelAttribute
	@RequestMapping("/moveFile")
	public void moveFileOrFolder(HttpServletRequest request,HttpServletResponse response, String oldPathsUUID, String userUUID, String newPathsUUID) throws Exception {
		String realPath = request.getServletContext().getRealPath("");
		String fileSystem = realPath + "fileSystem";
		
		UserDto userDto = userService.getUserDtosByUserUUID(userUUID);
		String rootPath = fileSystem + "/" + userDto.getUserName();
		
		PathsDto oldPathsDto = pathsService.getFilesByPathsUUID(oldPathsUUID);
		PathsDto newPathsDto = pathsService.getFilesByPathsUUID(newPathsUUID);
		
		if(oldPathsDto.getFilename().indexOf("/") != -1) {
			String oldPath = rootPath + oldPathsDto.getPath();
			String newPath = rootPath + newPathsDto.getPath() + oldPathsDto.getFilename();
			String cmd = "mv " + oldPath + " " + newPath;
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
			
			List<PathsDto> pathsDtosList = pathsService.getFilesByUserUUIDAndFilePathANDDepth(userUUID, "%" + oldPathsDto.getPath() + "%", oldPathsDto.getDepth());
			String oldFolderName = oldPathsDto.getFilename().replace("/", "");
			for(int i = 0; i < pathsDtosList.size(); i++) {
				
				String[] paths = pathsDtosList.get(i).getPath().split("/");
				String newFilePath = "";
				for(int j = 0; j < paths.length; j++) {
					if(paths[j].equals(oldFolderName) && j == oldPathsDto.getDepth() - 1) {
						for(int k = j + 1; k < paths.length; k++) {
							newFilePath += ("/" + paths[k]);
						}
						break;
					}
				}
				if(newFilePath.equals("")) {
					newFilePath = newPathsDto.getPath() + "/" + oldPathsDto.getFilename();
				}else {
					newFilePath = newPathsDto.getPath() + newFilePath;
				}
				
				int newFileDepth = pathsDtosList.get(i).getDepth() - oldPathsDto.getDepth() + newPathsDto.getDepth() + 1;
				String modTime = this.getStringDate(new Date());
				
				pathsService.movePathsByPathsUUID(newFilePath, newFileDepth, modTime, pathsDtosList.get(i).getPathsUUID());
			}
		}else {
			String oldPath = rootPath + oldPathsDto.getPath();
			String newPath = rootPath + newPathsDto.getPath() + "/" + oldPathsDto.getFilename();
			String cmd = "mv " + oldPath + " " + newPath;
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
			
			String modTime = this.getStringDate(new Date());
			pathsService.movePathsByPathsUUID(newPath, newPathsDto.getDepth() + 1, modTime, oldPathsUUID);
		}
	}
	
	@ModelAttribute
	@RequestMapping("/test")
	public void test(HttpServletRequest request,HttpServletResponse response, String json) throws Exception {
		JSONArray jsonArray = JSONArray.fromObject(json);
		System.out.println(jsonArray.size());
	}
	
}
