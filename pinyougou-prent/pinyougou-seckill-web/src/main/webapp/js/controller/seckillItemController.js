var app = new Vue({
    //1.与前端对应的标识
    el:"#app",
    //2.放置属性的地方
    data:{
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        timeString:'',  //定义一个绑定页面的倒计时的变量
        ids:[],
        searchEntity:[],
        seckillId:0,   //秒杀商品的id
    },
    //3.放置方法的地方
    methods:{


        /**
         *
         * @param alltime 为 时间的毫秒数。
         * @returns {string}
         */
        convertTimeString:function(alltime){
            var allsecond=Math.floor(alltime/1000);//毫秒数转成 秒数。
            var days= Math.floor( allsecond/(60*60*24));//天数
            var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
            var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
            var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
            if(days>0){
                days=days+"天 ";
            }
            if(hours<10){
                hours="0"+hours;
            }
            if(minutes<10){
                minutes="0"+minutes;
            }
            if(seconds<10){
                seconds="0"+seconds;
            }

            if(days<=0){
                days='';
            }
            return days+hours+":"+minutes+":"+seconds;
        },



        //定义一个倒计时的函数方法， time:代表倒计时的时间毫秒数
        caculate:function (time) {

            //setInterval中的参数：function倒计时的函数   1000：表示倒计时的毫秒数
           var clock =  window.setInterval(function () {

                time=time-1000;

                app.timeString = app.convertTimeString(time);   //从毫秒数转成秒数

               if(time<=0){
                   //停止计秒
                    window.clearInterval(clock);
               }

            },1000)
        },

        //秒杀商品提交下单的函数
        submitOrder:function () {
            alert(66666666666)

            axios.get('/seckillOrder/submitOrder/'+this.seckillId+'.shtml').then(
                function (response) {
                    alert(response.data.message)
                }
            )
        }


    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {

        var obj = this.getUrlParam("id");

        this.seckillId = obj.id;

        alert(obj.id);

        this.caculate(100000000)
    }


});