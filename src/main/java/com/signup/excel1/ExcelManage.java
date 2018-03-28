package com.signup.excel1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelManage {
	 private HSSFWorkbook workbook = null;  
     
	    /** 
	     * 判断文件是否存在. 
	     * @param fileDir  文件路径 
	     * @return 
	     */  
	    public boolean fileExist(String fileDir){  
	         boolean flag = false;  
	         File file = new File(fileDir);  
	         flag = file.exists();  
	         return flag;  
	    }  
	    /** 
	     * 判断文件的sheet是否存在. 
	     * @param fileDir   文件路径 
	     * @param sheetName  表格索引名 
	     * @return 
	     */  
	    public boolean sheetExist(String fileDir,String sheetName){  
	         boolean flag = false;  
	         File file = new File(fileDir);  
	         if(file.exists()){    //文件存在  
	            //创建workbook  
	             try {  
	                workbook = new HSSFWorkbook(new FileInputStream(file));  
	                //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)  
	                HSSFSheet sheet = workbook.getSheet(sheetName);    
	                if(sheet!=null)  
	                    flag = true;  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }   
	              
	         }else{    //文件不存在  
	             flag = false;  
	         }  
	           
	         return flag;  
	    }  
	    /** 
	     * 创建新excel. 
	     * @param fileDir  excel的路径 
	     * @param sheetName 要创建的表格索引 
	     * @param titleRow excel的第一行即表格头 
	     */  
	    public void createExcel(String fileDir,String sheetName,String titleRow[]){  
	        //创建workbook  
	        workbook = new HSSFWorkbook();  
	        //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)  
	        Sheet sheet1 = workbook.createSheet(sheetName);    
	        //新建文件  
	        FileOutputStream out = null;  
	        try {  
	            //添加表头  
	            Row row = workbook.getSheet(sheetName).createRow(0);    //创建第一行    
	            for(int i = 0;i < titleRow.length;i++){  
	                Cell cell = row.createCell(i);  
	                cell.setCellValue(titleRow[i]);  
	            }  
	              
	            out = new FileOutputStream(fileDir);  
	            workbook.write(out);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {    
	            try {    
	                out.close();    
	            } catch (IOException e) {    
	                e.printStackTrace();  
	            }    
	        }    
	      
	  
	    }  
	    /** 
	     * 删除文件. 
	     * @param fileDir  文件路径 
	     */  
	    public boolean deleteExcel(String fileDir){  
	        boolean flag = false;  
	        File file = new File(fileDir);  
	        // 判断目录或文件是否存在    
	        if (!file.exists()) {  // 不存在返回 false    
	            return flag;    
	        } else {    
	            // 判断是否为文件    
	            if (file.isFile()) {  // 为文件时调用删除文件方法    
	                file.delete();  
	                flag = true;  
	            }   
	        }  
	        return flag;  
	    }  
	    /** 
	     * 往excel中写入(已存在的数据无法写入). 
	     * @param fileDir    文件路径 
	     * @param sheetName  表格索引 
	     * @param object 
	     */  
	    public void writeToExcel(String fileDir,String sheetName, Object object){  
	        //创建workbook  
	        File file = new File(fileDir);  
	        try {  
	            workbook = new HSSFWorkbook(new FileInputStream(file));  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        //流  
	        FileOutputStream out = null;  
	        HSSFSheet sheet = workbook.getSheet(sheetName);  
	        // 获取表格的总行数  
	        int rowCount = sheet.getLastRowNum() + 1; // 需要加一  
	        // 获取表头的列数  
	        int columnCount = sheet.getRow(0).getLastCellNum();  
	        try {  
	            Row row = sheet.createRow(rowCount);     //最新要添加的一行  
	            //通过反射获得object的字段,对应表头插入  
	            // 获取该对象的class对象  
	            Class class_ = object.getClass();  
	            // 获得表头行对象  
	            HSSFRow titleRow = sheet.getRow(0);  
	            if(titleRow!=null){  
	                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {  //遍历表头  
	                    String title = titleRow.getCell(columnIndex).toString().trim().toString().trim();  
	                    String UTitle = Character.toUpperCase(title.charAt(0))+ title.substring(1, title.length()); // 使其首字母大写;  
	                    String methodName  = "get"+UTitle;  
	                    Method method = class_.getDeclaredMethod(methodName); // 设置要执行的方法  
	                    String data = method.invoke(object).toString(); // 执行该get方法,即要插入的数据  
	                    Cell cell = row.createCell(columnIndex);  
	                    cell.setCellValue(data);  
	                }  
	            }  
	  
	            out = new FileOutputStream(fileDir);  
	            workbook.write(out);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {    
	            try {    
	                out.close();    
	            } catch (IOException e) {    
	                e.printStackTrace();  
	            }    
	        }    
	    }  
	    /** 
	     * 读取excel表中的数据. 
	     *  
	     * @param fileDir    文件路径    
	     * @param sheetName 表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1) 
	     * @param object   object 
	     */  
	    public List readFromExcel(String fileDir,String sheetName, Object object) {  
	        //创建workbook  
	        File file = new File(fileDir);  
	        try {  
	            workbook = new HSSFWorkbook(new FileInputStream(file));  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	  
	        List result = new ArrayList();  
	        // 获取该对象的class对象  
	        Class class_ = object.getClass();  
	        // 获得该类的所有属性  
	        Field[] fields = class_.getDeclaredFields();  
	  
	        // 读取excel数据  
	        // 获得指定的excel表  
	        HSSFSheet sheet = workbook.getSheet(sheetName);  
	        // 获取表格的总行数  
	        int rowCount = sheet.getLastRowNum() + 1; // 需要加一  
	        System.out.println("rowCount:"+rowCount);  
	        if (rowCount < 1) {  
	            return result;  
	        }  
	        // 获取表头的列数  
	        int columnCount = sheet.getRow(0).getLastCellNum();  
	        // 读取表头信息,确定需要用的方法名---set方法  
	        // 用于存储方法名  
	        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数  
	        // 用于存储属性类型  
	        String[] fieldTypes = new String[columnCount];  
	        // 获得表头行对象  
	        HSSFRow titleRow = sheet.getRow(0);  
	        // 遍历  
	        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列  
	            String data = titleRow.getCell(columnIndex).toString(); // 某一列的内容  
	            String Udata = Character.toUpperCase(data.charAt(0))  
	                    + data.substring(1, data.length()); // 使其首字母大写  
	            methodNames[columnIndex] = "set" + Udata;  
	            for (int i = 0; i < fields.length; i++) { // 遍历属性数组  
	                if (data.equals(fields[i].getName())) { // 属性与表头相等  
	                    fieldTypes[columnIndex] = fields[i].getType().getName(); // 将属性类型放到数组中  
	                }  
	            }  
	        }  
	        // 逐行读取数据 从1开始 忽略表头  
	        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {  
	            // 获得行对象  
	            HSSFRow row = sheet.getRow(rowIndex);  
	            if (row != null) {  
	                Object obj = null;  
	                // 实例化该泛型类的对象一个对象  
	                try {  
	                    obj = class_.newInstance();  
	                } catch (Exception e1) {  
	                    e1.printStackTrace();  
	                }  
	  
	                // 获得本行中各单元格中的数据  
	                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {  
	                    String data = row.getCell(columnIndex).toString();  
	                    // 获取要调用方法的方法名  
	                    String methodName = methodNames[columnIndex];  
	                    Method method = null;  
	                    try {  
	                        // 这部分可自己扩展  
	                        if (fieldTypes[columnIndex].equals("java.lang.String")) {  
	                            method = class_.getDeclaredMethod(methodName,  
	                                    String.class); // 设置要执行的方法--set方法参数为String  
	                            method.invoke(obj, data); // 执行该方法  
	                        } else if (fieldTypes[columnIndex].equals("int")) {  
	                            method = class_.getDeclaredMethod(methodName,  
	                                    int.class); // 设置要执行的方法--set方法参数为int  
	                            double data_double = Double.parseDouble(data);  
	                            int data_int = (int) data_double;  
	                            method.invoke(obj, data_int); // 执行该方法  
	                        }  
	                    } catch (Exception e) {  
	                        e.printStackTrace();  
	                    }  
	                }  
	                result.add(obj);  
	            }  
	        }  
	        return result;  
	    }  
	    
	    /** 
	     * 删除,查询内容. 
	     *  
	     * @param fileDir    文件路径    
	     * @param sheetName 表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1) 
	     * @param object   object 
	     */  
	    public Object operExcelRow(String fileDir,String sheetName, String id,String oper) {  
	        //创建workbook  
	        File file = new File(fileDir);  
	        try {  
	            workbook = new HSSFWorkbook(new FileInputStream(file));  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        Class class_ = new Activity().getClass();
	  
	        // 读取excel数据  
	        // 获得指定的excel表  
	        FileOutputStream out = null;
	        HSSFSheet sheet = workbook.getSheet(sheetName);  
	        // 获取表格的总行数  
	        int rowCount = sheet.getLastRowNum() + 1; // 需要加一  
	        // 获取表头的列数  
	        int columnCount = sheet.getRow(0).getLastCellNum();  
	        // 读取表头信息,确定需要用的方法名---set方法  
	        // 用于存储方法名  
	        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数  
	        // 用于存储属性类型  
	        String[] fieldTypes = new String[columnCount];  
	        // 获得表头行对象  
	        HSSFRow titleRow = sheet.getRow(0);  
	        // 逐行读取数据 从1开始 忽略表头  
	        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {  
	            // 获得行对象  
	            HSSFRow row = sheet.getRow(rowIndex);  
	            if (row != null) {  
	                Object obj = null;  
	                // 获得本行中各单元格中的数据  
	                    String data = row.getCell(0).toString();  
	                    
	                    //删除逻辑
	                    if(id.equals(data)){
	                    	if("del".equals(oper)){
	                    		File file1=new File(ExcelController.imagesFilePath1+row.getCell(3).toString());
	                    		File file2=new File(ExcelController.userFilePath+row.getCell(0).toString()+".xls");
	                    	    if(file1.exists()){
	                    	    	file1.delete();
	                    	    }
	                    	    if(file2.exists()){
	                    	    	file2.delete();
	                    	    }
		                    	sheet.removeRow(row);
	                    	}
	                    	if("query".equals(oper)){
	                    		Activity activity=new Activity();
                    			activity.setActivityId(row.getCell(0).toString());
                    			activity.setActivityName(row.getCell(1).toString());
                    			activity.setActivityUrl(row.getCell(2).toString());
                    			activity.setImageUrl(row.getCell(3).toString());
                    			return activity;
	                    	}
	                    	
							try {
								out = new FileOutputStream(fileDir);  
					            workbook.write(out);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}catch (IOException e) {
								e.printStackTrace();
							}
	                    }
	            }  
	        }
			return "";  
	    }  
	    
	    /** 
	     * 编辑内容 
	     *  
	     * @param fileDir    文件路径    
	     * @param sheetName 表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1) 
	     * @param object   object 
	     */  
	    public Object updateExcelRow(String fileDir,String sheetName, String id,Activity activity) {  
	        //创建workbook  
	        File file = new File(fileDir);  
	        try {  
	            workbook = new HSSFWorkbook(new FileInputStream(file));  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        Class class_ = new Activity().getClass();
	  
	        // 读取excel数据  
	        // 获得指定的excel表  
	        FileOutputStream out = null;
	        HSSFSheet sheet = workbook.getSheet(sheetName);  
	        // 获取表格的总行数  
	        int rowCount = sheet.getLastRowNum() + 1; // 需要加一  
	        // 获取表头的列数  
	        int columnCount = sheet.getRow(0).getLastCellNum();  
	        // 读取表头信息,确定需要用的方法名---set方法  
	        // 用于存储方法名  
	        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数  
	        // 用于存储属性类型  
	        String[] fieldTypes = new String[columnCount];  
	        // 获得表头行对象  
	        HSSFRow titleRow = sheet.getRow(0);  
	        // 逐行读取数据 从1开始 忽略表头  
	        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {  
	            // 获得行对象  
	            HSSFRow row = sheet.getRow(rowIndex);  
	            if (row != null) {  
	                Object obj = null;  
	                // 获得本行中各单元格中的数据  
	                    String data = row.getCell(0).toString();  
	                    
	                    //修改逻辑
	                    if(id.equals(data)){
	                    	row.getCell(1).setCellValue(activity.getActivityName());
	                    	row.getCell(3).setCellValue(activity.getImageUrl());
							try {
								out = new FileOutputStream(fileDir);  
					            workbook.write(out);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}catch (IOException e) {
								e.printStackTrace();
							}
	                    }
	            }  
	        }
			return "";  
	    }  
	      
	      
//	    public static void main(String[] args) {  
//	        ExcelManage em = new ExcelManage();  
//	        //判断文件是否存在  
//	        System.out.println(em.fileExist("C:/Users/YoGo/Desktop/test1.xls"));  
//	        //创建文件  
//	        String title[] = {"id","name","password"};  
//	        em.createExcel("C:/Users/YoGo/Desktop/test1.xls","sheet1",title);  
//	        //判断sheet是否存在  
//	        System.out.println(em.sheetExist("C:/Users/YoGo/Desktop/test1.xls","sheet1"));  
//	        //写入到excel  
//	        User user = new User();  
//	        UUID uuid = UUID.randomUUID();
//	        user.setId(uuid.toString());  
//	        user.setName("qwer");  
//	        user.setPassword("zxcv");  
//	        User user3 = new User();  
//	        user3.setId(uuid.toString());  
//	        user3.setName("qwerwww");  
//	        user3.setPassword("zxcvwww");  
//	        em.writeToExcel("C:/Users/YoGo/Desktop/test1.xls","sheet1",user);  
//	        em.writeToExcel("C:/Users/YoGo/Desktop/test1.xls","sheet1",user3);  
//	        //读取excel  
//	        User user2 = new User();  
//	        List list = em.readFromExcel("C:/Users/YoGo/Desktop/test1.xls","sheet1", user2);  
//	        for (int i = 0; i < list.size(); i++) {  
//	            User newUser = (User) list.get(i);  
//	            System.out.println(newUser.getId() + " " + newUser.getName() + " "  
//	                    + newUser.getPassword());  
//	        }  
//	        //删除文件  
//	        //System.out.println(em.deleteExcel("E:/test2.xls"));  
//	    }  
}
