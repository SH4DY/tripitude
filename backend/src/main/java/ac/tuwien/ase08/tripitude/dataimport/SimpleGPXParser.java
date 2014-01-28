package ac.tuwien.ase08.tripitude.dataimport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;

@Component
public class SimpleGPXParser {
	
	@Autowired
	IHotspotService hotspotService;
	
	public List<Coordinate> parseGPXCoordinates(File file) throws SAXException, IOException, ParserConfigurationException {
		
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		
		NodeList nList = doc.getElementsByTagName("rtept");
		
		for (int i = 0; i < nList.getLength(); i++) {
			
			Node nNode = nList.item(i);
			
			Element eElement = (Element) nNode;
			
			coordinates.add(new Coordinate(Double.valueOf(eElement.getAttribute("lat")), 
					                       Double.valueOf(eElement.getAttribute("lon"))));
		}
		
	
		return coordinates;
	}
	
    public List<Hotspot> parseGPXHotspots(File file) throws SAXException, IOException, ParserConfigurationException {
		
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		
		List<Hotspot> hotspots = new ArrayList<Hotspot>();
		
		NodeList nList = doc.getElementsByTagName("hotspot");
		
		for (int i = 0; i < nList.getLength(); i++) {
			
			Node nNode = nList.item(i);
			
			Element eElement = (Element) nNode;
			
			Long.valueOf(eElement.getAttribute("id"));
			
			Hotspot hotspot = hotspotService.find(Long.valueOf(eElement.getAttribute("id")));
			
			if (hotspot != null) {				
				hotspots.add(hotspot);
			}
		}
		
	
		return hotspots;
	}
	
	
}
