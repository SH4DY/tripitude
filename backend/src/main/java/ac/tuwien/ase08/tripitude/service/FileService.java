package ac.tuwien.ase08.tripitude.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IFileDAO;
import ac.tuwien.ase08.tripitude.entity.File;
import ac.tuwien.ase08.tripitude.service.interfaces.IFileService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("fileService")
public class FileService implements IFileService {
	
	private static Integer MAX_SCALE = 60;
	private static String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
    private IFileDAO fileDAO;
	
	public void add(File entity) {
		fileDAO.add(entity);		
	}

	public void update(File entity) {
		fileDAO.update(entity);		
	}

	public void remove(File entity) {	
		deleteFile(entity);
		fileDAO.remove(entity);		
	}

	public File find(Long key) {
		return fileDAO.find(key);
	}

	public List<File> list() {
		return fileDAO.list();
	}
	
	public File findByLocation(String location){
		Query query = sessionFactory.getCurrentSession().createQuery("FROM File WHERE location = :location");
		query.setParameter("location", location);		
		List l = query.list();
		if (l.isEmpty()) {
			return null;
		}
		return (File) l.get(0);
	}
	
	@SuppressWarnings("resource")
	public File uploadFile(byte[] fileByteArray, String folder) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy-hhmmss.SSS");

		//get file extension
		MagicMatch match = null;		

		try {
			match = Magic.getMagicMatch(fileByteArray);
		} catch (Exception e) {
			return null;
		}

		
		//generate random string
		byte[] nonce_bytes = new byte[16];
		Random rand;
		String nonce = "";
		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
			rand.nextBytes(nonce_bytes);
			nonce = Long.toString(Math.abs(rand.nextLong()), 36).substring(0, 5);				
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}		
		
		String fileExtension = match.getExtension();
		
		if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(fileExtension.toLowerCase())) {
			return null;
		}

		String fullPath = servletContext.getRealPath("/" + folder);
		String filename =  "IMG_" + sdf.format(new Date()) +  "." + nonce + '.' + fileExtension;
		String filenameThumb =  "IMG_THUMB_" + sdf.format(new Date()) + "." + nonce + '.' + fileExtension;
		String destination = fullPath + "/" + filename;
		String destinationThumb = fullPath + "/" + filenameThumb;
		
		
		
		//crop and scale thumb
		InputStream in = new ByteArrayInputStream(fileByteArray);
		BufferedImage thumbNailBi = null;
		try {
			thumbNailBi = ImageIO.read(in);
			
			int minSide = Math.min(thumbNailBi.getWidth(), thumbNailBi.getHeight());
			int x = thumbNailBi.getWidth()/2-minSide/2;
			int y = thumbNailBi.getHeight()/2-minSide/2;
			thumbNailBi = Scalr.crop(thumbNailBi, x, y, minSide, minSide, Scalr.OP_ANTIALIAS);
			thumbNailBi = Scalr.resize(thumbNailBi, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT, MAX_SCALE, MAX_SCALE, Scalr.OP_ANTIALIAS);
		} catch (IOException e) {
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] fileThumbByteArray = null;
		try {
			ImageIO.write(thumbNailBi, fileExtension, baos);
			baos.flush();
			fileThumbByteArray = baos.toByteArray();
		} catch (IOException e) {
			return null;
		}
		
		// Write a image byte array into file system
		FileOutputStream imageOutFile;
		FileOutputStream imageOutFileThumb;
		try {
			imageOutFile = new FileOutputStream(destination);
			imageOutFileThumb = new FileOutputStream(destinationThumb);
		} catch (FileNotFoundException e) {
			return null;
		}
		try {
			imageOutFile.write(fileByteArray);
			imageOutFileThumb.write(fileThumbByteArray);
		} catch (IOException e) {
			return null;
		}
		try {
			imageOutFile.close();
		} catch (IOException e) {
			return null;
		}
		
		
		
		File file = new File();
		file.setLocation(folder + "/" + filename);
		file.setThumbLocation(folder + "/" + filenameThumb);
		
		return file;
				
	}
	
	private void deleteFile(File f) {
		
		java.io.File file = new java.io.File(servletContext.getRealPath("/" + f.getLocation()));
		java.io.File fileThumb = new java.io.File(servletContext.getRealPath("/" + f.getThumbLocation()));
		
		file.delete();
		fileThumb.delete();
	}
}
