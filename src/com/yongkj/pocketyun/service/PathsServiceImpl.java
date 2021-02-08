package com.yongkj.pocketyun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yongkj.pocketyun.dto.PathsDto;
import com.yongkj.pocketyun.mapper.PbPathsMapper;

@Service("pathsService")
public class PathsServiceImpl implements PathsService {
	
	@Autowired
	private PbPathsMapper pbPathsMapper;
	
	@Override
	public void addPaths(PathsDto pathsDto) {
		pbPathsMapper.addPaths(pathsDto);
	}
	
	@Override
	public void delPathsByPathsUUID(String pathsUUID) {
		pbPathsMapper.delPathsByPathsUUID(pathsUUID);
	}
	
	@Override
	public void delPathsByUserUUID(String userUUID) {
		pbPathsMapper.delPathsByUserUUID(userUUID);
	}
	
	@Override
	public void delPathsByUserUUIDAndFilePathAndDepth(String userUUID, String path, int depth) {
		pbPathsMapper.delPathsByUserUUIDAndFilePathAndDepth(userUUID, path, depth);
	}
	
	@Override
	public List<PathsDto> getPathsDtos() {
		return pbPathsMapper.getPathsDtos();
	}
	
	@Override
	public List<PathsDto> getFilesByUserUUID(String userUUID) {
		return pbPathsMapper.getFilesByUserUUID(userUUID);
	}
	
	@Override
	public List<PathsDto> getFilesByUserUUIDAndSize(String userUUID) {
		return pbPathsMapper.getFilesByUserUUIDAndSize(userUUID);
	}
	
	@Override
	public PathsDto getFilesByPathsUUID(String pathsUUID) {
		return pbPathsMapper.getFilesByPathsUUID(pathsUUID);
	}
	
	@Override
	public String getFilePathByPathsUUID(String pathsUUID) {
		return pbPathsMapper.getFilePathByPathsUUID(pathsUUID);
	}
	
	@Override
	public String getRootPathsUUIDByUserUUID(String userUUID) {
		return pbPathsMapper.getRootPathsUUIDByUserUUID(userUUID);
	}
	
	@Override
	public String getFileNameByPathsUUID(String pathsUUID) {
		return pbPathsMapper.getFileNameByPathsUUID(pathsUUID);
	}
	
	@Override
	public List<PathsDto> getFilesByUserUUIDAndFilePathANDDepth(String userUUID, String path, int depth) {
		return pbPathsMapper.getFilesByUserUUIDAndFilePathANDDepth(userUUID, path, depth);
	}
	
	@Override
	public List<PathsDto> getReNameOrDeleteOrMoveFilesByUserUUIDAndFileNameANDDepth( String userUUID, String path, int depth) {
		return pbPathsMapper.getReNameOrDeleteOrMoveFilesByUserUUIDAndFileNameANDDepth(userUUID, path, depth);
	}
	
	@Override
	public void movePathsByPathsUUID(String path, int depth, String modTime, String pathsUUID) {
		pbPathsMapper.movePathsByPathsUUID(path, depth, modTime, pathsUUID);
	}
	
	@Override
	public void modPathsByPathsUUID(String path, String filename, String modTime, String pathsUUID) {
		pbPathsMapper.modPathsByPathsUUID(path, filename, modTime, pathsUUID);
	}
	
	@Override
	public void modPathsModTimeAndSizeByPathsUUID(String nSize, String modTime, String pathsUUID) {
		pbPathsMapper.modPathsModTimeAndSizeByPathsUUID(nSize, modTime, pathsUUID);
	}

}
