var MD5 = require('crypto-js/md5');
var encHex = require('crypto-js/enc-hex');

jQuery(function () {
    var getGotoPage = function () {
        var querys = window.location.search.slice(1).split('&');
        var gotoPage = '';
        for (var i = 0; i < querys.length; i++) {
            var query = querys[i];
            if (query.indexOf('gotoPage=') === 0) {
                gotoPage = query.replace('gotoPage=', '');
                break;
            }
        }
        return decodeURIComponent(gotoPage);
    };

    var onCaptchaLink = function (evt) {
        evt.preventDefault();
        jQuery(this).find('img').attr('src', '/api/user/captcha?' + new Date().getTime());
    };

    var onSignin = function (evt) {
        evt.preventDefault();
        var bean = {};
        bean.account = jQuery('#account').val();
        var password = jQuery('#password').val();
        if (!bean.account) {
            // TODO 校验mobile格式
            window.alert('请输入邮箱');
            return;
        }
        if (!password || password.length < 6) {
            window.alert('请输入密码（密码最小长度为6位）');
            return;
        }
        bean.captcha = jQuery('#captcha').val() || '';
        bean.password = encHex.stringify(MD5(password));

        var msg = jQuery('#msg');
        msg.addClass('hidden');

        jQuery.ajax({
            type: 'POST',
            url: '/api/user/signin',
            data: JSON.stringify(bean),
            contentType: 'application/json; charset=UTF-8'
        }).done(function (data) {
            var gotoPage = getGotoPage();
            window.location.href = gotoPage || '/app';
        }).fail(function (data) {
            var errorMsg;
            try {
                errorMsg = JSON.parse(text).message;
            } catch (e) {
                errorMsg = '登录失败';
            }
            msg.removeClass('hidden').html(errorMsg);
        });
    };

    var onSignup = function (evt) {
        evt.preventDefault();
        var bean = {};
        bean.account = jQuery('#account').val();
        var password = jQuery('#password').val();
        if (!bean.account) {
            // TODO 校验mobile格式
            window.alert('请输入手机号');
            return;
        }
        if (!password || password.length < 6) {
            window.alert('请输入密码（密码最小长度为6位）');
            return;
        }
        bean.captcha = jQuery('#captcha').val() || '';
        bean.password = encHex.stringify(MD5(password));

        var msg = jQuery('#msg');
        msg.addClass('hidden');
        jQuery.ajax({
            type: 'POST',
            url: '/api/user/signup',
            data: JSON.stringify(bean),
            contentType: 'application/json; charset=UTF-8'
        }).done(function (data) {
            var gotoPage = getGotoPage();
            var url = '/account/signin.html';
            if (gotoPage) {
                url += '?gotoPage=' + encodeURIComponent(gotoPage);
            }
            window.alert('注册成功，进入登录页面');
            window.location.href = url;
        }).fail(function (data) {
            var errorMsg;
            try {
                errorMsg = JSON.parse(text).message;
            } catch (e) {
                errorMsg = '注册失败';
            }
            msg.removeClass('hidden').html(errorMsg);
        });

        return false;
    };

    jQuery('#signin-form').on('submit', onSignin).find('.captcha').on('click', onCaptchaLink);
    jQuery('#signup-form').on('submit', onSignup);
});
