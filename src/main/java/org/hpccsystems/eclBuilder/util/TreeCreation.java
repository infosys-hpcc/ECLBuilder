/**
 * 
 */
package org.hpccsystems.eclBuilder.util;

import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.hpccsystems.eclBuilder.controller.FileMetaTreeNode;
import org.hpccsystems.eclBuilder.domain.File;
import org.hpccsystems.eclBuilder.domain.FileMeta;
import org.hpccsystems.eclBuilder.domain.Folder;
import org.hpccsystems.eclBuilder.exceptions.HPCCException;
import org.hpccsystems.ws.client.HPCCWsClient;
import org.hpccsystems.ws.client.gen.wsdfu.v1_34.DFUInfoResponse;
import org.hpccsystems.ws.client.gen.wsdfu.v1_34.DFULogicalFile;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

/**
 * @author Bhuvaneswari_L02
 *
 */
public class TreeCreation {

	static {
		
		try{
		HttpsURLConnection httpsCon = (HttpsURLConnection) new URL("https://216.19.105.2:18010").openConnection();
  	  
        //TrustStore..
        char[] passphrase = "changeit".toCharArray(); //password
        KeyStore keystore = KeyStore.getInstance("JKS");
        //KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        System.getProperties().get("java_home");
        
        java.io.File d = new java.io.File(System.getenv("java_home"));
        java.io.File x =  Arrays.asList(d.list()).contains("lib") ? d : (Arrays.asList(d.getParentFile().list()).contains("lib") ? d.getParentFile() : d);
        Arrays.asList(d.getParentFile().list()).contains("lib");

        keystore.load(new FileInputStream(System.getenv("java_home") + "/lib/security/cacerts"), passphrase); //path

        //TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509"); //instance
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keystore);
        SSLContext context = SSLContext.getInstance("TLS");
        
        TrustManager[] trustManagers = tmf.getTrustManagers();
        context.init(null, trustManagers, null);
        SSLSocketFactory sf = context.getSocketFactory();

        httpsCon.setSSLSocketFactory(sf);
        
        HttpsURLConnection httpCon = httpsCon;
        
        String authStr = "rsundarrajan" + ":" + "March2016";
        
        String encoded = new String(Base64.encodeBase64(authStr.getBytes()));
        
        httpsCon.setRequestProperty("Authorization", "Basic " + encoded);
        
        httpsCon.setRequestMethod("POST");	
		}catch(Exception e){
			
		}
     }

	public static Folder populateTree(String currentFolder, HPCCWsClient connection, String hpccID) {

		FileMeta newFile = new FileMeta();

		newFile.setFileName("");

		List<FileMetaTreeNode> rootFile = new ArrayList<FileMetaTreeNode>();
		rootFile.add(new FileMetaTreeNode(newFile));

		List<FileMeta> files = new ArrayList<FileMeta>();

		try {
			files = getFileList(currentFolder, connection, hpccID);
		} catch (HPCCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Folder rootFolder = new Folder();
		rootFolder.setBaseFolderName("C");
		Folder f1;
		File file1;
		List<File> newFileList = new ArrayList<File>();
		List<Folder> newFolderList = new ArrayList<Folder>();

		for (FileMeta fm : files) {

			if (fm.getIsfileMeta()) {
				file1 = new File();
				file1.setFileName(fm.getFileName());
				file1.setActualFileName(fm.getDisplayFileName());

				newFileList.add(file1);
				rootFolder.setListOfFiles(newFileList);

			} else if (!fm.getIsfileMeta()) {
				f1 = new Folder();
				if (!fm.isDirectory()) {
					f1.setIsFile(true);
				}
				String[] nameParts = fm.getFileName().split("::");
				String name = nameParts[nameParts.length - 1];
				f1.setBaseFolderName(name);
				f1.setActualFolderName(
						((!f1.getIsFile() && !StringUtils.isEmpty(currentFolder)) ? currentFolder + "::" : "")
								+ fm.getFileName());
				if (null != rootFolder.getListOfFolders()) {
					rootFolder.getListOfFolders().add(f1);
				} else {
					newFolderList.add(f1);
					rootFolder.setListOfFolders(newFolderList);
				}
			}

		}
		return rootFolder;
	}

	public static List<FileMeta> getFileList(String scope, HPCCWsClient hpccConnection, String hpccID)
			throws org.hpccsystems.eclBuilder.exceptions.HPCCException {

		List<FileMeta> results = new ArrayList<FileMeta>();

		DFULogicalFile[] resultsArray;
		try {

			try {

				DFULogicalFile[] files = hpccConnection.getWsDFUClient().getFiles(scope);
				// hpccConnection.getFilenames(scope,
				// hpccConnection.getThorCluster());

				if (files == null || files.length == 0) {

					Map<String, String> mapValues = formBasicECLForFile(scope, false, hpccConnection);

					for (Map.Entry<String, String> s : mapValues.entrySet()) {

						FileMeta fm = new FileMeta();
						fm.setIsfileMeta(true);
						fm.setFileName(s.getKey());
						fm.setDisplayFileName(s.getValue());
						results.add(fm);
					}
					return results;

				}

			} catch (Exception e) {

			}

			resultsArray = hpccConnection.getWsDFUClient().getFiles(scope);
			// (scope, hpccConnection.getThorCluster());
			FileMeta fileMeta;

			for (DFULogicalFile hpccLogicalFile : resultsArray) {
				fileMeta = new FileMeta();
				if (hpccLogicalFile.getIsDirectory()) {
					fileMeta.setIsDirectory(true);
					fileMeta.setFileName(hpccLogicalFile.getDirectory());
					// fileMeta.setDisplayFileName(name);
					fileMeta = settingScope(scope, fileMeta, hpccLogicalFile);
				} else {
					fileMeta.setIsDirectory(false);
					fileMeta.setFileName(hpccLogicalFile.getName());
					// fileMeta.setDisplayFileName(name);
					fileMeta.setScope(hpccLogicalFile.getName());
				}
				results.add(fileMeta);
			}
		} catch (Exception e) {
			throw new org.hpccsystems.eclBuilder.exceptions.HPCCException(Labels.getLabel("unableToFetchFileList"), e);
		}

		return results;
	}

	private static FileMeta settingScope(String scope, FileMeta fileMeta, DFULogicalFile hpccLogicalFile) {
		if (scope.length() > 0) {
			fileMeta.setScope(scope + "::" + hpccLogicalFile.getName());
		} else {
			fileMeta.setScope("~" + hpccLogicalFile.getName());
		}
		return fileMeta;
	}

	public static void buildTree(List<Folder> listOfFolders, Treechildren treeChildren, Button addButton,
			HPCCWsClient connection) {
		for (Folder folders : listOfFolders) {

			Treeitem item = new Treeitem();
			item.setValue(folders.getActualFolderName());
			treeChildren.appendChild(item);
			item.setCheckable(false);

			Treerow row = new Treerow(folders.getBaseFolderName());
			item.appendChild(row);
			Treechildren children = new Treechildren();
			item.appendChild(children);
			item.setOpen(false);

			if (isLogicalFileSel(folders.getActualFolderName(), connection)) {
				item.setDraggable("true");
				item.setAttribute("type", "file");
				item.setImage("/eclBuilder/icons/FileOpen.png");
			} else {
				item.setAttribute("type", "folder");
				item.setImage("/eclBuilder/icons/FolderOpen.png");
			}

			if (null != folders.getListOfFolders()) {
				children = new Treechildren();
				item.appendChild(children);
				buildTree(folders.getListOfFolders(), children, addButton, connection);

			} else if (null != folders.getListOfFiles()) {
				children = new Treechildren();
				item.appendChild(children);

				for (File files : folders.getListOfFiles()) {
					Treeitem childItem = new Treeitem(files.getFileName());
					childItem.setValue(files.getFileName());
					childItem.setDraggable("true");
					childItem.appendChild(new Image("/images/thumbnail/chart_db.png"));
					childItem.setAttribute("isLogicalFile", true);
					childItem.appendChild(new Treechildren());
					children.appendChild(childItem);
					// childItem.addForward("onDoubleClick", addButton,
					// "onClick");
				}
			}

		}
	}

	public static boolean isLogicalFileSel(String fileName, HPCCWsClient hpccConnection) {
		try {
			DFULogicalFile[] files = hpccConnection.getWsDFUClient().getFiles(fileName);

			if (files == null || files.length == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}

	public static Map<String, String> formBasicECLForFile(String logicalFile, boolean addOutput,
			HPCCWsClient connection) throws Exception {

		logicalFile = logicalFile.startsWith("~") ? logicalFile : "~" + logicalFile;
		String tempStrArr[] = logicalFile.split("::");
		String datasetName = tempStrArr[tempStrArr.length - 1];
		org.hpccsystems.ws.client.gen.wsdfu.v1_34.DFUFileDetail dfuFileDetail;
		try {
			dfuFileDetail = ((DFUInfoResponse) connection.getWsDFUClient().getFileInfo(logicalFile, null))
					.getFileDetail();

			// ((HPCCService)
			// SpringUtil.getBean(Constants.HPCC_SERVICE)).getFileDetail(logicalFile,
			// connection, connection.getThorCluster());

			String record = dfuFileDetail.getEcl().replace(";\n", "@").replace("\n", "@").replace(",", "@");

			String tokens[] = record.replace("{", "").replace("}", "").split("@");

			List<String> invalidTokens = new ArrayList<String>();

			Map<String, String> validTokens = new HashMap<String, String>();

			invalidTokens.add("RECORD");
			invalidTokens.add("END");
			String[] tokenStr;
			for (String s : tokens) {
				s = s.trim();
				tokenStr = s.split(" ");
				if (!invalidTokens.contains(s)) {
					validTokens.put(tokenStr[1], tokenStr[1] + " (" + tokenStr[0] + ")");
				}
			}
			return validTokens;

		} catch (HPCCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
