			//用户是否存在
			function checkUserExit(contextPath, emailVal, func) {
                $.ajax({
                    type: "post",
                    url: contextPath + "/user/checkUserExit?field=email&value=" + emailVal,
                    success: function (res) {
                        func(JSON.parse(res));
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        func(null)
                    }
                });
            }


			function setImgSrc(userImg, eleId) {
				if (userImg.indexOf("static") != -1) {
					$("#" + eleId).attr("src", window.ctx + userImg);
				} else {
					if (userImg.indexOf("http://") != -1 || userImg.indexOf("https://") != -1) {
						$("#" + eleId).attr("src", userImg);
					} else {
						$("#" + eleId).attr("src", window.ctx + userImg);
					}
				}
			}
	    	
			function hideDiv(blockDiv){
				
				$("#"+blockDiv).hide();
			}

			//隐藏提示框
			function hideBlockDiv(){
//				$(".alert").hide();
	    		$("#errorDiv").hide();
		 		$("#blockDiv").hide();
		 		$("#successDiv").hide();
		 		$("#infoDiv").hide();
	    	}
			

			//得到选中的userId列表，id,id,id的形式
	    	function getSelectedIds(){
	    		var params = "";
	    		$("[name='checkbox']:checked").each(function(){
					params = params + $(this).val() + ",";
						
	    		});
	    		if(params.length>1){
		    		params = params.substring(0,params.length-1);
	    		}
	    		return params;
	    	}
			
			//下面是jquery对复选框的处理
			 $(document).ready(function() {
				//全选 
				$("#controlAll").click(function(){ 
					if($("#controlAll").is(":checked")){
						$("input[name='checkbox']").prop("checked","checked"); 
					}else {
						$("input[name='checkbox']").prop("checked",false); 
					}
				});
				
				//控制是否全部选中
				$("[name='checkbox']").click(function(){
					var checkedAll = true;
					$("[name='checkbox']").each(function(){
						if(!$(this).is(":checked")){
							checkedAll = false;
						}	
					});	
					if(checkedAll){
						$("#controlAll").prop("checked",true);
					}else {
						$("#controlAll").prop("checked",false);
					}
				});
			});
			
	    	/**
			 * 将省市级联封装成方法
			 * 省市级联的下拉框选择封装
			 */
			(function($){
			    $.fn.cityCasCade = function(operation,province,city){
			        var selectCity=null,params=null,overparams=null;
			        if(typeof operation == "string"){
			            if(operation == "select"){
			                if(province){
			                    $(this).find("option").each(function(){
			                        if($(this).val()==province){
			                            this.selected=true;
			                            $(this).change();
			                            if(city){
			                                $($.fn.cityCascadeParam.selectCity).find("option").each(function(){
			                                    if($(this).val()==city){
			                                        this.selected=true;
			                                        $(this).change();
			                                    }
			                                });
			                            }
			                        }
			                    });
			                }
			                return $(this);
			            }
			            selectCity=operation;
			        }
			        else{
			            if(operation.selector)selectCity=operation.selector;
			            if(operation.overide&&operation.data)overparams=operation.data;
			            else params=operation.data;
			        }
			        $.fn.cityCascadeParam ={};
			        $.fn.cityCascadeParam.data = _provinces_;
			        $.fn.cityCascadeParam.selectCity = null;
			        if(selectCity) $.fn.cityCascadeParam.selectCity=selectCity;
			        else throw "城市选择器不能为空";
			        $.fn.cityCascadeParam.cityValue= $($.fn.cityCascadeParam.selectCity+":first").val();
			        $.fn.cityCascadeParam.cityText= $($.fn.cityCascadeParam.selectCity+":first").text();
			        if(overparams)$.fn.cityCascadeParam.data=overparams;
			        else $.extend($.fn.cityCascadeParam.data, params);
			        $.fn.cityCascadeParam.loadProvince = function(self){
			              for(var p in $.fn.cityCascadeParam.data){
			               var opt=document.createElement("option");
			               opt.innerHTML=p;
			               opt.value=p;
			               self.appendChild(opt);
			              }
			        }
			        $.fn.cityCascadeParam.loadCity = function(parent){
			              var self = $($.fn.cityCascadeParam.selectCity).get(0);
			              if(!self) throw "未找到城市下拉框 城市选择器 >>" + $.fn.cityCascadeParam.selectCity;
			              var selectProvince=$(parent).val();
			              if(selectProvince in $.fn.cityCascadeParam.data){
			                  var citys=$.fn.cityCascadeParam.data[selectProvince];
			                  self.innerHTML="";
			                  for(var index=0;index<citys.length;index++){
			                   opt=document.createElement("option");
			                   opt.innerHTML=citys[index];
			                   opt.value=citys[index];
			                   self.appendChild(opt);
			                  }
			              }else{
			                  self.innerHTML="";
			                   opt=document.createElement("option");
			                   opt.innerHTML=$.fn.cityCascadeParam.cityText;
			                   opt.value=$.fn.cityCascadeParam.cityValue;
			                   self.appendChild(opt);
			              }
			        }
			        this.each(function(){
			            $.fn.cityCascadeParam.loadProvince(this);
			            $(this).change(function(){
			                $.fn.cityCascadeParam.loadCity(this);
			            });
			        });
			         
			        return $(this);
			    }
			})($);
			var _provinces_={
					选择省:["选择市"],
					北京市:["西城", "东城", "崇文", "宣武", "朝阳", "海淀", "丰台", "石景山", "门头沟", "房山", "通州", "顺义", "大兴", "昌平", "平谷", "怀柔", "密云", "延庆"], 
					天津市:["和平区","河东区","河西区","南开区","红桥区","塘沽区","汉沽区","大港区","西青区","津南区","武清区","蓟县","宁河县","静海县"], 
					河北省:["石家庄","唐山","秦皇岛","张家口","承德","廊坊","邯郸","邢台","保定","沧州","衡水"],
					山西省:["太原市","大同市","阳泉市","长治市","晋城市","朔州市","晋中市","运城市","忻州市","临汾市","吕梁市"],
					内蒙古:["呼和浩特市","包头市","乌海市","赤峰市","通辽市","鄂尔多斯市","呼伦贝尔市","巴彦淖尔市","乌兰察布市","兴安盟","锡林郭勒盟","阿拉善盟"],
					辽宁省:["沈阳市","大连市","鞍山市","抚顺市","本溪市","丹东市","锦州市","营口市","阜新市","辽阳市","盘锦市","铁岭市","朝阳市","葫芦岛市"],
					吉林省:["长春市","吉林市","四平市","辽源市","通化市","白山市","松原市","白城市","延边朝鲜族自治州"],
					黑龙江省:["哈尔滨市","齐齐哈尔市","鸡西市","鹤岗市","双鸭山市","大庆市","伊春市","佳木斯市","七台河市","牡丹江市","黑河市","绥化市","大兴安岭地区"],
					上海市:["浦东", "杨浦", "徐汇", "静安", "卢湾", "黄浦", "普陀", "闸北", "虹口", "长宁", "宝山", "闵行", "嘉定", "金山", "松江", "青浦", "崇明", "奉贤", "南汇"],
					江苏省:["南京市","无锡市","徐州市","常州市","苏州市","南通市","连云港市","淮安市","盐城市","扬州市","镇江市","泰州市","宿迁市"],
					浙江省:["杭州市","宁波市","温州市","嘉兴市","湖州市","绍兴市","金华市","衢州市","舟山市","台州市","丽水市"],
					安徽省:["合肥市","芜湖市","蚌埠市","淮南市","马鞍山市","淮北市","铜陵市","安庆市","黄山市","滁州市","阜阳市","宿州市","巢湖市","六安市","亳州市","池州市","宣城市"],
					福建省:["福州市","厦门市","莆田市","三明市","泉州市","漳州市","南平市","龙岩市","宁德市"],
					江西省:["南昌市","景德镇市","萍乡市","九江市","新余市","鹰潭市","赣州市","吉安市","宜春市","抚州市","上饶市"],
					山东省:["济南市","青岛市","淄博市","枣庄市","东营市","烟台市","潍坊市","济宁市","泰安市","威海市","日照市","莱芜市","临沂市","德州市","聊城市","滨州市","菏泽市"],
					河南省:["郑州市","开封市","洛阳市","平顶山市","安阳市","鹤壁市","新乡市","焦作市","濮阳市","许昌市","漯河市","三门峡市","南阳市","商丘市","信阳市","周口市","驻马店市"],
					湖北省:["武汉市","黄石市","十堰市","宜昌市","襄樊市","鄂州市","荆门市","孝感市","荆州市","黄冈市","咸宁市","随州市","恩施土家族苗族自治州"],
					湖南省:["长沙市","株洲市","湘潭市","衡阳市","邵阳市","岳阳市","常德市","张家界市","益阳市","郴州市","永州市","怀化市","娄底市","湘西土家族苗族自治州"],
					广东省:["广州市","韶关市","深圳市","珠海市","汕头市","佛山市","江门市","湛江市","茂名市","肇庆市","惠州市","梅州市","汕尾市","河源市","阳江市","清远市","东莞市","中山市","潮州市","揭阳市","云浮市"],
					广西省:["南宁市","柳州市","桂林市","梧州市","北海市","防城港市","钦州市","贵港市","玉林市","百色市","贺州市","河池市","来宾市","崇左市"],
					海南省:["海口市","通什市", "儋州市", "万宁市", "三亚市", "琼海市", "文 昌市", "西南中沙群岛", "琼山市"],
					重庆市:["渝中", "大渡口", "江北", "沙坪坝", "九龙坡", "南岸", "北碚", "万盛", "双桥", "渝北", "巴南", "万州", "涪陵", "黔江", "长寿"] ,
					四川省:["成都市","自贡市","攀枝花市","泸州市","德阳市","绵阳市","广元市","遂宁市","内江市","乐山市","南充市","眉山市","宜宾市","广安市","达州市","雅安市","巴中市","资阳市","阿坝藏族羌族自治州","甘孜藏族自治州","凉山彝族自治州"],
					贵州省:["贵阳市","六盘水市","遵义市","安顺市","铜仁地区","黔西南布依族苗族自治州","毕节地区","黔东南苗族侗族自治州","黔南布依族苗族自治州"],
					云南省:["昆明市","曲靖市","玉溪市","保山市","昭通市","丽江市","思茅市","临沧市","楚雄彝族自治州","红河哈尼族彝族自治州","文山壮族苗族自治州","西双版纳傣族自治州","大理白族自治州","德宏傣族景颇族自治州","怒江傈僳族自治州","迪庆藏族自治州"],
					西藏自治区:["拉萨市","昌都地区","山南地区","日喀则地区","那曲地区","阿里地区","林芝地区"],
					陕西省:["西安市","铜川市","宝鸡市","咸阳市","渭南市","延安市","汉中市","榆林市","安康市","商洛市"],
					甘肃省:["兰州市","嘉峪关市","金昌市","白银市","天水市","武威市","张掖市","平凉市","酒泉市","庆阳市","定西市","陇南市","临夏回族自治州","甘南藏族自治州"],
					青海省:["西宁市","海东地区","海北藏族自治州","黄南藏族自治州","海南藏族自治州","果洛藏族自治州","玉树藏族自治州","海西蒙古族藏族自治州"],
					宁夏自治区:["银川市","石嘴山市","吴忠市","固原市","中卫市"],
					新疆自治区:["乌鲁木齐市","克拉玛依市","吐鲁番地区","哈密地区","昌吉回族自治州","博尔塔拉蒙古自治州","巴音郭楞蒙古自治州","阿克苏地区","克孜勒苏柯尔克孜自治州","喀什地区","和田地区","伊犁哈萨克自治州","塔城地区","阿勒泰地区"],
					香港特区:["中西区", "湾仔区", "东区", "南区", "九龙-油尖旺区", "九龙-深水埗区", "九龙-九龙城区", "九龙-黄大仙区", "九龙-观塘区", "新界-北区", "新界-大埔区", "新界-沙田区", "新界-西贡区", "新界-荃湾区", "新界-屯门区", "新界-元朗区", "新界-葵青区", "新界-离岛区"],
					澳门特区:["花地玛堂区", "圣安多尼堂区", "大堂区", "望德堂区", "风顺堂区", "嘉模堂区", "圣方济各堂区", "路氹城"],
					台湾省:["台北市","高雄市","台北县","桃园县","新竹县","苗栗县","台中县","彰化县","南投县","云林县","嘉义县","台南县","高雄县","屏东县","宜兰县","花莲县","台东县","澎湖县","基隆市","新竹市","台中市","嘉义市","台南市"]
			};
			
			
			
			