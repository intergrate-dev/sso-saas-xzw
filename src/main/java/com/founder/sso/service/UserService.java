package com.founder.sso.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.founder.redis.JedisClient;
import com.founder.sso.dao.SystemConfigDao;
import com.founder.sso.service.oauth.entity.*;
import com.founder.sso.util.*;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.founder.sso.admin.entity.Subsystem;
import com.founder.sso.admin.service.SubsystemService;
import com.founder.sso.admin.utils.CollectionsUtil;
import com.founder.sso.admin.utils.DynamicSpecifications;
import com.founder.sso.admin.utils.SearchFilter;
import com.founder.sso.dao.OauthClientConfigDao;
import com.founder.sso.dao.UserDao;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.service.oauth.dao.UserOauthBindingDao;
import com.founder.sso.util.json.JSONArray;
import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

//Spring Bean的标识.
@Service
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class UserService {

    // 默认密码
    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserOauthBindingDao userOauthBindingDao;
    @Autowired
	private OauthClientConfigDao oauthClientConfigDao;
    @Autowired
    private LocalPrincipalService principalService;
    @Autowired
	private SubsystemService subsystemService;
    @Autowired
    private SystemConfigDao systemConfigDao;

    @Autowired
    JedisClient jedisClient;

    /**
     * 保存用户
     */
    public User save(User user) {
        return userDao.save(user);
    }

    /**
     * 查询所有用户
     * 
     * @return
     */
    public List<User> findAll() {
        return ImmutableList.copyOf(userDao.findAll());
    }

    /**
     * 查询用户
     */
    public User findUserById(long userId) {
        return userDao.findOne(userId);
    }
    

    /**
     * 查询某些用户
     */
    public List<User> findUserByIds(String userIds) {
        return (List<User>) userDao.findAll(CollectionsUtil.convetDotStringToList(userIds));
    }

    /**
     * 通过用户名去查询一个用户
     * 
     * @param username
     * @return 相应用户对象 不存在返回null
     */
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);
        return user;
    }
    
    /**
     * 查询绑定
     * @return 相应用户对象 不存在返回null
     */
    public UserOauthBinding findBinding(String oauthUid, String provider) {
    	UserOauthBinding binding = userOauthBindingDao.findByOauthUidAndProvider(oauthUid, provider);
    	return binding;
    }
    
    /**
     * 查询绑定
     * @return 相应用户对象 不存在返回null
     */
    public List<UserOauthBinding> findByUserIdAndOauthUid(long userId, String oauthUid) {
    	return userOauthBindingDao.findByUserIdAndOauthUid(userId, oauthUid);
    }
    
    /**
     * 删除一个用户
     */
    public void delete(long userId) {
        userDao.delete(userId);
    }

    public void updateUser(User user) {
        userDao.save(user);
    }
    /**
     * 删除某些用户
     * 如果牵扯到第三方账号的绑定信息，也一并删除
     */
    public void deleteByIds(String userIds) {
    	
        List<User> users = findUserByIds(userIds);
        userDao.delete(users);
    }

    /**
     * 分页查询User
     */
    public Page<User> getUsers(Map<String, Object> searchParams, int pageNumber, int pageSize, String sortType) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
        Specification<User> spec = buildSpecification(searchParams);
        return userDao.findAll(spec, pageRequest);
    }

    /**
     * 分页排序.
     */
    private PageRequest buildPageRequest(int pageNumber, int pagzSize, String sortType) {
        Sort sort = new Sort(Direction.ASC, sortType);
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }

    /**
     * 创建动态查询条件组合.
     */
    private Specification<User> buildSpecification(Map<String, Object> searchParams) {
        Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        Specification<User> spec = DynamicSpecifications.bySearchFilter(filters.values(), User.class);
        return spec;
    }

    /**
     * 批量冻结或恢复用户
     * 
     * @param userIds User id：以id,id,id的形式传入
     * @return 返回修改的个数
     */
    public int freezeOrRecoverUser(boolean value, String userIds) {
        return userDao.freezeOrRecoverUser(value, CollectionsUtil.convetDotStringToList(userIds));
    }

    /**
     * 批量重置密码
     * 
     * @param userIds User id：以id,id,id的形式传入
     * @return 返回修改的个数
     */
    public void resetPassword(Long[] userIds) {
        for (Long id : userIds) {
            LocalPrincipal principal = principalService.getByUserId(id);
            principalService.resetPassword(principal, DEFAULT_PASSWORD);
        }
    }
    
    /**
     * 提取到service里，方便admin和user两个controller共用
     */
    public void initViewFace(Model model, User user, String info) {
    	String context = SystemConfigHolder.getConfig("context");
    	String avatarLarge = user.getAvatarLarge();
    	String avatarMiddle = user.getAvatarMiddle();
    	model.addAttribute("user", user);
    	model.addAttribute("avatarMiddle", avatarMiddle);
    	model.addAttribute("avatarLarge", avatarLarge);
    	model.addAttribute("info", info);
    }
    
    public String initCutFace(MultipartFile file, String picType, ModelMap model, String module, HttpServletRequest request){
    	//图片名称的前半部分
    	String fileNameAhead = FaceUtil.getFileNameAhead();
        //图片相对根路径
        String folder = FaceUtil.getFolderAfter();
        //原图片的路径
        // String path = FaceUtil.faceRootPath + folder;
        String path = request.getSession().getServletContext().getRealPath("/") + folder;
        System.out.println("================================= path: " + path + " =================");
        //原图片的名字
        String fileName = fileNameAhead + picType+"";

        //图片后缀校验
        String suffix = file.getOriginalFilename().substring
				(file.getOriginalFilename().lastIndexOf(".")+1);
        if(!("jpg".equals(suffix)||"gif".equals(suffix)||
				"jpeg".equals(suffix)||"png".equals(suffix)||
				"JPG".equals(suffix)||"GIF".equals(suffix)||
				"JPEG".equals(suffix)||"PNG".equals(suffix))){
        	return "failpic";
		}

        File targetFile = new File(path, fileName);
        System.out.println("================================= targetFile: " + targetFile.getAbsolutePath() + " =================");
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        //生成的标准图片322*322的名字
        String avatarLarge = fileNameAhead + "_avatarLarge" + picType;
        //保存
        try {
        	//生成原始图片
        	file.transferTo(targetFile);
        	//生成322*322的图片，作为大头像
        	Small_pic mypic = new Small_pic();
			mypic.t_pic(path + fileName, path + avatarLarge, 322, 322, true);
        } catch (Exception e) {
            System.out.println("================================= file transfer exception =================");
            e.printStackTrace();
        }
        //folder = request.getContextPath() + folder;
        avatarLarge = folder + avatarLarge;
        String cutFace = folder + fileNameAhead + "_cut" + picType;
        String avatarSmall = folder + fileNameAhead + "_avatarSmall" + picType;
        String avatarMiddle = folder + fileNameAhead + "_avatarMiddle" + picType;
        System.out.println("================================= cutFace: " + cutFace + " =================");
        System.out.println("================================= avatarSmall: " + avatarSmall + " =================");
        System.out.println("================================= avatarMiddle: " + avatarMiddle + " =================");
        System.out.println("================================= avatarLarge: " + avatarLarge + " =================");
        //322*322的图片，需要在这个图片的基础上切割
        //剪切的图片
        model.addAttribute("cutFace", cutFace);
        //小头像
        model.addAttribute("avatarSmall", avatarSmall);
        //中头像
        model.addAttribute("avatarMiddle", avatarMiddle);
        //大头像
        model.addAttribute("avatarLarge", avatarLarge);
        
        model.addAttribute("module", module);
        System.out.println("================================= targetFile, end  =================");
        return "success";
    }
    

    public String cutFace(User user, int x1, int y1, int x2, int y2, String cutFace, String avatarSmall, String avatarMiddle, String avatarLarge, HttpServletRequest request){
    	if(x1==0&x2==0&y1==0&y1==0){
    		x2 = 322;
    		y2 = 322;
    	}
		
		// String rootPath = FaceUtil.faceRootPath;
        //String folder = FaceUtil.getFolderAfter();
        String rootPath = request.getSession().getServletContext().getRealPath("/");
		String upload = rootPath + avatarLarge ;
		String subpath = rootPath + cutFace;
        System.out.println("================================= upload: " + upload + " =================");
        System.out.println("================================= subpath: " + subpath + " =================");
		//图片后缀校验
		File src = new File(upload);
        String suffix = src.getName().substring  
				(src.getName().lastIndexOf(".")+1);
        if(!("jpg".equals(suffix)||"gif".equals(suffix)||
				"jpeg".equals(suffix)||"png".equals(suffix)||
				"JPG".equals(suffix)||"GIF".equals(suffix)||
				"JPEG".equals(suffix)||"PNG".equals(suffix))){
        	return "failpic";
		}
		//根据标准的322*322图像，进行切割成x2-x1, y2-y1尺寸的头像
		OperateImage image = new OperateImage(x1, y1, x2-x1, y2-y1);
		image.setMime("jpg");
		image.setSrcpath(upload);
		image.setSubpath(subpath);
		try {
			image.cut();
			Small_pic mypic = new Small_pic();
			mypic.t_pic(subpath, rootPath + avatarSmall, FaceUtil.avatarSmallSize, FaceUtil.avatarSmallSize, true);
			mypic.t_pic(subpath, rootPath + avatarMiddle, FaceUtil.avatarMiddleSize, FaceUtil.avatarMiddleSize, true);
		} catch (IOException e) {
			e.printStackTrace();
			return "fail";
		}
		user.setAvatarSmall(avatarSmall);
		user.setAvatarMiddle(avatarMiddle);
		user.setAvatarLarge(avatarLarge);
		updateUser(user);
		
		//删除 剪切生成的图片_cut
		File f = new File(subpath);
	    f.delete();
	    return "success";
    }
    

    /**
     * 根据字段查询用户
     */
    public User getUserByField(Object value, String field) {
        if (field.equals("phone")) {
            return userDao.findByPhone(value.toString());
        } else if (field.equals("username")) {
            return userDao.findByUsername(value.toString());
        } else if (field.equals("email")) {
            return userDao.findByEmail(value.toString());
        }
        return null;
    }
   
    
    /**
     * 进入绑定账号的页面
     * @param model
     * @param userId
     */
    public void initBind(Model model, long userId) {
    	
    	//先把已经绑定的封装进map。可能有2个以上绑定同一媒体（QQ、新浪）
    	List<UserOauthBinding> userOauthBindings = userOauthBindingDao.findByUserId(userId);
    	model.addAttribute("userId", userId);
    	
    	//1、媒体用户，未绑定本地注册用户，进行用户绑定
    	if(userOauthBindings==null||userOauthBindings.isEmpty()){
    		User user = userDao.findOne(userId);
    		if(user.getLocalPrincipal()==null){
    			model.addAttribute("bindMap",null);
    			model.addAttribute("comFrom", user.getProvider());
    			return ;
    		}
    	}
    	
    	Map<String, UserOauthBinding> bindMap = new HashMap<String, UserOauthBinding>();
    	for (UserOauthBinding binding : userOauthBindings) {
    		String provider = binding.getProvider();
    		System.out.println("binded provider:"+provider);
    		bindMap.put(provider, binding);
		}
    	
    	//2、没绑定的config也要显示，构造出Set即可
    	List<OauthClientConfig> configList = oauthClientConfigDao.findByEnabled(true);
    	for (OauthClientConfig config : configList) {
    		String provider = config.getProvider();
    		if(bindMap.containsKey(provider)){
    			continue;
    		}else {
    			UserOauthBinding binding = new UserOauthBinding();
    			binding.setProvider(provider);
    			System.out.println("unbinded provider:"+provider);
    			bindMap.put(provider, binding);
    		}
		}
    	
    	model.addAttribute("bindMap", bindMap);
    }
    
    /**
     * 解除绑定的账号
     */
    public void unBind(long bindingId) {
    	
    	userOauthBindingDao.delete(bindingId);
    }
    
    /**
     * 绑定远程用户和本地用户
     * 
     * @param remoteUser
     * @return
     */
    public User doUserOauthBinding(RemoteUser remoteUser, User localUser, String password) {
    	
//    	if( localUser != null && localUser.getId() == null){
//    		localUser.setNickname(localUser.getUsername());
//        	localUser.setActived(true);
//        	localUser.setRegisterDate(Clock.DEFAULT.getCurrentDate());
//        	
//        	/**下面保存头像，从第三方服务器下载下来，保存在本地*/
//        	String avatarMiddleUrl = remoteUser.getAvatarLarge(); 
//        	generateFace(localUser, avatarMiddleUrl);
//        	userDao.save(localUser);
//        	principalService.savePrincipal(localUser, password);
//    	}
    	
        // 通过UserService创建一个本地用户
        UserOauthBinding bing = new UserOauthBinding();
        bing.setUser(localUser);
        bing.setOauthUid(remoteUser.getUid());
        bing.setProvider(remoteUser.getProvider());
        bing.setBindTime(Clock.DEFAULT.getCurrentDate());
        bing.setNickname(remoteUser.getNickname());
        userOauthBindingDao.save(bing);
        
        OauthAccessToken token = new OauthAccessToken();
        token.setUid(remoteUser.getUid());
        token.setProvider(remoteUser.getProvider());
        token.setTokenExpiresTime(remoteUser.getTokenExpiresTime());
        token.setAccessToken(remoteUser.getAccessToken());
        
        return localUser;
    }
    
    /**
     * 将图片从faceUrl下载下来，生成大中小三种尺寸的头像，并赋值给user
     * @param user
     * @param faceUrl
     */
    public static User generateFace(User user, String faceUrl){
        if (!StringUtils.isEmpty(faceUrl)) {
            user.setAvatarSmall(faceUrl);
            user.setAvatarMiddle(faceUrl);
            user.setAvatarLarge(faceUrl);
        } else {
            System.out.println("第三方头像为空！");
        }
    	return user;
    }
    

    /**
     * 检测新用户是否与本地已经有的用户存在冲突 并返回冲突解决的推荐值
     * 
     * @param newUser
     * @return 返回用户冲突检测集合，key为冲突的字段、value为解决冲突的推荐值 不会修改原对象
     */
    public Map<String, Object> detectConflict(RemoteUser newUser) {
        Map<String, Object> duplications = Maps.newHashMap();
        User existedUsername = userDao.findByUsername(newUser.convertUserName());
        if (existedUsername == null) {
            return duplications;
        } else {
            return findNoneConflictValue(newUser);
        }
    }

    private Map<String, Object> findNoneConflictValue(RemoteUser newUser) {
        Map<String, Object> duplications = Maps.newHashMap();
        RemoteUser dealed = BeanMapper.map(newUser, RemoteUser.class);
        int tryCount = 0;
        boolean gotIt = false;
        do {
            tryCount++;
            String newName = newUser.convertUserName() + RandomUtils.nextInt(99);
            dealed.setNickname(newName);
            duplications = detectConflict(dealed);
            if (duplications.size() == 0) {
                duplications.put("username", newName);
                gotIt = true;
                break;
            }
        } while (tryCount < 10);
        if (gotIt == true) {
            return duplications;
        } else {
            throw new RuntimeException("Try More Than 10 times get no None Conflict Value");
        }
    }

    /**
     * 默认的冲突解决策略 直接将推荐值赋予这个用户
     * 改变原来用户重新Copy一个对象
     * 
     * @param remoteUser
     * @param conflicts
     * @return
     */
    public RemoteUser dealConflict(RemoteUser remoteUser, Map<String, Object> conflicts) {
        RemoteUser dealed = BeanMapper.map(remoteUser, RemoteUser.class);
        Set<String> keySet = conflicts.keySet();
        for (String key : keySet) {
        	String value = conflicts.get(key).toString();
			if(key.equals("nickname")){
				dealed.setNickname(value);
			}
			
		}
        
        return dealed;
    }

    /**
     * API接口，根据id查询用户，并返回json对象
     */
    public JSONObject findUserInfoById(long userId, HttpServletRequest request) {
    	JSONObject json = new JSONObject();
    	//JSONObject json = new JSONObject();
		User user = findUserById(userId);
		try {
			if(user == null){
				json.put("ret", "1001");
				json.put("msg", "用户不存在");
                return json;
            }

            json.put("ret", "0");
            json.put("msg", "成功");
            json.put("id", user.getId()+"");
            json.put("username", user.getUsername());
            json.put("nickname", user.getConvertNickname());
            json.put("phone", user.getConvertPhone());
            json.put("email", user.getConvertEmail());
            json.put("userId", user.getUid());
            json.put("actived", user.isActived());
            json.put("registerDate", user.getConvertRegisterDate());
            json.put("avatarSmall", user.getAvatarSmall());
            json.put("avatarMiddle", user.getAvatarMiddle());
            json.put("avatarLarge", user.getAvatarLarge());
            user.asJson(json);
			//会员信息同步
            return this.getMemberInfo(json, user, request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
    }

    public JSONObject getMemberInfo(JSONObject json, User user, HttpServletRequest request){
        try {
            //调amuc接口,像amuc中保存用户信息
            String inner_api_url = "";
            if(System.getenv("INNER_API_URL") != null && System.getenv("INNER_API_URL") != ""){
                inner_api_url = System.getenv("INNER_API_URL");
            }else{
                inner_api_url = SystemConfigHolder.getConfig("inner_api_url");
            }
            String httpOrgCreateTest = inner_api_url + "/api/member/getUserMessage.do";
            String charset = "utf-8";
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            if (user.getUid() == null) {
                json.put("ret", "1002");
                json.put("msg", "该用户为第三方用户，没有会员信息");
                return json;
            }
            formparams.add(new BasicNameValuePair("ssoid", String.valueOf(user.getId())));
            System.out.println("获取到的url:"+httpOrgCreateTest);
            String httpOrgCreateTestRtn = new HttpClientUtil().doPost(httpOrgCreateTest,formparams,charset);
            System.out.println("result:" + httpOrgCreateTestRtn);
            weibo4j.org.json.JSONObject obj = new weibo4j.org.json.JSONObject(httpOrgCreateTestRtn);
            if(obj.getString("code").equals("1")){
                json.put("userid", obj.getString("uid"));
                json.put("score", obj.getString("mScore"));
                json.put("sex", obj.getString("sex")==null?"":obj.getString("sex"));
                json.put("birthday", obj.getString("birthday")==null?"":obj.getString("birthday"));
                json.put("head", obj.getString("mHead"));
                json.put("address", obj.getString("address")==null?"":obj.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * API接口，注册一个用户，凡是不为null的字段都要保存
     */
    /*public JSONObject registerUser(User user, String password) {
    	JSONObject json = new JSONObject();
    	try {
	    	if(user != null){
	    		String username = user.getUsername();
    			if(StringUtils.isBlank(username)){
    				json.put("ret", "1000");
    				json.put("msg", "username不存在或为空");
    				return json;
    			}
    			if(StringUtils.isBlank(password)){
    				json.put("ret", "1002");
    				json.put("msg", "password不存在或为空");
    				return json;
    			}
    			User queryUser = findByUsername(username);
    			if(queryUser != null){
    				json.put("ret", "1001");
    				json.put("msg", "username已注册");
    				return json;
	    		}
    			//只要phone和email有一个不为空，就要检查是否phone或者email有没有被注册
    			String phone = user.getPhone();
    			String email = user.getEmail();
    			if(StringUtils.isNoneBlank(phone) || StringUtils.isNotBlank(email)){
    				Set<User> targetUser = userDao.findByPhoneOrEmail(phone, email);
    				if(targetUser.size() > 0){
    					json.put("ret", "1003");
    					json.put("msg", "邮箱已注册");
    					return json;
    				}
    			}
    			
    			//如果头像不为空,就需要保存头像，根据其提供的url下载头像，并赋值
    			String avatarMiddle = user.getAvatarMiddle();
    			if(StringUtils.isNotBlank(avatarMiddle)){
	    			generateFace(user, avatarMiddle);
    			}
    			if(StringUtils.isBlank(user.getNickname())){
    				user.setNickname(username);
    			}
		        user.setRegisterDate(Clock.DEFAULT.getCurrentDate());
		        save(user);
		        principalService.savePrincipal(user, password);
		        json.put("ret", "0");
				json.put("msg", "成功");
	    	}else {
    			json.put("ret", "1");
    			json.put("msg", "参数不正确");
    		}
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	return json;
    }*/
    
    /**
     * API接口，修改用户资料/头像/密码/冻结，凡是不为null或空的字段都要保存
     */
    /*public JSONObject updateUser(User userSource, String password) {
    	JSONObject json = new JSONObject();
    	try {
    		if(userSource != null){
    			*//**
    			 * 先进行检查参数是否都合法
    			 *//*
    			Long id = userSource.getId();
    			if(id == null){
    				json.put("ret", "1000");
    				json.put("msg", "id不存在或为空");
    				return json;
    			}
    			User oldUser = findUserById(id);
    			if(oldUser == null){
    				json.put("ret", "1001");
    				json.put("msg", "id对应的用户不存在");
    				return json;
    			}
    			
    			*//**
    			 * 下面进行赋值,只要不为null或空的,都会update
    			 *//*
    			String phone = userSource.getPhone();
    			String email = userSource.getEmail();
    			//只要phone和email有一个不为空，就要检查是否phone或者email有没有被注册
    			if(StringUtils.isNoneBlank(phone) || StringUtils.isNotBlank(email)){
    				Set<User> targetUser = userDao.findByPhoneOrEmail(phone, email);
    				boolean hasOthers = false;
    				//只要能根据phone和email查询出user,并且和本身不是同一个id,就说明phone或email已注册过了
    				for (User user : targetUser) {
    					if(id != user.getId()){
    						hasOthers = true;
    						break;
    					}
					}
    				if(hasOthers){
    					json.put("ret", "1003");
    					json.put("msg", "邮箱已注册");
    					return json;
    				}
    			}
    			
    			//检查是否还是原来的电话号码
    			if(StringUtils.isNoneBlank(phone)){
    				oldUser.setPhone(phone);
    			}
    			if(StringUtils.isNoneBlank(email)){
    				oldUser.setEmail(email);
    			}
    			
    			if(StringUtils.isNoneBlank(userSource.getNickname())){
    				oldUser.setNickname(userSource.getNickname());
    			}
    			
    			//如果头像不为空,就需要保存头像，根据其提供的url下载头像，并赋值
    			String avatarMiddle = userSource.getAvatarMiddle();
    			if(StringUtils.isNotBlank(avatarMiddle)){
    				generateFace(oldUser, avatarMiddle);
    			}
    			updateUser(oldUser);
    			principalService.savePrincipal(oldUser, password);
    			json.put("ret", "0");
    			json.put("msg", "成功");
    		}else {
    			json.put("ret", "1");
    			json.put("msg", "参数不正确");
    		}
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	return json;
    }*/
    
    
    /**
     * API接口，注册一个用户，凡是不为null的字段都要保存
     */
    @SuppressWarnings("finally")
	public JSONObject synchronizedLogin(Long userId, ServletRequest request, Subject subject) {
    	JSONObject json = new JSONObject();
    	try {
    		if(userId == null){
    			json.put("ret", "1000");
    			json.put("msg", "userId为空");
    			return json;
    		}else {
    			User user = findUserById(userId);
    			if(user == null){
    				json.put("ret", "1001");
        			json.put("msg", "用户不存在");
    				return json;
    			}
    			LoginInfo info = new LoginInfo(user, request.getRemoteHost(), OauthToken.LOGIN_TYPE.OTHER_SYSTEM.toString());
    			AuthenticationToken token = new OauthToken(info);
    			subject.login(token);
    			json.put("ret", "0");
    			json.put("msg", "success");
    		}
    	} catch (JSONException e) {
    		json.put("ret", "2000");
			json.put("msg", "服务器出错");
    		e.printStackTrace();
    	}finally {
			return json;
    	}
    }
    
    
    /**
     * API
     * 根据用户名/邮箱 + 密码 认证用户是否存在，
	 * 如果存在将返回用户信息、需要同步登录的子系统信息
     */
    @SuppressWarnings("finally")
	public JSONObject authenUser(String identity, String password, String systemCode, HttpServletRequest request) {
    	JSONObject json = new JSONObject();
    	try {
    		if(StringUtils.isBlank(identity)){
    			json.put("ret", "1000");
    			json.put("msg", "用户名/邮箱不能为空");
    			return json;
    		}else if(StringUtils.isBlank(password)){
    			json.put("ret", "1001");
    			json.put("msg", "密码不能为空");
    			return json;
    		}else {
    			LocalPrincipal principal = principalService.findPrincipal(identity, password);
    			if(principal == null){
    				json.put("ret", "1002");
    				json.put("msg", "对应的用户不存在");
    				return json;
    			}else {
    				Long userId = principal.getUser().getId();
    				JSONObject userInfo = findUserInfoById(userId, request);
    				json.put("ret", "0");
    				json.put("msg", "success");
    				json.put("userInfo", userInfo); 
    				
    				JSONArray systemJsonArray = new JSONArray();
    				List<Subsystem> subsystemEnabled = subsystemService.findByEnabledTrueAndCodeNot(systemCode);
    				
    				JSONObject systemJson  = new JSONObject();
					systemJson.put("code", SystemConfigHolder.getConfig("code"));
					systemJson.put("name", SystemConfigHolder.getConfig("name"));
					systemJson.put("homePage", SystemConfigHolder.getConfig("homePage"));
					systemJson.put("encryptType", SystemConfigHolder.getConfig("encryptType"));
					systemJson.put("logoutUrl", SystemConfigHolder.getConfig("logoutUrl"));
					systemJson.put("redirectUrl", SystemConfigHolder.getConfig("redirectUrl"));
					systemJson.put("description", SystemConfigHolder.getConfig("description"));
					JSONObject ssoSystemJson  = new JSONObject();
					ssoSystemJson.put("id", userId+"");
					systemJson.put("passport", ssoSystemJson);
					systemJsonArray.put(systemJson);
    				
//    				Subsystem sso = new Subsystem();
//    				sso.setId(Long.valueOf(SystemConfigHolder.getConfig("id")));
//    				sso.setName(SystemConfigHolder.getConfig("name"));
//    				sso.setDescription(SystemConfigHolder.getConfig("description"));
//    				sso.setHomePage(SystemConfigHolder.getConfig("homePage"));
//    				sso.setLoginUrl(SystemConfigHolder.getConfig("loginUrl"));
//    				sso.setLogoutUrl(SystemConfigHolder.getConfig("logoutUrl"));
//    				subsystemEnabled.add(sso);
    				for (Subsystem subsystem : subsystemEnabled) {
    					systemJson = new JSONObject();
    					systemJson.put("code", subsystem.getCode());
    					systemJson.put("name", subsystem.getConvertName());
    					systemJson.put("homePage", subsystem.getConvertHomePage());
    					systemJson.put("encryptType", subsystem.getConvertEncryptType());
    					systemJson.put("logoutUrl", subsystem.getConvertLogoutUrl());
    					systemJson.put("redirectUrl", subsystem.getConvertRedirectUrl());
    					systemJson.put("description", subsystem.getConvertDescription());
    					systemJson.put("passport", userInfo);
    					systemJsonArray.put(systemJson);
					}
    				json.put("subsystems", systemJsonArray); 
    			}
    		}
    	} catch (JSONException e) {
    		json.put("ret", "2000");
    		json.put("msg", "服务器出错");
    		e.printStackTrace();
    	}finally {
    		return json;
    	}
    }

	public List<UserOauthBinding> findByUserIdAndOauthUidAndProvider(Long id, String uid, String provider) {
		return userOauthBindingDao.findByUserIdAndOauthUidAndProvider(id,uid, provider);
	}

    public User findByOauthUidAndProvider(String authId, String provider) {
        return userDao.findByOauthUidAndProvider(authId, provider);
	}


    public com.restfb.json.JsonObject sendMail(Map<String, String> pMap) {
        com.restfb.json.JsonObject jsonObject = new com.restfb.json.JsonObject();
        jsonObject.put("code", "fail");
        Properties props = new Properties();
        props.put("mail.smtp.host", pMap.get("mail_host"));
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        // 是否使用ssl加密端口
        String mailSSLPort = "465";
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.socketFactory.port", mailSSLPort);
        props.put("mail.smtp.port", mailSSLPort);

        String senSubject = null;
        String sendContent = null;
        try {
            senSubject = "星洲网登录验证码";
            Integer sendCode = (int) ((Math.random() * 9 + 1) * 100000);
            sendContent = "【星洲网】验证码：" + sendCode + ",您正在星洲网进行邮件注册用户，30分钟内输入有效，请注意保密！";
            jedisClient.set("xzw.sso.email." + pMap.get("email"), String.valueOf(sendCode), 1800);
        } catch (Exception e) {
            jsonObject.put("msg", "redis save sendCode fail");
            e.printStackTrace();
            return jsonObject;
        }

        Transport transport = null;
        try {
            javax.mail.Session session = javax.mail.Session.getInstance(props);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(pMap.get("mail_from")));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(pMap.get("email")));
            message.setSubject(senSubject);
            message.setContent(sendContent, "text/html;charset=gbk");
            message.setSentDate(new Date());
            message.saveChanges();
            transport = session.getTransport();
            transport.connect(pMap.get("mail_user"), pMap.get("mail_password"));
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            jsonObject.put("msg", "send email fail");
            e.printStackTrace();
            return jsonObject;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        jsonObject.put("code", "success");
        jsonObject.put("msg", "邮件发送成功！");
        return jsonObject;
    }

    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public void bindThirAccount(LoginInfo loginInfo, String provider, String userID, String userName) throws Exception{
        UserOauthBinding bing = new UserOauthBinding();
        bing.setUser(loginInfo.getUser());
        bing.setOauthUid(userID);
        bing.setProvider(provider);
        bing.setBindTime(Clock.DEFAULT.getCurrentDate());
        bing.setNickname(userName);
        userOauthBindingDao.save(bing);
    }

    public org.json.JSONObject synRegistToMember(User user, String password, String siteId) throws org.json.JSONException {
        if (user.getId() == null) {
            user.setRegisterDate(Clock.DEFAULT.getCurrentDate());
            this.save(user);
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("siteid", StringUtils.isNoneBlank(siteId)?siteId:"1"));
        params.add(new BasicNameValuePair("ssoid",""+user.getId()));
        params.add(new BasicNameValuePair("username",user.getUsername()));
        params.add(new BasicNameValuePair("nickname",user.getNickname()));
        params.add(new BasicNameValuePair("mobile",user.getPhone()));
        params.add(new BasicNameValuePair("headImg",user.getAvatarLarge()));
        params.add(new BasicNameValuePair("email",user.getEmail()));
        params.add(new BasicNameValuePair("password",password));
        params.add(new BasicNameValuePair("provider", user.getProvider()));
        params.add(new BasicNameValuePair("oauthid", user.getOauthUid()));
        String res = new HttpClientUtil().callAmucAPI("/api/member/syn/registerEx.do", params);
        org.json.JSONObject json = new org.json.JSONObject(res);
        System.out.println("register, call syn/registerEx.do result: " + json.toString());
        if (json.getString("code").equals("1")) {
            if (json.has("head")) {
                user.setAvatarSmall(json.getString("head"));
                user.setAvatarMiddle(json.getString("head"));
                user.setAvatarLarge(json.getString("head"));
            }
            user.setUid(json.getInt("uid"));
            this.save(user);
            if (!StringUtils.isEmpty(password)) {
                principalService.savePrincipal(user, password);
            }
        } else {
            this.delete(user.getId());
        }
        return json;
    }

    public String setDefaultImg() {
        SystemConfig config = systemConfigDao.findByScode("head_img_path");
        String default_img = config != null ? config.getSstatus() : "undifine";
        default_img += "default/head_img.jpg";
        return default_img;
    }
}
