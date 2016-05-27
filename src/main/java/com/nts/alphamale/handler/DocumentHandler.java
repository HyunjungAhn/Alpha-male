/**
 * AlphaMale for web
Copyright (C) 2016 NHN Technology Services

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 */

package com.nts.alphamale.handler;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nts.alphamale.data.ElementInfo;

public class DocumentHandler {

	
	static Logger log = LogManager.getLogger(DocumentHandler.class);

	/**
	 * Hierarchy dump의 bounds 정보를 이용하여 입력된 좌표(x,y)가 포함된 면적이 가장 작은 Node를 반환
	 * 
	 * @param Hierarchy Dump XML Document
	 * @param x-axis
	 * @param y-axis
	 * @return Node 입력된 좌표(x,y)가 포함된 면적(area)이 가장 작은 Node
	 */
	public static Node findElementWithBounds(Document doc, int evt_x, int evt_y) {
		NodeList bounds = findNodeListByXPath(doc, "//@bounds");
		Rectangle findRectangle = null;
		for (int i = 0, len = bounds.getLength(); i < len; i++) {
			String[] point = bounds.item(i).getTextContent()
					.replaceAll("\\]\\[", ",").replaceAll("\\[", "")
					.replaceAll("\\]", "").split(",");
			int x = Integer.parseInt(point[0]);
			int y = Integer.parseInt(point[1]);
			int width = Integer.parseInt(point[2]) - x;
			int height = Integer.parseInt(point[3]) - y;
			Rectangle rec = new Rectangle(x, y, width, height);
			if (rec.contains(evt_x, evt_y)) {
				if (findRectangle == null) {
					findRectangle = rec;
				}
				findRectangle = findRectangle.width * findRectangle.height > rec.width
						* rec.height ? rec : findRectangle;
			}
		}

		if (null != findRectangle) {
			String rec = "[" + (int) findRectangle.getX() + ","
					+ (int) findRectangle.getY() + "]["
					+ ((int) findRectangle.getMaxX()) + ","
					+ ((int) findRectangle.getMaxY()) + "]";
			return findNodeByXPath(doc, "//*[@bounds='" + rec + "']");
		}
		return null;
	}
	
	
	/**
	 * 식별된 element의 상위의 scrollable="true"인 element를 식별해서 id를 노출함.
	 * @param node
	 * @return
	 */
	public static String findScollableNodeId(Node node){
		if(node.getAttributes().getNamedItem("scrollable").getNodeValue().equals("true") && !node.getAttributes().getNamedItem("resource-id").getNodeValue().isEmpty()){
			String id= node.getAttributes().getNamedItem("resource-id").getNodeValue();
			String boundsInfo = node.getAttributes().getNamedItem("bounds").getNodeValue();
			String[] point = boundsInfo.replaceAll("\\]\\[", ",").replaceAll("\\[", "")
					.replaceAll("\\]", "").split(",");
			int x = Integer.parseInt(point[0]);
			int y = Integer.parseInt(point[1]);
			int width = Integer.parseInt(point[2]) - x;
			int height = Integer.parseInt(point[3]) - y;
			return String.format("%s,%d,%d,%d,%d", id,x,y,width,height);
			
		}else{
			if(node.getParentNode()==null)
				return "";
			else
				return findScollableNodeId(node.getParentNode());
		}
	}
	
	/**
	 * 식별된 element의 상위의 scrollable="true"인 element를 식별해서 id를 노출함.
	 * 
	 * @param node
	 * @return
	 */
	public static Node findScollableNodeInfo(Node node) {
		if (node == null) {
			return null;
		} else {
			if (node.getAttributes() != null &&
				node.getAttributes().getNamedItem("scrollable") != null &&
				node.getAttributes().getNamedItem("scrollable").getNodeValue().equals("true") && 
				!node.getAttributes().getNamedItem("resource-id").getNodeValue().isEmpty()) {
				return node;
			} else {
				if (node.getParentNode() == null)
					return null;
				else
					return findScollableNodeInfo(node.getParentNode());
			}
		}
	}

	/**
	 * Hierarchy dump에서 xpath에 해당되는 NodeList를 반환
	 * 
	 * @param Hierarchy Dump XML Document
	 * @param xpath expression
	 * @return NodeList xpath에 해당되는 NodeList
	 */
	public static NodeList findNodeListByXPath(Document doc, String xpathExpr) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			return (NodeList) xpath.evaluate(xpathExpr, doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Hierarchy dump에서 xpath에 해당되는 Node반환
	 * 
	 * @param Hierarchy Dump XML Document
	 * @param xpath expression
	 * @return Node xpath에 해당되는 Node
	 */
	public static Node findNodeByXPath(Document doc, String xpathExpr) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			return (Node) xpath.evaluate(xpathExpr, doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			log.error(e.getCause() + " : " + e.getMessage()+"\n"+doc.toString());
		}
		return null;
	}

	/**
	 * Hierarchy dump에서 xpath에 해당되는 문자열을 반환
	 * 
	 * @param Hierarchy Dump XML Document
	 * @param xpath expression
	 * @return String xpath에 해당되는 문자열
	 */
	public static String findStringByXPath(Document doc, String xpathExpr) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			return xpath.evaluate(xpathExpr, doc);
		} catch (XPathExpressionException e) {
			log.error(e.getCause() + " : " + e.getMessage()+"\n"+doc.toString());
		}
		return null;
	}

	/**
	 * Node의 attribute 정보를 이용하여 xpath를 반환
	 * 
	 * @param node
	 * @return String Node를 xpath로 반환
	 */
	public static String convertNodeToXpath(Node node) {
		StringBuilder xpathExpr = new StringBuilder();
		xpathExpr.append("//*[");

		String att_class = node.getAttributes().getNamedItem("class").getNodeValue();
		xpathExpr.append("@class='" + att_class + "'");

//		String att_pkg = node.getAttributes().getNamedItem("package").getNodeValue();
//		xpathExpr.append(" and @package='" + att_pkg + "'");

		String att_res = node.getAttributes().getNamedItem("resource-id").getNodeValue();
		if (!att_res.isEmpty())
			xpathExpr.append(" and @resource-id='" + att_res + "'");

		String att_desc = excapeQuote("content-desc", node.getAttributes().getNamedItem("content-desc").getNodeValue());
		if (!att_desc.isEmpty())
			xpathExpr.append(att_desc);

		String att_txt = excapeQuote("text", node.getAttributes().getNamedItem("text").getNodeValue());
		if (!att_txt.isEmpty())
			xpathExpr.append(att_txt);

		xpathExpr.append("]");
		return xpathExpr.toString();
	}

	/**
	 * @param doc
	 * @param xpath
	 * @param matchAttr
	 * @param attValue
	 * @return
	 */
	public static int getInstanceAt(Document doc, String xpath, String matchAttr, String attValue) {
		NodeList nl = findNodeListByXPath(doc, xpath);
		int instanceAt = 0;
		if (null != nl) {
			for (int i = 0, len = nl.getLength(); i < len; i++) {
				if (nl.item(i).getAttributes().getNamedItem(matchAttr).getNodeValue().equals(attValue))
					break;
				instanceAt++;
			}
		}
		return instanceAt;
	}

	/**
	 * xml 문자열을 Document로 변환
	 * 
	 * @param str
	 * @return dom
	 */
	public static Document convertStringToDocument(String str) {
		if (null != str && !str.isEmpty()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			Document doc = null;
			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return doc;
		}
		return null;
	}

	private static String excapeQuote(String attribute, String text) {
		if (text.isEmpty())
			return "";

		if (text.contains("'")) {
			if (text.split("'")[0].isEmpty())
				return " and contains(@" + attribute + ", '" + text.split("'")[1] + "')";
			else
				return " and contains(@" + attribute + ", '" + text.split("'")[0] + "')";
		}

		if (text.contains("\"")) {
			if (text.split("\"")[0].isEmpty())
				return " and contains(@" + attribute + ", '" + text.split("\"")[1] + "')";
			else
				return " and contains(@" + attribute + ", '" + text.split("\"")[0] + "')";
		}

		return " and @" + attribute + "='" + text + "'";
	}

	public static ElementInfo convertNodeToElementObject(Node node){
		if(node == null){
			return null;
		}
		NamedNodeMap attrMap = node.getAttributes();
		ElementInfo element = new ElementInfo();
		element.setIndex(Integer.valueOf(attrMap.getNamedItem("index").getNodeValue()));
		element.setText(attrMap.getNamedItem("text").getNodeValue());
		element.setResourceId(attrMap.getNamedItem("resource-id").getNodeValue());
		element.setClassName(attrMap.getNamedItem("class").getNodeValue());
		element.setPackageName(attrMap.getNamedItem("package").getNodeValue());
		element.setContentDesc(attrMap.getNamedItem("content-desc").getNodeValue());
		element.setCheckable(Boolean.valueOf(attrMap.getNamedItem("checkable").getNodeValue()));
		element.setClickable(Boolean.valueOf(attrMap.getNamedItem("clickable").getNodeValue()));
		element.setEnalbled(Boolean.valueOf(attrMap.getNamedItem("enabled").getNodeValue()));
		element.setFocusable(Boolean.valueOf(attrMap.getNamedItem("focusable").getNodeValue()));
		element.setFoucsed(Boolean.valueOf(attrMap.getNamedItem("focused").getNodeValue()));
		element.setScrollable(Boolean.valueOf(attrMap.getNamedItem("scrollable").getNodeValue()));
		element.setLongClickable(Boolean.valueOf(attrMap.getNamedItem("long-clickable").getNodeValue()));
		element.setPassword(Boolean.valueOf(attrMap.getNamedItem("password").getNodeValue()));
		element.setBounds(attrMap.getNamedItem("bounds").getNodeValue());
		return element;
	}
	
}
