package com.signup.excel1;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class ExcelController {
	public static String activitysFilePath=System.getProperty("user.dir")+"/src/main/resources/static/excel/activitys.xls";
	public static String imagesFilePath=System.getProperty("user.dir")+"/src/main/resources/static/imgupload/";
	public static String imagesFilePath1=System.getProperty("user.dir")+"/src/main/resources/static/";
	public static String userFilePath=System.getProperty("user.dir")+"/src/main/resources/static/excel/user/";
	//首页
	@RequestMapping(value = "/")
    public String index(Model model){
		return "login";
	}
	
	//管理后台
	@RequestMapping(value = "/admin")
    public String admin(Model model){
		//读取excel  
		String path=activitysFilePath;
		ExcelManage em = new ExcelManage();
        Activity activity = new Activity();  
        List list = em.readFromExcel(path,"sheet1", activity); 
        model.addAttribute("list", list);
		return "index";
	}
	
	//后台登陆
	@RequestMapping(value = "/login")
    public String login(Model model){
		return "login";
	}
	
	//添加活动页面
	@RequestMapping(value = "/addActivityPage")
    public String addActivityPage(Model model){
		return "addActivity";
	}
	
	//添加活动页面
	@RequestMapping(value = "/updateActivityPage")
    public String updateActivityPage(Model model,Activity activity){
		model.addAttribute("activity", activity);
		return "updateActivity";
	}
	
	//新建活动
	@RequestMapping(value = "/admin/addActivity")
    public String addActivity(Model model,MultipartFile file,String activityName, HttpServletRequest request){
		UUID uuid = UUID.randomUUID();
		String fileName = file.getOriginalFilename();
		fileName=uuid.toString()+fileName.substring(fileName.indexOf("."),fileName.length());
        String filePath = imagesFilePath;
        try {
            uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
		
		Activity activity = new Activity();  
		String path=activitysFilePath;
		ExcelManage em = new ExcelManage();
		
		activity.setActivityId(uuid.toString());
		activity.setActivityName(activityName);
		String url="http://" + request.getServerName() //服务器地址    
        + ":"     
        + request.getServerPort()+"/detail?activityId="+uuid.toString();
		activity.setActivityUrl(url);
		activity.setImageUrl("imgupload"+File.separator+fileName);
		
	    em.writeToExcel(path,"sheet1",activity);
	    String[] titleRow=new String[]{"userId","userName","company","job","email","phone"};
	    em.createExcel(userFilePath+activity.getActivityId()+".xls", "sheet1", titleRow);
	    return "redirect:/admin"; 
	}
	
	
	//编辑活动
	@RequestMapping(value = "/admin/updateActivity")
    public String updateActivity(Model model,MultipartFile file,Activity activity, HttpServletRequest request){
		UUID uuid = UUID.randomUUID();
		String fileName = file.getOriginalFilename();
		fileName=uuid.toString()+fileName.substring(fileName.indexOf("."),fileName.length());
        String filePath = imagesFilePath;
        try {
            uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
		
		String path=activitysFilePath;
		ExcelManage em = new ExcelManage();
		
		
		File file1=new File(imagesFilePath1+activity.getImageUrl());
	    if(file1.exists()){
	    	file1.delete();
	    }
	    
		activity.setImageUrl("imgupload"+File.separator+fileName);
		
	    em.updateExcelRow(path, "sheet1", activity.getActivityId(), activity);
	    
	    
	    return "redirect:/admin"; 
	}
	
	//删除活动
	@RequestMapping(value = "/admin/delActivity")
    public String delActivity(Model model,String activityId){
		Activity activity = new Activity();  
		ExcelManage em = new ExcelManage();
		em.operExcelRow(activitysFilePath, "sheet1", activityId,"del");
		return "index";
	}
	
	//上传图片
	public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception { 
        File targetFile = new File(filePath);  
        if(!targetFile.exists()){    
            targetFile.mkdirs();    
        }       
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
	
	//活动详情
	@RequestMapping(value = "/detail")
    public String detail(Model model,String activityId){
		ExcelManage em = new ExcelManage();
		Activity activity =(Activity)em.operExcelRow(activitysFilePath, "sheet1", activityId,"query");
		model.addAttribute("activity",activity);
		return "detail";
	}
	
	//用户列表
	@RequestMapping(value = "/userlist")
    public String userlist(Model model,String activityId){
		//读取excel  
		String path=userFilePath+activityId+".xls";
		ExcelManage em = new ExcelManage();
        User user = new User();  
        List list = em.readFromExcel(path,"sheet1", user); 
        model.addAttribute("list", list);
		return "userlist";
	}
	//添加用户
	@RequestMapping(value = "/addUser")
    public String addUser(Model model,String activityId,User user){
		UUID uuid = UUID.randomUUID();
		String path=userFilePath+activityId+".xls";
		ExcelManage em = new ExcelManage();
		user.setUserId(uuid.toString());
		em.writeToExcel(path,"sheet1",user);
		return "success";
	}
	
}
