package com.yongkj.pocketyun.service;

import java.util.List;

import com.yongkj.pocketyun.dto.PathsDto;

public interface PathsService {
	
	void addPaths(PathsDto pathsDto);
	
	void delPathsByPathsUUID(String pathsUUID);
	
	void delPathsByUserUUID(String userUUID);
	
	void delPathsByUserUUIDAndFilePathAndDepth(String userUUID, String path, int depth);
	
	List<PathsDto> getPathsDtos();
	
	List<PathsDto> getFilesByUserUUID(String userUUID);
	
	List<PathsDto> getFilesByUserUUIDAndSize(String userUUID);
	
	PathsDto getFilesByPathsUUID(String pathsUUID);
	
	String getFilePathByPathsUUID(String pathsUUID);
	
	String getRootPathsUUIDByUserUUID(String userUUID);
	
	String getFileNameByPathsUUID(String pathsUUID);
	
	List<PathsDto> getFilesByUserUUIDAndFilePathANDDepth(String userUUID, String path, int depth);
	
	List<PathsDto> getReNameOrDeleteOrMoveFilesByUserUUIDAndFileNameANDDepth( String userUUID, String path, int depth);
	
	void movePathsByPathsUUID(String path, int depth, String modTime, String pathsUUID);
	
	void modPathsByPathsUUID(String path, String filename, String modTime, String pathsUUID);
	
	void modPathsModTimeAndSizeByPathsUUID(String nSize, String modTime, String pathsUUID);
}
