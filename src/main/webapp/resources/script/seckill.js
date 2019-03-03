var seckill = {
    //封装秒杀相关的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + "/" + md5 + "/execution";
        }
    },

    //详情页秒杀逻辑
    detail: {
        //初始化工作，涉及验证手机号和倒计时工作
        init: function (params) {
            var killPhone = $.cookie('killPhone');

            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,
                    backdrop: 'static', //禁止位置关闭
                    keyboard: false //关闭键盘事件
                });

                $('#killPhoneBtn').click(function () {
                    var input = $('#killPhoneKey').val();
                    if (seckill.validatePhone(input)) {
                        //手机号写入cookie
                        $.cookie('killPhone', input, {expires:7, path: '/seckill'});
                        //重新刷新界面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(500);
                    }
                });
            }

            //倒计时
            var seckillId = params.seckillId;
            var startTime = params.startTime;
            var endTime = params.endTime;
            $.get(seckill.URL.now(), {}, function (result) {
                console.log("result" + result);//TODO
                if (result && result['success'])  {
                    var nowTime = result['data'];
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log("result: " + result);
                }
            });
        }
    },

    validatePhone: function (killPhone) {
        if (killPhone && killPhone.length == 11 && !isNaN(killPhone)) {
            return true;
        } else {
            return false;
        }
    },

    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            //秒杀未开启，倒计时
            seckillBox.countdown(new Date(startTime + 1000), function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //时间跑完后回调函数，获取秒杀地址，可以秒杀
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {
            //可以秒杀
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    },

    handlerSeckill: function (seckillId, seckillBox) {
        //2.执行秒杀
        //3.根据秒杀返回结果，设置seckill-box显示内容
        seckillBox.hide()
            .html('<button class="btn btn-primary btn-lg" id="seckillBtn">开始秒杀</button>');

        //1.获取秒杀地址
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];

                if (exposer['exposed']) {
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);

                    //绑定一次点击事件
                    $('#seckillBtn').one('click', function () {
                        //1.按钮置灰
                        $(this).addClass('disabled');
                        //2.执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result) {
                                var data = result['data'];
                                var stateInfo = data['stateInfo'];

                                seckillBox.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        })
                    });
                    seckillBox.show();

                } else {
                    //未开启秒杀，用户客户端等待秒杀开启时会出现时间走不一致的情况，这时需要重新执行countdown方法
                    var now = exposer['now'];
                    var startTime = exposer['startTime'];
                    var endTime = exposer['endTime'];

                    seckill.countDown(seckillId, now, startTime, endTime);
                }
            } else {
                console.log("result: " + result);
            }
        })
    }
}