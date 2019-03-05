/**
 * 第三方平台登录
 * Created by yuan-pc on 2018/11/16.
 */

/**
 * common function
 */
var js_sdks = [{id: 'facebook-jssdk', jsSrc: 'https://connect.facebook.net/en_US/sdk.js'},
    {id: 'google-jssdk', jsSrc: 'https://apis.google.com/js/client:plusone.js'},
    {id: 'google-api', jsSrc: 'https://apis.google.com/js/api.js'},
    {id: 'hello-api', jsSrc: 'http://adodson.com/hello.js/dist/hello.all.js'},
    {id: 'weichat-api', jsSrc: 'http://res.wx.qq.com/open/js/jweixin-1.0.0.js'},
];

function loadThirdJSSDK(config, setAttr) {
    globalConfig = config;
    if (!config.fbAppID || !config.googleClientId) {
        alert('参数配置为空！');
        return;
    }
    if (setAttr) {
        $('.g-signin').attr('data-clientid', config.googleClientId);
        localStorage.removeItem("cycle");
        // console.log("login load jssdk ....");
    }
    $.each(js_sdks, function (i, v) {
        var js, fjs = document.getElementsByTagName('script')[0];
        if (document.getElementById(v.id)) {
            return;
        }
        js = document.createElement('script');
        js.async = true;
        js.id = v.id;
        js.src = v.jsSrc;
        fjs.parentNode.insertBefore(js, fjs);

    })

    twitter = (function (d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0],
            t = twitter || {};
        if (d.getElementById(id)) return t;
        js = d.createElement(s);
        js.id = id;
        js.src = "https://platform.twitter.com/widgets.js";
        fjs.parentNode.insertBefore(js, fjs);

        t._e = [];
        t.ready = function (f) {
            t._e.push(f);
            //twitter_init();
        };
        return t;
    }(document, "script", "twitter-wjs"));

    if (Boolean(localStorage.getItem("cycle"))) {
        globalConfig.logoutInterval = setInterval(action, 2000);
    }

}

function initWXjs() {
    var urlPath = window.location.href;
    urlPath = urlPath.replace(/\:/g, '%3A').replace(/\//g, '%2F').replace(/\&/g, '%26').replace(/\?/g, '%3F').replace(/\=/g, '%3D').replace(/\-/g, '%2D');
    //微信JS注入；通过接口获取 微信权限验证配置所需 的 信息
    $.ajax({
        type: 'GET',
        url: globalConfig.contextPath + "/auth/wxJsHandler",
        data: {url: urlPath},
        dataType: 'json',
        success: function (result) {
            if (result.status == '0') {
                wx.config({
                    debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: result.data.appId, //必填，公众号的唯一标识
                    timestamp: result.data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: result.data.nonceStr, // 必填，生成签名的随机串
                    signature: result.data.signature,// 必填，签名，见附录1
                    jsApiList: ['chooseWXPay', 'chooseImage'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });

                wx.ready(function () {
                    wx.checkJsApi({
                        jsApiList: [
                            'chooseWXPay', 'chooseImage'
                        ]
                    });

                });
                wx.error(function (res) {
                    alert("config信息验证失败");
                    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。

                });
            } else {
                alert("获取WXjs失败");
            }
        }
    });
}

/**
 * 轮询登录状态
 * 第三方网站登录/登出操作，做相应登录/登出
 */

function action() {
    //location.reload();
    FB.getLoginStatus(function (response) {
        //张三方账户是否已登出
        if (globalConfig.isLogin) {
            if (!response.authResponse) {
                clearInterval(globalConfig.logoutInterval);
                syncLogout();
            } else {
                //globalConfig.logoutInterval = setInterval(action, 2000);
                localStorage.setItem("cycle", true);
                location.reload();
            }
        }
    });
}

function loginOut(provider) {
    //syncLogout();
    //var provider = '<%=loginProvider %>';
    if (!!provider && provider == 'googlePlus') {
        loginOutGP();
    } else if (!!provider && provider == 'facebook') {
        loginOutFB()
    } else if (!!provider && provider == 'twitter') {
        loginOutTwitter()
    } else {
        syncLogout();
    }
}

function syncLogout() {
    var reqUrl = globalConfig.contextPath + "/user/toSynchroLogout";
    location.href = reqUrl;
}

function loginOutFB(func) {
    FB.logout(function (response) {
        //console.log("fb login out .... reponse: " + JSON.stringify(response));
        //syncLogout();
        if (!func) {
            syncLogout();
            return;
        }
        func(true)
    });
}

function loginOutGP(func) {
    var revokeUrl = 'https://accounts.google.com/o/oauth2/revoke?token=' + localStorage.getItem("gp_accessToken");
    $.ajax({
        type: 'GET',
        url: revokeUrl,
        async: false,
        contentType: "application/json",
        dataType: 'jsonp',
        success: function (nullResponse) {
            if (!func) {
                syncLogout();
                return;
            }
            func(true)
        },
        error: function (e) {
            if (!func) {
                syncLogout();
                return;
            }
            func(true)
        }
    });
}

function loginOutTwitter(func) {
    twitter_init(true);
    var name = 'twitter', options = {force: false}, callFn = after_logout;
    twitter.logout(name, options, callFn()).then(function (res) {
        // Get Profile
        console.log("twitter out res:" + JSON.stringify(res));
        syncLogout();
        func(true);
    }, log);
}

function after_logout() {
    console.log("callback after_logout")
}

/**
 * 绑定第三方账号
 */
function bindOperate(provider) {
    if (provider == 'facebook') {
        bindAccountFB(provider);
    } else if (provider == 'googlePlus') {
        bindAccountGP(provider);
    }
     else if (provider == 'twitter') {
        bindAccountTwitter(provider);
    }
     else if (provider == 'wechat') {
        bindAccountWechat(provider);
    }
}

function isBinded(provider, userID, func) {
    $.ajax({
        type: "post",
        url: globalConfig.contextPath + "/user/connection/isBinded?provider=" + provider + "&oauthUid=" + userID,
        success: function (res) {
            func(JSON.parse(res));
        },
        error: function (jqXHR, textStatus, errorThrown) {
            func(null)
        }
    });
}

function bindAccountFB(provider) {
    FB.login(function (response) {
        if (response.authResponse) {
            /*console.log('Welcome!  Fetching your information.... ');
             FB.api('/me', function(response) {
             console.log('Good to see you, ' + response.name + '.');
             });*/
            var authRes = response.authResponse;
            isBinded(provider, authRes.userID, function (res) {
                if (!res) {
                    alert("请求失败！");
                    return;
                }
                if (res.code) {
                    alert("该" + provider + "账号已被绑定，请更换账号！");
                    console.log("该" + provider + "账号已被绑定，请更换账号！");
                    return;
                }

                //localStorage.setItem("fb_accessToken", authRes.accessToken);
                // console.log(response.authResponse.accessToken);
                FB.api('/me?fields=name,first_name,last_name,email,picture', function (res) {
                    //注入用户信息 session()
                    globalConfig.fb_oauthUrl_bind = globalConfig.contextPath + "/user/oauth2/session/new?provider=facebook&access_token=" + authRes.accessToken +
                        "&userID=" + authRes.userID + "&userName=" + res.name + "&toBind=1";
                    window.location.href = globalConfig.fb_oauthUrl_bind;
                });
            })

        } else {
            console.log('User cancelled login or did not fully authorize.')
            ;
        }
    }, {
        scope: 'public_profile,email'
    });
}

function bindAccountGP(provider) {
    gapi.load('client:auth2', initClient);
}

function bindAccountTwitter(provider) {
    twitter_login();
}

function bindAccountWechat(provider) {
    wechat_login();
}

function initClient() {
    gapi.client.init({
        apiKey: 'AIzaSyACB1X2aG47WbhiIrGpobWr9ex9osenEd0',
        clientId: '331174060034-nkaeg4q7b4em33nm9lb8gf2bs3n5k2s3.apps.googleusercontent.com',
        scope: 'profile'
    }).then(function () {
        gapi.auth2.getAuthInstance().isSignedIn.listen(updateSigninStatus);
        updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
    });
}

function updateSigninStatus(isSignedIn) {
    if (isSignedIn) {
        gapi.client.request({
            'path': 'https://people.googleapis.com/v1/people/me?requestMask.includeField=person.names',
        }).then(function (response) {
            var result = response.result;
            //return;
            // response.result.names[0]
            isBinded("googlePlus", result, function (res) {
                if (!res) {
                    alert("请求失败！");
                    return;
                }
                if (res.code) {
                    alert("该" + provider + "账号已被绑定，请更换账号！");
                    console.log("该" + provider + "账号已被绑定，请更换账号！");
                    return;
                }

                localStorage.setItem("gp_accessToken", authRes.accessToken);
                //注入用户信息 session()
                globalConfig.gp_oauthUrl_bind = globalConfig.contextPath + "/user/oauth2/session/new?provider=facebook&access_token=" + authRes.accessToken +
                    "&userID=" + authRes.userID + "&userName=" + res.name + "&toBind=1";
                window.location.href = globalConfig.gp_oauthUrl_bind;
            });
        }, function (reason) {
            console.log('reason: ' + reason.result.error.message);
        });
    } else {
        gapi.auth2.getAuthInstance().signIn();
    }
}


/**
 * facebook 认证登录
 *
 */
var globalConfig, userID, resp, twitter, log = console.log;
window.fbAsyncInit = function () {
    // console.log("fbAsynInit ....");
    FB.init({
        appId: globalConfig.fbAppID,
        cookie: true,
        xfbml: true,
        version: 'v3.2'
    });
    //FB.AppEvents.logPageView();
    FB.getLoginStatus(function (response) {
        if (!response.authResponse || globalConfig.isLogin) return;
        resp = response;
        userID = response.authResponse.userID;
        statusChangeCallback(response);
    });
    /*FB.logout(function(response) {
     console.log("fb login out ....");
     });*/
};

function checkLoginState() {
    //console.log("checkLoginState .... ");
    //location.reload();
    FB.getLoginStatus(function (response) {
        if (!response.authResponse || globalConfig.isLogin) return;
        resp = response;
        userID = response.authResponse.userID;
        statusChangeCallback(response);
    });
    /*if(!globalConfig.fb_oauthUrl) return;
     window.location.href = globalConfig.fb_oauthUrl;*/
}

function statusChangeCallback(response) {
    //可用于后台验证，但是前台调用SDK则会自动验证
    var authRes = response.authResponse;
    //globalConfig.fb_accessToken = authRes.accessToken;
    //localStorage.setItem("fb_accessToken", authRes.accessToken);
    // console.log(response.authResponse.accessToken);
    FB.api('/me?fields=name,first_name,last_name,email,picture', function (res) {
        //注入用户信息 session()
        globalConfig.fb_oauthUrl = globalConfig.contextPath + "/user/oauth2/session/new?provider=facebook&access_token=" + authRes.accessToken +
            "&userID=" + authRes.userID + "&userName=" + res.name;
        //isLogOut("facebook", function (res) {
        //if (!res || res.code == false) return;
        window.location.href = globalConfig.fb_oauthUrl;
        //});
        /*globalConfig.isLogin = false;
         globalConfig.loginInterval = setInterval(action, 2000);*/
    });
}

/**
 * google+ 认证登录
 *
 */
function signinCallback(authResult) {
    if (authResult && !authResult.error) {
        btn_googlePlus();
        //var accessToken = authResult.access_token;
        //globalConfig.gp_accessToken = authResult.access_token;
        localStorage.setItem("gp_accessToken", authResult.access_token);
        //console.log('authResult: ' + JSON.stringify(authResult));
        window.authClient = gapi.client.load("oauth2", "v2", function () {
            var instance = gapi.auth2.getAuthInstance();
            var request = gapi.client.oauth2.userinfo.get();
            request.execute(function (profile) {
                if (!!profile.error) return;
                //console.log("obj: " + JSON.stringify(profile))
                globalConfig.gp_oauthUrl = globalConfig.contextPath + "/user/oauth2/session/new?provider=googlePlus&access_token=" + authResult.access_token +
                    "&userID=" + profile.id + "&userName=" + profile.name + "&headImg=" + profile.picture;
                //isLogOut("googlePlus", function (res) {
                //if (!res || res.code == false) return;
                window.location.href = globalConfig.gp_oauthUrl;
                //});
            });
        });
    }
}

// 初始化函数
/*function render() {
 gapi.signin.render('google_login', {
 'callback': 'signinCallback',
 'approvalprompt': 'auto',
 'clientid': '331174060034-nkaeg4q7b4em33nm9lb8gf2bs3n5k2s3.apps.googleusercontent.com',
 'cookiepolicy': 'single_host_origin',
 'requestvisibleactions': 'http://schemas.google.com/AddActivity',
 'scope': 'https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/userinfo.email'
 });
 }*/

function btn_googlePlus() {
    $('#signinButton div').on('click', function () {
        if (!globalConfig.gp_oauthUrl) return;
        setTimeout(function () {
            window.location.href = globalConfig.gp_oauthUrl;
        }, 1500)
    })
}

/**
 * twitter 认证登录
 */


function twitter_init(sw) {
    hello.init({
        'twitter': globalConfig.twitterAppID
    }, {
        // redirect_uri:'/', //代理后的重定向路径，可不填
        oauth_proxy: 'https://auth-server.herokuapp.com/proxy',
        force: sw
    });
    // Twitter instance
    twitter = hello('twitter');
}

function twitter_login(network) {  //登录方法，并将twitter 作为参数传入
    twitter_init(false);
    twitter.login().then(function (r) {
        // Get Profile
        return twitter.api('/me');
    }, log).then(function (res) {
        console.log("twitter login res:" + JSON.stringify(res));
        //twitter.getAuthResponse().then(function (response) {
        globalConfig.twitter_oauthUrl = globalConfig.contextPath + "/user/oauth2/session/new?provider=twitter&access_token=" +
            twitter.getAuthResponse().access_token + "&userID=" + res.id + "&userName=" + res.name + "&headImg=" + res.profile_image_url;
        window.location.href = globalConfig.twitter_oauthUrl;
        //})
    }, log);
}

/**
 * wechat认证登录
 */

function wechat_login() {
    var appid = globalConfig.wechatAppID, redirect_uri = encodeURIComponent(globalConfig.contextPath +  '/auth/twitter/callback'), state = random_state(32, 64);
    //console.log("random_state: " + state)
    window.location.href='https://open.weixin.qq.com/connect/qrconnect?' + 'appid=' + appid + '&redirect_uri=' +
         redirect_uri + '&response_type=code&scope=snsapi_login&state=' + state + ' #wechat_redirect';
}

function random_state(len, radix) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    var uuid = [], i;
    radix = radix || chars.length;

    if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
        // rfc4122, version 4 form
        var r;

        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';

        // Fill in random data.  At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | Math.random()*16;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
    }

    return uuid.join('');
}