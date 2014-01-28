package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.File;

public interface IFileService extends IGenericService<File, Long> {
	
	public File findByLocation(String location);
	public File uploadFile(byte[] fileByteArray, String folder);
}
