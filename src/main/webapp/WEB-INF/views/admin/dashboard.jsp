<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
<title>控制台</title>
<script>
// 激活菜单
$(document).ready(function() {
    $("#dashboard_tab").addClass("active");
});
</script>
</head>
<body>
<div class="row">
    <div id="attention_msg" class="span6">
        <h1>关键通知</h1>
        <ul class="unstyled">
            <li><span class="label label-important">通信失败!</span>与Ucenter通信失败</li>
            <li><span class="label label-info">设置更改</span>系统域名更新</li>
            <li><span class="label label-warning">存储空间告警</span>系统用户空间存储剩余不足1G</li>
            <li><span class="label label-warning">存储空间告警</span>系统用户空间存储剩余不足1G</li>
        </ul>
    </div>
    <div id="sysinfo" class="span6">
        <h1>系统状态</h1>
        <ul class="unstyled">
            <li><i class="icon-ok"></i>数据库连接正常</li>
            <li><i class="icon-ok"></i>存储连接正常</li>
            <li><i class="icon-ok"></i>缓存服务器连接正常</li>
            <li><i class="icon-remove"></i>子系统通信异常</li>
        </ul>
    </div>
</div>
    
    <div id="stat_data">
        <!-- 总户数 活跃用户数  -->
        <div id="user_count" style="height: 400px"></div>
    </div>

    <!-- 演示用从百度CDN引入AMD模块 -->
    <script src="http://s1.bdstatic.com/r/www/cache/ecom/esl/1-6-10/esl.js"></script>

    <script type="text/javascript">
        //演示数据
        var option = {
            title : {
                text : '用户统计',
                subtext : '演示数据'
            },
            tooltip : {
                trigger : 'axis'
            },
            legend : {
                data : [ '用户总数', '活跃用户数' ]
            },
            toolbox : {
                show : true,
                feature : {
                    mark : {
                        show : true
                    },
                    dataView : {
                        show : true,
                        readOnly : false
                    },
                    magicType : {
                        show : true,
                        type : [ 'line', 'bar' ]
                    },
                    restore : {
                        show : true
                    },
                    saveAsImage : {
                        show : true
                    }
                }
            },
            calculable : true,
            xAxis : [ {
                type : 'category',
                data : [ '1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月',
                        '10月', '11月', '12月' ]
            } ],
            yAxis : [ {
                type : 'value'
            } ],
            series : [
                    {
                        name : '用户总数',
                        type : 'bar',
                        data : [ 2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2,
                                163.6, 180.0, 196.4, 203.3 ],
                    },
                    {
                        name : '活跃用户数',
                        type : 'bar',
                        data : [ 0.6, 1.9, 2.0, 16.4, 18.7, 50.7, 105.6, 112.2,
                                128.7, 98.8, 186.0, 126.3 ],
                        markPoint : {
                            data : [ {
                                name : '年最高',
                                value : 182.2,
                                xAxis : 7,
                                yAxis : 183,
                                symbolSize : 18
                            }, {
                                name : '年最低',
                                value : 2.3,
                                xAxis : 11,
                                yAxis : 3
                            } ]
                        },
                        markLine : {
                            data : [ {
                                type : 'average',
                                name : '平均值'
                            } ]
                        }
                    }

            ]
        };
    </script>

    <!-- 初始化 -->
    <script type="text/javascript">
        // 路径配置
        require.config({
            paths : {
                'echarts' : 'http://echarts.baidu.com/build/echarts',
                'echarts/chart/bar' : 'http://echarts.baidu.com/build/echarts'
            }
        });

        // 使用
        require([ 'echarts', 'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
        ], function(ec) {
            // 基于准备好的dom，初始化echarts图表
            var myChart = ec.init(document.getElementById('user_count'));
            // 为echarts对象加载数据 
            myChart.setOption(option);
        });
    </script>

</body>
</html>